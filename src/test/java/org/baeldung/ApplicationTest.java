package org.baeldung;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.MOCK)
public class ApplicationTest {
    /**
     * Test that the application context is loaded correctly
     */
    @Test
    public void whenStartApplication_thenNoExceptions() {
        assert(true);
    }
}