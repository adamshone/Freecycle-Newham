package com.adamshone.freecycle;

import java.util.List;

public interface PostReceiver
{
	void receivePosts(List<Post> posts);
}