package cz.gattserver.grass.language.util;

import java.util.ArrayList;
import java.util.List;

import cz.gattserver.grass.core.mock.MockRandomSourceImpl;
import cz.gattserver.grass.core.util.DBCleanTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import cz.gattserver.grass.language.model.domain.LanguageItem;
import cz.gattserver.grass.language.model.dto.CrosswordTO;

public class CrosswordBuilderTest extends DBCleanTest {

	@Test
	public void testCrosswordBuilder() {
		List<LanguageItem> dictionary = new ArrayList<>();
		dictionary.add(new LanguageItem().setContent("Plž").setTranslation("Zvíře"));
		dictionary.add(new LanguageItem().setContent("Led").setTranslation("Písmeno"));
		dictionary.add(new LanguageItem().setContent("Žal").setTranslation("Pocit"));
		dictionary.add(new LanguageItem().setContent("Pes").setTranslation("Zvíře"));

		MockRandomSourceImpl.intValuesIndex = 0;
		MockRandomSourceImpl.intValues = new int[]{0, 3, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
				1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,};

		CrosswordBuilder cb = new CrosswordBuilder(6, dictionary);
		CrosswordTO to = cb.build();

		assertEquals(" |2| |3| | |\n" + "1|p|l|ž| | |\n" + " |e| |a| | |\n" + " |s|4|l|e|d|\n" + " | | | | | |\n"
				+ " | | | | | |\n", to.toString());
	}

}
