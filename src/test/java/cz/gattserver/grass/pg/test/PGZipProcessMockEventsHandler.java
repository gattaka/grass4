package cz.gattserver.grass.pg.test;

import cz.gattserver.grass.pg.events.PGZipProcessResultEvent;
import net.engio.mbassy.listener.Handler;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;


public class PGZipProcessMockEventsHandler {

	private Path zipFile;
	private String resultDetails = null;
	private boolean success = false;
	private Throwable resultException;

	public CompletableFuture<PGZipProcessMockEventsHandler> future;

	public CompletableFuture<PGZipProcessMockEventsHandler> expectEvent() {
		future = new CompletableFuture<>();
		return future;
	}

	@Handler
	public void onResult(PGZipProcessResultEvent event) {
		synchronized (this) {
			zipFile = event.zipFile();
			resultDetails = event.resultDetails();
			success = event.success();
			resultException = event.resultException();
		}
		future.complete(this);
	}

	public synchronized Path getZipFile() {
		return zipFile;
	}

	public synchronized String getResultDetails() {
		return resultDetails;
	}

	public synchronized boolean isSuccess() {
		return success;
	}

	public synchronized Throwable getResultException() {
		return resultException;
	}

	public synchronized CompletableFuture<PGZipProcessMockEventsHandler> getFuture() {
		return future;
	}

}
