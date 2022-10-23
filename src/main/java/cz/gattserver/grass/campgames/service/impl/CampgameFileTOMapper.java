package cz.gattserver.grass.campgames.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.grass.campgames.interfaces.CampgameFileTO;

public class CampgameFileTOMapper {

	public static CampgameFileTO mapPathToItem(Path path) {
		CampgameFileTO to = new CampgameFileTO().setName(path.getFileName().toString());
		try {
			to.setSize(HumanBytesSizeFormatter.format(Files.size(path), true));
		} catch (IOException e) {
			to.setSize("n/a");
		}
		try {
			to.setLastModified(
					LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault()));
		} catch (IOException e) {
			to.setLastModified(null);
		}
		return to;
	}

}
