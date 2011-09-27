package com.adamshone.freecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.adamshone.freecycle.Post;

@RunWith(MockitoJUnitRunner.class)
public class FreecycleNewhamScraperSpecs
{
	private static final String URL = "url";
	private FreecycleNewhamScraper scraper;
	@Mock Parser parser;
	@Mock NodeList nodeList;
	@Mock SimpleNodeIterator simpleNodeIterator;
	@Mock Node node;
	@Mock Logger log;
	
	@Before
	public void setUp() throws ParserException
	{
		scraper = new FreecycleNewhamScraper(parser, "EEE MMM dd HH:mm:ss yyyy");
		scraper.log = log;
		when(parser.extractAllNodesThatMatch(any(NodeFilter.class))).thenReturn(nodeList);
		when(nodeList.elements()).thenReturn(simpleNodeIterator);
		when(parser.getURL()).thenReturn(URL);
	}
	
	@Test
	public void itReturnsAnEmptyListOfPostsIfTheNodeIteratorReturnedNoHTMLNodes() throws ParserException
	{
		// Given
		when(simpleNodeIterator.hasMoreNodes()).thenReturn(false);
		
		// When
		List<Post> posts = scraper.getPosts();
		
		// Then
		verify(parser).setURL(URL);
		assertTrue(posts.isEmpty());
	}
	
	@Test
	@SuppressWarnings("serial")
	public void itReturnsAPostIfTheNodeIteratorReturnsTwoValidHTMLNodes() throws ParserException
	{
		// Given
		final Node typeAndDateNode = mock(Node.class);
		final Node linkAndDescriptionNode = mock(Node.class);
		final Node typeLinkNode = mock(Node.class);
		final Node dateNode = mock(Node.class);
		final TagNode linkNode = mock(TagNode.class);
		
		when(simpleNodeIterator.hasMoreNodes()).thenReturn(true, false);
		when(simpleNodeIterator.nextNode()).thenReturn(typeAndDateNode, linkAndDescriptionNode);
		
		when(typeAndDateNode.getChildren()).thenReturn(new NodeList() {{ add(node); add(typeLinkNode); add(node); add(dateNode); }});
		when(typeLinkNode.toHtml()).thenReturn("WANTED");
		when(dateNode.toPlainTextString()).thenReturn("Thu Sep 15 10:33:42 2011");
		
		when(linkAndDescriptionNode.getChildren()).thenReturn(new NodeList() {{ add(node); add(node); add(linkNode); }});
		when(linkNode.toPlainTextString()).thenReturn("descrip");
		when(linkNode.getAttribute("href")).thenReturn("href");
		
		// When
		List<Post> posts = scraper.getPosts();
		
		// Then
		verify(parser).setURL(URL);
		assertEquals(1, posts.size());
		assertEquals("Thu Sep 15 10:33:42 BST 2011: WANTED: descrip (href)", posts.get(0).toString());
	}
	
	@Test
	public void itLogsAnErrorIfTheParserThrowsAnExceptionWhenRetrievingNodes() throws ParserException
	{
		// Given
		ParserException e = new ParserException();
		when(parser.extractAllNodesThatMatch(any(NodeFilter.class))).thenThrow(e);
		
		// When
		scraper.getPosts();
		
		// Then
		verify(log).error(anyString(), eq(e));
	}
	
	@Test
	@SuppressWarnings("serial")
	public void itLogsAnErrorIfAParseExceptionIsThrownWhenProcessingAnHTMLNode() throws ParserException
	{
		// Given
		final Node typeAndDateNode = mock(Node.class);
		final Node typeLinkNode = mock(Node.class);
		final Node dateNode = mock(Node.class);
		
		when(simpleNodeIterator.hasMoreNodes()).thenReturn(true, false);
		when(simpleNodeIterator.nextNode()).thenReturn(typeAndDateNode);
		
		when(typeAndDateNode.getChildren()).thenReturn(new NodeList() {{ add(node); add(typeLinkNode); add(node); add(dateNode); }});
		when(typeLinkNode.toHtml()).thenReturn("WANTED");
		when(dateNode.toPlainTextString()).thenReturn("unparseable");
		
		// When
		scraper.getPosts();
		
		// Then
		verify(parser).setURL(URL);
		verify(log).error(anyString(), any(ParseException.class));
	}
}