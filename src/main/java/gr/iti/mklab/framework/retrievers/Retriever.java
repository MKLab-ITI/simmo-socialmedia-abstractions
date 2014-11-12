package gr.iti.mklab.framework.retrievers;


import java.util.List;

import gr.iti.mklab.framework.common.domain.Feed;
import gr.iti.mklab.framework.common.domain.Item;

public interface Retriever {
	
	/**
	 * Retrieves a feed that is inserted into the system (Feeds currently supported
	 * by the platform are: KeywordFeeds,LocationFeeds,SourceFeeds,ListFeeds,URLFeeds)
	 * @param feed
	 * @return
	 */
	public List<Item> retrieve(Feed feed);
	
	/**
	 * Stops the retriever
	 * @param 
	 * @return
	 */
	public void stop();
}
