package com.example.haunted.events;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InteractionResultTest {
	//Tests happy path and edge cases for the constructor and the get methods
	@ParameterizedTest
	@CsvSource({
		"'True', 'Trade'",
		"False, ''",
		"False, ' '"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(boolean success, String message) {
		InteractionResult interactionResult= new InteractionResult(success, message);
		
		assertEquals(success, interactionResult.isSuccess());
		assertEquals(message, interactionResult.getMessage());
	}
}