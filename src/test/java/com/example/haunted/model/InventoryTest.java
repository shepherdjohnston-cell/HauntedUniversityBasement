package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InventoryTest {

	private static class TestItem extends Item {
		protected TestItem(String name, String description) {
			super(name, description);
		}
	}

	//Tests constructor and getters on Happy paths and edge cases
	@ParameterizedTest
	@CsvSource({
		"10",
		"1",
		"0",
		"-5",
		"2147483647",
		"-2147483648"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(int capacity) {
		Inventory inventory = new Inventory(capacity);

		assertEquals(capacity, inventory.getCapacity());
		assertTrue(inventory.getItems().isEmpty());
	}

	
	//Tests addItem method on happy paths and edge cases
	@ParameterizedTest
	@CsvSource({
		"5, 0, true, 1",
		"5, 4, true, 5",
		"5, 5, false, 5",
		"1, 0, true, 1",
		"1, 1, false, 1",
		"0, 0, false, 0",
		"-1, 0, false, 0"
	})
	void addItem_VariousCapacitiesAndItemCounts_ReturnsExpectedBooleanAndUpdatesItems(int capacity, int existingItems, boolean expectedAdded, int expectedFinalSize) {
		Inventory inventory = new Inventory(capacity);
		for (int i = 0; i < existingItems; i++) {
			inventory.addItem(new TestItem("Item " + i, "Description " + i));
		}

		Item newItem = new TestItem("New Item", "New Description");
		boolean added = inventory.addItem(newItem);

		assertEquals(expectedAdded, added);
		assertEquals(expectedFinalSize, inventory.getItems().size());
		assertEquals(expectedAdded, inventory.getItems().contains(newItem));
	}

	
	//Tests findItem on happy paths and edge cases
	@ParameterizedTest
	@CsvSource({
		"'Rusty Key', 'Rusty Key', true",
		"'Rusty Key', 'rusty key', true",
		"'Rusty Key', 'RUSTY KEY', true",
		"'Rusty Key', 'Silver Key', false",
		"'Rusty Key', '', false",
		"'', '', true",
		"' ', ' ', true"
	})
	void findItem_VariousSearchNames_ReturnsExpectedOptional(String storedItemName, String searchName, boolean expectedPresent) {
		Inventory inventory = new Inventory(5);
		Item item = new TestItem(storedItemName, "Description");
		inventory.addItem(item);

		var found = inventory.findItem(searchName);

		assertEquals(expectedPresent, found.isPresent());
		if (expectedPresent) {
			assertEquals(item, found.get());
		}
	}

	//Tests removeItem on happy paths and edge cases
	@ParameterizedTest
	@CsvSource({
		"'Health Potion', 'Health Potion', true, 0",
		"'Health Potion', 'health potion', true, 0",
		"'Health Potion', 'HEALTH POTION', true, 0",
		"'Health Potion', 'Mana Potion', false, 1",
		"'Health Potion', '', false, 1",
		"'', '', true, 0"
	})
	void removeItem_VariousSearchNames_RemovesAndReturnsExpectedItem(String storedItemName, String removeName, boolean expectedRemoved, int expectedRemainingSize) {
		Inventory inventory = new Inventory(5);
		Item item = new TestItem(storedItemName, "Description");
		inventory.addItem(item);

		Item removed = inventory.removeItem(removeName);

		if (expectedRemoved) {
			assertEquals(item, removed);
		} else {
			assertNull(removed);
		}
		assertEquals(expectedRemainingSize, inventory.getItems().size());
	}

	
	//Tests contains and happy paths and edge cases
	@ParameterizedTest
	@CsvSource({
		"'Lantern', 'Lantern', true",
		"'Lantern', 'lantern', true",
		"'Lantern', 'LANTERN', true",
		"'Lantern', 'Torch', false",
		"'Lantern', '', false",
		"'', '', true",
		"' ', ' ', true"
	})
	void contains_VariousSearchNames_ReturnsExpectedBoolean(String storedItemName, String searchName, boolean expectedContains) {
		Inventory inventory = new Inventory(5);
		inventory.addItem(new TestItem(storedItemName, "Description"));

		assertEquals(expectedContains, inventory.contains(searchName));
	}

	
	//Tests isFull on happy paths and edge cases
	@ParameterizedTest
	@CsvSource({
		"5, 0, false",
		"5, 4, false",
		"5, 5, true",
		"1, 0, false",
		"1, 1, true",
		"0, 0, true",
		"-5, 0, true"
	})
	void isFull_VariousCapacitiesAndSizes_ReturnsExpectedBoolean(int capacity, int itemsToAdd, boolean expectedFull) {
		Inventory inventory = new Inventory(capacity);
		for (int i = 0; i < itemsToAdd; i++) {
			inventory.addItem(new TestItem("Item " + i, "Description " + i));
		}

		assertEquals(expectedFull, inventory.isFull());
	}
}