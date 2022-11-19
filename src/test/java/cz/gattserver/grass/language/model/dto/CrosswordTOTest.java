package cz.gattserver.grass.language.model.dto;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CrosswordTOTest {

	@Test
	public void testInit() {
		CrosswordTO to = new CrosswordTO(2, 3);
		assertEquals(3, to.getHeight());
		assertEquals(2, to.getWidth());
	}

	@Test
	public void testInsertWord() {
		CrosswordTO to = new CrosswordTO(5, 4);

		to.insertWord(0, 1, "až", "Město", true);
		to.insertWord(2, 0, "žal", "Pocit", false);

		assertFalse(to.getCell(0, 1).isWriteAllowed());
		assertEquals("1", to.getCell(0, 1).getValue());
		assertEquals("a", to.getCell(1, 1).getValue());
		assertEquals("Město", to.getHints().get(0).getHint());
		assertEquals(1, to.getHints().get(0).getFromX());
		assertEquals(2, to.getHints().get(0).getToX());
		assertEquals(1, to.getHints().get(0).getFromY());
		assertEquals(1, to.getHints().get(0).getToY());
		assertEquals("1", to.getHints().get(0).getValue());

		assertFalse(to.getCell(2, 0).isWriteAllowed());
		assertEquals("2", to.getCell(2, 0).getValue());
		assertEquals("ž", to.getCell(2, 1).getValue());
		assertEquals("Pocit", to.getHints().get(1).getHint());
		assertEquals(2, to.getHints().get(1).getFromX());
		assertEquals(2, to.getHints().get(1).getToX());
		assertEquals(1, to.getHints().get(1).getFromY());
		assertEquals(3, to.getHints().get(1).getToY());
		assertEquals("2", to.getHints().get(1).getValue());

		assertEquals(" | |2| | |\n" + "1|a|ž| | |\n" + " | |a| | |\n" + " | |l| | |\n", to.toString());
	}

}
