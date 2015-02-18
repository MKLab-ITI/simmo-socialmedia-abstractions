package gr.iti.mklab.framework.retrievers.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import gr.iti.mklab.framework.Credentials;
import gr.iti.mklab.framework.feeds.AccountFeed;
import gr.iti.mklab.framework.feeds.Feed;
import gr.iti.mklab.framework.feeds.GroupFeed;
import gr.iti.mklab.framework.feeds.KeywordsFeed;
import gr.iti.mklab.framework.retrievers.SocialMediaRetriever;
import gr.iti.mklab.simmo.UserAccount;
import gr.iti.mklab.simmo.documents.Post;

/**
 * The retriever that implements the Vimeo simplified retriever 
 * @author manosetro
 * @email  manosetro@iti.gr
 */
public class VimeoRetriever extends SocialMediaRetriever {

	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private HttpRequestFactory requestFactory;
	private String requestPrefix = "http://vimeo.com/api/v2/video/";
	
	public VimeoRetriever(Credentials credentials) {
		super(credentials);
		
		requestFactory = HTTP_TRANSPORT.createRequestFactory(
				new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest request) {
						request.setParser(new JsonObjectParser(JSON_FACTORY));
					}
				});
	}
	
	/*
	public MediaItem getMediaItem(String id) {
		try {
			GenericUrl url = new GenericUrl(requestPrefix + id + ".json");
			HttpRequest request = requestFactory.buildGetRequest(url);
			HttpResponse response = request.execute();
			VimeoVideo.VimeoVideo[] videos = response.parseAs(VimeoVideo.VimeoVideo[].class);
			if(videos != null && videos.length>0) {
				MediaItem mediaItem = new VimeoVideo(videos[0]);
				return mediaItem;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return null;	 	
	}
	*/
	
	@Override
	public List<Post> retrieve(Feed feed) {
		return new ArrayList<Post>();
	}

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
	public List<Post> retrieve(Feed feed, Integer maxRequests,
			Integer maxResults) {
		return null;
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
