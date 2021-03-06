package gr.iti.mklab.framework.abstractions.socialmedia.users;

import com.flickr4java.flickr.people.User;

import gr.iti.mklab.framework.abstractions.socialmedia.Sources;
import gr.iti.mklab.simmo.UserAccount;
import org.mongodb.morphia.annotations.Entity;

/**
 * Class that holds the information of a flickr user
 *
 * @author ailiakop, kandreadou
 */
@Entity("UserAccount")
public class FlickrAccount extends UserAccount {

    public FlickrAccount(User user) {

        if (user == null) return;

        //Id
        setId(Sources.FLICKR + "#" + user.getId());
        //The id of the user in the network
        username = user.getId();
        //The name of the user
        name = user.getRealName();
        //The username of the user
        username = user.getUsername();
        //streamId
        source = Sources.FLICKR;
        //Profile picture of the user
        int iconFarm = user.getIconFarm();
        int iconServer = user.getIconServer();
        if (iconServer > 0) {
            avatarBig = "http://farm" + iconFarm + ".staticflickr.com/" + iconServer
                    + "/buddyicons/" + user.getId() + ".jpg";
        } else {
            avatarSmall = user.getSecureBuddyIconUrl();
        }


        //Location
        location = user.getLocation();

        pageUrl = "https://www.flickr.com/photos/" + username;

        numItems = user.getPhotosCount();

    }


}
