package com.praveenan.user.service.service;

import com.praveenan.user.service.model.User;

public interface UserService {
  public User getUserProfile(String jwt);
}
