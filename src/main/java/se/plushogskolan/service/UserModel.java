package se.plushogskolan.service;

public class UserModel {
  private String username;
  private String password;

  UserModel(String user, String password) {
    this.username = user;
    this.password = password;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
