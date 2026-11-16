package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.*;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TrapResolverTest {

	//Tests happy paths and edge cases for resolveTrap method
	@ParameterizedTest
	@CsvSource({
		"'Spike Pit', 25, true, true, 100, true, 'Trap ''Spike Pit'' triggered for 25 damage.', 75, false",
		"'Poison Dart', 15, true, false, 100, true, 'Trap ''Poison Dart'' triggered for 15 damage.', 85, true",
		"'Falling Chandelier', 120, true, true, 100, true, 'Trap ''Falling Chandelier'' triggered for 120 damage.', 0, false",
		"'Zero Trap', 0, true, false, 100, true, 'Trap ''Zero Trap'' triggered for 0 damage.', 100, true",
		"'Negative Trap', -10, true, true, 100, true, 'Trap ''Negative Trap'' triggered for -10 damage.', 100, false",
		"'Disarmed Trap', 30, false, true, 100, false, 'No trap was triggered.', 100, false",
		"'Disarmed Repeatable', 30, false, false, 100, false, 'No trap was triggered.', 100, false",
		", 20, true, true, 100, false, 'No trap was triggered.', 100, false"
	})
	void resolveTrap_VariousTrapStates_ReturnsExpectedResultAndUpdatesPlayerAndTrap(
			String trapName, int damage, boolean initialArmed, boolean oneTimeTrigger,
			int initialPlayerHealth, boolean expectedSuccess, String expectedMessage,
			int expectedPlayerHealth, boolean expectedFinalArmedState) {

		TrapResolver resolver = new TrapResolver();
		Player player = new Player("Hero", initialPlayerHealth, 10, 5, new Inventory(5));
		Trap trap = trapName == null ? null : new Trap(trapName, TrapType.values()[0], damage, initialArmed, oneTimeTrigger);

		InteractionResult result = resolver.resolveTrap(player, trap);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
		assertEquals(expectedPlayerHealth, player.getHealth());
		if (trap != null) {
			assertEquals(expectedFinalArmedState, trap.isArmed());
		}
	}
}