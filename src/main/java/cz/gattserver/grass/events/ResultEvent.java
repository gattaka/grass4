package cz.gattserver.grass.events;

public interface ResultEvent extends Event {

	public boolean isSuccess();
	
	public String getResultDetails();
}
