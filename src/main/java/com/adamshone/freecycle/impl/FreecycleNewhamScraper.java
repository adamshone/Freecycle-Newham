package com.adamshone.freecycle.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

import com.adamshone.freecycle.Post;
import com.adamshone.freecycle.PostProvider;
import com.adamshone.freecycle.PostType;

public class FreecycleNewhamScraper implements PostProvider
{
	Logger log = Logger.getLogger(FreecycleNewhamScraper.class);
	private static final NodeFilter tableDataContainingLinkFilter = new AndFilter(new TagNameFilter("td"), new HasChildFilter(new TagNameFilter("a")));
	private final Parser parser;
	private final SimpleDateFormat freecycleDateFormat;
	private final List<Post> posts = new ArrayList<Post>();
	
	public FreecycleNewhamScraper(Parser parser, String dateFormat)
	{
		log.info(String.format("Instantiated with url=%s, dateFormat=%s", parser.getURL(), dateFormat));
		this.parser = parser;
		this.freecycleDateFormat = new SimpleDateFormat(dateFormat);
	}
	
	@Override
	public List<Post> getPosts()
	{
		log.info(String.format("Fetching posts"));
		posts.clear();

		try
		{
			NodeList list = getHTMLNodes();
			log.info(String.format("Parsed %s matching HTML nodes", list.size()));
			
			SimpleNodeIterator iterator = list.elements();
			while(iterator.hasMoreNodes())
			{
				try
				{
					Post post = parsePostFromHTMLNodes(iterator);
					posts.add(0, post);
				}
				catch(ParseException e)
				{
					log.error("Unable to parse HTML node into a post", e);
				}
			}
		}
		catch(ParserException e)
		{
			log.error("Unable to retrieve HTML nodes", e);
		}

		log.info(String.format("Returning %s posts", posts.size()));
		return posts;
	}

	private NodeList getHTMLNodes() throws ParserException
	{
		log.info("Extracting HTML nodes");
		parser.setURL(parser.getURL());
		return parser.extractAllNodesThatMatch(tableDataContainingLinkFilter);
	}
	
	private Post parsePostFromHTMLNodes(SimpleNodeIterator iterator) throws ParseException
	{
		Node typeAndDateNode = iterator.nextNode();
		PostType postType = PostType.parse(typeAndDateNode);
		Date postDate = parseDateFrom(typeAndDateNode, freecycleDateFormat);
	
		Node linkAndDescriptionNode = iterator.nextNode();
		String description = linkAndDescriptionNode.getChildren().elementAt(2).toPlainTextString();
		String link = ((TagNode)linkAndDescriptionNode.getChildren().elementAt(2)).getAttribute("href");
		
		return new Post(postType, postDate, description, link);
	}
	
	private static Date parseDateFrom(Node typeAndDateNode, SimpleDateFormat dateFormat) throws ParseException
	{
		Node dateNode = typeAndDateNode.getChildren().elementAt(3);
		String dateString = dateNode.toPlainTextString().trim();
		Date date = dateFormat.parse(dateString);
		return date;
	}
}