package cz.gattserver.grass.core.ui.components;

import com.vaadin.flow.component.progressbar.ProgressBar;

public class BaseProgressBar extends ProgressBar {

	private int total = 0;
	private int current = 0;

	public int getTotal() {
		return total;
	}

	public BaseProgressBar setTotal(int total) {
		this.total = total;
		return this;
	}

	public int getCurrent() {
		return current;
	}

	public BaseProgressBar setCurrent(int current) {
		this.current = current;
		return this;
	}

	/**
	 * @return procentuální stav hotové práce
	 */
	public float getProgress() {
		return total == 0 ? 1 : (float) current / total;
	}

	/**
	 * inkrementuje stav
	 * 
	 * @return <code>true</code> pokud je to poslední increment, který byl
	 *         plánován (dle total count)
	 */
	public boolean increaseProgress() {
		current++;
		getUI().ifPresent(ui -> ui.access(() -> BaseProgressBar.this.setValue(getProgress())));
		return current == total - 1;
	}

}
