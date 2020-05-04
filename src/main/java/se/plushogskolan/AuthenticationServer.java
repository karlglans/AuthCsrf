package se.plushogskolan;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import se.plushogskolan.service.UserService;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationServer {
    static final Map noExtraParams = new HashMap<String, String>();
    static UserService userService = new UserService();

    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.enableDevLogging();
        }).start("localhost", 7000);

        app.before(context -> {
            context.contentType("text/html; charset=UTF-8");
        });

        app.get("/", context -> {
            handleCsrfCreation(context);

            // If there is no "username" attribute in the session, that means the user is still not logged in, so
            // show them the login form.
            if (context.sessionAttribute("username") == null) {
                loginPage(context);
            }
            // Otherwise, they are logged in and should see the welcome page.
            else {
                welcomePage(context);
            }
        });

        app.post("/login", context -> login(context));

        // If the user decides to delete their account, show a confirmation page first.
        app.get("/delete-account", context -> {
            if (context.sessionAttribute("username") == null) {
                context.redirect("/");
            }
            else {
                deleteAccountPage(context);
            }
        });

        // TODO: Complete this code so that it deletes the currently active user.
        app.post("/delete-account", context -> {
            String content = "<p>We have not implemented this feature yet.</p>";
            String html = template("Account Deleted", content);
            context.result(html);
        });

        // Added filter for csrf-checks for mutating actions
        app.before("/delete-account", AuthenticationServer::doCsrfChecks);
        app.before("/login", AuthenticationServer::doCsrfChecks);
    }

    // TODO: maybe a better name
    private static void handleCsrfCreation(Context context) {
        if (context.sessionAttribute("csrf") == null) {
            context.sessionAttribute("csrf", generateSafeToken());
        }
    }

    private static void doCsrfChecks(Context context) {
        // ignoring checks for GET calls since those should not mutate anything
        if (context.req.getMethod().compareTo("GET") == 0) {
            return;
        }
        if (context.sessionAttribute("csrf") == null) {
            throw new HttpResponseException(401, "No session", noExtraParams);
        } else {
            String providedCsrf = context.formParam("csrf");
            if (providedCsrf == null) {
//                context.status(403);
                throw new HttpResponseException(403, "Missing CSRF token in form", noExtraParams);
            }
            var sessionCsrf = context.sessionAttribute("csrf");
            if (providedCsrf == null || context.sessionAttribute("csrf").toString().compareTo(providedCsrf) != 0) {
//                context.status(403);
                logOutUser(context);
                context.redirect("/");
            }
        }
    }

    private static void logOutUser(Context context) {
        context.sessionAttribute("username", null);
    }

    private static void welcomePage(Context context) {
        String content =
            "<script> sessionStorage.setItem('csrf', '" + context.sessionAttribute("csrf") + "');</script>" +
            "<h1>Welcome</h1>" +
            "<p>Welcome, Brad! Here is our secret pancake recipe:</p>" +
            "<ul>" +
                "<li>milk</li>" +
                "<li>flour</li>" +
                "<li>salt</li>" +
            "</ul>";
        String html = template("Welcome!", content);
        context.result(html);
    }

    private static void loginPage(Context context, String message) {
        handleCsrfCreation(context);
        String content =
            "<h1>Login</h1>" +
            // If there is a message, show it in bold.
            (message == null ? "" : "<p><strong>" + message + "</strong></p>") +
            "<p>Before using this site, please log in:</p>" +
            "<form method='post' action='/login'>" +
                "<label>Username: <input type='text' name='username'></label>" +
                "<label>Password: <input type='password' name='password'></label>" +
                "<label>Username: <input type='hidden' name='csrf' value='" + context.sessionAttribute("csrf") + "'></label>" +
                "<button type='submit' id='signIn'>Log In</button>" +
            "</form>";
        String html = template("Login", content);
        context.result(html);
    }

    private static void login(Context context) {
        // Get the username and password from the submitted form.
        String providedUsername = context.formParam("username");
        String providedPassword = context.formParam("password");

        // If the username and password are correct, log the user in.
        if (userService.checkCredentials(providedUsername, providedPassword)) {
            // Save the username in the session, so we know who is logged in.
            context.sessionAttribute("username", providedUsername);

            // Show the welcome page.
            context.redirect("/");
        }
        // Otherwise, show the login form again, now with an error message and appropriate HTTP status code.
        else {
            context.status(403);
            loginPage(context, "The username and/or password are incorrect. Please try again.");
        }
    }

    private static void deleteAccountPage(Context context) {
        String content =
            "<h1>Delete Account</h1>" +
            "<p>Are you <strong>absolutely sure</strong> you want to delete your account?</p>" +
            "<form method='post' action='/delete-account'>" +
                "<button type='submit'>Yes, delete my account</button>" +
                "<a href='/'>No, I changed my mind</a>" +
            "</form>";
        String html = template("Delete Account", content);
        context.result(html);
    }

    private static void loginPage(Context context) {
        loginPage(context, null);
    }

    private static String template(String title, String content) {
        return
            "<!DOCTYPE html>" +
            "<html lang='en'>" +
                "<head>" +
                    "<meta charset='UTF-8'>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "<title>" + title + "</title>" +
                "</head>" +
                "<body>" +
                    content +
                    "<hr>" +
                    "<p><a href='/delete-account'>Delete My Account</a></p>" +
                "</body>" +
            "</html>";
    }

    private static String generateSafeToken() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
}
