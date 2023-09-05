package tests.weather;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.qameta.allure.Allure;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


public class StepDefinitions {
    final private String userDirectoryPath = System.getProperty("user.dir");
    final private HashMap<String, Response> responseHashMap = new HashMap<>();

    @Given("запросить погоду в городе '(.*)'")
    public void getWeatherInfo(String city) {
        String accessCode = "28dbac30b8a82c1031fd52d21133b7bd";
        Response response = given()
                .queryParam("access_key", accessCode)
                .queryParam("query", city)
                .when().get("http://api.weatherstack.com/current")
                .then()
                .log().all()
                .statusCode(200).
                assertThat()
                .body(JsonSchemaValidator.
                        matchesJsonSchema(new File(userDirectoryPath + "\\src\\main\\resources\\jsonSchema.json"))).
                extract().
                response();
        responseHashMap.put(city, response);
    }

    @Then("валидировать результаты последнего запроса погоды в городе '(.*)'")
    public void validateWeatherInfo(String city) {
        JsonPath jsonResponse = responseHashMap.get(city).jsonPath();
        assertThat(jsonResponse.get("request.type"), equalTo("City"));
        assertThat(jsonResponse.get("request.query"), containsString(city));
        assertThat(jsonResponse.get("request.language"), equalTo("en"));
        assertThat(jsonResponse.get("request.unit"), equalTo("m"));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone(jsonResponse.get("location.timezone_id").toString()));
        assertThat(jsonResponse.get("location.localtime"), containsString(dateFormat.format(date)));
    }

    @Then("сравнить значение узла '(.*)' последнего результата запроса погоды в городе '(.*)' с параметром '(.*)'")
    public void validateSingleValue(String path, String city, String value) {
        JsonPath jsonResponse = responseHashMap.get(city).jsonPath();
        assertThat(jsonResponse.get(path), equalTo(value));
    }

    @Then("выполнить запрос '(.*)' с ожиданием кода ответа '(.*)'")
    public void validateResponseCode(String request, Integer responseCode) {
        Response response = given()
                .when().get(request)
                .then()
                .log().all().
                body(JsonSchemaValidator.
                matchesJsonSchema(new File(userDirectoryPath + "\\src\\main\\resources\\jsonRSchema.json"))).
                extract().
                response();
        assertThat(response.jsonPath().get("error.code"), equalTo(responseCode));
    }
}
