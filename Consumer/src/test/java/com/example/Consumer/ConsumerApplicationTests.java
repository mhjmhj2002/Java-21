package com.example.Consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.example.consumer.ConsumerApplication;

@SpringBootTest
@ContextConfiguration(classes = ConsumerApplication.class)
class ConsumerApplicationTests {

	@Test
	void contextLoads() {
	}

}
