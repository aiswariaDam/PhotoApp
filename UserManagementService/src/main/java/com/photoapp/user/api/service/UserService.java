package com.photoapp.user.api.service;

import com.photoapp.user.api.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface UserService extends UserDetailsService {
    public UserDto createUser(UserDto userDto);
    public UserDto getUserDetailsByEmail(String email);

    UserDto getUserDetailsByUserId(String userId);
}