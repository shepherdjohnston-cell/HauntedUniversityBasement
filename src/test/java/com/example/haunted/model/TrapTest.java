package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TrapTest {

	//Tests happy paths and edge cases for constructor and getters
	@ParameterizedTest
	@CsvSource({
		"'Spike Pit', 0, 25, true, true",
		"'Poison Dart', 1, 15, true, false",
		"'Falling Chandelier', 0, 50, false, true",
		"'', 0, 0, false, false",
		"' ', 0, -10, true, false",
		"'Max Trap', 0, 2147483647, true, true",
		"'Min Trap', 0, -2147483648, false, false"
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(String name, int typeOrdinal, int damage, boolean armed, boolean oneTimeTrigger) {
		TrapType[] types = TrapType.values();
		TrapType type = types[typeOrdinal % types.length];

		Trap trap = new Trap(name, type, damage, armed, oneTimeTrigger);

		assertEquals(name, trap.getName());
		assertEquals(type, trap.getType());
		assertEquals(damage, trap.getDamage());
		assertEquals(armed, trap.isArmed());
		assertEquals(oneTimeTrigger, trap.isOneTimeTrigger());
	}

	//Confirms null exception on null name
	@Test
	void constructor_NullName_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Trap(null, TrapType.values()[0], 20, true, true);
		});
	}

	//Confirms null exception on null typeOrdinal
	@Test
	void constructor_NullType_ThrowsNullPointerException() {
		assertThrows(NullPointerException.class, () -> {
			new Trap("Spike Pit", null, 20, true, true);
		});
	}

	//Tests happy paths and edge cases on disarm method
	@ParameterizedTest
	@CsvSource({
		"true, true",
		"true, false",
		"false, true",
		"false, false"
	})
	void disarm_VariousInitialArmedStates_SetsArmedToFalse(boolean initialArmed, boolean oneTimeTrigger) {
		Trap trap = new Trap("Tripwire", TrapType.values()[0], 10, initialArmed, oneTimeTrigger);

		trap.disarm();

		assertFalse(trap.isArmed());
	}
}