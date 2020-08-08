package com.baeldung.test;

import com.baeldung.Application;
import com.baeldung.persistence.dao.DeviceMetadataRepository;
import com.baeldung.persistence.dao.UserRepository;
import com.baeldung.spring.TestDbConfig;
import com.baeldung.spring.TestIntegrationConfig;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import com.baeldung.persistence.model.DeviceMetadata;
import com.baeldung.persistence.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class, TestDbConfig.class, TestIntegrationConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeviceServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private DeviceMetadataRepository deviceMetadataRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JavaMailSender mailSender;

    @Value("${local.server.port}")
    int port;

    //

    private Long userId;

    @Before
    public void init() {
        User user = userRepository.findByEmail("test@test.com");
        if (user == null) {
            user = new User();
            user.setFirstName("Test");
            user.setLastName("Test");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setEnabled(true);
            user = userRepository.save(user);
        } else {
            user.setPassword(passwordEncoder.encode("test"));
            user = userRepository.save(user);
        }

        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        userId = user.getId();
    }

    @Test
    public void givenValidLoginRequest_whenNoPreviousKnownDevices_shouldSendLoginNotification() {
        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("X-Forwarded-For", "88.198.50.103") // Nuremberg
                .formParams("username", "test@test.com", "password", "test")
                .post("/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/console", response.getHeader("Location"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void givenValidLoginRequest_whenUsingKnownDevice_shouldNotSendLoginNotification() {
        DeviceMetadata existingDeviceMetadata = new DeviceMetadata();
        existingDeviceMetadata.setUserId(userId);
        existingDeviceMetadata.setLastLoggedIn(new Date());
        existingDeviceMetadata.setLocation("Nuremberg");
        existingDeviceMetadata.setDeviceDetails("Chrome 71.0 - Mac OS X 10.14");
        when(deviceMetadataRepository.findByUserId(userId)).thenReturn(Collections.singletonList(existingDeviceMetadata));

        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("X-Forwarded-For", "88.198.50.103") // Nuremberg
                .formParams("username", "test@test.com", "password", "test")
                .post("/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/console", response.getHeader("Location"));
        verify(mailSender, times(0)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void givenValidLoginRequest_whenUsingNewDevice_shouldSendLoginNotification() {
        DeviceMetadata existingDeviceMetadata = new DeviceMetadata();
        existingDeviceMetadata.setUserId(userId);
        existingDeviceMetadata.setLastLoggedIn(new Date());
        existingDeviceMetadata.setLocation("Nuremberg");
        existingDeviceMetadata.setDeviceDetails("Chrome 71.0 - Mac OS X 10.14");
        when(deviceMetadataRepository.findByUserId(userId)).thenReturn(Collections.singletonList(existingDeviceMetadata));

        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0 Safari/605.1.15")
                .header("X-Forwarded-For", "88.198.50.103") // Nuremberg
                .formParams("username", "test@test.com", "password", "test")
                .post("/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/console", response.getHeader("Location"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void givenValidLoginRequest_whenUsingKnownDeviceFromDifferentLocation_shouldSendLoginNotification() {
        DeviceMetadata existingDeviceMetadata = new DeviceMetadata();
        existingDeviceMetadata.setUserId(userId);
        existingDeviceMetadata.setLastLoggedIn(new Date());
        existingDeviceMetadata.setLocation("Nuremberg");
        existingDeviceMetadata.setDeviceDetails("Chrome 71.0 - Mac OS X 10.14");
        when(deviceMetadataRepository.findByUserId(userId)).thenReturn(Collections.singletonList(existingDeviceMetadata));

        final Response response = given()
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                .header("X-Forwarded-For", "81.47.169.143") // Barcelona
                .formParams("username", "test@test.com", "password", "test")
                .post("/login");

        assertEquals(302, response.statusCode());
        assertEquals("http://localhost:" + port + "/console", response.getHeader("Location"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}
