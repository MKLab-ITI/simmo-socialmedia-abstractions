package gr.iti.mklab.framework.retrievers;

import java.util.List;
import gr.iti.mklab.framework.feeds.Feed;
import gr.iti.mklab.simmo.documents.Post;

public interface Retriever {
	
	/**
	 * Retrieves a feed that is inserted into the system (Feeds currently supported
	 * by the platform are: KeywordFeeds,LocationFeeds,SourceFeeds,ListFeeds,URLFeeds)
	 * @param feed
	 * @return
	 */
	public List<Post> retrieve(Feed feed) throws Exception;
	
	public List<Post> retrieve(Feed feed, Integer maxRequests, Integer maxResults) throws Exception;
	
}
