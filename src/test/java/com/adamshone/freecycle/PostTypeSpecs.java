package com.adamshone.freecycle;

import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PostTypeSpecs
{
	@Mock Node typeAndDateNode;
	@Mock Node typeLink;
	@Mock NodeList nodeList;
	
	@Before
	public void setUp()
	{
		when(typeAndDateNode.getChildren()).thenReturn(nodeList);
		when(nodeList.elementAt(1)).thenReturn(typeLink);
	}
	
	@Test
	public void itReturnsTheWantedTypeForAStringContainingTheWordWanted()
	{
		// Given
		when(typeLink.toHtml()).thenReturn("---WANTED---");
		
		// Then
		assertEquals(PostType.WANTED, PostType.parse(typeAndDateNode));
	}
	
	@Test
	public void itReturnsTheOfferTypeForAStringContainingTheWordOffer()
	{
		// Given
		when(typeLink.toHtml()).thenReturn("---OFFER---");
		
		// Then
		assertEquals(PostType.OFFER, PostType.parse(typeAndDateNode));
	}
	
	@Test
	public void itReturnsTheUnknownTypeForAStringThatDoesntContainTheWordOfferOrWanted()
	{
		// Given
		when(typeLink.toHtml()).thenReturn("-------");
		
		// Then
		assertEquals(PostType.UNKNOWN, PostType.parse(typeAndDateNode));
	}
}