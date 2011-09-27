package com.adamshone.freecycle.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.adamshone.freecycle.Post;

@RunWith(MockitoJUnitRunner.class)
public class TwitterPostReceiverSpecs
{
	private static final long MOST_RECENT_STATUS_TIME = 1000l;
	private static final long BEFORE_MOST_RECENT_STATUS_TIME = 500l;
	private static final long AFTER_MOST_RECENT_STATUS_TIME = 2000l;
	private final String screenName = "FreecycleNewham";
	private TwitterPostReceiver receiver;
	private List<Post> posts;
	@Mock ResponseList<Status> statuses;
	@Mock Twitter twitter;
	@Mock Post post;
	@Mock Status mostRecentStatus;
	@Mock Logger log;
	
	@Before
	public void setUp() throws TwitterException
	{
		receiver = new TwitterPostReceiver(twitter, screenName);
		posts = new ArrayList<Post>();
		when(twitter.getUserTimeline(screenName)).thenReturn(statuses);
		when(statuses.get(0)).thenReturn(mostRecentStatus);
		when(mostRecentStatus.getText()).thenReturn("WANTED: item");
		when(mostRecentStatus.getCreatedAt()).thenReturn(new Date(MOST_RECENT_STATUS_TIME));
		receiver.log = log;
	}
	
	@Test
	public void itCanBeConstructedWithATwitterFactory()
	{
		// Given
		TwitterFactory factory = new TwitterFactory();
		
		// When
		new TwitterPostReceiver(factory, screenName);
	}
	
	@Test
	public void itFetchesTheMostRecentStatusWhenPostsAreProvided() throws TwitterException
	{		
		// When
		receiver.receivePosts(posts);
		
		// Then
		verify(twitter).getUserTimeline(screenName);
		verify(twitter, never()).updateStatus(anyString());
	}
	
	@Test
	public void itTweetsAPostThatIsNewerByDateThanTheMostRecentStatus() throws TwitterException
	{
		// Given
		String newPostString = "OFFER: new post";
		when(post.getText()).thenReturn("New post");
		when(post.getDate()).thenReturn(new Date(AFTER_MOST_RECENT_STATUS_TIME));
		when(post.toPost()).thenReturn(newPostString);
		posts.add(post);
		
		// When
		receiver.receivePosts(posts);
		
		// Then
		verify(twitter).updateStatus(newPostString);
	}
	
	@Test
	public void itDoesNotTweetAPostThatIsOlderThanTheMostRecentStatus() throws TwitterException
	{
		// Given
		when(post.getText()).thenReturn("new post");
		when(post.getDate()).thenReturn(new Date(BEFORE_MOST_RECENT_STATUS_TIME));
		posts.add(post);
		
		// When
		receiver.receivePosts(posts);
		
		// Then
		verify(twitter, never()).updateStatus(anyString());
	}
	
	@Test
	public void itLogsAnErrorAndDoesNotThrowAnExceptionIfATwitterExceptionIsThrownWhenSettingMostRecentStatus() throws TwitterException
	{
		// Given
		TwitterException e = mock(TwitterException.class);
		when(twitter.getUserTimeline(screenName)).thenThrow(e);
		
		// When
		receiver.receivePosts(posts);
		
		// Then
		verify(log).error(anyString(), eq(e));
	}
	
	@Test
	public void itLogsAnErrorAndDoesNotThrowAnExceptionIfATwitterExceptionIsThrownWhenTweetingANewPost() throws TwitterException
	{
		// Given
		String newPostString = "OFFER: new post";
		TwitterException e = mock(TwitterException.class);
		when(twitter.updateStatus(anyString())).thenThrow(e);
		when(post.getText()).thenReturn("New post");
		when(post.getDate()).thenReturn(new Date(AFTER_MOST_RECENT_STATUS_TIME));
		when(post.toPost()).thenReturn(newPostString);
		posts.add(post);
		
		// When
		receiver.receivePosts(posts);
		
		// Then
		verify(twitter).updateStatus(newPostString);
		verify(log).error(anyString(), eq(e));
	}
}