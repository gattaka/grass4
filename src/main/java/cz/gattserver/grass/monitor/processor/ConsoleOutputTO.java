package cz.gattserver.grass.monitor.processor;

import cz.gattserver.grass.monitor.processor.item.MonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.MonitorState;

/**
 * TO výstupu z příkazu konzole, nehodnotí obsah výstupu, na to je
 * {@link MonitorItemTO} a {@link MonitorState}. {@link ConsoleOutputTO} pouze
 * udává zda se podařilo získat nějaký výstup a pokud ano, tak jaký.
 * 
 * @author Hynek
 *
 */
public class ConsoleOutputTO {

	/**
	 * Výstup příkazu
	 */
	private String output;

	/**
	 * Zdařilo se příkaz provést
	 */
	private boolean success;

	public ConsoleOutputTO(String output) {
		this(output, true);
	}

	public ConsoleOutputTO(String output, boolean success) {
		this.output = output;
		this.success = success;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
