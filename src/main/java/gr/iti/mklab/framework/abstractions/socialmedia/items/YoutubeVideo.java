package gr.iti.mklab.framework.abstractions.socialmedia.items;

import java.util.Date;

import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoStatistics;
import gr.iti.mklab.framework.abstractions.socialmedia.Sources;
import gr.iti.mklab.simmo.items.Video;
import org.apache.log4j.Logger;
import org.mongodb.morphia.annotations.Entity;


/**
 * Class that holds the information of a youtube video
 * YouTube API v3
 *
 * @author kandreadou
 */
@Entity("Video")
public class YoutubeVideo extends Video {

    private Logger logger = Logger.getLogger(YoutubeVideo.class);

    public YoutubeVideo(com.google.api.services.youtube.model.Video v) {
        setId(Sources.YOUTUBE + '#' + v.getId());
        setStreamId(Sources.YOUTUBE);
        title = v.getSnippet().getTitle();
        description = v.getSnippet().getDescription();
        creationDate = new Date(v.getSnippet().getPublishedAt().getValue());
        crawlDate = new Date();
        VideoStatistics statistics = v.getStatistics();
        if (statistics != null) {
            numLikes = statistics.getFavoriteCount().longValue();
            numViews = statistics.getViewCount().longValue();
        }
        VideoContentDetails details = v.getContentDetails();
        if (details != null) {
            quality = details.getDefinition();
        }
        com.google.api.services.youtube.model.Thumbnail t = v.getSnippet().getThumbnails().getHigh();
        setThumbnail(t.getUrl());
        setWidth(t.getWidth().intValue());
        setHeight(t.getHeight().intValue());
        url = "https://www.youtube.com/watch?v=" + v.getId();
        webPageUrl = url;
        author = Sources.YOUTUBE + '#' + v.getSnippet().getChannelId();
    }

}
