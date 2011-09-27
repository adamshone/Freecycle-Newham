package com.adamshone.freecycle;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PostSpecs
{
	@Test
	public void itReturnsTheCorrectValues()
	{
		// Given
		Date date = new Date();
		Post post = new Post(PostType.OFFER, date, "descrip", "link");
		
		// Then
		assertEquals("OFFER: descrip (link)", post.toPost());
		assertEquals("descrip", post.getText());
		assertEquals(date.toString() + ": OFFER: descrip (link)", post.toString());
		assertEquals(date, post.getDate());
	}
}