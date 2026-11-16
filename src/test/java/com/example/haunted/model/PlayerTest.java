package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PlayerTest {

	//Tests happy paths and edge cases for constructor and getters
	@ParameterizedTest
	@CsvSource({
		"'Hero', 100, 15, 10, 5",
		"'Knight', 250, 30, 25, 10",
		"'', 0, 0, 0, 0",
		"' ', -10, -5, -2, 1",
		"'MaxPlayer', 2147483647, 2147483647, 2147483647, 20",
		"'MinPlayer', -2147483648, -2147483648, -2147483648, 0"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, int maxHealth, int baseAttack, int baseDefense, int capacity) {
		Inventory inventory = new Inventory(capacity);
		Player player = new Player(name, maxHealth, baseAttack, baseDefense, inventory);

		assertEquals(name, player.getName());
		assertEquals(maxHealth, player.getHealth());
		assertEquals(maxHealth, player.getMaxHealth());
		assertEquals(baseAttack, player.getBaseAttack());
		assertEquals(baseDefense, player.getBaseDefense());
		assertSame(inventory, player.getInventory());
		assertNull(player.getEquippedWeapon());
		assertNull(player.getEquippedArmor());
		assertNull(player.getCurrentRoom());
	}

	//Confirms that null exception is thrown when name is null
	@Test
	void constructor_NullName_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Player(null, 100, 10, 5, new Inventory(5));
		});
	}

	//Confirms that null exception is thrown when capacity is null
	@Test
	void constructor_NullInventory_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Player("Hero", 100, 10, 5, null);
		});
	}

	//Confirms that null exception is thrown when room is null
	@Test
	void setCurrentRoom_NullRoom_ThrowsNullPointerException() {
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		assertThrows(NullPointerException.class, () -> {
			player.setCurrentRoom(null);
		});
	}

	//Confirms that null exception is thrown when weapon is null
	@Test
	void equipWeapon_NullWeapon_ThrowsNullPointerException() {
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		assertThrows(NullPointerException.class, () -> {
			player.equipWeapon(null);
		});
	}

	//Confirms that null exception is thrown when armor is null
	@Test
	void equipArmor_NullArmor_ThrowsNullPointerException() {
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		assertThrows(NullPointerException.class, () -> {
			player.equipArmor(null);
		});
	}

	@ParameterizedTest
	@CsvSource({
		"'room1', 'Foyer', 'A dimly lit entrance'",
		"'room2', 'Cellar', 'Damp and dark'",
		"'', '', ''",
		"' ', ' ', ' '"
	})
	void setCurrentRoom_ValidRoom_UpdatesCurrentRoom(String roomId, String roomName, String roomDescription) {
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		Room room = new Room(roomId, roomName, roomDescription);

		player.setCurrentRoom(room);

		assertSame(room, player.getCurrentRoom());
		assertEquals(roomId, player.getCurrentRoom().getId());
		assertEquals(roomName, player.getCurrentRoom().getName());
		assertEquals(roomDescription, player.getCurrentRoom().getDescription());
	}

	//Tests happy paths and edge cases for takeDamage
	@ParameterizedTest
	@CsvSource({
		"100, 30, 70",
		"100, 100, 0",
		"100, 150, 0",
		"100, 0, 100",
		"100, -25, 100",
		"0, 10, 0",
		"-50, 10, 0",
		"2147483647, 2147483647, 0"
	})
	void takeDamage_VariousAmounts_UpdatesHealthCorrectly(int maxHealth, int damageAmount, int expectedHealth) {
		Player player = new Player("Hero", maxHealth, 10, 5, new Inventory(5));

		player.takeDamage(damageAmount);

		assertEquals(expectedHealth, player.getHealth());
		assertEquals(maxHealth, player.getMaxHealth());
	}

	//Tests happy paths and edge cases for heal
	@ParameterizedTest
	@CsvSource({
		"100, 50, 20, 70",
		"100, 50, 50, 100",
		"100, 50, 80, 100",
		"100, 50, 0, 50",
		"100, 50, -20, 50",
		"100, 0, 25, 100",
		"100, 100, 30, 30",
		"0, 0, 10, 0"
	})
	void heal_VariousAmounts_UpdatesHealthCorrectly(int maxHealth, int damageTaken, int healAmount, int expectedHealth) {
		Player player = new Player("Hero", maxHealth, 10, 5, new Inventory(5));
		player.takeDamage(damageTaken);

		player.heal(healAmount);

		assertEquals(expectedHealth, player.getHealth());
		assertEquals(maxHealth, player.getMaxHealth());
	}

	//Tests happy paths and edge cases for isAlive
	@ParameterizedTest
	@CsvSource({
		"100, 0, true",
		"100, 99, true",
		"100, 100, false",
		"100, 150, false",
		"1, 0, true",
		"1, 1, false",
		"0, 0, false",
		"-10, 0, false"
	})
	void isAlive_VariousHealthStates_ReturnsExpectedBoolean(int maxHealth, int damageTaken, boolean expectedAlive) {
		Player player = new Player("Hero", maxHealth, 10, 5, new Inventory(5));

		if (damageTaken != 0) {
			player.takeDamage(damageTaken);
		}

		assertEquals(expectedAlive, player.isAlive());
	}

	//Tests happy paths and edge cases for equipWeapon and getAttackPower
	@ParameterizedTest
	@CsvSource({
		"10, false, 0, 10",
		"10, true, 15, 25",
		"10, true, 0, 10",
		"10, true, -5, 5",
		"0, true, 20, 20",
		"-5, false, 0, -5"
	})
	void equipWeaponAndGetAttackPower_VariousWeapons_ReturnsExpectedAttackPower(int baseAttack, boolean equipWeapon, int weaponBonus, int expectedAttackPower) {
		Player player = new Player("Hero", 100, baseAttack, 5, new Inventory(5));

		if (equipWeapon) {
			Weapon weapon = new Weapon("Sword", "Sharp blade", weaponBonus);
			player.equipWeapon(weapon);
			assertSame(weapon, player.getEquippedWeapon());
		}

		assertEquals(expectedAttackPower, player.getAttackPower());
	}

	//Tests happy paths and edge cases for equipArmor and getDefensePower
	@ParameterizedTest
	@CsvSource({
		"5, false, 0, 5",
		"5, true, 12, 17",
		"5, true, 0, 5",
		"5, true, -3, 2",
		"0, true, 10, 10",
		"-5, false, 0, -5"
	})
	void equipArmorAndGetDefensePower_VariousArmors_ReturnsExpectedDefensePower(int baseDefense, boolean equipArmor, int armorBonus, int expectedDefensePower) {
		Player player = new Player("Hero", 100, 10, baseDefense, new Inventory(5));

		if (equipArmor) {
			Armor armor = new Armor("Shield", "Sturdy shield", armorBonus);
			player.equipArmor(armor);
			assertSame(armor, player.getEquippedArmor());
		}

		assertEquals(expectedDefensePower, player.getDefensePower());
	}
}