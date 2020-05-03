package login;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, features = "classpath:login/sample.feature", glue = "login") // glue = {"src/test/java/StepDefinitions"
public class RunCucumberTest{
}