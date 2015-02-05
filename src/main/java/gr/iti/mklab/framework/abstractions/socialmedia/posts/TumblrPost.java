package gr.iti.mklab.framework.abstractions.socialmedia.posts;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gr.iti.mklab.framework.abstractions.socialmedia.Sources;
import gr.iti.mklab.framework.abstractions.socialmedia.users.TumblrAccount;
import gr.iti.mklab.simmo.associations.Reference;
import gr.iti.mklab.simmo.documents.Webpage;
import gr.iti.mklab.simmo.items.Image;
import org.apache.log4j.Logger;

import com.tumblr.jumblr.types.LinkPost;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.PhotoSize;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.Video;
import com.tumblr.jumblr.types.VideoPost;


/**
 * Class that holds the information of a tumblr post
 *
 * @author ailiakop
 * @email ailiakop@iti.gr
 */
public class TumblrPost extends gr.iti.mklab.simmo.documents.Post {


    private Logger logger = Logger.getLogger(TumblrPost.class);

    public TumblrPost(Post post) throws MalformedURLException {

        if (post == null || post.getId() == null) {
            return;
        }

        setId(Sources.TUMBLR + "#" + post.getId());

        //SocialNetwork Name
        type = Sources.TUMBLR.toString();

        //Timestamp of the creation of the post
        creationDate = new Date(post.getTimestamp() * 1000);

        url = post.getPostUrl();

        //Tags
        post.getTags().stream().forEach(hashtag -> {
            tags.add(hashtag.toString());
        });

        //Items - WebPages in a post
        String pageURL = post.getPostUrl();

        int number = 0;
        if (post.getType().equals("photo")) {
            PhotoPost phPost;
            phPost = (PhotoPost) post;

            List<Photo> photos = phPost.getPhotos();
            if (photos == null)
                return;

            try {
                for (Photo photo : photos) {

                    String caption = photo.getCaption();
                    number++;

                    List<PhotoSize> allSizes = photo.getSizes();
                    String photoUrl = allSizes.get(0).getUrl();
                    String thumbnail = allSizes.get(allSizes.size() - 1).getUrl();

                    if (photoUrl != null) {

                        URL url = new URL(photoUrl);
                        //url
                        Image img = new Image();
                        img.setUrl(url.toString());
                        img.setId(Sources.TUMBLR + "#" + post.getId() + "_" + number);
                        img.setStreamId(Sources.TUMBLR);
                        img.setCreationDate(creationDate);
                        img.setWebPageUrl(pageURL);
                        img.setTitle(title);
                        img.setDescription(caption);
                        img.setThumbnail(thumbnail);
                        items.add(img);

                        //Author
                        //mediaItem.setUser(streamUser);

                        //Tags
                        //mediaItem.setTags(tags);

                    }
                }
            } catch (MalformedURLException e1) {
                logger.error("Photo URL is distorted: " + e1);
            } catch (Exception e2) {
                logger.error("Exception: " + e2);
            }
        } else if (post.getType().equals("video")) {
            VideoPost vidPost = (VideoPost) post;
            List<Video> videos = vidPost.getVideos();

            String embedCode = videos.get(0).getEmbedCode();

            if (embedCode == null)
                return;

            String postfix = "";
            String prefix = "src=";
            //String compl = "";
            String prefix_id = "embed/";
            String postfix_id = "?";

            int index;
            int startIndex_id = embedCode.indexOf(prefix_id);

            String videoIdUrl;
            String videoThumbnail;
            String videoUrl = null;

            if (embedCode.contains("youtube")) {
                postfix = "frameborder";
                index = embedCode.lastIndexOf(prefix);
                videoIdUrl = embedCode.substring(startIndex_id + prefix_id.length(), embedCode.indexOf(postfix_id));
                videoUrl = embedCode.substring(index + prefix.length(), embedCode.indexOf(postfix));
                videoUrl = videoUrl.substring(1, videoUrl.length() - 1);


                videoThumbnail = "http://img.youtube.com/vi/" + videoIdUrl + "/0.jpg";

            } else if (embedCode.contains("dailymotion")) {
                postfix = "width";
                index = embedCode.lastIndexOf(prefix);
                videoUrl = embedCode.substring(index + prefix.length(), embedCode.indexOf(postfix));
                videoUrl = videoUrl.substring(1, videoUrl.length() - 1);

                StringBuffer str = new StringBuffer(videoUrl);
                String thumb = "thumbnail";

                str.insert(videoUrl.indexOf("/video/"), thumb);

                videoThumbnail = str.toString();

            } else {
                return;
            }

            if (videoUrl == null)
                return;

            URL url = null;
            try {
                url = new URL(videoUrl);
            } catch (MalformedURLException e1) {
                logger.error("Video URL is distorted : " + e1);
            }
            number++;

            gr.iti.mklab.simmo.items.Video video = new gr.iti.mklab.simmo.items.Video();
            video.setUrl(url.toString());
            video.setId(Sources.TUMBLR + "#" + post.getId() + "_" + number);
            //SocialNetwork Name
            video.setStreamId(Sources.TUMBLR);
            //Time of publication
            video.setCreationDate(creationDate);
            //PageUrl
            video.setWebPageUrl(pageURL);
            //Thumbnail
            video.setThumbnail(videoThumbnail);
            //Title
            video.setTitle(title);

            items.add(video);

        } else if (post.getType().equals("link")) {

            LinkPost linkPost = (LinkPost) post;
            String link = linkPost.getLinkUrl();
            if (link != null) {
                Webpage p = new Webpage();
                p.setUrl(link);
                p.setId(id);
                p.setSource(Sources.TUMBLR);
                references.add(new Reference(p, Reference.ReferenceType.LINK));
            }
        }

    }

    public TumblrPost(Post post, TumblrAccount user) throws MalformedURLException {
        this(post);

        //User that posted the post
        //streamUser = user;
        //uid = streamUser.getId();

    }
}
