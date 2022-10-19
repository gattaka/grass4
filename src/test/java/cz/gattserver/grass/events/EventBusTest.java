package cz.gattserver.grass.events;

import static org.junit.jupiter.api.Assertions.*;

import cz.gattserver.grass.GrassApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootTest(classes = GrassApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EventBusTest  {

	@Autowired
	private EventBus eventBus;

	@Test
	public void testUnsubscribe() throws InterruptedException, ExecutionException {
		MockEventsHandler eventsHandler = new MockEventsHandler();
		assertEquals(0, eventsHandler.state);
		assertEquals(0, eventsHandler.currentStep);
		assertEquals(0, eventsHandler.steps);
		assertNull(eventsHandler.currentStepDesc);

		eventBus.subscribe(eventsHandler);

		CompletableFuture<MockEventsHandler> future = eventsHandler.expectEvent();
		eventBus.publish(new MockProcessStartEvent(3));
		future.get();

		assertEquals(1, eventsHandler.state);
		assertEquals(0, eventsHandler.currentStep);
		assertEquals(3, eventsHandler.steps);
		assertNull(eventsHandler.currentStepDesc);

		future = eventsHandler.expectEvent();
		eventBus.publish(new MockProcessProgressEvent("progress... 1"));
		future.get();
		assertEquals(2, eventsHandler.state);
		assertEquals(1, eventsHandler.currentStep);
		assertEquals(3, eventsHandler.steps);
		assertEquals("progress... 1", eventsHandler.currentStepDesc);

		eventBus.unsubscribe(eventsHandler);

		eventBus.publish(new MockProcessResultEvent(true, "ok"));

		assertEquals(2, eventsHandler.state);
		assertEquals(1, eventsHandler.currentStep);
		assertEquals(3, eventsHandler.steps);
		assertEquals("progress... 1", eventsHandler.currentStepDesc);

		eventBus.subscribe(eventsHandler);

		future = eventsHandler.expectEvent();
		eventBus.publish(new MockProcessResultEvent(true, "ok"));
		future.get();
		assertEquals(3, eventsHandler.state);
		assertEquals(1, eventsHandler.currentStep);
		assertEquals(3, eventsHandler.steps);
		assertEquals("progress... 1", eventsHandler.currentStepDesc);
	}

	@Test
	public void testSubscribe() throws InterruptedException, ExecutionException {
		MockEventsHandler eventsHandler = new MockEventsHandler();
		assertEquals(0, eventsHandler.state);
		assertEquals(0, eventsHandler.currentStep);
		assertEquals(0, eventsHandler.steps);
		assertNull(eventsHandler.currentStepDesc);

		eventBus.subscribe(eventsHandler);

		CompletableFuture<MockEventsHandler> future = eventsHandler.expectEvent();
		eventBus.publish(new MockProcessStartEvent(3));
		future.get();
		assertEquals(1, eventsHandler.state);
		assertEquals(0, eventsHandler.currentStep);
		assertEquals(3, eventsHandler.steps);
		assertNull(eventsHandler.currentStepDesc);

		future = eventsHandler.expectEvent();
		eventBus.publish(new MockProcessProgressEvent("progress... 1"));
		future.get();
		assertEquals(2, eventsHandler.state);
		assertEquals(1, eventsHandler.currentStep);
		assertEquals(3, eventsHandler.steps);
		assertEquals("progress... 1", eventsHandler.currentStepDesc);

		future = eventsHandler.expectEvent();
		eventBus.publish(new MockProcessProgressEvent("progress... 2"));
		future.get();
		assertEquals(2, eventsHandler.state);
		assertEquals(2, eventsHandler.currentStep);
		assertEquals(3, eventsHandler.steps);
		assertEquals("progress... 2", eventsHandler.currentStepDesc);

		future = eventsHandler.expectEvent();
		eventBus.publish(new MockProcessProgressEvent("progress... 3"));
		future.get();
		assertEquals(2, eventsHandler.state);
		assertEquals(3, eventsHandler.currentStep);
		assertEquals(3, eventsHandler.steps);
		assertEquals("progress... 3", eventsHandler.currentStepDesc);

		future = eventsHandler.expectEvent();
		eventBus.publish(new MockProcessResultEvent(true, "ok"));
		future.get();
		assertEquals(3, eventsHandler.state);
		assertEquals(3, eventsHandler.currentStep);
		assertEquals(3, eventsHandler.steps);
		assertEquals("progress... 3", eventsHandler.currentStepDesc);
	}

}
