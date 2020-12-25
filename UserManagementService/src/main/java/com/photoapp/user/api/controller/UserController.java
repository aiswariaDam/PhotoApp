package com.photoapp.user.api.controller;

import com.photoapp.user.api.model.UserCreationResponseModel;
import com.photoapp.user.api.model.UserRequestModel;
import com.photoapp.user.api.model.UserResponseModel;
import com.photoapp.user.api.service.UserService;
import com.photoapp.user.api.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private Environment env;

    @Autowired
    UserService userService;

    @GetMapping("/check/status")
    public String getUsers() {
        return "User Status " + env.getProperty("local.server.port" )+ " key " +env.getProperty("token.secret" );
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<UserCreationResponseModel> createUser(@Valid @RequestBody UserRequestModel userRequestModel) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = mapper.map(userRequestModel, UserDto.class);
        UserDto returnUserDto = userService.createUser(userDto);

        UserCreationResponseModel userCreationResponseModel = mapper.map(returnUserDto, UserCreationResponseModel.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreationResponseModel);
    }

    @GetMapping(value = "/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<UserResponseModel> getUserDetails(@PathVariable("userId") String userId){

        UserDto userDto = userService.getUserDetailsByUserId(userId);
        UserResponseModel responseModel = new ModelMapper().map(userDto, UserResponseModel.class);
        return ResponseEntity.status(HttpStatus.OK).body(responseModel);
    }
}
