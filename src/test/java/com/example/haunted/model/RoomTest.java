package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;

class RoomTest {

	private static class TestItem extends Item {
		protected TestItem(String name, String description) {
			super(name, description);
		}
	}

	//Tests happy paths and edge cases for constructors and getters
	@ParameterizedTest
	@CsvSource({
		"'r1', 'Foyer', 'A grand entrance hall'",
		"'r2', 'Library', 'Dusty shelves lined with old books'",
		"'', '', ''",
		"' ', ' ', ' '"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String id, String name, String description) {
		Room room = new Room(id, name, description);

		assertEquals(id, room.getId());
		assertEquals(name, room.getName());
		assertEquals(description, room.getDescription());
		assertTrue(room.getExits().isEmpty());
		assertTrue(room.getItems().isEmpty());
		assertTrue(room.getMonsters().isEmpty());
		assertFalse(room.hasLivingMonsters());
		assertFalse(room.isLocked());
		assertNull(room.getRequiredKeyName());
		assertNull(room.getTrap());
	}

	//Confirms null exception when id is null
	@Test
	void constructor_NullId_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Room(null, "Foyer", "Description");
		});
	}

	//Confirms null exception when name is null
	@Test
	void constructor_NullName_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Room("r1", null, "Description");
		});
	}

	//Confirms null exception when description is null
	@Test
	void constructor_NullDescription_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Room("r1", "Foyer", null);
		});
	}

	//Confirms connect raises null exception on null direction
	@Test
	void connect_NullDirection_ThrowsNullPointerException() {
		Room room1 = new Room("r1", "Foyer", "Description");
		Room room2 = new Room("r2", "Hall", "Description");

		assertThrows(NullPointerException.class, () -> {
			room1.connect(null, room2);
		});
	}

	//Confirms connect raises null exception on null room
	@Test
	void connect_NullRoom_ThrowsNullPointerException() {
		Room room1 = new Room("r1", "Foyer", "Description");

		assertThrows(NullPointerException.class, () -> {
			room1.connect(Direction.values()[0], null);
		});
	}

	//Confirms addItem raises null when item is null
	@Test
	void addItem_NullItem_ThrowsNullPointerException() {
		Room room = new Room("r1", "Foyer", "Description");

		assertThrows(NullPointerException.class, () -> {
			room.addItem(null);
		});
	}

	//Confirms addMonster raises null when monster is null
	@Test
	void addMonster_NullMonster_ThrowsNullPointerException() {
		Room room = new Room("r1", "Foyer", "Description");

		assertThrows(NullPointerException.class, () -> {
			room.addMonster(null);
		});
	}

	//Tests happy paths and edge cases on connect and getExit methods
	@ParameterizedTest
	@CsvSource({
		"0, 'r2', 'Kitchen', 'Smells of old food'",
		"1, 'r3', 'Cellar', 'Cold and damp'"
	})
	void connectAndGetExit_ValidInputs_StoresAndReturnsConnectedRoom(int directionOrdinal, String targetId, String targetName, String targetDesc) {
		Direction[] directions = Direction.values();
		Direction direction = directions[directionOrdinal % directions.length];
		Room room = new Room("r1", "Foyer", "Description");
		Room targetRoom = new Room(targetId, targetName, targetDesc);

		room.connect(direction, targetRoom);

		assertSame(targetRoom, room.getExit(direction));
		assertEquals(1, room.getExits().size());
		assertSame(targetRoom, room.getExits().get(direction));
	}

	//Tests happy paths and edge cases on addItem and findItem methods
	@ParameterizedTest
	@CsvSource({
		"'Brass Key', 'Brass Key', true",
		"'Brass Key', 'brass key', true",
		"'Brass Key', 'BRASS KEY', true",
		"'Brass Key', 'Iron Key', false",
		"'Brass Key', '', false",
		"'', '', true",
		"' ', ' ', true"
	})
	void addItemAndFindItem_VariousSearchNames_ReturnsExpectedOptional(String storedName, String searchName, boolean expectedFound) {
		Room room = new Room("r1", "Foyer", "Description");
		Item item = new TestItem(storedName, "Description");

		room.addItem(item);
		var found = room.findItem(searchName);

		assertEquals(1, room.getItems().size());
		assertEquals(expectedFound, found.isPresent());
		if (expectedFound) {
			assertSame(item, found.get());
		}
	}

	//Tests happy paths and edge cases on removeItemByName method
	@ParameterizedTest
	@CsvSource({
		"'Old Book', 'Old Book', true, 0",
		"'Old Book', 'old book', true, 0",
		"'Old Book', 'OLD BOOK', true, 0",
		"'Old Book', 'New Book', false, 1",
		"'Old Book', '', false, 1",
		"'', '', true, 0"
	})
	void removeItemByName_VariousSearchNames_RemovesAndReturnsExpectedItem(String storedName, String removeName, boolean expectedRemoved, int expectedRemainingSize) {
		Room room = new Room("r1", "Foyer", "Description");
		Item item = new TestItem(storedName, "Description");
		room.addItem(item);

		Item removed = room.removeItemByName(removeName);

		if (expectedRemoved) {
			assertSame(item, removed);
		} else {
			assertNull(removed);
		}
		assertEquals(expectedRemainingSize, room.getItems().size());
	}

	//Tests happy paths and edge cases on addMonster and findMonster methods
	@ParameterizedTest
	@CsvSource({
		"'Skeleton', 'Skeleton', true",
		"'Skeleton', 'skeleton', true",
		"'Skeleton', 'SKELETON', true",
		"'Skeleton', 'Zombie', false",
		"'Skeleton', '', false",
		"'', '', true"
	})
	void addMonsterAndFindMonster_VariousSearchNames_ReturnsExpectedOptional(String storedName, String searchName, boolean expectedFound) {
		Room room = new Room("r1", "Foyer", "Description");
		Monster monster = new Monster(storedName, 50, 10, 5, Collections.emptyList());

		room.addMonster(monster);
		var found = room.findMonster(searchName);

		assertEquals(1, room.getMonsters().size());
		assertEquals(expectedFound, found.isPresent());
		if (expectedFound) {
			assertSame(monster, found.get());
		}
	}

	//Tests happy paths and edge cases on hasLivingMonsters method
	@ParameterizedTest
	@CsvSource({
		"50, 0, true",
		"50, 49, true",
		"50, 50, false",
		"50, 100, false",
		"0, 0, false",
		"-10, 0, false"
	})
	void hasLivingMonsters_VariousMonsterHealthStates_ReturnsExpectedBoolean(int initialHealth, int damageTaken, boolean expectedLiving) {
		Room room = new Room("r1", "Foyer", "Description");
		Monster monster = new Monster("Ghoul", initialHealth, 10, 5, Collections.emptyList());
		monster.takeDamage(damageTaken);

		room.addMonster(monster);

		assertEquals(expectedLiving, room.hasLivingMonsters());
	}

	//Tests happy paths and edge cases on setLocked method
	@ParameterizedTest
	@CsvSource({
		"true, 'Master Key'",
		"false, 'Master Key'",
		"true, ''",
		"false, ''"
	})
	void setLocked_VariousInputs_UpdatesLockedAndKeyName(boolean locked, String requiredKeyName) {
		Room room = new Room("r1", "Foyer", "Description");

		room.setLocked(locked, requiredKeyName);

		assertEquals(locked, room.isLocked());
		assertEquals(requiredKeyName, room.getRequiredKeyName());
	}

	//Tests happy paths and edge cases on unlock method 
	@ParameterizedTest
	@CsvSource({
		"false, 'Cellar Key', 'Wrong Key', true, false",
		"false, 'Cellar Key', 'Cellar Key', true, false",
		"true, 'Cellar Key', 'Cellar Key', true, false",
		"true, 'Cellar Key', 'cellar key', true, false",
		"true, 'Cellar Key', 'CELLAR KEY', true, false",
		"true, 'Cellar Key', 'Attic Key', false, true",
		"true, 'Cellar Key', '', false, true",
		"true, '', '', true, false"
	})
	void unlock_VariousLockStatesAndKeys_ReturnsExpectedResultAndUpdatesLockState(boolean initialLocked, String requiredKey, String providedKey, boolean expectedUnlockResult, boolean expectedFinalLockedState) {
		Room room = new Room("r1", "Foyer", "Description");
		room.setLocked(initialLocked, requiredKey);

		boolean result = room.unlock(providedKey);

		assertEquals(expectedUnlockResult, result);
		assertEquals(expectedFinalLockedState, room.isLocked());
	}

	//Confirms unlock is true and false when the key is correct and incorrect respectively
	@Test
	void unlock_LockedWithNullRequiredKey_ReturnsFalseAndRemainsLocked() {
		Room room = new Room("r1", "Foyer", "Description");
		room.setLocked(true, null);

		assertFalse(room.unlock("Any Key"));
		assertTrue(room.isLocked());
	}

	//Confirms set trap works
	@Test
	void setTrap_NullTrap_SetsTrapToNull() {
		Room room = new Room("r1", "Foyer", "Description");

		room.setTrap(null);

		assertNull(room.getTrap());
	}
}