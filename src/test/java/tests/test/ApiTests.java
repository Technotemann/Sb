package tests.test;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class ApiTests {

    String accessCode = "28dbac30b8a82c1031fd52d21133b7bd";
    List cities;
    String city = "London";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
    String userDirectoryPath = System.getProperty("user.dir");


    @Test
    public void firstTest() {
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
        JsonPath jsonResponce = response.jsonPath();
        assertThat(jsonResponce.get("request.type"), containsString("City"));
        assertThat(jsonResponce.get("request.query"), containsString(city));
        assertThat(jsonResponce.get("request.language"), containsString("en"));
        assertThat(jsonResponce.get("request.unit"), containsString("m"));
        Date date = new Date();
        dateFormat.setTimeZone(TimeZone.getTimeZone(jsonResponce.get("location.timezone_id").toString()));
        assertThat(jsonResponce.get("location.localtime"), containsString(dateFormat.format(date)));
    }

    @Test
    public void negativeTest1() {
        Response response = given()
                .when().get("https://api.weatherstack.com/current")
                .then()
                .log().all().
        body(JsonSchemaValidator.
                matchesJsonSchema(new File(userDirectoryPath + "\\src\\main\\resources\\jsonRSchema.json"))).
                extract().
                response();
                assertThat(response.jsonPath().get("error.code"), equalTo(101));
    }

    @Test
    public void negativeTest2() {
        Response response = given()
                .queryParam("access_key", accessCode)
                .queryParam("query", city)
                .when().get("https://api.weatherstack.com/current?access_key=28dbac30b8a82c1031fd52d21133b7bd&query=London")
                .then()
                .log().all().
        body(JsonSchemaValidator.
                matchesJsonSchema(new File(userDirectoryPath + "\\src\\main\\resources\\jsonRSchema.json"))).
                extract().
                response();
        assertThat(response.jsonPath().get("error.code"), equalTo(105));
    }

    @Test
    public void negativeTest3() {
        Response response = given()
                .queryParam("access_key", accessCode)
                .queryParam("query", city)
                .when().get("http://api.weatherstack.com/historical?access_key=28dbac30b8a82c1031fd52d21133b7bd&query=New%20York&historical_date=2014-01-21")
                .then()
                .log().all().
        body(JsonSchemaValidator.
                matchesJsonSchema(new File(userDirectoryPath + "\\src\\main\\resources\\jsonRSchema.json"))).
                extract().
                response();
        assertThat(response.jsonPath().get("error.code"), equalTo(603));
    }

    @Test
    public void negativeTest4() {
        Response response = given()
                .queryParam("access_key", accessCode)
                .queryParam("query", city)
                .when().get("http://api.weatherstack.com/current?access_key=28dbac30b8a82c1031fd52d21133b7bd&query=Kitezh")
                .then()
                .log().all().
                body(JsonSchemaValidator.
                        matchesJsonSchema(new File(userDirectoryPath + "\\src\\main\\resources\\jsonRSchema.json"))).
                extract().
                response();
        assertThat(response.jsonPath().get("error.code"), equalTo(615));
    }
}
