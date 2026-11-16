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

		assertThrows(UnsupportedOperationException.class, () -> {
			room.getExits().clear();
		});
		assertThrows(UnsupportedOperationException.class, () -> {
			room.getItems().add(new TestItem("Illegal", "Illegal Description"));
		});
		assertThrows(UnsupportedOperationException.class, () -> {
			room.getMonsters().add(new Monster("Ghost", 10, 5, 2, Collections.emptyList()));
		});
	}

	@Test
	void constructor_NullId_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Room(null, "Foyer", "Description");
		});
	}

	@Test
	void constructor_NullName_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Room("r1", null, "Description");
		});
	}

	@Test
	void constructor_NullDescription_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Room("r1", "Foyer", null);
		});
	}

	@Test
	void connect_NullDirection_ThrowsNullPointerException() {
		Room room1 = new Room("r1", "Foyer", "Description");
		Room room2 = new Room("r2", "Hall", "Description");

		assertThrows(NullPointerException.class, () -> {
			room1.connect(null, room2);
		});
	}

	@Test
	void connect_NullRoom_ThrowsNullPointerException() {
		Room room1 = new Room("r1", "Foyer", "Description");

		assertThrows(NullPointerException.class, () -> {
			room1.connect(Direction.values()[0], null);
		});
	}

	@Test
	void addItem_NullItem_ThrowsNullPointerException() {
		Room room = new Room("r1", "Foyer", "Description");

		assertThrows(NullPointerException.class, () -> {
			room.addItem(null);
		});
	}

	@Test
	void addMonster_NullMonster_ThrowsNullPointerException() {
		Room room = new Room("r1", "Foyer", "Description");

		assertThrows(NullPointerException.class, () -> {
			room.addMonster(null);
		});
	}

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

	@ParameterizedTest
	@CsvSource({
		"'Old Book', 'Old Book', true, 0",
		"'Old Book', 'old book', true, 0",
		"'Old Book', 'OLD BOOK', true, 0",
		"'Old Book', 'New Book', false