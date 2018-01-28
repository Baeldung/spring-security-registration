package org.baeldung.test;

import org.baeldung.common.DatabaseCleaner;
import org.baeldung.common.EntityBootstrap;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.dao.VerificationTokenRepository;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.VerificationToken;
import org.baeldung.spring.ServiceConfig;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestIntegrationConfig;
import org.baeldung.validation.EmailExistsException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestDbConfig.class, ServiceConfig.class, TestIntegrationConfig.class})
public class UserIntegrationTest {

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private EntityBootstrap entityBootstrap;

    private Long tokenId;
    private Long userId;

    //

    @Before
    public void givenUserAndVerificationToken() throws EmailExistsException {
        databaseCleaner.clean();

        User user = entityBootstrap.newUser().save();
        VerificationToken verificationToken = entityBootstrap.newVerificationToken(user).save();

        userId = user.getId();
        tokenId = verificationToken.getId();
    }

    @Test
    public void whenContextLoad_thenCorrect() {
        assertThat(userRepository.count()).isEqualTo(1);
        assertThat(tokenRepository.count()).isEqualTo(1);
    }

    // @Test(expected = Exception.class)
    @Test
    @Ignore("needs to go through the service and get transactional semantics")
    public void whenRemovingUser_thenFkViolationException() {
        userRepository.delete(userId);
    }

    @Test
    public void whenRemovingTokenThenUser_thenCorrect() {
        tokenRepository.delete(tokenId);
        userRepository.delete(userId);
    }

}
