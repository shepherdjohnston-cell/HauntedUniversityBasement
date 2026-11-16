package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.*;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;
import com.example.haunted.rules.TrapResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MovementEngineTest {

	//Confirms TrapResolver constructor works
	@Test
	void constructor_ValidTrapResolver_CreatesInstance() {
		MovementEngine engine = new MovementEngine(new TrapResolver());

		assertNotNull(engine);
	}

	//Tests happy paths and edge cases on move method
	@ParameterizedTest
	@CsvSource({
		"false, false, , 0, false, false, false, 'There is no room in that direction.', false, 0, 100, false",
		"true, true, , 0, false, false, false, 'The room is locked.', false, 0, 100, false",
		"true, false, , 0, false, false, true, 'Moved to Hallway.', false, 0, 100, true",
		"true, false, 'Spike Pit', 25, false, true, true, 'Moved to Hallway.', false, 0, 100, true",
		"true, false, 'Spike Pit', 25, true, true, true, 'Moved to Hallway. Trap ''Spike Pit'' triggered for 25 damage.', true, 25, 75, true",
		"true, false, 'Poison Dart', 15, true, false, true, 'Moved to Hallway. Trap ''Poison Dart'' triggered for 15 damage.', true, 15, 85, true"
	})
	void move_VariousRoomAndTrapStates_ReturnsExpectedMoveResultAndUpdatesPlayer(
			boolean hasExit, boolean targetLocked, String trapName, int trapDamage,
			boolean trapArmed, boolean oneTimeTrigger, boolean expectedSuccess,
			String expectedMessage, boolean expectedTrapTriggered, int expectedTrapDamage,
			int expectedPlayerHealth, boolean expectedRoomChanged) {

		MovementEngine engine = new MovementEngine(new TrapResolver());
		Player player = new Player("Hero", 100, 10, 5, new Inventory(5));
		Room startRoom = new Room("r1", "Foyer", "Starting room");
		Room targetRoom = new Room("r2", "Hallway", "Target room");
		player.setCurrentRoom(startRoom);

		Direction direction = Direction.values()[0];
		if (hasExit) {
			startRoom.connect(direction, targetRoom);
			targetRoom.setLocked(targetLocked, "Key");
			if (trapName != null) {
				targetRoom.setTrap(new Trap(trapName, TrapType.values()[0], trapDamage, trapArmed, oneTimeTrigger));
			}
		}

		MoveResult result = engine.move(player, direction);

		assertEquals(expectedSuccess, result.isSuccess());
		assertEquals(expectedMessage, result.getMessage());
		assertEquals(expectedTrapTriggered, result.isTrapTriggered());
		assertEquals(expectedTrapDamage, result.getTrapDamage());
		assertEquals(expectedPlayerHealth, player.getHealth());
		assertSame(expectedRoomChanged ? targetRoom : startRoom, player.getCurrentRoom());
	}
}