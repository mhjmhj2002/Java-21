package com.mhj.crud.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

	@InjectMocks
	private JwtService jwtService;

	@Mock
	private UserDetails userDetails;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testExtractUsername() {
		// Arrange
		String token = "mockToken";
		String expectedUsername = "testUser";
		when(userDetails.getUsername()).thenReturn(expectedUsername);

		// Mock extractClaim behavior
		JwtService spyJwtService = spy(jwtService);
		doReturn(expectedUsername).when(spyJwtService).extractClaim(eq(token), any());

		// Act
		String username = spyJwtService.extractUsername(token);

		// Assert
		assertEquals(expectedUsername, username);
	}

	@Test
	void testGenerateToken() {
		// Arrange
		when(userDetails.getUsername()).thenReturn("testUser");

		// Act
		String token = jwtService.generateToken(userDetails);

		// Assert
		assertNotNull(token);
	}
}