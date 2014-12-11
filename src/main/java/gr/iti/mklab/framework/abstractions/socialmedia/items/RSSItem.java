package gr.iti.mklab.framework.abstractions.socialmedia.items;

import org.jsoup.Jsoup;

import com.sun.syndication.feed.synd.SyndEntry;

import gr.iti.mklab.framework.common.domain.Item;

/**
 * Class that holds the information of an RSS feed
 * @author ailiakop
 * @author ailiakop@iti.gr
 */
public class RSSItem extends Item {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1413164596016357110L;

	public RSSItem(SyndEntry rssEntry) {
		
		if(rssEntry == null || rssEntry.getLink() == null)
			return;
		//Id
		id = rssEntry.getLink();
		//Document's title
		title = rssEntry.getTitle();
		//Document's content - Extract text content from html structure
		if(rssEntry.getDescription()!=null)
			description = extractDocumentContent(rssEntry.getDescription().getValue());
		//Document's time of publication
		
		publicationTime = rssEntry.getPublishedDate().getTime();
		//The url where the document can be found
		url = rssEntry.getLink();
		

	}
	
	private String extractDocumentContent(String htmlContent){
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlContent);
		
		String content = doc.body().text();
		
		return content;
	}
}
