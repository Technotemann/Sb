package tests.weather;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Allure;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class StepDefinitions {

    private final HashMap<String, Response> responseHashMap = new HashMap<>();

    @Given("запросить погоду в городе {string}")
    public void getWeatherInfo(String city) {
        Allure.addAttachment("Запрос информации по погоде в городе", city);
        String accessCode = "28dbac30b8a82c1031fd52d21133b7bd";
        Response response = given()
                .queryParam("access_key", accessCode)
                .queryParam("query", city)
                .when().get("http://api.weatherstack.com/current")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(this.getClass().getResource("/jsonSchema.json")))
                .extract()
                .response();
        responseHashMap.put(city, response);
        Allure.addAttachment("Запрос успешен", response.body().prettyPrint());
    }

    @Then("проверить начальные значения результата последнего запроса погоды в городе {string}")
    public void validateWeatherInfo(String city) {
        JsonPath jsonResponse = responseHashMap.get(city).jsonPath();
        Allure.addAttachment("Ожидаемое значение request.type: City", jsonResponse.getString("request.type"));
        Allure.addAttachment("Ожидаемое значение request.query: " + city, jsonResponse.getString("request.query"));
        Allure.addAttachment("Ожидаемое значение request.language: en", jsonResponse.getString("request.language"));
        Allure.addAttachment("Ожидаемое значение request.unit: m", jsonResponse.getString("request.unit"));
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone(jsonResponse.getString("location.timezone_id")));
        Allure.addAttachment("Ожидаемое значение location.localtime: " + dateFormat.format(date), jsonResponse.getString("location.localtime"));
    }

    @Then("сравнить значение узла {string} последнего результата запроса погоды в городе {string} с параметром {string}")
    public void validateSingleValue(String path, String city, String value) {
        JsonPath jsonResponse = responseHashMap.get(city).jsonPath();
        Allure.addAttachment("Ожидаемое значение " + path + ": " + value, jsonResponse.getString(path));
        System.out.println(path + ": " + value.equals(jsonResponse.getString(path)));
    }

    @Then("выполнить запрос {string} с ожиданием кода ответа {int}")
    public void validateResponseCode(String request, int responseCode) {
        Response response = given()
                .when().get(request)
                .then()
                .log().all()
                .body(JsonSchemaValidator.matchesJsonSchema(this.getClass().getResource("/jsonSchema.json")))
                .extract()
                .response();
        assertThat(response.jsonPath().get("error.code"), equalTo(responseCode));
    }
}
