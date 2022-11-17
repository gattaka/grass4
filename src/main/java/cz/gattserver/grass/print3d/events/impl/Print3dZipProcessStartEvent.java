package cz.gattserver.grass.print3d.events.impl;


import cz.gattserver.grass.core.events.StartEvent;

public class Print3dZipProcessStartEvent implements StartEvent {

	private int steps;

	public Print3dZipProcessStartEvent(int steps) {
		this.steps = steps;
	}

	@Override
	public int getCountOfStepsToDo() {
		return steps;
	}

}
