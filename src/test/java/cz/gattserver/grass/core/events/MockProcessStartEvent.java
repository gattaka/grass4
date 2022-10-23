package cz.gattserver.grass.core.events;

public class MockProcessStartEvent implements StartEvent {

    private int steps;

    public MockProcessStartEvent(int steps) {
        this.steps = steps;
    }

    @Override
    public int getCountOfStepsToDo() {
        return steps;
    }

}
