package cz.gattserver.grass.language.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.grass.core.services.RandomSourceService;
import cz.gattserver.grass.language.model.domain.LanguageItem;
import cz.gattserver.grass.language.model.dto.CrosswordCell;
import cz.gattserver.grass.language.model.dto.CrosswordTO;

public class CrosswordBuilder {

	private static final int HINT_CELL_OFFSET = 1;

	private RandomSourceService randomSource;

	private int sideSize;
	private List<LanguageItem> dictionary;

	private CrosswordTO crosswordTO;
	private Set<String> usedWords;

	public CrosswordBuilder(int sideSize, List<LanguageItem> dictionary) {
		this.dictionary = dictionary;
		this.sideSize = sideSize;
		this.randomSource = SpringContextHelper.getBean(RandomSourceService.class);
	}

	public CrosswordTO build() {
		crosswordTO = new CrosswordTO(sideSize, sideSize);
		usedWords = new HashSet<>();

		// 1. počáteční slovo
		LanguageItem item = dictionary.get(randomSource.getRandomInt(dictionary.size()));
		crosswordTO.insertWord(0, HINT_CELL_OFFSET, item.getContent(), item.getTranslation(), true);
		usedWords.add(item.getContent());

		// opakuj několikrát aby se postupně náhodně provázaly vertikální a
		// horizontální slova, vytvořená v jednotlivých dávkách
		for (int i = 0; i < 4; i++) {

			// prováděj postupně posuvy od počátku
			for (int offset = 0; offset < sideSize; offset += 2) {

				// vertikální slova
				for (int x = HINT_CELL_OFFSET; x < sideSize; x += 2)
					fillCrosswordItem(x, offset, false);

				// horizontální slova
				for (int y = HINT_CELL_OFFSET; y < sideSize; y += 2)
					fillCrosswordItem(offset, y, true);
			}
		}

		return crosswordTO;
	}

	private void fillCrosswordItem(int x, int y, boolean horizontally) {
		int maxLength = horizontally ? crosswordTO.getWidth() - x - 1 : crosswordTO.getHeight() - y - 1;
		List<LanguageItem> workList = new ArrayList<>(dictionary);

		for (int i = 0; i < workList.size(); i++) {
			LanguageItem item = randomRemove(workList);
			if (item.getContent().length() <= maxLength && !usedWords.contains(item.getContent())
					&& fits(item.getContent(), x, y, horizontally)) {
				crosswordTO.insertWord(x, y, item.getContent().toLowerCase(), item.getTranslation(), horizontally);
				usedWords.add(item.getContent());
				break;
			}
		}
	}

	private boolean checkFrameOfPlacing(String word, int x, int y, boolean horizontally) {
		// vejde se tam vůbec to slovo?
		if (horizontally && x + word.length() > crosswordTO.getWidth()
				|| !horizontally && y + word.length() > crosswordTO.getHeight())
			return false;
		// počáteční souřadnice musí být úplně prázdné, aby tam šel dát hint
		return crosswordTO.getCell(x, y) == null;
	}

	private boolean connectsHorizontally(int checkX, int checkY) {
		// Pokud slovo pokládám horizontálně, pak buď:
		// 1.) jsem na průsečíku slov a pak se musí zkontrolovat
		// jestli jsme na stejném písmenu -> 1.a/2.a
		// 2.) jsem na prázdné buňce a nad/pod touto buňkou musí být
		// také prázdná (jinak jsem přidal písmeno k existujícímu
		// slovu, které končí/prochází na touto buňkou)
		CrosswordCell aboveCell = crosswordTO.getCell(checkX, checkY - 1);
		CrosswordCell belowCell = crosswordTO.getCell(checkX, checkY + 1);
		return !(aboveCell != null && aboveCell.isWriteAllowed() || belowCell != null && belowCell.isWriteAllowed());
	}

	private boolean connectsVertically(int checkX, int checkY) {
		// Pokud slovo pokládám vertikálně, pak buď:
		// 1.) jsem na průsečíku slov a pak se musí zkontrolovat
		// jestli jsme na stejném písmenu -> 1.a/2.a
		// 2.) jsem na prázdné buňce a před/za touto buňkou musí být
		// také prázdná (jinak jsem přidal písmeno k existujícímu
		// slovu, které končí/prochází na touto buňkou)
		CrosswordCell prevCell = crosswordTO.getCell(checkX - 1, checkY);
		CrosswordCell nextCell = crosswordTO.getCell(checkX + 1, checkY);
		return !(prevCell != null && prevCell.isWriteAllowed() || nextCell != null && nextCell.isWriteAllowed());
	}

	private boolean fitsOnEmptyCell(boolean horizontally, int checkX, int checkY, CrosswordCell cell) {
		if (cell != null)
			return true;
		if (horizontally) {
			if (!connectsHorizontally(checkX, checkY))
				return false;
		} else {
			if (!connectsVertically(checkX, checkY))
				return false;
		}
		return true;
	}

	private int computeCheckX(int i, int x, boolean horizontally) {
		return horizontally ? x + HINT_CELL_OFFSET + i : x;
	}

	private int computeCheckY(int i, int y, boolean horizontally) {
		return horizontally ? y : y + HINT_CELL_OFFSET + i;
	}

	private boolean failsToAppend(String word, int i, CrosswordCell cell) {
		if (i < word.length())
			return false;
		// souřadnice za koncem musí být prázdné nebo tam být mezera,
		// aby se konec slova nepropojil s vedlejším
		// obsahem, se kterým sousedí
		return cell != null && !cell.getValue().equals(" ");
	}

	/**
	 * Kontroluje, zda je možné na dané souřadnice daným směrem zapsat dané
	 * slovo
	 * 
	 * @param word
	 *            zapisované slovo
	 * @param x
	 *            počáteční souřadnice x, od které se bude zapisovat (včetně
	 *            hint buňky)
	 * @param y
	 *            počáteční souřadnice y, od které se bude zapisovat (včetně
	 *            hint buňky)
	 * @param horizontally
	 *            přepínač, zda se bude zapisovat vodorovně (<code>true</code>)
	 *            nebo svisle
	 * @return <code>true</code>, pokud je zápis slova možný
	 */
	private boolean fits(String word, int x, int y, boolean horizontally) {

		// aspoň jedno písmeno se musí protínat s jiným slovem
		boolean emptyCrossSection = true;

		// zkontroluj rozměry a obecně věci nezávislé na obsahu slova
		if (!checkFrameOfPlacing(word, x, y, horizontally))
			return false;

		// prochází postupně buňky umístění slova a kontroluje, zda v nich (nebo
		// v okolí) nedojde ke konfliktu
		for (int i = 0; i < word.length() + 1; i++) {
			int checkX = computeCheckX(i, x, horizontally);
			int checkY = computeCheckY(i, y, horizontally);
			CrosswordCell cell = crosswordTO.getCell(checkX, checkY);

			if (!fitsOnEmptyCell(horizontally, checkX, checkY, cell))
				return false;

			// 1.a/2.a jsem na průsečíku slov a pak se musí zkontrolovat
			// jestli jsme na stejném písmenu
			if (i < word.length() && cell != null) {
				emptyCrossSection = false;
				if (!String.valueOf(word.charAt(i)).equalsIgnoreCase(cell.getValue()))
					return false;
			}

			if (failsToAppend(word, i, cell))
				return false;
		}

		return !emptyCrossSection;

	}

	private LanguageItem randomRemove(List<LanguageItem> list) {
		int index = randomSource.getRandomInt(list.size());
		return list.remove(index);
	}

}
