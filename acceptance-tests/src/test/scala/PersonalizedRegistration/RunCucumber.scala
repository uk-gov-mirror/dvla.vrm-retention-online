package PersonalizedRegistration

import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("acceptance-tests/src/test/resources/PersonalizedRegistration/"),
  glue = Array("PersonalizedRegistration.StepDefs"),
  tags = Array("@HappyPath,@UnHappyPath","~@WIP"),
  plugin = Array("pretty","html:target/cucumber-report")
)
class RunCucumber


