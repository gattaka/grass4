package cz.gattserver.grass.songs.facades.impl;

import cz.gattserver.grass.songs.model.interfaces.SongTO;

public class SongFileParser {

	private static final String BASE_ERR = "Název souboru písně musí mít formát 'autor - název (rok)'";
	public static final String AUTHOR_ERR = "Autor je prázdný. " + BASE_ERR;
	public static final String NAME_ERR = "Název je prázdný. " + BASE_ERR;
	public static final String YEAR_ERR = "Rok má nevyhovující formát. " + BASE_ERR;

	public static SongTO parseSongInfo(String fileName) {
		SongTO to = new SongTO();
		int extensionStart = fileName.lastIndexOf('.');
		String string;
		if (extensionStart > 0)
			string = fileName.substring(0, extensionStart);
		else
			string = fileName;
		int authorEnd = string.indexOf("-");
		if (authorEnd < 0)
			throw new IllegalStateException(AUTHOR_ERR);
		String author = string.substring(0, authorEnd).trim();
		if (author.length() == 0)
			throw new IllegalStateException(AUTHOR_ERR);
		to.setAuthor(author);
		int nameEnd = fileName.indexOf("(");
		String name;
		if (nameEnd < 0)
			name = string.substring(authorEnd + 1).trim();
		else
			name = string.substring(authorEnd + 1, nameEnd).trim();
		if (name.length() == 0)
			throw new IllegalStateException(NAME_ERR);
		to.setName(name);
		if (nameEnd > 0) {
			String year = string.substring(nameEnd).trim();
			if (!year.matches("\\([0-9]{4}\\)"))
				throw new IllegalStateException(YEAR_ERR);
			to.setYear(Integer.parseInt(year.substring(1, 5)));
		}
		return to;
	}

}
