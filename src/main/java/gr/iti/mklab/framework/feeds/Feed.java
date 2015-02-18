package gr.iti.mklab.framework.feeds;

import java.util.Date;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(noClassnameStored = true)
public class Feed {

	@Id
	protected String id = null;

	protected Date dateToRetrieve = null;

	protected Integer totalNumberOfItems = 0;
	
	protected FeedType feedType;
	
	protected String label;
	
	public Feed(Date date, FeedType feedType) {
		this.dateToRetrieve = date;
		this.feedType = feedType;
		
	}
	
	public  FeedType getFeedtype() {
		return feedType;
	}
	
	public  void setFeedType(FeedType feedType) {
		this.feedType = feedType;
	}
	
	public  String getId() {
		return id;
	}
	
	public  void setId(String id) {
		this.id = id;
	}
	
	public  String getLabel() {
		return label;
	}
	
	public  void setLabel(String label) {
		this.label = label;
	}
	
	public Date getDateToRetrieve() {
		return dateToRetrieve;
	}
	
	public void setDateToRetrieve(Date dateToRetrieve) {
		this.dateToRetrieve = dateToRetrieve;
	}
	
	public  Integer getTotalNumberOfItems() {
		return this.totalNumberOfItems;
	}
	
	public  void setTotalNumberOfItems(Integer totalNumberOfItems) {
		this.totalNumberOfItems += totalNumberOfItems;
	}
	
	
	@Entity(noClassnameStored = true)
	public enum FeedType {
		KEYWORDS, ACCOUNT, URL, GROUP
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		
        return ((Feed) obj).id.equals(id);
    }
}
