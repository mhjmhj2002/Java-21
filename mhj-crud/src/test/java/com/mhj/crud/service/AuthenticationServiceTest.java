package com.mhj.crud.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mhj.crud.dto.LoginUserDto;
import com.mhj.crud.dto.RegisterUserDto;
import com.mhj.crud.entity.User;
import com.mhj.crud.repository.UserRepository;

class AuthenticationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private AuthenticationService authenticationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSignup() {
		// Arrange
		RegisterUserDto input = new RegisterUserDto();
		input.setFullName("Test User");
		input.setEmail("testuser@example.com");
		input.setPassword("password123");

		User user = User.builder().fullName(input.getFullName()).email(input.getEmail()).password("encodedPassword")
				.build();

		when(passwordEncoder.encode(input.getPassword())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		// Act
		User result = authenticationService.signup(input);

		// Assert
		assertNotNull(result);
		assertEquals("Test User", result.getFullName());
		assertEquals("testuser@example.com", result.getEmail());
		assertEquals("encodedPassword", result.getPassword());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void testAuthenticate() {
		// Arrange
		LoginUserDto input = new LoginUserDto();
		input.setEmail("testuser@example.com");
		input.setPassword("password123");

		User user = new User();
		user.setEmail(input.getEmail());

		when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(user));

		// Act
		User result = authenticationService.authenticate(input);

		// Assert
		assertNotNull(result);
		assertEquals("testuser@example.com", result.getEmail());
		verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(userRepository, times(1)).findByEmail(input.getEmail());
	}

	@Test
	void testAllUsers() {
		// Arrange
		User user1 = new User();
		user1.setFullName("User One");
		user1.setEmail("userone@example.com");

		User user2 = new User();
		user2.setFullName("User Two");
		user2.setEmail("usertwo@example.com");

		when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

		// Act
		List<User> result = authenticationService.allUsers();

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals("User One", result.get(0).getFullName());
		assertEquals("User Two", result.get(1).getFullName());
		verify(userRepository, times(1)).findAll();
	}
}