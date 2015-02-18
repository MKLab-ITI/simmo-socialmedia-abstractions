package gr.iti.mklab.framework.retrievers.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import gr.iti.mklab.framework.Credentials;
import gr.iti.mklab.framework.abstractions.socialmedia.media.TwitPicImage;
import gr.iti.mklab.framework.feeds.AccountFeed;
import gr.iti.mklab.framework.feeds.Feed;
import gr.iti.mklab.framework.feeds.GroupFeed;
import gr.iti.mklab.framework.feeds.KeywordsFeed;
import gr.iti.mklab.framework.retrievers.RateLimitsMonitor;
import gr.iti.mklab.framework.retrievers.SocialMediaRetriever;
import gr.iti.mklab.simmo.UserAccount;
import gr.iti.mklab.simmo.documents.Post;

/**
 * The retriever that implements the Twitpic simplified retriever
 * @author manosetro
 * @email  manosetro@iti.gr
 */
public class TwitpicRetriever extends SocialMediaRetriever {

	private static String requestPrefix = "http://api.twitpic.com/2/media/show.json?id=";
	
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private HttpRequestFactory requestFactory;

	public TwitpicRetriever(Credentials credentials, RateLimitsMonitor rateLimitsMonitor) {
		
		super(credentials, rateLimitsMonitor);
		
		requestFactory = HTTP_TRANSPORT.createRequestFactory(
				new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest request) {
						request.setParser(new JsonObjectParser(JSON_FACTORY));
					}
				});
	}
	
	/*
	public MediaItem getMediaItem(String shortId) {
		try {
			GenericUrl requestUrl = new GenericUrl(requestPrefix + shortId);
			HttpRequest request = requestFactory.buildGetRequest(requestUrl);
			HttpResponse response = request.execute();
			TwitPicImage image = response.parseAs(TwitPicImage.class);
			if(image != null) {
				MediaItem mediaItem = new TwitPicImage(image);
				return mediaItem;
			}
		} catch (Exception e) { }	
		return null;
	}
	 */
	
	@Override
	public List<Post> retrieveKeywordsFeed(KeywordsFeed feed) throws Exception {
		return new ArrayList<Post>();
	}

	@Override
	public List<Post> retrieveAccountFeed(AccountFeed feed) throws Exception {
		return new ArrayList<Post>();
	}

	@Override
	public UserAccount getStreamUser(String uid) {
		return null;
	}

	@Override
	public List<Post> retrieveGroupFeed(GroupFeed feed) {
		return new ArrayList<Post>();
	}

	@Override
	public List<Post> retrieveKeywordsFeed(KeywordsFeed feed,
			Integer maxRequests, Integer maxResults) throws Exception {
		return null;
	}

	@Override
	public List<Post> retrieveAccountFeed(AccountFeed feed, Integer maxRequests,
			Integer maxResults) throws Exception {
		return null;
	}

	@Override
	public List<Post> retrieveGroupFeed(GroupFeed feed, Integer maxRequests,
			Integer maxResults) {
		return null;
	}

}
