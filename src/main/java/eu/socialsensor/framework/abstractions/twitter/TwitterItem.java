package eu.socialsensor.framework.abstractions.twitter;

import eu.socialsensor.framework.common.domain.Item;
import eu.socialsensor.framework.common.domain.Location;
import eu.socialsensor.framework.common.domain.MediaItem;
import eu.socialsensor.framework.common.domain.MediaItemLight;
import eu.socialsensor.framework.common.domain.Source;
import eu.socialsensor.framework.common.domain.WebPage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.MediaEntity.Size;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

/**
 * Class that holds the information regarding the twitter status
 * @author manosetro
 * @email  manosetro@iti.gr
 */
public class TwitterItem extends Item {
	
	private static final String RETWEET = "retweetCount";

	public TwitterItem(String id, Operation operation) {
		super(Source.Type.Twitter.toString(),operation);
		setId(Source.Type.Twitter+"::"+id);
	}
    
	public TwitterItem(Status status) {
		super(Source.Type.Twitter.toString(), Operation.NEW_UPDATE);
		if (status == null) return;
		
		id = Source.Type.Twitter+"::"+status.getId();
		source = "Twitter";
		
		Status retweetStatus = status.getRetweetedStatus();
		if(retweetStatus != null) {
			reference = Source.Type.Twitter+"::"+retweetStatus.getId();
		}
		
		Date pubDate = status.getCreatedAt();
		publicationTime = pubDate.getTime();
		
//		we must extract the json once we have a status object within current thread
//		text = DataObjectFactory.getRawJSON(status);
		
		HashtagEntity[] hashtags = status.getHashtagEntities();
		tags = null;
		if (hashtags != null) {
			tags = new String[hashtags.length];
			for (int i=0;i<tags.length;i++){
				tags[i] = hashtags[i].getText();
			}
		}
		
		author = null;
		User user = status.getUser();
		if (user != null) {
			streamUser = new TwitterStreamUser(user);
			uid = streamUser.getId();
			author = user.getScreenName();
		}
		
		UserMentionEntity[] userMentions = status.getUserMentionEntities();
		List<String> mentions = new ArrayList<String>();
		for(UserMentionEntity userMention : userMentions) {
			String screenname = userMention.getScreenName();
			mentions.add(screenname);
		}
		super.mentions = mentions.toArray(new String[mentions.size()]);
		
		URLEntity[] urlEntities = status.getURLEntities();
		Set<URL> urls = new HashSet<URL>();
		webPages = new ArrayList<WebPage>();
		
		if (urlEntities != null) {
			for (URLEntity urlEntity : urlEntities) {
				
				String urlStr = urlEntity.getExpandedURL();
				if (urlStr == null) {
					urlStr = urlEntity.getURL();
					if (urlStr == null) {
						urlStr = urlEntity.getDisplayURL();
					}
				}
				
				if(urlStr == null)
					continue;
				
				try {
					URL url = new URL(urlStr);
					urls.add(url);
					
					WebPage webPage = new WebPage(urlStr, id);
					webPage.setStreamId(streamId);
					webPage.setDate(new Date(publicationTime));
					
					webPages.add(webPage);
				} catch (Exception e) {
					continue;
				}
				
				
			}
		}
		links = urls.toArray(new URL[urls.size()]);

		mediaLinks = new ArrayList<MediaItemLight>();
		MediaEntity[] mediaEntities = status.getMediaEntities();
		if (mediaEntities != null) {
			for (MediaEntity mediaEntity : mediaEntities) {
				String mediaUrl = mediaEntity.getMediaURL();
				if (mediaUrl == null) {
					mediaUrl = mediaEntity.getMediaURLHttps();
				}
				URL temp_url;
				try {
					temp_url = new URL(mediaUrl);
				} catch (MalformedURLException e) {
					continue;
				}
				
				String pageUrl = mediaEntity.getExpandedURL();
				if(pageUrl == null) {
					pageUrl = mediaEntity.getURL();
				}
				
				String mediaId = Source.Type.Twitter + "::" + mediaUrl;
				
				MediaItem mediaItem = new MediaItem(temp_url);
				mediaItem.setId(mediaId);
				mediaItem.setType("image");
				mediaItem.setRef(id);
				String thumbnail = mediaUrl+":thumb";
				mediaLinks.add(new MediaItemLight(mediaUrl, thumbnail));
				
				mediaItem.setThumbnail(thumbnail);
				mediaItem.setPageUrl(pageUrl);
				
				Map<Integer, Size> sizes = mediaEntity.getSizes();
				Size size = sizes.get(Size.MEDIUM);
				if(size != null) {
					mediaItem.setSize(size.getWidth(), size.getHeight());
				}
				
				mediaItems.put(temp_url, mediaItem);
				mediaIds.add(mediaId);
			}
		}
		
		title = status.getText();
		
		Long retweet = status.getRetweetCount();
		if (retweet > 0) {
			popularity = new HashMap<String, Integer>();
			popularity.put(RETWEET, retweet.intValue());
		}
		
		GeoLocation geoLocation = status.getGeoLocation();
		if (geoLocation != null) {
			double latitude = status.getGeoLocation().getLatitude();
			double longitude = status.getGeoLocation().getLongitude();
			
			location = new Location(latitude, longitude);
		}
		Place place = status.getPlace();
		if (place != null) { 
			String placeName = status.getPlace().getFullName();
			if(location==null) {
				location = new Location(placeName);
			}
			else {
				location.setName(placeName);
			}
		}
	}

}
