package gr.iti.mklab.framework.retrievers.impl;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gr.iti.mklab.framework.abstractions.socialmedia.posts.TumblrPost;
import gr.iti.mklab.framework.abstractions.socialmedia.users.TumblrAccount;

import org.apache.log4j.Logger;
import org.scribe.exceptions.OAuthConnectionException;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.types.Blog;

import gr.iti.mklab.framework.Credentials;
import gr.iti.mklab.framework.feeds.AccountFeed;
import gr.iti.mklab.framework.feeds.GroupFeed;
import gr.iti.mklab.framework.feeds.KeywordsFeed;
import gr.iti.mklab.framework.retrievers.RateLimitsMonitor;
import gr.iti.mklab.framework.retrievers.SocialMediaRetriever;
import gr.iti.mklab.simmo.UserAccount;
import gr.iti.mklab.simmo.documents.Post;

/**
 * Class responsible for retrieving Tumblr content based on keywords or tumblr users
 * The retrieval process takes place through Tumblr API (Jumblr)
 * @author ailiakop
 * @email  ailiakop@iti.gr
 */
public class TumblrRetriever extends SocialMediaRetriever {
	
	private Logger logger = Logger.getLogger(TumblrRetriever.class);
	
	private JumblrClient client;
	
	public TumblrRetriever(Credentials credentials, RateLimitsMonitor rateLimitsMonitor) {
		
		super(credentials, rateLimitsMonitor);
		
		client = new JumblrClient(credentials.getKey(), credentials.getSecret());
	}

	
	@Override
	public List<Post> retrieveAccountFeed(AccountFeed feed, Integer maxResults, Integer maxRequests){
		
		List<Post> items = new ArrayList<Post>();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date lastItemDate = feed.getDateToRetrieve();
		
		int numberOfRequests = 0;
		
		boolean isFinished = false;
		
		String uName = feed.getUsername();
		if(uName == null){
			logger.info("#Tumblr : No source feed");
			return null;
		}
		
		Blog blog = client.blogInfo(uName);
		TumblrAccount tumblrStreamUser = new TumblrAccount(blog);
		List<com.tumblr.jumblr.types.Post> posts;
		Map<String,String> options = new HashMap<String,String>();
		
		Integer offset = 0;
		Integer limit = 20;
		options.put("limit", limit.toString());
	
		while(true){
			
			options.put("offset", offset.toString());
			
			posts = blog.posts(options);
			if(posts == null || posts.isEmpty())
				break;
			
			numberOfRequests ++;
			
			for(com.tumblr.jumblr.types.Post post : posts){
				
				if(post.getType().equals("photo") || post.getType().equals("video") || post.getType().equals("link")){
					
					String retrievedDate = post.getDateGMT().replace(" GMT", "");
					retrievedDate+=".0";
					
					Date publicationDate = null;
					try {
						publicationDate = (Date) formatter.parse(retrievedDate);
						
					} catch (ParseException e) {
						return items;
					}
					
					if(publicationDate.after(lastItemDate) && post != null && post.getId() != null){
						
						TumblrPost tumblrItem = null;
						try {
							tumblrItem = new TumblrPost(post,tumblrStreamUser);
						} catch (MalformedURLException e) {
							
							return items;
						}
						
						items.add(tumblrItem);
						
					}
				
				}
				if(items.size()>maxResults || numberOfRequests>maxRequests){
					isFinished = true;
					break;
				}
			}
			if(isFinished)
				break;
			
			offset+=limit;
		}

		//logger.info("#Tumblr : Done retrieving for this session");
//		logger.info("#Tumblr : Handler fetched " +totalRetrievedItems + " posts from " + uName + 
//				" [ " + lastItemDate + " - " + new Date(System.currentTimeMillis()) + " ]");
		
		return items;
	}
	
	@Override
	public List<Post> retrieveKeywordsFeed(KeywordsFeed feed, Integer maxResults, Integer maxRequests) {
		
		List<Post> items = new ArrayList<Post>();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date currentDate = new Date(System.currentTimeMillis());
		Date indexDate = currentDate;
		Date lastItemDate = feed.getDateToRetrieve();
		DateUtil dateUtil = new DateUtil();
		
		int numberOfRequests=0;
		
		boolean isFinished = false;
		
		List<String> keywords = feed.getKeywords();
		
		if(keywords == null || keywords.isEmpty()) {
			logger.info("#Tumblr : No keywords feed");
			return items;
		}
		
		String tags = "";
		for(String key : keywords) {
			String [] words = key.split("\\s+");
			for(String word : words) {
				if(!tags.contains(word) && word.length()>1) {
					tags += word.toLowerCase()+" ";
				}
			}
		}
		
		
		if(tags.equals(""))
			return items;
		
		while(indexDate.after(lastItemDate) || indexDate.equals(lastItemDate)){
			
			Map<String,String> options = new HashMap<String,String>();
			Long checkTimestamp = indexDate.getTime();
			Integer check = checkTimestamp.intValue();
			options.put("featured_timestamp", check.toString());
			List<com.tumblr.jumblr.types.Post> posts;
			try{
				posts = client.tagged(tags);
			}catch(JumblrException e){
				return items;
			}catch(OAuthConnectionException e1){
				return items;
			}
			
			if(posts == null || posts.isEmpty())
				break;
			
			numberOfRequests ++;
			
			for(com.tumblr.jumblr.types.Post post : posts){
				
				if(post.getType().equals("photo") || post.getType().equals("video") ||  post.getType().equals("link")) {
					
					String retrievedDate = post.getDateGMT().replace(" GMT", "");
					retrievedDate+=".0";
					Date publicationDate = null;
					try {
						publicationDate = (Date) formatter.parse(retrievedDate);
						
					} catch (ParseException e) {
						return items;
					}
					
					if(publicationDate.after(lastItemDate) && post != null && post.getId() != null){
						//Get the blog
						String blogName = post.getBlogName();
						Blog blog = client.blogInfo(blogName);
						TumblrAccount tumblrStreamUser = new TumblrAccount(blog);
						
						TumblrPost tumblrItem = null;
						try {
							tumblrItem = new TumblrPost(post, tumblrStreamUser);
						} catch (MalformedURLException e) {
							return items;
						}
						
						if(tumblrItem != null){
							items.add(tumblrItem);
						}
						
					}
				
				}
				
				if(items.size()>maxResults || numberOfRequests>=maxRequests){
					isFinished = true;
					break;
				}
			}
			
			if(isFinished)
				break;
			
			indexDate = dateUtil.addDays(indexDate, -1);
				
		}
		
		return items;
		
	}

	@Override
	public List<Post> retrieveGroupFeed(GroupFeed feed, Integer maxResults, Integer maxRequests) {
		return new ArrayList<Post>();
	}

	public class DateUtil
	{
	    public Date addDays(Date date, int days)
	    {
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        cal.add(Calendar.DATE, days); //minus number decrements the days
	        return cal.getTime();
	    }
	}



	@Override
	public UserAccount getStreamUser(String uid) {
		return null;
	}

}
