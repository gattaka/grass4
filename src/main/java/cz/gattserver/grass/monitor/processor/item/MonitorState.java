package cz.gattserver.grass.monitor.processor.item;

public enum MonitorState {

	/**
	 * Monitorování se zdařilo a výsledek je v pořádku
	 */
	SUCCESS,

	/**
	 * Monitorování se nezdařilo a není tedy znám výsledek
	 */
	UNAVAILABLE,

	/**
	 * Monitorování se zdařilo a výsledek není v pořádku
	 */
	ERROR

}
