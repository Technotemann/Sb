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
import static org.hamcrest.Matchers.equalTo;


public class StepDefinitions {
    final private String userDirectoryPath = System.getProperty("user.dir");
    final private HashMap<String, Response> responseHashMap = new HashMap<>();

    @Given("запросить погоду в городе '(.*)'")
    public void getWeatherInfo(String city) {
        Allure.addAttachment("Запрос информации по погоде в городе", city);
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
        Allure.addAttachment("Запрос успешен", response.toString());
    }

    @Then("валидировать результаты последнего запроса погоды в городе '(.*)'")
    public void validateWeatherInfo(String city) {
        JsonPath jsonResponse = responseHashMap.get(city).jsonPath();
        Allure.addAttachment(jsonResponse.get("request.type"), "City");
        Allure.addAttachment(jsonResponse.get("request.query"), city);
        Allure.addAttachment(jsonResponse.get("request.language"), "en");
        Allure.addAttachment(jsonResponse.get("request.unit"), "m");
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
        dateFormat.setTimeZone(TimeZone.getTimeZone(jsonResponse.get("location.timezone_id").toString()));
        Allure.addAttachment(jsonResponse.get("location.localtime"), dateFormat.format(date));
    }

    @Then("сравнить значение узла '(.*)' последнего результата запроса погоды в городе '(.*)' с параметром '(.*)'")
    public void validateSingleValue(String path, String city, String value) {
        JsonPath jsonResponse = responseHashMap.get(city).jsonPath();
        Allure.addAttachment(jsonResponse.get(path), value);
        Allure.addAttachment("test", "value");
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
