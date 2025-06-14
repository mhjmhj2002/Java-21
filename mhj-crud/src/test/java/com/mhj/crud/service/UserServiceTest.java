package com.mhj.crud.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.mhj.crud.entity.User;
import com.mhj.crud.repository.UserRepository;

class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindUserById() {
		// Arrange
		Integer userId = 1;
		User user = new User();
		user.setId(userId);
		user.setFullName("Test User");
		user.setEmail("testuser@example.com");
		user.setPassword("password123");

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// Act
		User result = userService.findUserById(userId);

		// Assert
		assertNotNull(result);
		assertEquals(userId, result.getId());
		assertEquals("Test User", result.getFullName());
		assertEquals("testuser@example.com", result.getEmail());
		assertEquals("password123", result.getPassword());
		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	void testSaveUser() {
		// Arrange
		User user = new User();
		user.setFullName("New User");
		user.setEmail("newuser@example.com");
		user.setPassword("newpassword");

		when(userRepository.save(user)).thenReturn(user);

		// Act
		User result = userService.saveUser(user);

		// Assert
		assertNotNull(result);
		assertEquals("New User", result.getFullName());
		assertEquals("newuser@example.com", result.getEmail());
		assertEquals("newpassword", result.getPassword());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	void testDeleteUserById() {
		// Arrange
		Long userId = 1L;

		// Act
		userService.deleteById(userId);

		// Assert
		verify(userRepository, times(1)).deleteById(userId);
	}

	@Test
	void testFindUserByIdThrowsExceptionWhenUserNotFound() {
		// Arrange
		Integer userId = 1;
		when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> userService.findUserById(userId), "User not found, id: " + userId);
	}

	@Test
	void testAllUsers() {
		// Arrange
		User user1 = new User();
		user1.setId(1);
		user1.setFullName("User One");
		user1.setEmail("userone@example.com");

		User user2 = new User();
		user2.setId(2);
		user2.setFullName("User Two");
		user2.setEmail("usertwo@example.com");

		List<User> users = Arrays.asList(user1, user2);
		when(userRepository.findAll()).thenReturn(users);

		// Act
		List<User> result = userService.allUsers();

		// Assert
		assertEquals(2, result.size());
		assertEquals("User One", result.get(0).getFullName());
		assertEquals("User Two", result.get(1).getFullName());
	}
}