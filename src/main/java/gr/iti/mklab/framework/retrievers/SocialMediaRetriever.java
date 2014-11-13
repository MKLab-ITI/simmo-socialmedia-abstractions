package gr.iti.mklab.framework.retrievers;

import java.util.ArrayList;
import java.util.List;

import gr.iti.mklab.framework.Credentials;
import gr.iti.mklab.framework.common.domain.Feed;
import gr.iti.mklab.framework.common.domain.Item;
import gr.iti.mklab.framework.common.domain.MediaItem;
import gr.iti.mklab.framework.common.domain.StreamUser;
import gr.iti.mklab.framework.common.domain.feeds.KeywordsFeed;
import gr.iti.mklab.framework.common.domain.feeds.ListFeed;
import gr.iti.mklab.framework.common.domain.feeds.LocationFeed;
import gr.iti.mklab.framework.common.domain.feeds.SourceFeed;

/**
 * The interface for retrieving from social media - Currently the
 * social networks supprorted by the platform are the following:
 * YouTube,Google+,Twitter, Facebook,Flickr,Instagram,Topsy,Tumblr,
 * Vimeo,DailyMotion,Twitpic
 * @author ailiakop
 * @email  ailiakop@iti.gr
 */
public abstract class SocialMediaRetriever implements Retriever {
	
	protected RateLimitsMonitor rateLimitsMonitor;

	public SocialMediaRetriever(Credentials credentials, Integer maxRequestPerWindow, Long windowLenth) {
		rateLimitsMonitor = new RateLimitsMonitor(maxRequestPerWindow, windowLenth);
	}
	
	/**
	 * Retrieves a keywords feed that contains certain keywords
	 * in order to retrieve relevant content
	 * @param feed
	 * @return
	 * @throws Exception
	 */
	public List<Item> retrieveKeywordsFeeds(KeywordsFeed feed) throws Exception {
		return retrieveKeywordsFeeds(feed, null, null);
	}
	
	public abstract List<Item> retrieveKeywordsFeeds(KeywordsFeed feed, Integer maxRequests, Integer maxResults) throws Exception;
	
	/**
	 * Retrieves a user feed that contains the user/users in 
	 * order to retrieve content posted by them
	 * @param feed
	 * @return
	 * @throws Exception
	 */
	public List<Item> retrieveUserFeeds(SourceFeed feed) throws Exception {
		return retrieveUserFeeds(feed, null, null);
	}
	
	public abstract List<Item> retrieveUserFeeds(SourceFeed feed, Integer maxRequests, Integer maxResults) throws Exception;
	
	/**
	 * Retrieves a location feed that contains the coordinates of the location
	 * that the retrieved content must come from.
	 * @param feed
	 * @return
	 * @throws Exception
	 */
	public List<Item> retrieveLocationFeeds(LocationFeed feed) throws Exception {
		return retrieveLocationFeeds(feed, null, null);
	}
	
	
	public abstract List<Item> retrieveLocationFeeds(LocationFeed feed, Integer maxRequests, Integer maxResults) throws Exception;

	/**
	 * Retrieves a list feed that contains the owner of a list an a slug 
	 * used for the description of the list.
	 * @param feed
	 * @return
	 * @throws Exception
	 */
	public List<Item> retrieveListsFeeds(ListFeed feed) {
		return retrieveListsFeeds(feed, null, null);
	}
	
	
	public abstract List<Item> retrieveListsFeeds(ListFeed feed, Integer maxRequests, Integer maxResults);
	
	
	/**
	 * Retrieves the info for a specific user on the basis
	 * of his id in the social network
	 * @param uid
	 * @return a StreamUser instance
	 */
	public abstract StreamUser getStreamUser(String uid);
	
	/**
	 * Retrieves the info for a specific media object on the basis
	 * of its id in the social network
	 * @param id
	 * @return a MediaItem instance
	 */
	public abstract MediaItem getMediaItem(String id);
	
	@Override
	public List<Item> retrieve(Feed feed) throws Exception {
		return retrieve(feed, null, null);
	}
	
	@Override
	public List<Item> retrieve (Feed feed, Integer maxRequests, Integer maxResults) throws Exception {
	
		switch(feed.getFeedtype()) {
			case SOURCE:
				SourceFeed userFeed = (SourceFeed) feed;				
				return retrieveUserFeeds(userFeed);
			
			case KEYWORDS:
				KeywordsFeed keyFeed = (KeywordsFeed) feed;
				return retrieveKeywordsFeeds(keyFeed);
				
			case LOCATION:
				LocationFeed locationFeed = (LocationFeed) feed;
				return retrieveLocationFeeds(locationFeed);
			
			case LIST:
				ListFeed listFeed = (ListFeed) feed;
				
				return retrieveListsFeeds(listFeed);
				
			default:
				break;
		}
	
		return new ArrayList<Item>();
	}
	
}
