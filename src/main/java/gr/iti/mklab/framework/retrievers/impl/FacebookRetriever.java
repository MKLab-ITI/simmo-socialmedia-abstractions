package gr.iti.mklab.framework.retrievers.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.exception.FacebookNetworkException;
import com.restfb.exception.FacebookResponseStatusException;
import com.restfb.types.CategorizedFacebookType;
import com.restfb.types.Comment;
import com.restfb.types.Page;
import com.restfb.types.Photo;
import com.restfb.types.Post;
import com.restfb.types.Post.Comments;
import com.restfb.types.User;

import gr.iti.mklab.framework.Credentials;
import gr.iti.mklab.framework.abstractions.socialmedia.items.FacebookItem;
import gr.iti.mklab.framework.abstractions.socialmedia.users.FacebookStreamUser;
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
 * Class responsible for retrieving facebook content based on keywords or facebook users/facebook pages
 * The retrieval process takes place through facebook graph API.
 * @author ailiakop
 * @email  ailiakop@iti.gr
 * 
 */
public class FacebookRetriever extends SocialMediaRetriever {
			
	private FacebookClient facebookClient;
	
	private Logger  logger = Logger.getLogger(FacebookRetriever.class);
	
	public FacebookRetriever(Credentials credentials, Integer maxRequestPerWindow, Long windowLenth) {
		super(credentials, maxRequestPerWindow, windowLenth);
		
		facebookClient = new DefaultFacebookClient(credentials.getAccessToken());
	}

	@Override
	public List<Item> retrieveUserFeeds(SourceFeed feed, Integer maxRequests, Integer maxResults) {
		
		List<Item> items = new ArrayList<Item>();

		Integer totalRequests = 0;
		
		Date lastItemDate = feed.getDateToRetrieve();
		String label = feed.getLabel();
		
		boolean isFinished = false;
		
		Source source = feed.getSource();
		
		String userName = source.getName();
		if(userName == null) {
			logger.error("#Facebook : No source feed");
			return items;
		}
		String userFeed = source.getName()+"/feed";
		
		Connection<Post> connection;
		User page;
		try {
			connection = facebookClient.fetchConnection(userFeed , Post.class);
			page = facebookClient.fetchObject(userName, User.class);
		}
		catch(Exception e) {
			return items;
		}
		
		FacebookStreamUser facebookUser = new FacebookStreamUser(page);
		for(List<Post> connectionPage : connection) {
			
			rateLimitsMonitor.check();
			
			totalRequests++;
			for(Post post : connectionPage) {	
				
				Date publicationDate = post.getCreatedTime();
				
				if(publicationDate.after(lastItemDate) && post!=null && post.getId() != null) {
					FacebookItem facebookUpdate = new FacebookItem(post, facebookUser);
					facebookUpdate.setList(label);
					
					items.add(facebookUpdate);
					
				    Comments comments = post.getComments();
				    if(comments != null) {
				    	for(Comment comment : comments.getData()) {
			    			FacebookItem facebookComment = new FacebookItem(comment, post, null);
			    			facebookComment.setList(label);
			    			
			    			items.add(facebookComment);
			    		} 
				    }
				 }
				
				if(publicationDate.before(lastItemDate) || items.size()>maxResults || totalRequests>maxRequests){
					isFinished = true;
					break;
				}
			
			}
			if(isFinished)
				break;
			
		}

		logger.info("Facebook: " + items.size() + " posts from " + userFeed + " [ " + lastItemDate + " - " + new Date(System.currentTimeMillis()) + " ]");
		
		return items;
	}
	
	@Override
	public List<Item> retrieveKeywordsFeeds(KeywordsFeed feed, Integer maxRequests, Integer maxResults) {
		
		List<Item> items = new ArrayList<Item>();
		
		Date lastItemDate = feed.getDateToRetrieve();
		String label = feed.getLabel();
		
		boolean isFinished = false;
		
		Keyword keyword = feed.getKeyword();
		List<Keyword> keywords = feed.getKeywords();
		
		if(keywords == null && keyword == null) {
			logger.error("#Facebook : No keywords feed");
			return items;
		}

		String tags = "";
		if(keyword != null) {
			String name = keyword.getName();
			String [] words = name.split("\\s+");
			for(String word : words) {
				if(!tags.contains(word) && word.length() > 2) {
					tags += word.toLowerCase()+" ";
				}
			}
		}
		else if(keywords != null) {
			for(Keyword key : keywords) {
				String [] words = key.getName().split(" ");
				for(String word : words) {
					if(!tags.contains(word) && word.length() > 1) {
						tags += word.toLowerCase()+" ";
					}
				}
			}
		}
		
		if(tags.equals(""))
			return items;
		
		Connection<Post> connection = null;
		try {
			connection = facebookClient.fetchConnection("search", Post.class, Parameter.with("q", tags), Parameter.with("type", "post"));
		}catch(FacebookResponseStatusException e) {
			logger.error(e.getMessage());
			return items;
		}
		catch(Exception e) {
			logger.error(e.getMessage());
			return items;
		}
		
		try {
			for(List<Post> connectionPage : connection) {
				for(Post post : connectionPage) {	
					
					Date publicationDate = post.getCreatedTime();
					try {
						if(publicationDate.after(lastItemDate) && post!=null && post.getId()!=null) {
							
							FacebookItem fbItem;
							
							//Get the user of the post
							CategorizedFacebookType cUser = post.getFrom();
							if(cUser != null) {
								User user = facebookClient.fetchObject(cUser.getId(), User.class);
								FacebookStreamUser facebookUser = new FacebookStreamUser(user);
								
								fbItem = new FacebookItem(post, facebookUser);
								fbItem.setList(label);
							}
							else {
								fbItem = new FacebookItem(post);
								fbItem.setList(label);
							}
							
							items.add(fbItem);
						}
					}
					catch(Exception e) {
						logger.error(e.getMessage());
						break;
					}
					
					if(publicationDate.before(lastItemDate) || items.size()>maxResults){
						isFinished = true;
						break;
					}
					
				}
				if(isFinished)
					break;
				
			}
		}
		catch(FacebookNetworkException e){
			logger.error(e.getMessage());
			return items;
		}
		
		logger.info("Facebook: " + items.size() + " posts for " + tags + " [ " + lastItemDate + " - " + new Date(System.currentTimeMillis()) + " ]");
		
		return items;
	}
	
	
	public List<Item> retrieveLocationFeeds(LocationFeed feed, Integer maxRequests, Integer maxResults) {
		return new ArrayList<Item>();
	}
	
	@Override
	public List<Item> retrieveListsFeeds(ListFeed feed, Integer maxRequests, Integer maxResults) {
		return new ArrayList<Item>();
	}
	
	
	@Override
	public void stop(){
		if(facebookClient != null)
			facebookClient = null;
	}

	@Override
	public MediaItem getMediaItem(String mediaId) {
		Photo photo = facebookClient.fetchObject(mediaId, Photo.class);
		
		if(photo == null)
			return null;

		MediaItem mediaItem = null;
		try {
			String src = photo.getSource();
			mediaItem = new MediaItem(new URL(src));
			mediaItem.setId("Facebook#" + photo.getId());
			
			mediaItem.setPageUrl(photo.getLink());
			mediaItem.setThumbnail(photo.getPicture());
			
			mediaItem.setStreamId("Facebook");
			mediaItem.setType("image");
			
			mediaItem.setTitle(photo.getName());
			
			Date date = photo.getCreatedTime();
			mediaItem.setPublicationTime(date.getTime());
			
			mediaItem.setSize(photo.getWidth(), photo.getHeight());
			mediaItem.setLikes((long) photo.getLikes().size());
			
			CategorizedFacebookType from = photo.getFrom();
			if(from != null) {
				StreamUser streamUser = new FacebookStreamUser(from);
				mediaItem.setUser(streamUser);
				mediaItem.setUserId(streamUser.getUserid());
			}
			
			
		} catch (MalformedURLException e) {
			logger.error(e);
		}
		
		return mediaItem;
	}

	@Override
	public StreamUser getStreamUser(String uid) {
		try {
			Page page = facebookClient.fetchObject(uid, Page.class);
			StreamUser facebookUser = new FacebookStreamUser(page);
			return facebookUser;
		}
		catch(Exception e) {
			logger.error(e);
			return null;
		}
	}

}
