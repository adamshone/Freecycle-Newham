package com.adamshone.freecycle;

import java.util.Date;

public class Post
{
	private final PostType postType;
	private final Date postDate;
	private final String description;
	private final String link;

	public Post(PostType postType, Date postDate, String description, String link)
	{
		this.postType = postType;
		this.postDate = postDate;
		this.description = description;
		this.link = link;
	}

	public String toPost()
	{
		return String.format("%s: %s (%s)", postType, description, link);
	}
	
	public String getText()
	{
		return description;
	}
	
	public Date getDate()
	{
		return postDate;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s: %s: %s (%s)", postDate, postType, description, link);
	}
}