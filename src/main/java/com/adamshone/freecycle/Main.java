package com.adamshone.freecycle;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Main
{
	public static void main(String[] args)
	{
		PropertyConfigurator.configure("conf/log4j.properties");
		ApplicationContext spring = new FileSystemXmlApplicationContext("conf/applicationContext.xml");
		FreecycleFeed feed = (FreecycleFeed) spring.getBean("freecycleFeed");
		feed.start();
	}
}