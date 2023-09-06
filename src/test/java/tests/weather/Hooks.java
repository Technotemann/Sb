package tests.weather;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class Hooks {

    @Before
    public void getScenarioInfo(Scenario scenario) {
        System.out.println("____________________________");
        System.out.println(scenario.getName());
        System.out.println(scenario.getUri());
        System.out.println("____________________________");
    }
}
