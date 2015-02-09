package gr.iti.mklab.framework.abstractions.socialmedia.media;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.google.api.client.util.Key;

import gr.iti.mklab.framework.abstractions.socialmedia.Sources;
import gr.iti.mklab.framework.abstractions.socialmedia.users.TwitPicAccount;
import gr.iti.mklab.simmo.items.Image;
import org.mongodb.morphia.annotations.Entity;

/**
 * Class that holds the information regarding the twitpic media item
 *
 * @author manosetro, kandreadou
 */
@Entity("Image")
public class TwitPicImage extends Image {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static String urlBase = "http://d3j5vwomefv46c.cloudfront.net/photos/large/";
    private static String thumbBase = "http://d3j5vwomefv46c.cloudfront.net/photos/thumb/";
    private static String pageBase = "http://twitpic.com/";

    public TwitPicImage(TwitPicImageItem image) throws Exception {
        url = urlBase + image.id + "." + image.type;

        //Id
        this.setId("Twitpic#" + image.id);
        //SocialNetwork Name
        this.setSource(Sources.TWITPIC);

        //Time of publication
        try {
            Date date = formatter.parse(image.timestamp);
            creationDate = date;
        } catch (Exception e) {

        }
        //PageUrl
        this.setWebPageUrl(pageBase + image.short_id);
        //Thumbnail
        this.setThumbnail(thumbBase + image.id + "." + image.type);
        //Title
        this.setTitle(image.message);
        //Tags
        if (image.tags != null) {
            this.setTags(Arrays.asList(image.tags.split(",")));
        }
        //Popularity
        //comments = new Long(image.number_of_comments);
        //numViews = new Long(image.views);
        //Size
        setWidth(image.width);
        setHeight(image.height);
        setContributor(image.user);
    }


    /**
     * Class that holds the information regarding the twitpic image
     *
     * @author manosetro
     * @email manosetro@iti.gr
     */
    public static class TwitPicImageItem {
        @Key
        public String id, message, tags, short_id, type;
        @Key
        public int views, number_of_comments, height, width;
        @Key
        public String timestamp;
        @Key
        public String user_id, location;
        @Key
        public TwitPicAccount user;
    }


}
