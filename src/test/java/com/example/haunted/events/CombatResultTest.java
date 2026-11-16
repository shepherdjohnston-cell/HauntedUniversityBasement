package com.example.haunted.events;

import static org.junit.jupiter.api.Assertions.*;
import com.example.haunted.model.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;

class CombatResultTest {

	private static class TestItem extends Item {
		protected TestItem(String name, String description) {
			super(name, description);
		}
	}

	//Tests happy paths and edge cases for constructor and getters and isMonsterDefeated and isSuccess methods
	@ParameterizedTest
	@CsvSource({
		"true, 'Hit the monster!', 25, 10, false, 0",
		"true, 'Monster defeated!', 50, 0, true, 3",
		"false, 'Attack missed!', 0, 15, false, 0",
		"false, '', 0, 0, false, 1",
		"true, ' ', -10, -5, true, 2",
		"true, 'Max values', 2147483647, 2147483647, true, 4",
		"false, 'Min values', -2147483648, -2147483648, false, 0",
		"true, , 10, 5, false, 1"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(boolean success, String message, int damageToMonster, int damageToPlayer, boolean monsterDefeated, int droppedItemCount) {
		List<Item> inputItems = new ArrayList<>();
		for (int i = 0; i < droppedItemCount; i++) {
			inputItems.add(new TestItem("Item " + i, "Description " + i));
		}

		CombatResult result = new CombatResult(success, message, damageToMonster, damageToPlayer, monsterDefeated, inputItems);

		assertEquals(success, result.isSuccess());
		assertEquals(message, result.getMessage());
		assertEquals(damageToMonster, result.getDamageToMonster());
		assertEquals(damageToPlayer, result.getDamageToPlayer());
		assertEquals(monsterDefeated, result.isMonsterDefeated());
		assertEquals(droppedItemCount, result.getDroppedItems().size());
		assertEquals(inputItems, result.getDroppedItems());

		inputItems.add(new TestItem("Extra", "Extra Description"));
		assertEquals(droppedItemCount, result.getDroppedItems().size());
	}

	//Confirms droppedItem is unmodifiable 
	@Test
	void getDroppedItems_ModifyReturnedList_ThrowsUnsupportedOperationException() {
		List<Item> inputItems = new ArrayList<>();
		inputItems.add(new TestItem("Gold Coin", "Shiny coin"));
		CombatResult result = new CombatResult(true, "Victory", 20, 5, true, inputItems);

		List<Item> droppedItems = result.getDroppedItems();

		assertThrows(UnsupportedOperationException.class, () -> {
			droppedItems.add(new TestItem("Illegal", "Illegal Description"));
		});
		assertThrows(UnsupportedOperationException.class, () -> {
			droppedItems.remove(0);
		});
		assertThrows(UnsupportedOperationException.class, () -> {
			droppedItems.clear();
		});
	}
}