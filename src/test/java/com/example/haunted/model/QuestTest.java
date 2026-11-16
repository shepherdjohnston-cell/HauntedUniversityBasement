package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class QuestTest {

	//Tests happy paths and edge cases for constructors and getters
	@ParameterizedTest
	@CsvSource({
		"'Recover the Gradebook', 'Find the missing gradebook and defeat the phantom'",
		"'Haunted Hallways', 'Clear the school of ghosts'",
		"'', ''",
		"' ', ' '"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, String description) {
		Quest quest = new Quest(name, description);

		assertEquals(name, quest.getName());
		assertEquals(description, quest.getDescription());
		assertEquals(QuestStatus.NOT_STARTED, quest.getStatus());
		assertFalse(quest.isGradebookRecovered());
		assertFalse(quest.isPhantomDefeated());
		assertFalse(quest.isComplete());
	}

	//Confirms null exception on null name
	@Test
	void constructor_NullName_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Quest(null, "Valid description");
		});
	}

	//Confirms null exception on null description
	@Test
	void constructor_NullDescription_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Quest("Valid name", null);
		});
	}

	//Tests happy paths and edge cases for Quest methods
	@ParameterizedTest
	@CsvSource({
		"false, false, NOT_STARTED, false",
		"true, false, IN_PROGRESS, false",
		"false, true, IN_PROGRESS, false",
		"true, true, COMPLETED, true"
	})
	void updateStatusAndIsComplete_VariousProgressStates_UpdatesStatusAndReturnsExpectedCompletion(boolean recoverGradebook, boolean defeatPhantom, QuestStatus expectedStatus, boolean expectedComplete) {
		Quest quest = new Quest("Main Quest", "Complete all objectives");

		if (recoverGradebook) {
			quest.markGradebookRecovered();
		}
		if (defeatPhantom) {
			quest.markPhantomDefeated();
		}
		quest.updateStatus();

		assertEquals(recoverGradebook, quest.isGradebookRecovered());
		assertEquals(defeatPhantom, quest.isPhantomDefeated());
		assertEquals(expectedStatus, quest.getStatus());
		assertEquals(expectedComplete, quest.isComplete());
	}
}