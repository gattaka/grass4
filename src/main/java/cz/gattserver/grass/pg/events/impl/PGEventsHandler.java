package cz.gattserver.grass.pg.events.impl;

import net.engio.mbassy.listener.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PGEventsHandler {

	private static final Map<UUID, CompletableFuture<PGEventsHandler>> futureMap = new HashMap<>();
	private static final Map<UUID, PGProcessResultEvent> resultsMap = new HashMap<>();

	public CompletableFuture<PGEventsHandler> expectEvent(UUID uuid) {
		CompletableFuture<PGEventsHandler> future = new CompletableFuture<>();
		synchronized (futureMap) {
			futureMap.put(uuid, future);
		}
		return future;
	}

	public PGProcessResultEvent getResultAndDelete(UUID uuid) {
		synchronized (futureMap) {
			PGProcessResultEvent result = resultsMap.get(uuid);
			resultsMap.remove(uuid);
			return result;
		}
	}

	@Handler
	public void onResult(PGProcessResultEvent event) {
		synchronized (futureMap) {
			CompletableFuture<PGEventsHandler> future = futureMap.get(event.operationId());
			if (future != null) {
				resultsMap.put(event.operationId(), event);
				future.complete(this);
			}
		}
	}

}
