package com.zj.retrieval.master.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.zj.retrieval.master.listener.InitDataBaseListener;

public class InitDataBaseTests {
	@Test
	public void testInitDataBase() {
		ApplicationContext ctx = new FileSystemXmlApplicationContext("/WebContent/WEB-INF/beans.xml");
		InitDataBaseListener lsner = (InitDataBaseListener) ctx.getBean("initDataBaseListener");
		lsner.initalDataBase();
	}
}
