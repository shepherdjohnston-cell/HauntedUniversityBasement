package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MonsterTest {

	//Subclass used to instantiate an object of abstract class Item to create the item list
	private static class TestItem extends Item {
		protected TestItem(String name, String description) {
			super(name, description);
		}
	}

	//Tests both happy paths and boundaries on all parameters for Monster
	@ParameterizedTest
	@CsvSource({
		"'Goblin', 50, 10, 5, 0",
		"'Dragon', 1000, 150, 80, 3",
		"'', 0, 0, 0, 0",
		"' ', -10, -5, -1, 1",
		"'Boss', 2147483647, 2147483647, 2147483647, 5",
		"'Weakling', -2147483648, -2147483648, -2147483648, 2"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, int health, int attack, int defense, int lootCount) {
		//Creates Item List
		List<Item> inputLoot = new ArrayList<>();
		for (int i = 0; i < lootCount; i++) {
			inputLoot.add(new TestItem("Item " + i, "Description " + i));
		}

		Monster monster = new Monster(name, health, attack, defense, inputLoot);

		assertEquals(name, monster.getName());
		assertEquals(health, monster.getHealth());
		assertEquals(health, monster.getMaxHealth());
		assertEquals(attack, monster.getAttack());
		assertEquals(defense, monster.getDefense());
		assertEquals(lootCount, monster.getLoot().size());
		assertEquals(inputLoot, monster.getLoot());
	}

	//Confirms that a null pointer exception is thrown when Name is null
	@Test
	void constructor_NullName_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Monster(null, 100, 10, 5, Collections.emptyList());
		});
	}

	//Confirms that a NullPointerException is throw when loot list is null
	@Test
	void constructor_NullLoot_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Monster("Skeleton", 100, 10, 5, null);
		});
	}

	
	//Tests that health is update properly when monster takes damage on happy path and edge case inputs
	@ParameterizedTest
	@CsvSource({
		"100, 30, 70",
		"100, 100, 0",
		"100, 150, 0",
		"100, 0, 100",
		"100, -25, 100",
		"0, 10, 0",
		"0, 0, 0",
		"0, -10, 0",
		"-50, 10, 0",
		"-50, -10, 0",
		"2147483647, 2147483647, 0",
		"2147483647, 0, 2147483647"
	})
	void takeDamage_VariousAmounts_UpdatesHealthCorrectly(int initialHealth, int damageAmount, int expectedHealth) {
		Monster monster = new Monster("Zombie", initialHealth, 10, 5, Collections.emptyList());

		monster.takeDamage(damageAmount);

		assertEquals(expectedHealth, monster.getHealth());
		assertEquals(initialHealth, monster.getMaxHealth());
	}

	//Tests if expectedAlive works on happy paths and edge cases.
	@ParameterizedTest
	@CsvSource({
		"100, 0, true",
		"100, 99, true",
		"100, 100, false",
		"100, 150, false",
		"1, 0, true",
		"1, 1, false",
		"0, 0, false",
		"0, 10, false",
		"-10, 0, false",
		"-10, -5, false"
	})
	void isAlive_VariousHealthStates_ReturnsExpectedBoolean(int initialHealth, int damageTaken, boolean expectedAlive) {
		Monster monster = new Monster("Ghost", initialHealth, 15, 10, Collections.emptyList());

		if (damageTaken != 0) {
			monster.takeDamage(damageTaken);
		}

		assertEquals(expectedAlive, monster.isAlive());
	}
}