package gr.iti.mklab.framework.retrievers.impl;

import java.util.List;

import gr.iti.mklab.framework.Credentials;
import gr.iti.mklab.framework.common.domain.Item;
import gr.iti.mklab.framework.common.domain.MediaItem;
import gr.iti.mklab.framework.common.domain.StreamUser;
import gr.iti.mklab.framework.common.domain.feeds.KeywordsFeed;
import gr.iti.mklab.framework.common.domain.feeds.ListFeed;
import gr.iti.mklab.framework.common.domain.feeds.LocationFeed;
import gr.iti.mklab.framework.common.domain.feeds.SourceFeed;
import gr.iti.mklab.framework.retrievers.Retriever;

/**
 * The interface for retrieving from social media - Currently the
 * social networks supprorted by the platform are the following:
 * YouTube,Google+,Twitter, Facebook,Flickr,Instagram,Topsy,Tumblr,
 * Vimeo,DailyMotion,Twitpic
 * @author ailiakop
 * @email  ailiakop@iti.gr
 */
public abstract class SocialMediaRetriever implements Retriever {
	
	public SocialMediaRetriever(Credentials credentials) {
		
	}
	
	/**
	 * Retrieves a keywords feed that contains certain keywords
	 * in order to retrieve relevant content
	 * @param feed
	 * @return
	 * @throws Exception
	 */
	public abstract List<Item> retrieveKeywordsFeeds(KeywordsFeed feed) throws Exception;
	
	/**
	 * Retrieves a user feed that contains the user/users in 
	 * order to retrieve content posted by them
	 * @param feed
	 * @return
	 * @throws Exception
	 */
	public abstract List<Item> retrieveUserFeeds(SourceFeed feed) throws Exception;
	
	/**
	 * Retrieves a location feed that contains the coordinates of the location
	 * that the retrieved content must come from.
	 * @param feed
	 * @return
	 * @throws Exception
	 */
	public abstract List<Item> retrieveLocationFeeds(LocationFeed feed) throws Exception;

	/**
	 * Retrieves a list feed that contains the owner of a list an a slug 
	 * used for the description of the list.
	 * @param feed
	 * @return
	 * @throws Exception
	 */
	public abstract List<Item> retrieveListsFeeds(ListFeed feed);
	
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
	
}
