package cz.gattserver.grass.core.events;

import net.engio.mbassy.listener.Handler;

import java.util.concurrent.CompletableFuture;

public class MockEventsHandler {

	public volatile int state = 0;
	public volatile int steps = 0;
	public volatile int currentStep = 0;
	public volatile String currentStepDesc = null;

	public CompletableFuture<MockEventsHandler> future;

	public CompletableFuture<MockEventsHandler> expectEvent() {
		future = new CompletableFuture<>();
		return future;
	}

	@Handler
	public void onStart(MockProcessStartEvent event) {
		state = 1;
		steps = event.steps();
		future.complete(this);
	}

	@Handler
	public void onProgress(MockProcessProgressEvent event) {
		state = 2;
		currentStep = currentStep + 1;
		currentStepDesc = event.description();
		future.complete(this);
	}

	@Handler
	public void onResult(MockProcessResultEvent event) {
		state = 3;
		future.complete(this);
	}

}
