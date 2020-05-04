package se.plushogskolan.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UserService {
  private Map<String, UserModel> users = new HashMap<String, UserModel>();
  private JwtHelper jwtHelper = new JwtHelper("aaa", "plusshogskolan");

  public void init() {
    extractUsersFromFile();
  }

  private void extractUsersFromFile() {
    Path pwFile = Path.of("secrets/passwords.txt");
    try {
      for (String line : Files.readAllLines(pwFile)) {
        if (line.trim().length() > 0) {
          String[] parts = line.split(" ");
          String name = parts[0].trim();
          String pw = parts[1].trim();
          users.put(name, new UserModel(name, pw));
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String makeLoggedInToken(String username) {
    return jwtHelper.makeToken(username);
  }

  public boolean checkCredentials(String username, String password) {
    return users.containsKey(username) && users.get(username).getPassword().equals(password);
  }
}
