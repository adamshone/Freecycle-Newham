package com.adamshone.freecycle;

import java.util.List;

import twitter4j.internal.logging.Logger;

public class FreecycleFeed
{
	private static final Logger log = Logger.getLogger(FreecycleFeed.class);
	private final PostProvider postProvider;
	private final PostReceiver postReceiver;
	private final long timeBetweenUpdates;
	private final int maxUpdates;
	private int updatesMade = 0;

	FreecycleFeed(PostProvider postProvider, PostReceiver postReceiver, String timeBetweenUpdates, String maxUpdates)
	{
		log.info(String.format("Instantiated with timeBetweenUpdates=%s, maxUpdates=%s", timeBetweenUpdates, maxUpdates));
		this.postProvider = postProvider;
		this.postReceiver = postReceiver;
		this.timeBetweenUpdates = Long.parseLong(timeBetweenUpdates);
		this.maxUpdates = Integer.parseInt(maxUpdates);
	}
	
	public FreecycleFeed(PostProvider postProvider, PostReceiver postReceiver, String timeBetweenUpdates)
	{
		this(postProvider, postReceiver, timeBetweenUpdates, "-1");
	}
	
	public void start()
	{
		log.info("FreecycleFeed starting");
		
		while(updatesMade < maxUpdates || maxUpdates == -1)
		{
			log.info("Woken up");
			List<Post> posts = postProvider.getPosts();
			postReceiver.receivePosts(posts);
			
			updatesMade++;
			log.info(String.format("Made %s checks since startup, sleeping for %sms", updatesMade, timeBetweenUpdates));
			try { Thread.sleep(timeBetweenUpdates); } catch (InterruptedException e) {}
		}
		log.info(String.format("FreecycleFeed terminated after %s updates", updatesMade));
	}
}