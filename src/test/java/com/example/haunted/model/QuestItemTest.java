package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class QuestItemTest {
	//Tests happy path and edge cases for the constructor and the get methods
	@ParameterizedTest
	@CsvSource({
		"'Reach New Heights', 'Summit Mt. Fuji'",
		"'', ''",
		"' ', ' '"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, String description) {
		QuestItem questItem = new QuestItem(name, description);
		
		assertEquals(name, questItem.getName());
		assertEquals(description, questItem.getDescription());
	}
}