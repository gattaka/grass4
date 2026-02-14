package cz.gattserver.grass.core.ui.components;

import java.io.Serial;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.server.VaadinSession;

public abstract class PooledProgressBar extends ProgressBar {

	private static final int THREAD_POOL_SIZE = 10;
	private static ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	private ProgressThread progressThread;

	private int total;
	private int current;

	/**
	 * Je voláno vláknem v hlavní metodě - zde se provádí činnost, které
	 * odpovídá progressbar
	 * 
	 * @param thread
	 *            pracující vlákno, ve kterém je operace spuštěna - je možné na
	 *            něm volat metodu {@code increaseProgress() }, kterou se
	 *            zvyšuje čítač zpracovaných elementů a tím i stav progressbaru
	 */
	protected abstract void process(ProgressThread thread);

	/**
	 * Je-li potřeba ještě jinde indikovat stav operace, dá se přetížit tato
	 * metoda, která je volána při každém posuvu progressbaru
	 * 
	 * @param progress
	 *            procentuální stav operace
	 */
	protected void indicateProgress(float progress) {
	}

	/**
	 * 
	 * @param total
	 *            celkový počet elementů ke zpracování
	 */
	public PooledProgressBar(int total) {
		// +1 protože se musí započítat i samotné
		// generování procesu (jinak se bude dělit 0)
		this.total = total + 1;
		progressThread = new ProgressThread();
		executor.execute(progressThread);
	}

	public class ProgressThread implements Runnable, Serializable {

		@Serial
        private static final long serialVersionUID = -4546444983833528128L;

		@Override
		public void run() {
			current = 0;
			process(this);
		}

		/**
		 * inkrementuje stav
		 */
		public void increaseProgress() {
			current++;

			UI ui = UI.getCurrent();

			VaadinSession session = VaadinSession.getCurrent();
			if (session == null) {
				incrementState();
			} else {
				ui.access(this::incrementState);
			}
		}

		private void incrementState() {
			PooledProgressBar.this.setValue(getProgress());
			indicateProgress(getProgress());
		}

		/**
		 * @return procentuální stav hotové práce
		 */
		private float getProgress() {
			return (float) current / total;
		}
	}

	public ProgressThread getProgressThread() {
		return progressThread;
	}
}
