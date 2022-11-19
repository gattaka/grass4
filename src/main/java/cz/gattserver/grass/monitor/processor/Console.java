package cz.gattserver.grass.monitor.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Console {

	private static Logger logger = LoggerFactory.getLogger(Console.class);

	public static ConsoleOutputTO executeCommand(String command) {
		return executeCommand(Arrays.asList(command));
	}

	public static ConsoleOutputTO executeCommand(List<String> commandAndArguments) {
		Path dummyInput = null;
		try {
			dummyInput = Files.createTempFile(UUID.randomUUID().toString(), "GRASS-CONSOLE-DUMMY-INPUT");
		} catch (IOException e) {
			logger.error("Nezdařilo se vytvořit dummy input soubor pro příkaz console", e);
			return new ConsoleOutputTO(e.getMessage(), false);
		}
		try {
			ProcessBuilder pb = new ProcessBuilder(commandAndArguments);

			// staré API... toFile :( Ale tohle se stejně nedá testovat,
			// protože OS-specific
			pb.redirectInput(dummyInput.toFile());
			Process process = pb.start();
			int returned = process.waitFor();
			if (returned != 0)
				return new ConsoleOutputTO(
						"Vrácená hodnota prováděného příkazu je '" + returned + "', namísto očekávané '0'", false);
			try (BufferedReader buffer = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				return new ConsoleOutputTO(buffer.lines().collect(Collectors.joining("\n")));
			}
		} catch (Exception e) {
			logger.error("Nezdařilo se provést příkaz console", e.getMessage());
			return new ConsoleOutputTO(e.getMessage(), false);
		} finally {
			try {
				Files.delete(dummyInput);
			} catch (IOException e) {
				logger.error("Nezdařilo se smazat dočasný soubor logu console", e);
			}
		}
	}
}
