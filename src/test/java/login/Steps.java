package login;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.html5.WebStorage;
import org.openqa.selenium.remote.Augmenter;

import java.util.concurrent.TimeUnit;

public class Steps {
  WebDriver dr;
  final String driverPath = "P:\\prog\\selenium\\chromedriver.exe";

  @Before
  public void setUp() {
    System.setProperty("webdriver.chrome.driver", driverPath);
    dr = new ChromeDriver();
  }

  @After
  public void cleanUp() {
    if (dr != null) dr.close();
  }

  @Given("navigate to start page")
  public void navigate_to_Gmail_page() {
    dr.get("http://localhost:7000");
  }

  @When("^user logged in using username as \"(.*)\" and password as \"(.*)\"$")
  public void user_logged_in_using_username_as_userA_and_password_as_password(String username, String password) {
    dr.findElement(By.xpath("//input[@name='username']")).sendKeys(username);
    dr.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
    dr.findElement(By.xpath("//*[@id='signIn']")).click();
    dr.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
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

  @Then("refresh page")
  public void refreshTab() {
    dr.navigate().refresh();
  }
}