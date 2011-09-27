package com.adamshone.freecycle;

import org.htmlparser.Node;

public enum PostType 
{
	OFFER,
	WANTED,
	UNKNOWN;

	private static final String OFFER_STRING = "OFFER";
	private static final String WANTED_STRING = "WANTED";

	public static PostType parse(Node typeAndDateNode)
	{
		Node typeLink = typeAndDateNode.getChildren().elementAt(1);
		String typeLinkString = typeLink.toHtml();
		
		if(typeLinkString.contains(WANTED_STRING))
			return WANTED;
		else if(typeLinkString.contains(OFFER_STRING))
			return OFFER;
		else
			return UNKNOWN;
	}
}