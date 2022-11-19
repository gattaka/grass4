package cz.gattserver.grass.language.model.dto;

public class StatisticsTO {

	/**
	 * Počet
	 */
	private Integer count = 0;

	/**
	 * Úspěšnost při zkoušení
	 */
	private Double successRate = 0.0;

	public StatisticsTO(Integer count, Double successRate) {
		this.count = count;
		this.successRate = successRate;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Double getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(Double successRate) {
		this.successRate = successRate;
	}

}
