package Resources;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;

public class HTTPClient {
    TestData testData;
    private final String JSON = "application/json";
    private final String baseURL = " https://stellarburgers.nomoreparties.site";
    private final String userLoginAPIMethod = "/api/auth/login";
    private final String deleteUserAPIMethod = "/api/auth/user";
    private final String registrationAPIMethod = "/api/auth/register";

    public HTTPClient(TestData testData) {
        this.testData = testData;
    }

    @Step("Delete test user")
    public void doDeleteUserRequest() {
        String body = String.format("{%n\"email\": \"%s\",%n\"password\": \"%s\"%n}", testData.getTestEmail(), testData.getTestPassword());
        String requestBody = given().header("Content-type", JSON).and().body(body)
                .when().post(baseURL + userLoginAPIMethod).getBody().asString();
        JsonPath jp = new JsonPath(requestBody);
        String token = jp.getString("accessToken").split(" ")[1];
        given().auth().oauth2(token).header("Content-type", JSON).when()
                .delete(baseURL + deleteUserAPIMethod).then().assertThat().statusCode(202);
    }

    @Step("Create test user")
    public void doCreateUserRequest() {
        given().header("Content-type", JSON).and().body(String.format("{\n" +
                                "\"email\": \"%s\",\n" +
                                "\"password\": \"%s\",\n" +
                                "\"name\": \"%s\"\n" +
                                "}\n",
                        testData.getTestEmail(), testData.getTestPassword(), testData.getTestName()))
                .when().post(baseURL + registrationAPIMethod);
    }
}

