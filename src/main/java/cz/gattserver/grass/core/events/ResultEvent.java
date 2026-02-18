package cz.gattserver.grass.core.events;

public interface ResultEvent extends Event {

	boolean isSuccess();
	
	String getResultDetails();
}