package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.*;
import com.example.haunted.events.CombatResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Item;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Quest;
import com.example.haunted.model.Room;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;
import java.util.List;

class CombatEngineTest {

	private static class TestItem extends Item {
		protected TestItem(String name, String description) {
			super(name, description);
		}
	}

	//Confirms constructor works
	@Test
	void constructor_ValidDependencies_CreatesInstance() {
		CombatEngine engine = new CombatEngine(new DamageCalculator(), new QuestTracker());

		assertNotNull(engine);
	}

	//Tests happy paths and edge cases for most of attack method
	@ParameterizedTest
	@CsvSource({
		"50, 30, 35, 15, true, 'Defeated Goblin.', 20, 0, true, 1, 100",
		"100, 10, 35, 15, true, 'Attacked Goblin.', 20, 10, false, 0, 90"
	})
	void attack_ValidCombatScenarios_ReturnsExpectedCombatResultAndUpdatesState(
			int monsterHealth, int damageTakenBefore,
			int playerAttack, int monsterDefense, boolean expectedSuccess,
			String expectedMessage, int expectedDamageToMonster, int expectedDamageToPlayer,
			boolean expectedMonsterDefeated, int expectedDroppedItemCount, int expectedPlayerHealth) {

		CombatEngine engine = new CombatEngine(new DamageCalculator(), new QuestTracker());
		Player player = new Player("Hero", 100, playerAttack, 5, new Inventory(5));
		Room room = new Room("r1", "Foyer", "Starting room");
		player.setCurrentRoom(room);
		Quest quest = new Quest("Main Quest", "Defeat the phantom");

		List<Item> loot = expectedDroppedItemCount > 0 ? List.of(new TestItem("Gold", "Shiny")) : Collections.emptyList();
		Monster monster = new Monster("Goblin", monsterHealth, 15, monsterDefense, loot);
		monster.takeDamage(damageTakenBefore);
		room.addMonster(monster);

		CombatResult result = engine.attack(player, quest, monster);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
		assertEquals(expectedDamageToMonster, result.getDamageToMonster());
		assertEquals(expectedDamageToPlayer, result.getDamageToPlayer());
		assertEquals(expectedMonsterDefeated, result.isMonsterDefeated());
		assertEquals(expectedDroppedItemCount, result.getDroppedItems().size());
		assertEquals(expectedPlayerHealth, player.getHealth());
	}

	//Tests attack on monster not found event
	@Test
	void attack_NullMonster_ReturnsFailureResult() {
		CombatEngine engine = new CombatEngine(new DamageCalculator(), new QuestTracker());
		Player player = new Player("Hero", 100, 20, 5, new Inventory(5));
		Quest quest = new Quest("Main Quest", "Defeat monsters");

		CombatResult result = engine.attack(player, quest, null);

		assertFalse(result.isSuccess());
		assertEquals("Monster not found.", result.getMessage());
		assertEquals(0, result.getDamageToMonster());
		assertEquals(0, result.getDamageToPlayer());
		assertFalse(result.isMonsterDefeated());
		assertTrue(result.getDroppedItems().isEmpty());
	}

	//Tests attack on monster is already defeated event
	@Test
	void attack_MonsterAlreadyDefeated_ReturnsFailureResult() {
		CombatEngine engine = new CombatEngine(new DamageCalculator(), new QuestTracker());
		Player player = new Player("Hero", 100, 20, 5, new Inventory(5));
		Monster monster = new Monster("Goblin", 50, 10, 5, Collections.emptyList());
		monster.takeDamage(50); // Defeat monster
		Quest quest = new Quest("Main Quest", "Defeat monsters");

		CombatResult result = engine.attack(player, quest, monster);

		assertFalse(result.isSuccess());
		assertEquals("Monster is already defeated.", result.getMessage());
		assertEquals(0, result.getDamageToMonster());
		assertEquals(0, result.getDamageToPlayer());
		assertTrue(result.isMonsterDefeated());
		assertTrue(result.getDroppedItems().isEmpty());
	}

	//Tests attack on player is defeated event
	@Test
	void attack_PlayerDefeated_ReturnsFailureResult() {
		CombatEngine engine = new CombatEngine(new DamageCalculator(), new QuestTracker());
		Player player = new Player("Hero", 100, 20, 5, new Inventory(5));
		player.takeDamage(100);
		Monster monster = new Monster("Goblin", 50, 10, 5, Collections.emptyList());
		Quest quest = new Quest("Main Quest", "Defeat monsters");

		CombatResult result = engine.attack(player, quest, monster);

		assertFalse(result.isSuccess());
		assertEquals("Player is defeated.", result.getMessage());
		assertEquals(0, result.getDamageToMonster());
		assertEquals(0, result.getDamageToPlayer());
		assertFalse(result.isMonsterDefeated());
		assertTrue(result.getDroppedItems().isEmpty());
	}
}
