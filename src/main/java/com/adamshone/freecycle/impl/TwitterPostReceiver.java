package com.adamshone.freecycle.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.adamshone.freecycle.Post;
import com.adamshone.freecycle.PostReceiver;

public class TwitterPostReceiver implements PostReceiver
{
	Logger log = Logger.getLogger(TwitterPostReceiver.class);
	private final String twitterScreenName;
	private final Twitter twitter;
	private Status mostRecentStatus;

	TwitterPostReceiver(Twitter twitter, String twitterScreenName)
	{
		log.info(String.format("Instantiated with twitterScreenName=%s", twitterScreenName));
		this.twitterScreenName = twitterScreenName;
		this.twitter = twitter;
	}
	
	public TwitterPostReceiver(TwitterFactory twitterFactory, String twitterScreenName)
	{
		this(twitterFactory.getInstance(), twitterScreenName);
	}
	
	/** PostReceiver implementation **/
	
	@Override
	public void receivePosts( List<Post> posts )
	{
		log.info(String.format("Received %s posts", posts.size()));
		try
		{
			setMostRecentStatus();
			tweetAllNewPostsFrom(posts);
		}
	   catch (TwitterException e)
		{
	   	log.error("Unable to set most recent status", e);
		}
	}
	
	/** implementation detail **/
	
	private void setMostRecentStatus() throws TwitterException
	{
		log.info(String.format("Retrieving most recent status for %s", twitterScreenName));
		mostRecentStatus = twitter.getUserTimeline(twitterScreenName).get(0);
		log.info(String.format("Text of most recent status=\"%s\"", mostRecentStatus.getText()));
		log.info(String.format("Date of most recent status: %s", mostRecentStatus.getCreatedAt()));
	}
	
	private void tweetAllNewPostsFrom(List<Post> posts)
	{
		int postsTweetedSuccessfully = 0;
		int postsNotTweeted = 0;
		for(Post post : posts)
		{
			try
			{
				if(isNew(post))
				{
					String newStatus = post.toPost();
					twitter.updateStatus(newStatus);
					log.info(String.format("Tweeted: %s", newStatus));
					postsTweetedSuccessfully++;
				}
				else
				{
					postsNotTweeted++;
				}
			}
			catch(TwitterException e)
			{
				log.error(String.format("Unable to send tweet: \"%s\"", post.toPost()), e);
			}	
		}
		
		String postString = (postsTweetedSuccessfully > 0) ? String.valueOf(postsTweetedSuccessfully) : "no";
		log.info(String.format("Tweeted %s new posts to the %s feed, did not tweet %s posts", postString, twitterScreenName, postsNotTweeted));
	}
	
	private boolean isNew(Post post)
	{
		Date statusTime = mostRecentStatus.getCreatedAt();
		Date postTime = post.getDate();
		boolean isNewer = postTime.compareTo(statusTime) > 0;
		
		if(!isNewer) log.debug(String.format("Post is older than most recent status (%s older than %s): \"%s\"", postTime, statusTime, post.getText()));
		return isNewer;
	}
}