package org.baeldung.test;

import org.baeldung.common.DatabaseCleaner;
import org.baeldung.common.EntityBootstrap;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.dao.VerificationTokenRepository;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.VerificationToken;
import org.baeldung.spring.TestDbConfig;
import org.baeldung.spring.TestTaskConfig;
import org.baeldung.task.TokensPurgeTask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestDbConfig.class, TestTaskConfig.class})
public class TokenExpirationIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private TokensPurgeTask tokensPurgeTask;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private EntityBootstrap entityBootstrap;

    private Long token_id;
    private Long user_id;

    //

    @Before
    public void givenUserWithExpiredToken() {
        databaseCleaner.clean();

        User user = entityBootstrap.newUser().save();
        VerificationToken verificationToken = entityBootstrap.newVerificationToken(user)
                .withExpiryDate(Date.from(Instant.now().minus(2, ChronoUnit.DAYS)))
                .save();

        token_id = verificationToken.getId();
        user_id = user.getId();
    }

    @Test
    @Transactional
    public void whenContextLoad_thenCorrect() {
        assertNotNull(user_id);
        assertNotNull(token_id);
        assertNotNull(userRepository.findOne(user_id));

        VerificationToken verificationToken = tokenRepository.findOne(token_id);
        assertNotNull(verificationToken);

        assertTrue(tokenRepository.findAllByExpiryDateLessThan(Date.from(Instant.now())).anyMatch((token) -> token.equals(verificationToken)));
    }

    @Test
    @Transactional
    public void whenRemoveByGeneratedQuery_thenCorrect() {
        tokenRepository.deleteByExpiryDateLessThan(Date.from(Instant.now()));
        assertEquals(0, tokenRepository.count());
    }

    @Test
    @Transactional
    public void whenRemoveByJPQLQuery_thenCorrect() {
        tokenRepository.deleteAllExpiredSince(Date.from(Instant.now()));
        assertEquals(0, tokenRepository.count());
    }

    @Test
    public void whenPurgeTokenTask_thenCorrect() {
        tokensPurgeTask.purgeExpired();
        assertNull(tokenRepository.findOne(token_id));
    }
}
