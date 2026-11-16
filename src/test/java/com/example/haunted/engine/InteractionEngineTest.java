package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.*;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Armor;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Item;
import com.example.haunted.model.Key;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestStatus;
import com.example.haunted.model.Room;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.QuestTracker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InteractionEngineTest {

	private static class TestItem extends Item {
		protected TestItem(String name, String description) {
			super(name, description);
		}
	}

	//Confirms constructor works
	@Test
	void constructor_ValidQuestTracker_CreatesInstance() {
		InteractionEngine engine = new InteractionEngine(new QuestTracker());

		assertNotNull(engine);
	}

	//Tests happy paths and edge cases for pickUpItem method
	@ParameterizedTest
	@CsvSource({
		"'Rusty Key', 'Rusty Key', 5, true, 'Picked up Rusty Key.', true, false, false, NOT_STARTED",
		"'Rusty Key', 'rusty key', 5, true, 'Picked up Rusty Key.', true, false, false, NOT_STARTED",
		"'Lost Gradebook', 'Lost Gradebook', 5, true, 'Picked up Lost Gradebook.', true, false, true, IN_PROGRESS",
		"'Rusty Key', 'Silver Key', 5, false, 'Item not found in the room.', false, true, false, NOT_STARTED",
		"'Rusty Key', 'Rusty Key', 0, false, 'Inventory is full.', false, true, false, NOT_STARTED"
	})
	void pickUpItem_VariousRoomAndInventoryStates_ReturnsExpectedResultAndUpdatesState(
			String roomItemName, String targetItemName, int inventoryCapacity,
			boolean expectedSuccess, String expectedMessage, boolean expectedInInventory,
			boolean expectedInRoom, boolean expectedGradebookRecovered, QuestStatus expectedQuestStatus) {

		InteractionEngine engine = new InteractionEngine(new QuestTracker());
		Player player = new Player("Hero", 100, 10, 5, new Inventory(inventoryCapacity));
		Room room = new Room("r1", "Foyer", "Starting room");
		room.addItem(new TestItem(roomItemName, "Description"));
		player.setCurrentRoom(room);
		Quest quest = new Quest("Main Quest", "Recover the gradebook");

		InteractionResult result = engine.pickUpItem(player, quest, targetItemName);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
		assertEquals(expectedInInventory, player.getInventory().contains(roomItemName));
		assertEquals(expectedInRoom, room.findItem(roomItemName).isPresent());
		assertEquals(expectedGradebookRecovered, quest.isGradebookRecovered());
		assertEquals(expectedQuestStatus, quest.getStatus());
	}

	//Tests happy paths and edge cases for useItem method
	@ParameterizedTest
	@CsvSource({
		"'POTION', 'Health Potion', 'Health Potion', 25, 50, true, 'Used Health Potion.', 75, false",
		"'POTION', 'Health Potion', 'health potion', 30, 50, true, 'Used Health Potion.', 80, false",
		"'WEAPON', 'Iron Sword', 'Iron Sword', 0, 50, false, 'That item cannot be used.', 50, true",
		"'OTHER', 'Old Book', 'Old Book', 0, 50, false, 'That item cannot be used.', 50, true",
		"'POTION', 'Health Potion', 'Mana Potion', 25, 50, false, 'Item not found in inventory.', 50, true"
	})
	void useItem_VariousInventoryItems_ReturnsExpectedResultAndUpdatesPlayer(
			String itemType, String storedItemName, String targetItemName, int healAmount,
			int initialDamage, boolean expectedSuccess, String expectedMessage,
			int expectedHealth, boolean expectedItemRemains) {

		InteractionEngine engine = new InteractionEngine(new QuestTracker());
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		player.takeDamage(initialDamage);

		Item item = switch (itemType) {
			case "POTION" -> new Potion(storedItemName, "Heals player", healAmount);
			case "WEAPON" -> new Weapon(storedItemName, "Sharp blade", 10);
			default -> new TestItem(storedItemName, "Generic item");
		};
		player.getInventory().addItem(item);

		InteractionResult result = engine.useItem(player, targetItemName);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
		assertEquals(expectedHealth, player.getHealth());
		assertEquals(expectedItemRemains, player.getInventory().contains(storedItemName));
	}

	//Tests happy paths and edge cases for equipItem method
	@ParameterizedTest
	@CsvSource({
		"'WEAPON', 'Iron Sword', 'Iron Sword', 15, true, 'Equipped weapon Iron Sword.', 25, 5",
		"'WEAPON', 'Iron Sword', 'iron sword', 15, true, 'Equipped weapon Iron Sword.', 25, 5",
		"'ARMOR', 'Steel Shield', 'Steel Shield', 10, true, 'Equipped armor Steel Shield.', 10, 15",
		"'ARMOR', 'Steel Shield', 'steel shield', 10, true, 'Equipped armor Steel Shield.', 10, 15",
		"'POTION', 'Health Potion', 'Health Potion', 20, false, 'That item cannot be equipped.', 10, 5",
		"'WEAPON', 'Iron Sword', 'Wooden Club', 15, false, 'Item not found in inventory.', 10, 5"
	})
	void equipItem_VariousInventoryItems_ReturnsExpectedResultAndUpdatesEquipment(
			String itemType, String storedItemName, String targetItemName, int bonus,
			boolean expectedSuccess, String expectedMessage, int expectedAttackPower, int expectedDefensePower) {

		InteractionEngine engine = new InteractionEngine(new QuestTracker());
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));

		Item item = switch (itemType) {
			case "WEAPON" -> new Weapon(storedItemName, "Sharp blade", bonus);
			case "ARMOR" -> new Armor(storedItemName, "Sturdy armor", bonus);
			default -> new Potion(storedItemName, "Heals player", bonus);
		};
		player.getInventory().addItem(item);

		InteractionResult result = engine.equipItem(player, targetItemName);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
		assertEquals(expectedAttackPower, player.getAttackPower());
		assertEquals(expectedDefensePower, player.getDefensePower());
	}

	//Tests happy paths and edge cases for unlockRoom method
	@ParameterizedTest
	@CsvSource({
		"false, false, , 'NONE', , false, 'There is no room in that direction.', false",
		"true, false, 'Cellar Key', 'NONE', , true, 'The room is already unlocked.', false",
		"true, true, , 'KEY', 'Cellar Key', false, 'The room cannot be unlocked.', true",
		"true, true, 'Cellar Key', 'NONE', , false, 'You do not have the correct key.', true",
		"true, true, 'Cellar Key', 'KEY', 'Attic Key', false, 'You do not have the correct key.', true",
		"true, true, 'Cellar Key', 'OTHER', 'Cellar Key', false, 'You do not have the correct key.', true",
		"true, true, 'Cellar Key', 'KEY', 'Cellar Key', true, 'Unlocked Cellar.', false",
		"true, true, 'Cellar Key', 'KEY', 'cellar key', true, 'Unlocked Cellar.', false"
	})
	void unlockRoom_VariousRoomAndKeyStates_ReturnsExpectedResultAndUpdatesLockState(
			boolean hasExit, boolean initialLocked, String requiredKeyName, String inventoryItemType,
			String inventoryItemName, boolean expectedSuccess, String expectedMessage, boolean expectedFinalLockedState) {

		InteractionEngine engine = new InteractionEngine(new QuestTracker());
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		Room startRoom = new Room("r1", "Foyer", "Starting room");
		Room targetRoom = new Room("r2", "Cellar", "Target room");
		player.setCurrentRoom(startRoom);

		Direction direction = Direction.values()[0];
		if (hasExit) {
			startRoom.connect(direction, targetRoom);
			targetRoom.setLocked(initialLocked, requiredKeyName);
		}

		if ("KEY".equals(inventoryItemType)) {
			player.getInventory().addItem(new Key(inventoryItemName, "Opens a door"));
		} else if ("OTHER".equals(inventoryItemType)) {
			player.getInventory().addItem(new TestItem(inventoryItemName, "Not a key"));
		}

		InteractionResult result = engine.unlockRoom(player, direction);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
		if (hasExit) {
			assertEquals(expectedFinalLockedState, targetRoom.isLocked());
		}
	}
}