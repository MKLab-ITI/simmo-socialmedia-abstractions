package gr.iti.mklab.framework.retrievers.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gr.iti.mklab.framework.abstractions.socialmedia.posts.TwitterPost;
import gr.iti.mklab.framework.abstractions.socialmedia.users.TwitterAccount;

import org.apache.log4j.Logger;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import gr.iti.mklab.framework.Credentials;
import gr.iti.mklab.framework.feeds.AccountFeed;
import gr.iti.mklab.framework.feeds.Feed;
import gr.iti.mklab.framework.feeds.GroupFeed;
import gr.iti.mklab.framework.feeds.KeywordsFeed;
import gr.iti.mklab.framework.retrievers.SocialMediaRetriever;
import gr.iti.mklab.simmo.UserAccount;
import gr.iti.mklab.simmo.documents.Post;

/**
 * Class responsible for retrieving Twitter content based on keywords, twitter users or locations
 * The retrieval process takes place through Twitter API (twitter4j)
 * @author Manos Schinas
 * @email  manosetro@iti.gr
 */
public class TwitterRetriever extends SocialMediaRetriever {
	
	private Logger  logger = Logger.getLogger(TwitterRetriever.class);
	private boolean loggingEnabled = false;
	
	private Twitter twitter = null;
	private TwitterFactory tf = null;
	
	public TwitterRetriever(Credentials credentials) throws Exception {
		
		super(credentials);
		
		if (credentials.getKey() == null || credentials.getSecret() == null ||
				credentials.getAccessToken() == null || credentials.getAccessTokenSecret() == null) {
			logger.error("Twitter requires authentication.");
			throw new Exception("Twitter requires authentication.");
		}
		
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setJSONStoreEnabled(false)
			.setOAuthConsumerKey(credentials.getKey())
			.setOAuthConsumerSecret(credentials.getSecret())
			.setOAuthAccessToken(credentials.getAccessToken())
			.setOAuthAccessTokenSecret(credentials.getAccessTokenSecret());
		Configuration conf = cb.build();
		
		this.tf = new TwitterFactory(conf);
		twitter = tf.getInstance();
	
	}
	
	@Override
	public List<Post> retrieveAccountFeed(AccountFeed feed, Integer maxRequests, Integer maxResults) {
		
		if(maxRequests == null)
			maxRequests = 1;
		
		if(maxResults == null)
			maxResults = 500;
		
		List<Post> posts = new ArrayList<Post>();
		
		int count = 200;
		
		Integer numberOfRequests = 0;
		
		Date sinceDate = feed.getSinceDate();
		Date newSinceDate = sinceDate;
		
		// TODO: Add feed label on 
		String feedLabel = feed.getLabel();
		
		String userId = feed.getId();
		String screenName = feed.getUsername();
		
		if(userId==null && screenName==null)
			return posts;
		
		int page = 1;
		Paging paging = new Paging(page, count);
		boolean sinceDateReached = false;
		while(true) {
			try {
				ResponseList<Status> response = null;
				if(userId != null) {
					response = twitter.getUserTimeline(Integer.parseInt(userId), paging);
				}
				else if(screenName != null) {
					if(loggingEnabled)
						logger.info("Retrieve timeline for " + screenName + ". Page: " + paging);
					response = twitter.getUserTimeline(screenName, paging);
				}
				else {
					break;
				}
				numberOfRequests++;
				
				for(Status status : response) {
					if(status != null) {
						
						if(sinceDate != null) {
							Date createdAt = status.getCreatedAt();
							if(newSinceDate.before(createdAt)) {
								newSinceDate = new Date(createdAt.getTime());
							}
							if(sinceDate.after(createdAt)) {
								sinceDateReached = true;
								break;
							}
						}
						
						TwitterPost post = new TwitterPost(status);
						
						posts.add(post);
					}
				}
				
				if(posts.size() > maxResults) {
					if(loggingEnabled)
						if(loggingEnabled)logger.info("totalRetrievedItems: " + posts.size() + " > " + maxResults);
					break;
				}
				
				if(numberOfRequests >= maxRequests) {
					if(loggingEnabled)	
						if(loggingEnabled)logger.info("numberOfRequests: " + numberOfRequests + " > " + maxRequests);
					break;
				}
				if(sinceDateReached) {
					if(loggingEnabled)
						if(loggingEnabled)logger.info("Since date reached: " + sinceDate);
					break;
				}
				
				paging.setPage(++page);
			} catch (TwitterException e) {
				logger.error(e);
				break;
			}
		}
		feed.setSinceDate(newSinceDate);
		return posts;
		
	}
	
	@Override
	public List<Post> retrieveKeywordsFeed(KeywordsFeed feed, Integer maxRequests, Integer maxResults) {
			
		
		List<Post> posts = new ArrayList<Post>();
		
		int count = 100;
		int numberOfRequests = 0;

		Date sinceDate = feed.getSinceDate();
		Date newSinceDate = sinceDate;
		
		// TODO: Add feed label on 
		String feedLabel = feed.getLabel();
		
		List<String> keywords = feed.getKeywords();
		if(keywords == null || keywords.isEmpty()) {
			logger.error("#Twitter : No keywords feed");
			return posts;
		}
		
		
		String tags = "";
		for(String key : keywords){
			String [] words = key.split(" ");
			for(String word : words)
				if(!tags.contains(word) && word.length()>1)
					tags += word.toLowerCase()+" ";
		}
		
		if(tags.equals("")) 
			return posts;
		
		//Set the query
		if(loggingEnabled)
			logger.info("Query String: " + tags);
		Query query = new Query(tags);
	
		query.count(count);
		query.setResultType(Query.RECENT); //do not set last item date-causes problems!

		boolean sinceDateReached = false;
		try {
			if(loggingEnabled)
				logger.info("Request for " + query);
			
			QueryResult response = twitter.search(query);
			while(response != null) {
				numberOfRequests++;
				
				List<Status> statuses = response.getTweets();
				
				if(statuses == null || statuses.isEmpty()) {
					if(loggingEnabled)
						logger.info("No more results.");	
					break;
				}
				
				if(loggingEnabled)
					logger.info(statuses.size() + " statuses retrieved.");	
				
				for(Status status : statuses) {
					if(status != null) {
						
						if(sinceDate != null) {
							Date createdAt = status.getCreatedAt();
							if(newSinceDate.before(createdAt)) {
								newSinceDate = new Date(createdAt.getTime());
							}
							if(sinceDate.after(createdAt)) {
								sinceDateReached = true;
								break;
							}
						}
						
						TwitterPost post = new TwitterPost(status);
						
						posts.add(post);
					}
				}
				
				if(posts.size() > maxResults) {
					if(loggingEnabled)
						logger.info("totalRetrievedItems: " + posts.size() + " > " + maxResults);
					break;
				}
				if(numberOfRequests >= maxRequests) {
					if(loggingEnabled)
						logger.info("numberOfRequests: " + numberOfRequests + " > " + maxRequests);
					break;
				}
				if(sinceDateReached) {
					if(loggingEnabled)
						logger.info("Since date reached: " + sinceDate);
					break;
				}
			
				query = response.nextQuery();
				if(query == null)
					break;
				
				if(loggingEnabled)
					logger.info("Request for " + query);
				response = twitter.search(query);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}	
	
		feed.setSinceDate(newSinceDate);
		return posts;
	}
	
	/*
	public List<Item> retrieveLocationFeed(LocationFeed feed, Integer maxRequests, Integer maxResults) {
		
		List<Item> items = new ArrayList<Item>();
		
		int count = 100;
		
		Integer numberOfRequests = 0;
		Date sinceDate = feed.getDateToRetrieve();
		
		Location location = feed.getLocation();
		if(location == null)
			return items;
		
		//Set the query
		Query query = new Query();
		Double radius = location.getRadius();
		if(radius == null) {
			radius = 1.5; // default radius 1.5 Km 
		}
		
		GeoLocation geoLocation = new GeoLocation(location.getLatitude(), location.getLongitude());
		query.setGeoCode(geoLocation, radius, Query.KILOMETERS);
		query.count(count);
				
		boolean sinceDateReached = false;
		while(true) {
			try {
				numberOfRequests++;
				QueryResult response = twitter.search(query);
				
				
				List<Status> statuses = response.getTweets();
				for(Status status : statuses) {
					if(status != null) {
						
						if(sinceDate != null) {
							Date createdAt = status.getCreatedAt();
							if(sinceDate.after(createdAt)) {
								sinceDateReached = true;
								break;
							}
						}
						
						TwitterPost twitterItem = new TwitterPost(status);
						
						items.add(twitterItem);
					}
				}
				
				if(!response.hasNext()) {
					if(loggingEnabled)
						logger.info("There is not next query.");
					break;
				}
				if(items.size() > maxResults) {
					if(loggingEnabled)
						logger.info("totalRetrievedItems: " + items.size() + " > " + maxResults);
					break;
				}
				if(numberOfRequests > maxRequests) {
					if(loggingEnabled)
						logger.info("numberOfRequests: " + numberOfRequests + " > " + maxRequests);
					break;
				}
				if(sinceDateReached) {
					if(loggingEnabled)
						logger.info("Since date reached: " + sinceDate);
					break;
				}
				
				query = response.nextQuery();
				if(query == null)
					break;
			} catch (TwitterException e) {
				logger.error(e);
				break;
			}
		}
		
		return items;
	}
	*/
	
	@Override
	public List<Post> retrieveGroupFeed(GroupFeed feed, Integer maxRequests, Integer maxResults) {
		
		List<Post> posts = new ArrayList<Post>();
		
		Integer numberOfRequests = 0;

		// TODO: Add feed label on 
		String feedLabel = feed.getLabel();
			
		String ownerScreenName = feed.getGroupCreator();
		String slug = feed.getGroupId();
				
		int page = 1;
		Paging paging = new Paging(page, 200);
		while(true) {
			try {
				numberOfRequests++;
				ResponseList<Status> response = twitter.getUserListStatuses(ownerScreenName, slug, paging);
				for(Status status : response) {
					if(status != null) {
						TwitterPost twitterItem = new TwitterPost(status);

						posts.add(twitterItem);
					}
				}
					
				paging.setPage(++page);
			} catch (TwitterException e) {
				logger.error(e);	
				break;
			}
		}
		return posts;
	}

	@Override
	public UserAccount getStreamUser(String uid) {
		try {
			long userId = Long.parseLong(uid);
			User user = twitter.showUser(userId);
			
			UserAccount userAccount = new TwitterAccount(user);
			return userAccount;
		}
		catch(Exception e) {
			logger.error(e);
			return null;
		}
	}


	public static void main(String...args) throws Exception {
		
		Credentials credentials = new Credentials ();
		credentials.setKey("");
		credentials.setSecret("");
		credentials.setAccessToken("");
		credentials.setAccessTokenSecret("");
		
		TwitterRetriever retriever = new TwitterRetriever(credentials);
	
		Date since = new Date(System.currentTimeMillis()-9600000);
		Feed feed = new AccountFeed("398789134", null, since);
		//Feed feed = new KeywordsFeed("1", "obama", since);
		
		
		List<Post> posts = retriever.retrieve(feed);
		System.out.println(posts.size() + " posts found!");
		
		for(Post post : posts) {
			System.out.println(post.getId());
			System.out.println(post.getContributor().getUsername());
			System.out.println(post.getContributor().getId());
			System.out.println(post.getTitle());
			System.out.println(post.getType());
			System.out.println(post.getCreationDate());
			System.out.println(post.getLanguage());
			System.out.println("==============================");
		}
		
	}
	
}
