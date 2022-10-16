package cz.gattserver.grass.events;

import net.engio.mbassy.bus.IMessagePublication;

public interface EventBus {

	/**
	 * Asynchronní publikace události průběhu operace
	 * 
	 * @param event
	 *            událost
	 * @return info objekt o asynchronním zveřejnění události
	 */
	public IMessagePublication publish(ProgressEvent event);

	/**
	 * Synchronní publikace události
	 * 
	 * @param event
	 *            událost
	 */
	public void publish(Event event);

	public void subscribe(Object listener);

	public void unsubscribe(Object listener);

}
