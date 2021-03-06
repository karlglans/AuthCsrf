package login;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.remote.Augmenter;
import se.plushogskolan.AuthenticationServer;
import se.plushogskolan.service.JwtHelper;

import java.util.concurrent.TimeUnit;

public class Steps {
  WebDriver dr;
  // chrome driver should be a manually added path to a Webdriver for chrome.
  // https://chromedriver.chromium.org/downloads
  final String driverPath = System.getenv("CHROME_DRIVER");
  long lastHttpResponse = 0;

  @Given("navigate to start page")
  public void navigate_to_Gmail_page() {
    System.setProperty("webdriver.chrome.driver", driverPath);
    dr = new ChromeDriver();
    dr.get("http://localhost:7000");
  }

  @Given("navigate to google")
  public void navigate_to_Google() {
    System.setProperty("webdriver.chrome.driver", driverPath);
    dr = new ChromeDriver();
    dr.get("http://www.google.com");
  }

  @Before()
  public void startApp() {
    String[] args = {};
    AuthenticationServer.main(args);
  }

  @After()
  public void closeApp() {
    AuthenticationServer.shoutDown();
  }

  @And("close browser")
  public void closeBrowser() {
    if (dr != null) dr.close();
  }

  @When("^user logged in using username as \"(.*)\" and password as \"(.*)\"$")
  public void user_logged_in_using_username_as_userA_and_password_as_password(String username, String password) {
    dr.findElement(By.xpath("//input[@name='username']")).sendKeys(username);
    dr.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
    dr.findElement(By.xpath("//*[@id='signIn']")).click();
    dr.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
  }

  @When("^user logged in as \"(.*)\" and password as \"(.*)\" and clicking stay$")
  public void loginAndClickStay(String username, String password) {
    dr.findElement(By.xpath("//input[@name='username']")).sendKeys(username);
    dr.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
    dr.findElement(By.xpath("//input[@name='stay']")).click();
    dr.findElement(By.xpath("//*[@id='signIn']")).click();
    dr.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
  }

  @And("cookie stay should be present")
  public void verifyTokenStayIsPresent() {
    var stay = dr.manage().getCookieNamed("stay");
    Assert.assertNotNull(stay);
  }

  @And("cookie stay should not be present")
  public void verifyTokenStayIsNotPresent() {
    var stay = dr.manage().getCookieNamed("stay");
    Assert.assertNull(stay);
  }

  @And("adding valid logged in token")
  public void addingValidLogedInToken() {
    JwtHelper jwtHelper = new JwtHelper("aaa", "plusshogskolan");
    String token = jwtHelper.makeToken("Brad");
    Cookie stayCookie = new Cookie("stay", token);
    dr.manage().addCookie(stayCookie);
  }

  @Then("^user should see title \"(.*)\" on page")
  public void verifyTitle(String expectedPageTitle){
    String actualTitle = dr.getTitle();
    Assert.assertEquals(expectedPageTitle, actualTitle);
  }

  @And("csrf should be set in sessionStorage")
  public void verifyCsrfTokenInSessionStorage() {
    WebStorage webStorage = (WebStorage) new Augmenter().augment(dr);
    String csrf = webStorage.getSessionStorage().getItem("csrf");
    Assert.assertNotNull(csrf);
    Assert.assertTrue(csrf.length() > 5);
  }

  @When("refresh page")
  public void refreshTab() {
    dr.navigate().refresh();
  }

  @When("posting delete account")
  public void postingDeleteAccount() {
    JavascriptExecutor js = (JavascriptExecutor) dr;
//    dr.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
    var resp = js.executeAsyncScript(
        "var callback = arguments[arguments.length - 1];" +
        "fetch('http://localhost:7000/delete-account', { method: 'POST'})" +
        ".then(response => callback(response.status));"
    );

    lastHttpResponse = (long) resp;
  }

  @Then("http response should be 403")
  public void response403Forbidden() {
    Assert.assertEquals(403, lastHttpResponse);
  }
}
