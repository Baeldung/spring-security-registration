/**
 * 
 */
package org.baeldung.service;

import static org.junit.Assert.*;

import org.baeldung.persistence.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author mycom
 *
 */
/*@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class, MvcConfig.class,
		ServiceConfig.class, PersistenceJPAConfig.class,CaptchaConfig.class, SecSecurityConfig.class, SetupDataLoader.class,SpringTaskConfig.class })
@PropertySource("classpath:application.properties")*/
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ContextLoaderTest {

	@Autowired
	private IUserService userService;

	@Test
	public void testTransferService() {
		User user=userService.findUserByEmail("kgandhi@gmail.com");
		System.out.println(user);
		assertNull(user);
	}
}
