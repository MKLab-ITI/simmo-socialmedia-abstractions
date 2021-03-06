package gr.iti.mklab.framework.abstractions.socialmedia.posts;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.GeoData;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.tags.Tag;

import gr.iti.mklab.framework.abstractions.socialmedia.Sources;
import gr.iti.mklab.framework.abstractions.socialmedia.users.FlickrAccount;
import gr.iti.mklab.simmo.documents.Post;
import gr.iti.mklab.simmo.items.Image;
import gr.iti.mklab.simmo.util.Location;
import org.mongodb.morphia.annotations.Entity;

/**
 * Class that holds the information of a flickr photo
 *
 * @author ailiakop, kandreadou
 */
@Entity("Post")
public class FlickrPost extends Post {

    @SuppressWarnings("deprecation")
    public FlickrPost(Photo photo) {

        if (photo == null || photo.getId() == null) return;

        //Id
        id = Sources.FLICKR + "#" + photo.getId();
        //SocialNetwork Name
        type = Sources.FLICKR.toString();
        //Timestamp of the creation of the photo
        creationDate = photo.getDatePosted();
        //Title of the photo
        if (photo.getTitle() != null) {
            title = photo.getTitle();
        }
        //Description of the photo
        description = photo.getDescription();
        //Tags of the photo
        Collection<Tag> photoTags = photo.getTags();
        if (photoTags != null) {
            List<String> tagsList = new ArrayList<String>();
            for (Tag tag : photoTags) {
                String tagStr = tag.getValue();
                if (tagStr != null && !tagStr.contains(":"))
                    tagsList.add(tagStr);
            }
            tags = tagsList;
        }

        //User that posted the photo
        User user = photo.getOwner();
        if (user != null) {
            setContributor(new FlickrAccount(user));
        }

        //Location
        if (photo.hasGeoData()) {

            GeoData geo = photo.getGeoData();

            double latitude = (double) geo.getLatitude();
            double longitude = (double) geo.getLongitude();

            location = new Location(latitude, longitude);
        }

        url = photo.getUrl();

        //Popularity
        numComments = photo.getComments();

        //Getting the photo
        try {
            String url = null;
            String thumbnail = photo.getMediumUrl();
            if (thumbnail == null) {
                thumbnail = photo.getThumbnailUrl();
            }
            URL mediaUrl = null;
            if ((url = photo.getLargeUrl()) != null) {
                mediaUrl = new URL(url);

            } else if ((url = photo.getMediumUrl()) != null) {
                mediaUrl = new URL(url);
            }

            if (mediaUrl != null) {
                Image img = new Image();
                img.setUrl(mediaUrl.toString());

                String mediaId = Sources.FLICKR + "#" + photo.getId();

                //id
                img.setId(mediaId);
                //SocialNetwork Name
                img.setSource(Sources.FLICKR);
                //Reference
                img.setSourceDocumentId(id);
                //Time of publication
                img.setCreationDate(creationDate);
                img.setContributor(getContributor());
                //PageUrl
                img.setWebPageUrl(photo.getUrl());
                //Thumbnail
                img.setThumbnail(thumbnail);
                //Title
                img.setTitle(title);
                //Description
                img.setDescription(description);
                //Tags
                img.setTags(tags);
                //Popularity
                img.setNumComments(numComments);
                img.setNumViews(photo.getViews());
                //Location
                img.setLocation(location);
                addItem(img);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public FlickrPost(Photo photo, FlickrAccount account) {
        this(photo);
        setContributor(account);

        getItems().stream().forEach(i -> i.setContributor(account));

    }

}
