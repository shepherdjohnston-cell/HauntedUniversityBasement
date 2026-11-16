package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class WeaponTest {
	//Tests happy path and edge cases for the constructor and the get methods
	@ParameterizedTest
	@CsvSource({
		"'Diamond Rod', 'Description A', 30",
		"'', '', 0",
		"' ', ' ', -30"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, String description, int attackBonus) {
		Weapon weapon = new Weapon(name, description, attackBonus);
		
		assertEquals(name, weapon.getName());
		assertEquals(description, weapon.getDescription());
		assertEquals(attackBonus, weapon.getAttackBonus());
	}
}