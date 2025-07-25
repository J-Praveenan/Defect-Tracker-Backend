package com.praveenan.user.service.controller;

import com.praveenan.user.service.config.JwtProvider;
import com.praveenan.user.service.model.User;
import com.praveenan.user.service.repository.UserRepository;
import com.praveenan.user.service.request.LoginRequest;
import com.praveenan.user.service.response.AuthResponse;
import com.praveenan.user.service.service.CustomUserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private CustomUserServiceImplementation customUserDetails;

  @PostMapping("/signup")
  public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
    String email = user.getEmail();
    String password = user.getPassword();
    String fullName = user.getFullName();
    String role = user.getRole();

    User isEmailExist = userRepository.findByEmail(email);
    if (isEmailExist != null) {
      throw new Exception("Email is Already Used with Another Account.");
    }

    // Create new User
    User newUser = new User();
    newUser.setEmail(email);
    newUser.setFullName(fullName);
    newUser.setPassword(passwordEncoder.encode(password));
    newUser.setRole(role);

    userRepository.save(newUser);

    Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String token = JwtProvider.generateToken(authentication);
    AuthResponse authResponse = new AuthResponse();

    authResponse.setJwt(token);
    authResponse.setMessage("Register Success");
    authResponse.setStatus(true);

    return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);
  }

  @PostMapping("/signIn")
  public ResponseEntity<AuthResponse> signIn(@RequestBody LoginRequest loginRequest){
    String username = loginRequest.getEmail();
    String password = loginRequest.getPassword();

    Authentication authentication = authenticate(username, password);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String token = JwtProvider.generateToken(authentication);
    AuthResponse authResponse = new AuthResponse();
    authResponse.setMessage("Login Success");
    authResponse.setJwt(token);
    authResponse.setStatus(true);

    return new ResponseEntity<AuthResponse>(authResponse,HttpStatus.OK);
  }

  private Authentication authenticate(String username, String password) {
    UserDetails userDetails = customUserDetails.loadUserByUsername(username);

    if(userDetails == null){
      throw new BadCredentialsException("Invalid username or password.");
    }

    if(!passwordEncoder.matches(password, userDetails.getPassword())){
      throw new BadCredentialsException("Invalid username or password.");
    }

    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }
}
