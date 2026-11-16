package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.*;
import com.example.haunted.model.Armor;
import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Weapon;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;

class DamageCalculatorTest {

	//Tests happy paths and edge cases for calculatePlayerDamage method
	@ParameterizedTest
	@CsvSource({
		"20, 0, 5, 15",
		"20, 10, 5, 25",
		"10, 0, 10, 1",
		"10, 0, 25, 1",
		"5, 5, 15, 1",
		"0, 0, 0, 1",
		"15, 5, -5, 25",
		"-10, 0, 5, 1"
	})
	void calculatePlayerDamage_VariousAttackAndDefenseValues_ReturnsExpectedDamage(int baseAttack, int weaponBonus, int monsterDefense, int expectedDamage) {
		DamageCalculator calculator = new DamageCalculator();
		Player player = new Player("Hero", 100, baseAttack, 5, new Inventory(5));
		if (weaponBonus != 0) {
			player.equipWeapon(new Weapon("Sword", "Sharp blade", weaponBonus));
		}
		Monster monster = new Monster("Goblin", 50, 10, monsterDefense, Collections.emptyList());

		int damage = calculator.calculatePlayerDamage(player, monster);

		assertEquals(expectedDamage, damage);
	}

	//Tests happy paths and edge cases for calculateMonsterDamage method
	@ParameterizedTest
	@CsvSource({
		"25, 10, 0, 15",
		"25, 10, 5, 10",
		"15, 15, 0, 1",
		"15, 20, 10, 1",
		"0, 0, 0, 1",
		"20, -5, 0, 25",
		"-10, 5, 0, 1"
	})
	void calculateMonsterDamage_StandardMonster_ReturnsExpectedDamage(int monsterAttack, int playerBaseDefense, int armorBonus, int expectedDamage) {
		DamageCalculator calculator = new DamageCalculator();
		Monster monster = new Monster("Orc", 80, monsterAttack, 5, Collections.emptyList());
		Player player = new Player("Hero", 100, 10, playerBaseDefense, new Inventory(5));
		if (armorBonus != 0) {
			player.equipArmor(new Armor("Shield", "Sturdy shield", armorBonus));
		}

		int damage = calculator.calculateMonsterDamage(monster, player);

		assertEquals(expectedDamage, damage);
	}

	//Further tests happy paths and edge cases for calculateMonsterDamage method
	@ParameterizedTest
	@CsvSource({
		"100, 0, 30, 15, 10, 0, 20",
		"100, 49, 30, 15, 10, 5, 15",
		"100, 50, 30, 15, 10, 0, 35",
		"100, 75, 30, 15, 10, 5, 30",
		"100, 50, 20, 10, 30, 0, 1",
		"100, 50, 20, 10, 25, 10, 1",
		"100, 0, 10, 20, 15, 0, 1"
	})
	void calculateMonsterDamage_BossMonster_ReturnsExpectedDamage(int bossMaxHealth, int damageTaken, int bossBaseAttack, int enragedBonus, int playerBaseDefense, int armorBonus, int expectedDamage) {
		DamageCalculator calculator = new DamageCalculator();
		BossMonster boss = new BossMonster("Dragon", bossMaxHealth, bossBaseAttack, 10, Collections.emptyList(), enragedBonus);
		if (damageTaken != 0) {
			boss.takeDamage(damageTaken);
		}
		Player player = new Player("Hero", 100, 10, playerBaseDefense, new Inventory(5));
		if (armorBonus != 0) {
			player.equipArmor(new Armor("Plate", "Heavy armor", armorBonus));
		}

		int damage = calculator.calculateMonsterDamage(boss, player);

		assertEquals(expectedDamage, damage);
	}
}