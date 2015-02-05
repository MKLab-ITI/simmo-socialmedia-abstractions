package gr.iti.mklab.framework.abstractions.socialmedia.posts;

import gr.iti.mklab.framework.abstractions.socialmedia.Sources;
import gr.iti.mklab.framework.abstractions.socialmedia.users.TwitterAccount;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import gr.iti.mklab.simmo.UserAccount;
import gr.iti.mklab.simmo.annotations.Original;
import gr.iti.mklab.simmo.associations.Mention;
import gr.iti.mklab.simmo.associations.Reference;
import gr.iti.mklab.simmo.documents.Post;
import gr.iti.mklab.simmo.documents.Webpage;
import gr.iti.mklab.simmo.items.Image;
import gr.iti.mklab.simmo.util.Location;
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
 * Class that holds the information of a twitter status
 *
 * @author manosetro
 * @email manosetro@iti.gr
 */
public class TwitterPost extends Post {


    public TwitterPost(Status status) {

        if (status == null) return;

        //Id
        setId(Sources.TWITTER + "#" + status.getId());
        //SocialNetwork Name
        type = Sources.TWITTER;
        //Timestamp of the creation of the tweet
        setCreationDate(status.getCreatedAt());
        //User that wrote the tweet
        User user = status.getUser();
        if (user != null) {
            this.user = new TwitterAccount(user);
        }

        url = "https://twitter.com/" + user.getScreenName() + "/statuses/" + status.getId();

        //Store/Update on the basis that it is an original tweet or a retweet
        Status retweetStatus = status.getRetweetedStatus();

        Original original = new Original(retweetStatus == null);
        addAnnotation(original);

        if (retweetStatus != null) {

            Post reference = new Post();
            reference.setId(Sources.TWITTER + "#" + retweetStatus.getId());
            //TODO: set user to the reference post
            //super.referencedUser = retweetStatus.getUser().getScreenName();
            //super.referencedUserId = Source.Twitter + "#" + retweetStatus.getUser().getId();
        }

        //Title of the tweet
        title = status.getText();
        language = status.getLang();

        //Tags
        HashtagEntity[] hashtags = status.getHashtagEntities();
        Arrays.stream(hashtags).forEach(hashtag -> {
            tags.add(hashtag.getText());
        });

        //User that are mentioned inside the tweet
        UserMentionEntity[] userMentions = status.getUserMentionEntities();
        Arrays.stream(userMentions).forEach(mention -> {
            UserAccount mentioned = new UserAccount();
            mentioned.setId(Sources.TWITTER + "#" + mention.getId());
            mentioned.setName(mention.getScreenName());
            mentioned.setStreamId(Sources.TWITTER);
            Mention m = new Mention(this, mentioned);
            //TODO: add this mention somewhere
        });

        /*if (status.getInReplyToUserId() > 0) {
            super.inReply = Source.Twitter + "#" + status.getInReplyToUserId();
        }*/

        //Popularity
        numLikes = status.getFavoriteCount();
        numShares = status.getRetweetCount();

        //Location
        GeoLocation geoLocation = status.getGeoLocation();
        if (geoLocation != null) {
            double latitude = status.getGeoLocation().getLatitude();
            double longitude = status.getGeoLocation().getLongitude();
            location = new Location(latitude, longitude);
        }
        Place place = status.getPlace();
        if (place != null) {
            if (location == null) {
                location = new Location();
            }
            location.setCountry(place.getName());
            location.setCountry(place.getCountry());
            location.setAdress(place.getStreetAddress());
        }

        //WebPages inside the tweet
        URLEntity[] urlEntities = status.getURLEntities();
        Set<URL> urls = new HashSet<URL>();

        if (urlEntities != null) {
            for (URLEntity urlEntity : urlEntities) {

                String urlStr = urlEntity.getExpandedURL();
                if (urlStr == null) {
                    urlStr = urlEntity.getURL();
                    if (urlStr == null) {
                        urlStr = urlEntity.getDisplayURL();
                    }
                }

                if (urlStr == null)
                    continue;

                try {
                    URL url = new URL(urlStr);
                    urls.add(url);
                    Webpage webpage = new Webpage();
                    webpage.setUrl(urlStr);
                    webpage.setId(id);
                    webpage.setSource(Sources.TWITTER);
                    webpage.setCreationDate(status.getCreatedAt());
                    references.add(new Reference(webpage, Reference.ReferenceType.LINK));
                } catch (Exception e) {
                    continue;
                }

            }
        }

        //Media entities inside the tweet
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
                if (pageUrl == null) {
                    pageUrl = mediaEntity.getURL();
                }

                if ("photo".equalsIgnoreCase(mediaEntity.getType())) {
                    Image img = new Image();
                    img.setUrl(temp_url.toString());
                    img.setId(Sources.TWITTER + "#" + mediaEntity.getId());
                    img.setStreamId(Sources.TWITTER);
                    img.setCreationDate(status.getCreatedAt());
                    //PageUrl
                    img.setWebPageUrl(pageUrl);
                    //Thumbnail
                    String thumbnail = mediaUrl + ":thumb";
                    img.setThumbnail(thumbnail);
                    //Title
                    img.setTitle(title);
                    //Description
                    img.setDescription(description);
                    //Tags
                    //img.setTags(tags);
                    //Popularity
                    //img.setLikes(likes);
                    //img.setShares(shares);
                    //Author
                    //if(streamUser != null) {
                    //    mediaItem.setUser(streamUser);
                    //    mediaItem.setUserId(streamUser.getId());
                    //}
                    //Location
                    img.setLocation(location);

                    //Size
                    Map<Integer, Size> sizes = mediaEntity.getSizes();
                    Size size = sizes.get(Size.MEDIUM);
                    if (size != null) {
                        img.setWidth(size.getWidth());
                        img.setHeight(size.getHeight());
                    }
                    items.add(img);
                }

            }
        }

    }

}
