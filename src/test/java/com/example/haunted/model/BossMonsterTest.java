package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BossMonsterTest {

	private static class TestItem extends Item {
		protected TestItem(String name, String description) {
			super(name, description);
		}
	}

	//Tests constructor and getter on both happy paths and edge cases
	@ParameterizedTest
	@CsvSource({
		"'Vampire Lord', 200, 25, 15, 2, 10",
		"'Headless Horseman', 500, 40, 20, 0, 25",
		"'', 0, 0, 0, 0, 0",
		"' ', -20, -10, -5, 1, -5",
		"'Titan', 2147483647, 2147483647, 2147483647, 4, 2147483647",
		"'Wraith', -2147483648, -2147483648, -2147483648, 3, -2147483648"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, int health, int attack, int defense, int lootCount, int enragedAttackBonus) {
		List<Item> inputLoot = new ArrayList<>();
		for (int i = 0; i < lootCount; i++) {
			inputLoot.add(new TestItem("Item " + i, "Description " + i));
		}

		BossMonster boss = new BossMonster(name, health, attack, defense, inputLoot, enragedAttackBonus);

		assertEquals(name, boss.getName());
		assertEquals(health, boss.getHealth());
		assertEquals(health, boss.getMaxHealth());
		assertEquals(attack, boss.getAttack());
		assertEquals(defense, boss.getDefense());
		assertEquals(lootCount, boss.getLoot().size());
		assertEquals(inputLoot, boss.getLoot());
	}
	
	//Tests happy paths and edge cases for getCurrentAttach method
	@ParameterizedTest
	@CsvSource({
		"100, 20, 15, 0, 20",
		"100, 20, 15, 49, 20",
		"100, 20, 15, 50, 35",
		"100, 20, 15, 75, 35",
		"100, 20, 15, 100, 35",
		"101, 20, 15, 50, 20",
		"101, 20, 15, 51, 35",
		"1, 10, 5, 0, 10",
		"1, 10, 5, 1, 15",
		"0, 10, 5, 0, 15",
		"100, 20, 0, 60, 20",
		"100, 20, -5, 60, 15"
	})
	void getCurrentAttack_VariousHealthAndBonusStates_ReturnsExpectedAttack(int initialHealth, int baseAttack, int enragedBonus, int damageTaken, int expectedAttack) {
		BossMonster boss = new BossMonster("Demon King", initialHealth, baseAttack, 10, Collections.emptyList(), enragedBonus);

		if (damageTaken != 0) {
			boss.takeDamage(damageTaken);
		}

		assertEquals(expectedAttack, boss.getCurrentAttack());
	}
}
