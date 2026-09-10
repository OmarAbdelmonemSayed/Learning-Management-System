package com.example.lms.auth;

import com.example.lms.auth.dto.LoginDto;
import com.example.lms.auth.dto.RegisterDto;
import com.example.lms.auth.dto.UpdateProfileDto;
import com.example.lms.common.enums.UserRole;
import com.example.lms.token.BlacklistedToken;
import com.example.lms.token.TokenRepository;
import com.example.lms.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest
//@WebMvcTest(AuthController.class)
//@AutoConfigureMockMvc
public class AuthControllerTest {
    
    @Mock
    private AuthService authService;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private TokenRepository tokenRepository;
    
    @InjectMocks
    private AuthController authController;
    
    private MockMvc mockMvc;
    
    private String mockToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbIlJPTEUiXSwiZXhwIjoxNjgwMTg4MjIwfQ.abcdefg";
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build(); // Configure MockMvc manually
    }
    
    @Test
    void register_ShouldReturnSuccessResponse() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("test@example.com");
        registerDto.setPassword("password");
        registerDto.setName("Test User");
        registerDto.setRole(UserRole.ADMIN);
        
        ResponseEntity<Object> response = ResponseEntity.ok("User registered successfully");
        doReturn(response).when(authService).register(any(RegisterDto.class));
        
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "test@example.com",
                            "password": "password",
                            "name": "Test User",
                            "role": "ADMIN"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }
    
    @Test
    void login_ShouldReturnSuccessResponse() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password");
        
        ResponseEntity<Object> response = ResponseEntity.ok("Login successful");
        doReturn(response).when(authService).login(any(LoginDto.class));
        
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "test@example.com",
                            "password": "password"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }
    
    @Test
    void logout_ShouldReturnSuccessResponse() throws Exception {
        String token = "validToken";
        ResponseEntity<Object> response = ResponseEntity.ok("Logout successful");
        
        doReturn(response).when(authService).logout(any(String.class));
        
        mockMvc.perform(delete("/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successful"));
    }
    
    @Test
    void getProfile_ShouldReturnProfileResponse() throws Exception {
        String token = "validToken";
        String userId = "123";
        ResponseEntity<Object> response = ResponseEntity.ok("User profile data");
        
        Mockito.when(jwtService.extractUsername(any(String.class))).thenReturn(userId);
        doReturn(response).when(authService).getProfile(userId);
        
        mockMvc.perform(get("/auth/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("User profile data"));
    }
    
    @Test
    void updateProfile_ShouldReturnSuccessResponse() throws Exception {
        String token = "validToken";
        String userId = "123";
        UpdateProfileDto updateProfileDto = new UpdateProfileDto();
        updateProfileDto.setName("New Name");
        updateProfileDto.setEmail("test@example.com");
        
        ResponseEntity<Object> response = ResponseEntity.ok("Profile updated successfully");
        
        Mockito.when(jwtService.extractUsername(any(String.class))).thenReturn(userId);
        doReturn(response).when(authService).updateProfile(any(String.class), any(UpdateProfileDto.class));
        
        mockMvc.perform(put("/auth/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("""
                        {
                            "name": "New Name",
                            "email": "test@example.com"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile updated successfully"));
    }
}