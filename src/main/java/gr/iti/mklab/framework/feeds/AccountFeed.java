package gr.iti.mklab.framework.feeds;

import java.util.Date;
import org.mongodb.morphia.annotations.Entity;


@Entity(noClassnameStored = true)
public class AccountFeed extends Feed {
	
	private String username = null;
	
	public AccountFeed(String username, Date since, String id) {
		super(since, Feed.FeedType.ACCOUNT);
		this.id = id;
		this.username = username;
	}

	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

}
