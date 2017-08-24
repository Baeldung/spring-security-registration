=========

## Login and Registration Example Project with Spring Security


### Relevant Articles: 
- [Spring Security Registration Tutorial](http://www.baeldung.com/spring-security-registration)
- [The Registration Process With Spring Security](http://www.baeldung.com/registration-with-spring-mvc-and-spring-security)
- [Registration – Activate a New Account by Email](http://www.baeldung.com/registration-verify-user-by-email)
- [Registration with Spring Security – Password Encoding](http://www.baeldung.com/spring-security-registration-password-encoding-bcrypt)
- [Spring Security – Roles and Privileges](http://www.baeldung.com/role-and-privilege-for-spring-security-registration)
- [Prevent Brute Force Authentication Attempts with Spring Security](http://www.baeldung.com/spring-security-block-brute-force-authentication-attempts)
- [Spring Security – Reset Your Password](http://www.baeldung.com/spring-security-registration-i-forgot-my-password)
- [Spring Security Registration – Resend Verification Email](http://www.baeldung.com/spring-security-registration-verification-email)
- [The Registration API becomes RESTful](http://www.baeldung.com/registration-restful-api)
- [Registration – Password Strength and Rules](http://www.baeldung.com/registration-password-strength-and-rules)
- [Updating your Password](http://www.baeldung.com/updating-your-password/)
- [Registration - Integrate reCAPTCHA](http://www.baeldung.com/spring-security-registration-captcha/)
- [Two Factor Auth with Spring Security](http://www.baeldung.com/spring-security-two-factor-authentication-with-soft-token)
- [Registration with Spring – Integrate reCAPTCHA](http://www.baeldung.com/spring-security-registration-captcha)
- [Purging Expired Tokens Generated By The Registration](http://www.baeldung.com/registration-token-cleanup)
- [Custom Login Page for Returning User](http://www.baeldung.com/custom-login-page-for-returning-user)
- [Allow Authentication from Accepted Locations Only with Spring Security](http://www.baeldung.com/spring-security-restrict-authentication-by-geography)

### Build and Deploy the Project
```
mvn clean install
```

This is a Spring Boot project, so you can deploy it by simply using the main class: `Application.java`



### Set up MySQL
```
mysql -u root -p 
> CREATE USER 'tutorialuser'@'localhost' IDENTIFIED BY 'tutorialmy5ql';
> GRANT ALL PRIVILEGES ON *.* TO 'tutorialuser'@'localhost';
> FLUSH PRIVILEGES;
```


### Set up Email

You need to configure the email by renaming file "email.properties.sample" to "email.properties" and provide your own username and password.
You also need to use your own host, you can use Amazon or Google for example.
You may also setup an email server locally.  See "email.properties.localhost.sample" for more details.

### AuthenticationSuccessHandler configuraion for Custom Login Page article
If you want to activate the configuration for the article [Custom Login Page for Returning User](http://www.baeldung.com/custom-login-page-for-returning-user), then you need to comment the @Component("myAuthenticationSuccessHandler") annotation in the MySimpleUrlAuthenticationSuccessHandler and uncomment the same in MyCustomLoginAuthenticationSuccessHandler.
