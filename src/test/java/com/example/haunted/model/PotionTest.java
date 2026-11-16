package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PotionTest {

	//Tests happy paths and edge cases for constructors and getters
	@ParameterizedTest
	@CsvSource({
		"'Minor Healing Potion', 'Restores a small amount of health', 25",
		"'Greater Elixir', 'Fully restores vitality', 100",
		"'', '', 0",
		"' ', ' ', -10",
		"'Max Potion', 'Max healing', 2147483647",
		"'Min Potion', 'Min healing', -2147483648"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, String description, int healingAmount) {
		Potion potion = new Potion(name, description, healingAmount);

		assertEquals(name, potion.getName());
		assertEquals(description, potion.getDescription());
		assertEquals(healingAmount, potion.getHealingAmount());
	}

	//Tests happy paths and edge cases for use method
	@ParameterizedTest
	@CsvSource({
		"100, 50, 25, 75",
		"100, 50, 50, 100",
		"100, 50, 80, 100",
		"100, 50, 0, 50",
		"100, 50, -20, 50",
		"100, 0, 30, 100",
		"100, 100, 40, 40"
	})
	void use_VariousHealingAmountsAndPlayerStates_UpdatesPlayerHealthCorrectly(int maxHealth, int damageTaken, int healingAmount, int expectedHealth) {
		Player player = new Player("Hero", maxHealth, 10, 5, new Inventory(5));
		player.takeDamage(damageTaken);
		Potion potion = new Potion("Health Potion", "Restores health", healingAmount);

		potion.use(player);

		assertEquals(expectedHealth, player.getHealth());
	}
}