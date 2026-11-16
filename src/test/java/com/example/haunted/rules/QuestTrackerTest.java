package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.*;
import com.example.haunted.model.Item;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Collections;

class QuestTrackerTest {

	private static class TestItem extends Item {
		protected TestItem(String name, String description) {
			super(name, description);
		}
	}

	//Tests happy paths and edge cases for updateQuestForItem method
	@ParameterizedTest
	@CsvSource({
		"'Lost Gradebook', true, IN_PROGRESS",
		"'lost gradebook', true, IN_PROGRESS",
		"'LOST GRADEBOOK', true, IN_PROGRESS",
		"'Rusty Key', false, NOT_STARTED",
		"'', false, NOT_STARTED",
		"' ', false, NOT_STARTED",
		", false, NOT_STARTED"
	})
	void updateQuestForItem_VariousItems_UpdatesQuestCorrectly(String itemName, boolean expectedRecovered, QuestStatus expectedStatus) {
		QuestTracker tracker = new QuestTracker();
		Quest quest = new Quest("Main Quest", "Recover the gradebook and defeat the phantom");
		Item item = itemName == null ? null : new TestItem(itemName, "Description");

		tracker.updateQuestForItem(quest, item);

		assertEquals(expectedRecovered, quest.isGradebookRecovered());
		assertEquals(expectedStatus, quest.getStatus());
	}

	//Tests happy paths and edge cases for updateQuestForMonster method
	@ParameterizedTest
	@CsvSource({
		"'Final Exam Phantom', 50, 50, true, IN_PROGRESS",
		"'final exam phantom', 50, 100, true, IN_PROGRESS",
		"'FINAL EXAM PHANTOM', 0, 0, true, IN_PROGRESS",
		"'Final Exam Phantom', 50, 0, false, NOT_STARTED",
		"'Final Exam Phantom', 50, 49, false, NOT_STARTED",
		"'Hallway Ghost', 50, 50, false, NOT_STARTED",
		"'', 50, 50, false, NOT_STARTED",
		", 50, 50, false, NOT_STARTED"
	})
	void updateQuestForMonster_VariousMonstersAndStates_UpdatesQuestCorrectly(String monsterName, int initialHealth, int damageTaken, boolean expectedDefeated, QuestStatus expectedStatus) {
		QuestTracker tracker = new QuestTracker();
		Quest quest = new Quest("Main Quest", "Recover the gradebook and defeat the phantom");
		Monster monster = null;
		if (monsterName != null) {
			monster = new Monster(monsterName, initialHealth, 15, 5, Collections.emptyList());
			monster.takeDamage(damageTaken);
		}

		tracker.updateQuestForMonster(quest, monster);

		assertEquals(expectedDefeated, quest.isPhantomDefeated());
		assertEquals(expectedStatus, quest.getStatus());
	}

	//Tests happy paths and edge cases for updateQuestForMonster and updateQuestForItem method
	@ParameterizedTest
	@CsvSource({
		"'Lost Gradebook', 'Final Exam Phantom', 50, 50, COMPLETED, true",
		"'Lost Gradebook', 'Final Exam Phantom', 50, 0, IN_PROGRESS, false",
		"'Old Book', 'Final Exam Phantom', 50, 50, IN_PROGRESS, false",
		"'Old Book', 'Goblin', 50, 50, NOT_STARTED, false"
	})
	void updateQuestForItemAndMonster_CombinedProgress_UpdatesQuestStatusAndCompletion(String itemName, String monsterName, int initialHealth, int damageTaken, QuestStatus expectedStatus, boolean expectedComplete) {
		QuestTracker tracker = new QuestTracker();
		Quest quest = new Quest("Main Quest", "Recover the gradebook and defeat the phantom");
		Item item = new TestItem(itemName, "Description");
		Monster monster = new Monster(monsterName, initialHealth, 15, 5, Collections.emptyList());
		monster.takeDamage(damageTaken);

		tracker.updateQuestForItem(quest, item);
		tracker.updateQuestForMonster(quest, monster);

		assertEquals(expectedStatus, quest.getStatus());
		assertEquals(expectedComplete, quest.isComplete());
	}
}