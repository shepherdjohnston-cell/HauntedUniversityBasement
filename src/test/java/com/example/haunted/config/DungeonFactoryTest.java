package com.example.haunted.config;

import static org.junit.jupiter.api.Assertions.*;
import com.example.haunted.engine.GameEngine;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Player;
import com.example.haunted.model.Quest;
import com.example.haunted.model.Room;
import org.junit.jupiter.api.Test;

class DungeonFactoryTest {

	//Tests creation of all player, room, and quest parameters
	@Test
	void createGame_ValidFactorySetup_FullyInitializesGameEngineAndWorld() {
		GameEngine gameEngine = DungeonFactory.createGame();

		assertNotNull(gameEngine);

		Player player = gameEngine.getPlayer();
		assertNotNull(player);
		assertEquals("Student Explorer", player.getName());
		assertEquals(50, player.getHealth());
		assertEquals(50, player.getMaxHealth());
		assertEquals(7, player.getBaseAttack());
		assertEquals(2, player.getBaseDefense());
		assertEquals(8, player.getInventory().getCapacity());

		Room stairwell = player.getCurrentRoom();
		assertNotNull(stairwell);
		assertEquals("stairwell", stairwell.getId());
		assertEquals("Maintenance Stairwell", stairwell.getName());

		Quest quest = gameEngine.getQuest();
		assertNotNull(quest);
		assertEquals("Escape the Basement", quest.getName());
		assertEquals("Recover the Lost Gradebook and defeat the Final Exam Phantom.", quest.getDescription());
		assertFalse(quest.isComplete());

		Room lectureHall = stairwell.getExit(Direction.EAST);
		assertNotNull(lectureHall);
		assertEquals("lectureHall", lectureHall.getId());
		assertSame(stairwell, lectureHall.getExit(Direction.WEST));
		assertFalse(lectureHall.getItems().isEmpty());
		assertFalse(lectureHall.getMonsters().isEmpty());

		Room labStorage = lectureHall.getExit(Direction.EAST);
		assertNotNull(labStorage);
		assertEquals("labStorage", labStorage.getId());
		assertEquals(2, labStorage.getItems().size());

		Room brokenElevator = lectureHall.getExit(Direction.SOUTH);
		assertNotNull(brokenElevator);
		assertEquals("brokenElevator", brokenElevator.getId());
		assertNotNull(brokenElevator.getTrap());
		assertEquals("Loose Wires Trap", brokenElevator.getTrap().getName());
		assertTrue(brokenElevator.getTrap().isArmed());

		Room examArchive = lectureHall.getExit(Direction.NORTH);
		assertNotNull(examArchive);
		assertEquals("examArchive", examArchive.getId());
		assertTrue(examArchive.isLocked());
		assertEquals("Archive Key", examArchive.getRequiredKeyName());
		assertNotNull(examArchive.findItem("Lost Gradebook").orElse(null));

		Room serverCloset = labStorage.getExit(Direction.NORTH);
		assertNotNull(serverCloset);
		assertEquals("serverCloset", serverCloset.getId());
		assertFalse(serverCloset.getItems().isEmpty());
		assertFalse(serverCloset.getMonsters().isEmpty());

		Room deanVault = serverCloset.getExit(Direction.EAST);
		assertNotNull(deanVault);
		assertEquals("deanVault", deanVault.getId());
		assertNotNull(deanVault.findItem("Vault Key").orElse(null));
		assertSame(serverCloset, deanVault.getExit(Direction.SOUTH));

		Room finalChamber = deanVault.getExit(Direction.NORTH);
		assertNotNull(finalChamber);
		assertEquals("finalChamber", finalChamber.getId());
		assertTrue(finalChamber.isLocked());
		assertEquals("Vault Key", finalChamber.getRequiredKeyName());
		assertTrue(finalChamber.hasLivingMonsters());
		assertEquals("Final Exam Phantom", finalChamber.getMonsters().get(0).getName());
	}
}