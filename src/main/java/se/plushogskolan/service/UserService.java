package se.plushogskolan.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UserService {
  private final Path PASSWORD_FILE = Path.of("secrets/passwords.txt");

  public boolean addUser(String name, String password) {
    throw new Error("not yet implemented");
  }

  public boolean checkCredentials(String username, String password) {
    try {
      for (String line : Files.readAllLines(PASSWORD_FILE)) {
        if (line.trim().length() > 0) {
          String[] parts = line.split(" ");
          String name = parts[0].trim();
          String pw = parts[1].trim();
          if (name.equals(username)) {
            return pw.equals(password);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
}
