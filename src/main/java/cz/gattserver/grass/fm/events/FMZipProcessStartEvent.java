package cz.gattserver.grass.fm.events;


import cz.gattserver.grass.core.events.StartEvent;

public class FMZipProcessStartEvent implements StartEvent {

	private int steps;

	public FMZipProcessStartEvent(int steps) {
		this.steps = steps;
	}

	@Override
	public int getCountOfStepsToDo() {
		return steps;
	}

}
