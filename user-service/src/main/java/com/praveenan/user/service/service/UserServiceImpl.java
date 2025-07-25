package com.praveenan.user.service.service;

import com.praveenan.user.service.config.JwtProvider;
import com.praveenan.user.service.model.User;
import com.praveenan.user.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{
  @Autowired
  private UserRepository userRepository;

  @Override
  public User getUserProfile(String jwt) {
    String email = JwtProvider.getEmailFromJwtToken(jwt);
    return userRepository.findByEmail(email);
  }
}
