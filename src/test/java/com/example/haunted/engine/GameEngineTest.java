package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.*;
import com.example.haunted.events.CombatResult;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Item;
import com.example.haunted.model.Key;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.Room;
import com.example.haunted.model.Weapon;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;
import com.example.haunted.rules.TrapResolver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;

class GameEngineTest {

	private static class TestItem extends Item {
		protected TestItem(String name, String description) {
			super(name, description);
		}
	}

	private GameEngine createGameEngine(Player player, Quest quest) {
		QuestTracker questTracker = new QuestTracker();
		MovementEngine movementEngine = new MovementEngine(new TrapResolver());
		CombatEngine combatEngine = new CombatEngine(new DamageCalculator(), questTracker);
		InteractionEngine interactionEngine = new InteractionEngine(questTracker);
		return new GameEngine(player, quest, movementEngine, combatEngine, interactionEngine);
	}

	//Tests happy path and edge cases for constructor and getters
	@ParameterizedTest
	@CsvSource({
		"'Hero', 100, 'Main Quest', 'Recover the gradebook', 'r1', 'Foyer'",
		"'Knight', 50, 'Side Quest', 'Clear the cellar', 'r2', 'Cellar'"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String playerName, int maxHealth, String questName, String questDesc, String roomId, String roomName) {
		Player player = new Player(playerName, maxHealth, 10, 5, new Inventory(5));
		Room room = new Room(roomId, roomName, "Description");
		player.setCurrentRoom(room);
		Quest quest = new Quest(questName, questDesc);

		GameEngine gameEngine = createGameEngine(player, quest);

		assertSame(player, gameEngine.getPlayer());
		assertSame(quest, gameEngine.getQuest());
		assertSame(room, gameEngine.getCurrentRoom());
	}

	//Tests happy path and edge cases for move method
	@ParameterizedTest
	@CsvSource({
		"true, true, false",
		"false, false, true"
	})
	void move_VariousExitStates_ReturnsExpectedMoveResult(boolean hasExit, boolean expectedSuccess, boolean expectedStaysInStartRoom) {
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		Room startRoom = new Room("r1", "Foyer", "Starting room");
		Room targetRoom = new Room("r2", "Hallway", "Target room");
		player.setCurrentRoom(startRoom);
		Direction direction = Direction.values()[0];
		if (hasExit) {
			startRoom.connect(direction, targetRoom);
		}
		GameEngine gameEngine = createGameEngine(player, new Quest("Quest", "Desc"));

		MoveResult result = gameEngine.move(direction);

		assertEquals(expectedSuccess, result.isSuccess());
		assertSame(expectedStaysInStartRoom ? startRoom : targetRoom, gameEngine.getCurrentRoom());
	}

	//Tests happy path and edge cases for pickUpItem method
	@ParameterizedTest
	@CsvSource({
		"'Rusty Key', 'Rusty Key', true, 'Picked up Rusty Key.'",
		"'Rusty Key', 'Silver Key', false, 'Item not found in the room.'"
	})
	void pickUpItem_VariousItems_ReturnsExpectedInteractionResult(String roomItemName, String targetItemName, boolean expectedSuccess, String expectedMessage) {
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		Room room = new Room("r1", "Foyer", "Starting room");
		room.addItem(new TestItem(roomItemName, "Description"));
		player.setCurrentRoom(room);
		GameEngine gameEngine = createGameEngine(player, new Quest("Quest", "Desc"));

		InteractionResult result = gameEngine.pickUpItem(targetItemName);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
	}

	//Tests happy path and edge cases for equipItem method
	@ParameterizedTest
	@CsvSource({
		"'Iron Sword', 'Iron Sword', true, 'Equipped weapon Iron Sword.'",
		"'Iron Sword', 'Wooden Club', false, 'Item not found in inventory.'"
	})
	void equipItem_VariousItems_ReturnsExpectedInteractionResult(String storedWeaponName, String targetItemName, boolean expectedSuccess, String expectedMessage) {
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		player.getInventory().addItem(new Weapon(storedWeaponName, "Sharp blade", 15));
		GameEngine gameEngine = createGameEngine(player, new Quest("Quest", "Desc"));

		InteractionResult result = gameEngine.equipItem(targetItemName);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
	}

	//Tests happy path and edge cases for useItem method
	@ParameterizedTest
	@CsvSource({
		"'Health Potion', 'Health Potion', true, 'Used Health Potion.'",
		"'Health Potion', 'Mana Potion', false, 'Item not found in inventory.'"
	})
	void useItem_VariousItems_ReturnsExpectedInteractionResult(String storedPotionName, String targetItemName, boolean expectedSuccess, String expectedMessage) {
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		player.getInventory().addItem(new Potion(storedPotionName, "Heals health", 25));
		GameEngine gameEngine = createGameEngine(player, new Quest("Quest", "Desc"));

		InteractionResult result = gameEngine.useItem(targetItemName);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
	}

	//Tests happy path and edge cases for unlockRoom method
	@ParameterizedTest
	@CsvSource({
		"true, 'Cellar Key', 'Cellar Key', true, 'Unlocked Cellar.'",
		"true, 'Cellar Key', 'Wrong Key', false, 'You do not have the correct key.'",
		"false, 'Cellar Key', 'Cellar Key', false, 'There is no room in that direction.'"
	})
	void unlockRoom_VariousRoomAndKeyStates_ReturnsExpectedInteractionResult(boolean hasExit, String requiredKey, String heldKey, boolean expectedSuccess, String expectedMessage) {
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		Room startRoom = new Room("r1", "Foyer", "Starting room");
		Room targetRoom = new Room("r2", "Cellar", "Target room");
		player.setCurrentRoom(startRoom);
		Direction direction = Direction.values()[0];
		if (hasExit) {
			startRoom.connect(direction, targetRoom);
			targetRoom.setLocked(true, requiredKey);
		}
		player.getInventory().addItem(new Key(heldKey, "Key description"));
		GameEngine gameEngine = createGameEngine(player, new Quest("Quest", "Desc"));

		InteractionResult result = gameEngine.unlockRoom(direction);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
	}

	//Tests happy path and edge cases for attack method
	@ParameterizedTest
	@CsvSource({
		"'Goblin', 'Goblin', 20, true, true",
		"'Goblin', 'Goblin', 100, true, false",
		"'Goblin', 'Dragon', 20, false, false"
	})
	void attack_VariousMonsterTargets_ReturnsExpectedCombatResult(String roomMonsterName, String targetMonsterName, int monsterHealth, boolean expectedSuccess, boolean expectedMonsterDefeated) {
		Player player = new Player("Hero", 100, 30, 5, new Inventory(5));
		Room room = new Room("r1", "Foyer", "Starting room");
		room.addMonster(new Monster(roomMonsterName, monsterHealth, 10, 5, Collections.emptyList()));
		player.setCurrentRoom(room);
		GameEngine gameEngine = createGameEngine(player, new Quest("Quest", "Desc"));

		CombatResult result = gameEngine.attack(targetMonsterName);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMonsterDefeated, result.isMonsterDefeated());
	}

	//Tests happy path and edge cases for isGameOver and isGameWon methods
	@ParameterizedTest
	@CsvSource({
		"100, 0, true, true, false, true",
		"100, 0, true, false, false, false",
		"100, 0, false, false, false, false",
		"100, 100, true, true, true, false",
		"100, 100, false, false, true, false"
	})
	void isGameOverAndIsGameWon_VariousPlayerAndQuestStates_ReturnsExpectedBooleans(int maxHealth, int damageTaken, boolean gradebookRecovered, boolean phantomDefeated, boolean expectedGameOver, boolean expectedGameWon) {
		Player player = new Player("Hero", maxHealth, 10, 5, new Inventory(5));
		player.takeDamage(damageTaken);
		Quest quest = new Quest("Main Quest", "Complete objectives");
		if (gradebookRecovered) {
			quest.markGradebookRecovered();
		}
		if (phantomDefeated) {
			quest.markPhantomDefeated();
		}
		GameEngine gameEngine = createGameEngine(player, quest);

		assertEquals(expectedGameOver, gameEngine.isGameOver());
		assertEquals(expectedGameWon, gameEngine.isGameWon());
	}
}