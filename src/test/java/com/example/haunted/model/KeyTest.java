package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KeyTest {
	//Tests happy path and edge cases for the constructor and the get methods
	@ParameterizedTest
	@CsvSource({
		"'Rusty Key', 'Unlocks the cellar'",
		"'', ''",
		"' ', ' '"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, String description) {
		Key key = new Key(name, description);
		
		assertEquals(name, key.getName());
		assertEquals(description, key.getDescription());
	}
}