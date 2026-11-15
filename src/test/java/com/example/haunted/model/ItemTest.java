package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ItemTest {
	
	//Concrete helper subclass to instantiate an Item object since its an abstract class
	private static class TestItem extends Item {
		protected TestItem(String name, String description) {
			super(name, description);
		}
	}


	//Tests happy path and edge cases for get and toString methods
	@ParameterizedTest
	@CsvSource({
		"'Rusty Key', 'Opens the cellar'",
		"'Iron Armor','Grants 10 Toughness'",
		"'', ''",
		"' ', ' '"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, String description) {
		Item item = new TestItem(name, description);
		
		assertEquals(name, item.getName());
		assertEquals(description, item.getDescription());
		assertEquals(name, item.toString());
	}
	
	//Confirms that a NullPointerException will be thrown if name is null
	@Test
	void constructor_NullName_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new TestItem(null, "Valid description");
		});
	}
	
	//Confirms that a NullPointerException will be thrown if description is null
	@Test
	void constructor_NullDescription_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new TestItem("Valid name", null);
		});
	}
}