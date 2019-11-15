package com.briup.environment.test;

import com.briup.environment.client.Client;
import com.briup.environment.client.Gather;
import com.briup.environment.client.impl.ClientImpl;
import com.briup.environment.client.impl.GatherImpl;
import com.briup.environment.conf.impl.ConfigurationImpl;
import com.briup.environment.server.Server;
import com.briup.environment.server.impl.ServerImpl;
import org.junit.Before;
import org.junit.Test;
import com.briup.environment.conf.Configuration;
import com.briup.environment.log.Log;

public class EnvironmentTest {
	private Log log;
	private Configuration configuration;
	/*
	 * 一个JUnit4的单元测试用例执行顺序为:
	 * @BeforeClass -> @Before -> @Test -> @After -> @AfterClass; 
	 * 每一个测试方法的调用顺序为:
	 * @Before -> @Test -> @After;
	 */
	@Before
	public void setUp() throws Exception {
		this.configuration = ConfigurationImpl.getInstance();
	}
	@Test
	public void serverTest() throws Exception {
		this.configuration.getServer().reciver();
	}
	@Test
	public void clientTest() throws Exception {
		Gather gather = this.configuration.getGather();
		this.configuration.getClient().send(gather.gather());
//		Client client = new ClientImpl();
//		client.send(gather.gather());
	}
}
