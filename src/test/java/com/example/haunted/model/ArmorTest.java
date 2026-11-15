package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ArmorTest {
	//Tests happy path and edge cases for the constructor and the get methods
	@ParameterizedTest
	@CsvSource({
		"'Diamond Chestplate', 'The greatest chestplate known to man', 30",
		"'', '', 0",
		"' ', ' ', -30"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, String description, int defenseBonus) {
		Armor armor = new Armor(name, description, defenseBonus);
		
		assertEquals(name, armor.getName());
		assertEquals(description, armor.getDescription());
		assertEquals(defenseBonus, armor.getDefenseBonus());
	}
}