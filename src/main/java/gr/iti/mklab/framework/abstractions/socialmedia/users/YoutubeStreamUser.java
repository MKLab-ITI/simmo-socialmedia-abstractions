package gr.iti.mklab.framework.abstractions.socialmedia.users;

import com.google.gdata.data.Link;
import com.google.gdata.data.Person;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.youtube.UserProfileEntry;
import com.google.gdata.data.youtube.YtUserProfileStatistics;

import gr.iti.mklab.framework.common.domain.SocialNetwork;
import gr.iti.mklab.framework.common.domain.StreamUser;

/**
 * Class that holds the information of a youtube user
 * @author ailiakop
 * @email  ailiakop@iti.gr
 */
public class YoutubeStreamUser extends StreamUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9208863907526546716L;

	public YoutubeStreamUser(String user) {

		if (user == null)
			return;
		
		//Id
		id = SocialNetwork.Youtube+"#"+user;
		//The name of the user
		username = user;
		//streamId
		streamId = SocialNetwork.Youtube.toString();
	}

	public YoutubeStreamUser(Person user) {

		if (user == null) 
			return;
		
		//Id
		id = SocialNetwork.Youtube+"#"+user.getName();
		//The id of the user in the network
		userid = user.getName();
		//The name of the user
		username = user.getName();
		//streamId
		streamId = SocialNetwork.Youtube.toString();
		//The link to the user's profile
		linkToProfile = user.getUri();
	}
	
	public YoutubeStreamUser(UserProfileEntry user) {

		if (user == null) 
			return;

		//Id
		id = SocialNetwork.Youtube+"#"+user.getUsername();

		//The id of the user in the network
		userid = user.getUsername();
		//The username of the user
		username = user.getUsername();
		//The name of the user
		name = (user.getFirstName()==null?"":user.getFirstName()+" ") + (user.getLastName()==null?"":user.getLastName());
		//streamId
		streamId = SocialNetwork.Youtube.toString();

		MediaThumbnail thumnail = user.getThumbnail();
		profileImage = thumnail.getUrl();

		Link link = user.getLink("alternate", "text/html");
		if(link != null)
			pageUrl = link.getHref();

		location = user.getLocation();

		description = user.getAboutMe();

		YtUserProfileStatistics statistics = user.getStatistics();
		if(statistics != null) {
			followers = statistics.getSubscriberCount();
		}
	}
	
}
