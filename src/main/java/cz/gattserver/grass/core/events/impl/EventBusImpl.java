package cz.gattserver.grass.core.events.impl;

import cz.gattserver.grass.core.events.Event;
import net.engio.mbassy.bus.IMessagePublication;
import net.engio.mbassy.bus.MBassador;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.events.ProgressEvent;

import javax.annotation.PostConstruct;

@Component
public class EventBusImpl implements EventBus {

	private MBassador<Event> eventBus;

	@PostConstruct
	public void init() {
		eventBus = new MBassador<>();
	}

	@Override
	public IMessagePublication publish(ProgressEvent event) {
		return eventBus.publishAsync(event);
	}

	@Override
	public void publish(Event event) {
		eventBus.publish(event);
	}

	@Override
	public void subscribe(Object listener) {
		eventBus.subscribe(listener);
	}

	@Override
	public void unsubscribe(Object listener) {
		eventBus.unsubscribe(listener);
	}
}
