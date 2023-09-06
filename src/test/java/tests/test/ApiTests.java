package tests.test;

import io.qameta.allure.Allure;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class ApiTests {

    private static final String ACCESS_CODE = "28dbac30b8a82c1031fd52d21133b7bd";
    private static final String CITY = "London";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH");

    @Test
    public void firstTest() {
        Response response = given()
                .queryParam("access_key", ACCESS_CODE)
                .queryParam("query", CITY)
                .when().get("http://api.weatherstack.com/current")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchema(this.getClass().getResource("/jsonSchema.json")))
                .extract()
                .response();
        JsonPath jsonResponse = response.jsonPath();
        assertThat(jsonResponse.get("request.type"), containsString("City"));
        assertThat(jsonResponse.get("request.query"), containsString(CITY));
        assertThat(jsonResponse.get("request.language"), containsString("en"));
        assertThat(jsonResponse.get("request.unit"), containsString("m"));
        Date date = new Date();
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone(jsonResponse.get("location.timezone_id").toString()));
        assertThat(jsonResponse.get("location.localtime"), containsString(DATE_FORMAT.format(date)));
    }

    @Test
    public void negativeTest1() {
        String url = "https://api.weatherstack.com/current";
        Allure.addAttachment("Негативный запрос 1", url);
        Response response = given()
                .when().get(url)
                .then()
                .log().all()
                .body(JsonSchemaValidator.matchesJsonSchema(this.getClass().getResource("/jsonSchema.json")))
                .extract()
                .response();
        assertThat(response.jsonPath().get("error.code"), equalTo(101));
        Allure.addAttachment("Ответ", response.body().prettyPrint());
    }

    @Test
    public void negativeTest2() {
        Response response = given()
                .queryParam("access_key", ACCESS_CODE)
                .queryParam("query", CITY)
                .when().get("https://api.weatherstack.com/current?access_key=28dbac30b8a82c1031fd52d21133b7bd&query=London")
                .then()
                .log().all()
                .body(JsonSchemaValidator.matchesJsonSchema(this.getClass().getResource("/jsonSchema.json")))
                .extract()
                .response();
        assertThat(response.jsonPath().get("error.code"), equalTo(105));
        Allure.addAttachment("Ответ", response.body().prettyPrint());
    }

    @Test
    public void negativeTest3() {
        Response response = given()
                .queryParam("access_key", ACCESS_CODE)
                .queryParam("query", CITY)
                .when().get("http://api.weatherstack.com/historical?access_key=28dbac30b8a82c1031fd52d21133b7bd&query=New%20York&historical_date=2014-01-21")
                .then()
                .log().all()
                .body(JsonSchemaValidator.matchesJsonSchema(this.getClass().getResource("/jsonSchema.json")))
                .extract()
                .response();
        assertThat(response.jsonPath().get("error.code"), equalTo(603));
        Allure.addAttachment("Ответ", response.body().prettyPrint());
    }

    @Test
    public void negativeTest4() {
        Response response = given()
                .queryParam("access_key", ACCESS_CODE)
                .queryParam("query", CITY)
                .when().get("http://api.weatherstack.com/current?access_key=28dbac30b8a82c1031fd52d21133b7bd&query=Kitezh")
                .then()
                .log().all()
                .body(JsonSchemaValidator.matchesJsonSchema(this.getClass().getResource("/jsonSchema.json")))
                .extract()
                .response();
        assertThat(response.jsonPath().get("error.code"), equalTo(615));
        Allure.addAttachment("Ответ", response.body().prettyPrint());
    }
}
