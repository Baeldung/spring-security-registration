package com.baeldung.web.controller;

import com.baeldung.persistence.model.User;
import com.baeldung.persistence.model.VerificationToken;
import com.baeldung.registration.OnRegistrationCompleteEvent;
import com.baeldung.security.ISecurityUserService;
import com.baeldung.service.IUserService;
import com.baeldung.web.dto.PasswordDto;
import com.baeldung.web.dto.UserDto;
import com.baeldung.web.error.InvalidOldPasswordException;
import com.baeldung.web.util.GenericResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@RestController
public class RegistrationRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private IUserService userService;

    @Autowired
    private ISecurityUserService securityUserService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;

    public RegistrationRestController() {
        super();
    }

    // Registration
    @PostMapping("/user/registration")
    public GenericResponse registerUserAccount(@Valid final UserDto accountDto, final HttpServletRequest request) {
        LOGGER.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);
        userService.addUserLocation(registered, getClientIP(request));
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
    }

    // User activation - verification
    @GetMapping("/user/resendRegistrationToken")
    public GenericResponse resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        final VerificationToken newToken = userService.generateNewVerificationToken(existingToken);
        final User user = userService.getUser(newToken.getToken());
        mailSender.send(constructResendVerificationTokenEmail(getAppUrl(request), request.getLocale(), newToken, user));
        return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
    }

    // Reset password
    @PostMapping("/user/resetPassword")
    public GenericResponse resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        final User user = userService.findUserByEmail(userEmail);
        if (user != null) {
            final String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, user));
        }
        return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    // Save password
    @PostMapping("/user/savePassword")
    public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {

        final String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

        if(result != null) {
            return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
        }

        Optional<User> user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if(user.isPresent()) {
            userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
        } else {
            return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
        }
    }

    // Change user password
    @PostMapping("/user/updatePassword")
    public GenericResponse changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final User user = userService.findUserByEmail(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
        if (!userService.checkIfValidOldPassword(user, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        userService.changeUserPassword(user, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }

    // Change user 2 factor authentication
    @PostMapping("/user/update/2fa")
    public GenericResponse modifyUser2FA(@RequestParam("use2FA") final boolean use2FA) throws UnsupportedEncodingException {
        final User user = userService.updateUser2FA(use2FA);
        if (use2FA) {
            return new GenericResponse(userService.generateQRUrl(user));
        }
        return null;
    }

    // ============== NON-API ============

    private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final User user) {
        final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, user);
    }

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final User user) {
        final String url = contextPath + "/user/changePassword?token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body, User user) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
