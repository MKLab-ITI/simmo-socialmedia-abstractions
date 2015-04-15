package gr.iti.mklab.framework.retrievers;

import gr.iti.mklab.framework.Credentials;
import gr.iti.mklab.framework.feeds.AccountFeed;
import gr.iti.mklab.framework.feeds.Feed;
import gr.iti.mklab.framework.feeds.GroupFeed;
import gr.iti.mklab.framework.feeds.KeywordsFeed;
import gr.iti.mklab.simmo.UserAccount;

/**
 * The interface for retrieving from social media - Currently the
 * social networks supprorted by the platform are the following:
 * YouTube, Google+,Twitter, Facebook, Flickr, Instagram, Topsy, 
 * Tumblr, Vimeo, DailyMotion, Twitpic
 * 
 * @author Manos Schinas
 * @email  manosetro@iti.gr
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
	public Response retrieveKeywordsFeed(KeywordsFeed feed) throws Exception {
		return retrieveKeywordsFeed(feed, 1);
	}
	
	public abstract Response retrieveKeywordsFeed(KeywordsFeed feed, Integer maxRequests) throws Exception;
	
	/**
	 * Retrieves a user feed that contains the user/users in 
	 * order to retrieve content posted by them
	 * @param feed
	 * @return
	 * @throws Exception
	 */
	public Response retrieveAccountFeed(AccountFeed feed) throws Exception {
		return retrieveAccountFeed(feed, 1);
	}
	
	public abstract Response retrieveAccountFeed(AccountFeed feed, Integer maxRequests) throws Exception;
	
	/**
	 * Retrieves a list feed that contains the owner of a list an a slug 
	 * used for the description of the list.
	 * @param feed
	 * @return
	 * @throws Exception
	 */
	public Response retrieveGroupFeed(GroupFeed feed) {
		return retrieveGroupFeed(feed, 1);
	}
	
	public abstract Response retrieveGroupFeed(GroupFeed feed, Integer maxRequests);
	
	
	/**
	 * Retrieves the info for a specific user on the basis
	 * of his id in the social network
	 * @param uid
	 * @return a StreamUser instance
	 */
	public abstract UserAccount getStreamUser(String uid);
	
	@Override
	public Response retrieve(Feed feed) throws Exception {
		return retrieve(feed, 1);
	}
	
	@Override
	public Response retrieve (Feed feed, Integer maxRequests) throws Exception {
	
		switch(feed.getFeedtype()) {
			case ACCOUNT:
				AccountFeed userFeed = (AccountFeed) feed;				
				return retrieveAccountFeed(userFeed, maxRequests);
			
			case KEYWORDS:
				KeywordsFeed keyFeed = (KeywordsFeed) feed;
				return retrieveKeywordsFeed(keyFeed, maxRequests);
			
			case GROUP:
				GroupFeed listFeed = (GroupFeed) feed;
				return retrieveGroupFeed(listFeed, maxRequests);
				
			default:
				break;
		}
	
		return new Response();
	}
	
}
