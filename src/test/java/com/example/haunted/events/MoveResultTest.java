package com.example.haunted.events;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MoveResultTest {
	//Tests happy path and edge cases for the constructor and the get methods
	@ParameterizedTest
	@CsvSource({
		"'True', 'Walked into Fire', 'True', '50'",
		"'False', '', 'False', '0'",
		"'False', ' ', 'True', '-50'",
	})
	void constructorAndGetters_ValidInputs_ReturnsExpectedValues(boolean success, String message, boolean trapTriggered, int trapDamage) {
		MoveResult moveResult= new MoveResult(success, message, trapTriggered, trapDamage);
		
		assertEquals(success, moveResult.isSuccess());
		assertEquals(message, moveResult.getMessage());
		assertEquals(trapTriggered, moveResult.isTrapTriggered());
		assertEquals(trapDamage, moveResult.getTrapDamage());
	}
}