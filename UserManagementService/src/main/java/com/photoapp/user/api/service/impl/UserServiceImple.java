package com.photoapp.user.api.service.impl;

import com.photoapp.user.api.data.AlbumsServiceClient;
import com.photoapp.user.api.data.UserEntity;
import com.photoapp.user.api.data.UserRepository;
import com.photoapp.user.api.model.AlbumResponseModel;
import com.photoapp.user.api.service.UserService;
import com.photoapp.user.api.shared.UserDto;
import feign.FeignException;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImple implements UserService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
//    RestTemplate restTemplate;
    Environment environment;
    AlbumsServiceClient albumsServiceClient;

    @Autowired
    public UserServiceImple(UserRepository userRepository,
                            BCryptPasswordEncoder bCryptPasswordEncoder,
                            RestTemplate restTemplate,
                            Environment environment,
                            AlbumsServiceClient albumsServiceClient){
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    this.restTemplate = restTemplate;
    this.environment = environment;
    this.albumsServiceClient = albumsServiceClient;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity =  mapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userRepository.save(userEntity);

        UserDto returnValue = mapper.map(userEntity, UserDto.class);
        return returnValue;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if(null == userEntity){
            throw new UsernameNotFoundException(email);
        }
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto returnValue = mapper.map(userEntity, UserDto.class);
        return returnValue;
    }

    @Override
    public UserDto getUserDetailsByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if(null == userEntity){
            throw new UsernameNotFoundException(userId);
        }
        UserDto userDto = new ModelMapper().map (userEntity, UserDto.class);

        String albumsUrl = String.format(environment.getProperty("albums.service.url"), userId);

//        ResponseEntity<List<AlbumResponseModel>> albumsListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
//        });
        //        List<AlbumResponseModel> albumsList = albumsListResponse.getBody();
        List<AlbumResponseModel> albumsList = null;
        try {
            albumsList = albumsServiceClient.getAlbums(userId);

        }catch (FeignException e){
            logger.error(e.getMessage());
        }
        userDto.setAlbumsList(albumsList);
        return userDto;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(userName);
        if(null == userEntity){
            throw new UsernameNotFoundException(userName);
        }
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }
}
