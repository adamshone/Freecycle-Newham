package com.adamshone.freecycle;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FreecycleFeedSpecs
{
	private FreecycleFeed feed;
	private List<Post> posts;
	@Mock PostProvider postProvider;
	@Mock PostReceiver postReceiver;
	
	@Before
	public void setUp()
	{
		feed = new FreecycleFeed(postProvider, postReceiver, "1", "1");
		posts = new ArrayList<Post>();
	}
	
	@Test
	public void itCanBeConstructedWithAnUnlimitedNumberOfUpdates()
	{
		new FreecycleFeed(postProvider, postReceiver, "1");
	}
	
	@Test
	public void itDoesAnUpdateWhenItWakesUp()
	{
		// Given
		when(postProvider.getPosts()).thenReturn(posts);
		
		// When
		feed.start();
		
		// Then
		verify(postProvider).getPosts();
		verify(postReceiver).receivePosts(posts);
	}
}