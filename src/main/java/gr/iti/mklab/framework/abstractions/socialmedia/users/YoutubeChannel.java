package gr.iti.mklab.framework.abstractions.socialmedia.users;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelStatistics;

import gr.iti.mklab.framework.abstractions.socialmedia.Sources;
import gr.iti.mklab.simmo.UserAccount;
import org.mongodb.morphia.annotations.Entity;

import java.util.Date;

/**
 * Class that holds the information of a youtube user
 *
 * @author kandreadou
 */
@Entity("UserAccount")
public class YoutubeChannel extends UserAccount {

    public YoutubeChannel(Channel c) {
        setId(Sources.YOUTUBE + '#' + c.getId());
        source = Sources.YOUTUBE;
        name = c.getSnippet().getTitle();
        description = c.getSnippet().getDescription();
        com.google.api.services.youtube.model.Thumbnail t = c.getSnippet().getThumbnails().getDefault();
        setAvatarBig(t.getUrl());
        ChannelStatistics s = c.getStatistics();
        if (s != null) {
            setNumFollowers(s.getSubscriberCount().intValue());
        }
        pageUrl = "https://www.youtube.com/channel/" + c.getId();
        numItems = s.getVideoCount().intValue();
        creationDate = new Date(c.getSnippet().getPublishedAt().getValue());
    }

}
