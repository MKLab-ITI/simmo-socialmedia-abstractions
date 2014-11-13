package gr.iti.mklab.framework.retrievers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Builder;
import com.google.api.services.youtube.YouTube.Search;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import gr.iti.mklab.framework.Credentials;
import gr.iti.mklab.framework.common.domain.Item;
import gr.iti.mklab.framework.common.domain.Keyword;
import gr.iti.mklab.framework.common.domain.MediaItem;
import gr.iti.mklab.framework.common.domain.Source;
import gr.iti.mklab.framework.common.domain.StreamUser;
import gr.iti.mklab.framework.common.domain.feeds.KeywordsFeed;
import gr.iti.mklab.framework.common.domain.feeds.ListFeed;
import gr.iti.mklab.framework.common.domain.feeds.LocationFeed;
import gr.iti.mklab.framework.common.domain.feeds.SourceFeed;
import gr.iti.mklab.framework.retrievers.SocialMediaRetriever;

/**
 * Class responsible for retrieving YouTube content based on keywords and YouTube users 
 * The retrieval process takes place through Google API 
 * @author ailiakop
 * @email  ailiakop@iti.gr
 */
public class YoutubeV3Retriever extends SocialMediaRetriever {

	public static void main(String...args) throws IOException {
		HttpRequestInitializer initializer = new HttpRequestInitializer() {
			public void initialize(HttpRequest request) throws IOException {
			}
		};
		
		HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		JsonFactory JSON_FACTORY = new JacksonFactory();
				
		Builder builder = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, initializer);
		builder.setApplicationName("mklab-youtube-search");
		
		YouTube youtube = builder.build();
		
		Search.List search = youtube.search().list("snippet");

		search.setQ("ΘΙΑΣΟΣ");
		search.setType("video");
		
		 search.setMaxResults(5l);
		 
		// Call the API and print results.
         SearchListResponse searchResponse = search.execute();
         List<SearchResult> searchResultList = searchResponse.getItems();
	}
	
	private Logger logger = Logger.getLogger(YoutubeV3Retriever.class);
	
	public YoutubeV3Retriever(Credentials credentials, Integer maxRequestPerWindow, Long windowLenth) {			
		super(credentials, maxRequestPerWindow, windowLenth);	
	}

	
	@Override
	public List<Item> retrieveUserFeeds(SourceFeed feed, Integer maxResults, Integer maxRequests) {
		return new ArrayList<Item>();
	}
	
	@Override
	public List<Item> retrieveKeywordsFeeds(KeywordsFeed feed, Integer maxRequests, Integer maxResults) throws Exception {
		return new ArrayList<Item>();
	}
	
	@Override
	public List<Item> retrieveLocationFeeds(LocationFeed feed, Integer maxResults, Integer maxRequests) {
		return new ArrayList<Item>();
    }
	
	@Override
	public List<Item> retrieveListsFeeds(ListFeed feed, Integer maxResults, Integer maxRequests) {
		return new ArrayList<Item>();
	}

	public void stop() {
		
	}
		
	public MediaItem getMediaItem(String id) {
		return null;
	}

	@Override
	public StreamUser getStreamUser(String uid) {
		return null;
	}
	
}
