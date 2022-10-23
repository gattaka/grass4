package cz.gattserver.grass.core.events;

public interface ResultEvent extends Event {

	public boolean isSuccess();
	
	public String getResultDetails();
}
