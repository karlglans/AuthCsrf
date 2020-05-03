# Authentication

**Authentication** (sometimes abbreviated **auth**) is the process of verifying the identity of a user, which usually involves **credentials** such as usernames and passwords. Authentication is sometimes distinguished from *authorization* (or *access control*), which involves determining what a user is allowed to do once their identity has been verified.

## Reading

- *Iron-Clad Java*
    - **Chapter 2:** Authentication and Session Management
    - **Chapter 5:** Cross-Site Request Forgery Defense and Clickjacking
        - *Focus on CSRF, not clickjacking.*
- PortSwigger
    - https://portswigger.net/web-security/csrf

## Exercises

This app shows a secret pancake recipe to logged-in users, but it only has extremely basic authentication. Your task is to add more useful authentication to the app, following the steps below.

### 1

The app requires users to log in before accessing the website. However, it only supports a single user account and the credentials are hardcoded in the source code. Change the code so that the usernames and passwords are instead read from [`secrets/passwords.txt`](secrets/passwords.txt). We should be able to easily add and remove users by changing the text file. The file format is simple:

    username1 password1
    username2 password2
    ...

Note that this means spaces are not allowed in usernames.

### 2

Add a form that allows visitors to register if they are not already members. New users should be added to the text file and logged in immediately upon registration. Make sure that the same username cannot be registered twice. Also require the visitor to provide their password twice when registering (as is common across the web).

### 3

Add a button that allows visitors to log out if they want to end their session early. Use the `invalidate` method:

https://javalin.io/tutorials/jetty-session-handling#invalidating-sessions

### 4

Complete the code in [`AuthenticationServer`](src/main/java/se/plushogskolan/AuthenticationServer.java) that allows the user to delete their account.

### 5

Run [`CSRFAttackerServer`](src/main/java/se/plushogskolan/CSRFAttackerServer.java) to simulate an attacker hosting a fake website. Then demonstrate how a user visiting the fake website can be tricked into deleting their own account, due to a CSRF vulnerability in [`AuthenticationServer`](src/main/java/se/plushogskolan/AuthenticationServer.java).

Once you have tested the exploit, also fix the vulnerability. Use the [`java.security.SecureRandom`](https://docs.oracle.com/javase/8/docs/api/java/security/SecureRandom.html) class to generate 32 bytes of random data, and turn it into a string with [`Hex.encodeHexString`](https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/binary/Hex.html).

Document your work as follows:

1. **Exploit:** An example of how to perform an exploit and what the effects are.
2. **Vulnerability:** Where the vulnerable lines of code are and why they are vulnerable.
3. **Fix:** How to fix the vulnerable lines of code and why the fix works.

### 6

If you want more CSRF practice, also complete the labs linked to from this PortSwigger page:

https://portswigger.net/web-security/csrf

### 7

Implement a basic "Remember Me" feature using this approach:

1. Add a "Remember Me" checkbox to the login form.
2. If the user checks the "Remember Me" checkbox, generate 32 bytes of random data and turn it into a hex string, using the approach in the CSRF task. We will call this value our *token*.
3. Store the token along with the user's credentials.
4. Create a new cookie, in addition to the session cookie automatically created by Javalin. The value should be the token, and the expiration time should be one week. Do this by creating a [`javax.servlet.http.Cookie`](https://docs.oracle.com/javaee/6/api/javax/servlet/http/Cookie.html) object and passing it into `context.cookie()`.
5. When a user visits the site and is not already logged in, the server should see if the user has a token cookie and if it matches the one stored for them. If it does, the user should be automatically logged in.
6. Make sure that the token cookie is removed when the user actively logs out by pressing the button that you implemented in the previous task.

### 8

Answer the following questions:

1. Why is it a bad idea to store user credentials directly in source code, as the original app did?
2. What could happen if there were a path traversal vulnerability in this app?
3. What could happen if somebody obtained the `JSESSIONID` cookie of another user?
4. Why should we use `POST`, not `GET`, for the login and registration forms?
5. What does the *cross-site* part of *cross-site request forgery* mean? Is it possible to perform a CSRF exploit that is not actually cross-site?
6. Why is the "Remember Me" feature a security risk?