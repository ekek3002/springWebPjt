package org.zerock.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.Setter;
import lombok.extern.log4j.Log4j;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j
public class SampleServiceTests {

	@Setter(onMethod_ = @Autowired)
	private SampleService sevice;
	
	@Test
	public void testClass() {
		log.info(sevice);
		log.info(sevice.getClass().getName());
	}
	
	@Test
	public void testAdd() throws Exception {
		log.info(sevice.doAdd("123", "456"));
	}
	@Test
	public void testAddError() throws Exception {
		log.info(sevice.doAdd("123", "ABC"));
	}
}
