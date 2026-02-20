package cz.gattserver.grass.core.events;

public interface ResultEvent extends Event {

	boolean success();
	
	String resultDetails();
}