package gr.iti.mklab.framework.feeds;

import java.util.Date;

public class GroupFeed extends Feed {
	
	private String groupId;
	private String groupCreator;

	public GroupFeed() {
		
	}
	
	public GroupFeed(String groupCreator, String groupId, Date since, String id) {
		super(since, Feed.FeedType.GROUP);
		this.id = id;
		this.groupCreator = groupCreator;
		this.groupId = groupId;
	}

	public String getGroupCreator() {
		return groupCreator;
	}
	
	public String getGroupId() {
		return groupId;
	}
	
}
