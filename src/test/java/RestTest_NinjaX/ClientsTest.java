package RestTest_NinjaX;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ClientsTest {

    RestAssured xRA;
    RequestSpecification httpRequest;
    Response response;


    @BeforeMethod
    public void setUp() {
        xRA = new RestAssured();
        xRA.baseURI = "http://79.137.68.21/api/v1/clients";
        httpRequest = xRA.given();
        httpRequest.header("Content-Type", "application/json").and().header("X-Ninja-Token", "pwamm76crdewrh9ldxssghfcebjdw2pl");
    }

    @AfterMethod
    public void tearDown() {
        xRA = null;
        httpRequest = null;
        response = null;
    }

    @Test
    public void testClient80id() {
        /*response = httpRequest.get("/80");
        String bodyAsString = response.getBody().asString();
        Assert.assertTrue(bodyAsString.contains("\"id\": 80"));
        System.out.println(bodyAsString);*/

        String bodyAsString = httpRequest.get("/80").getBody().asString();
        Assert.assertTrue(bodyAsString.contains("\"id\": 80"));
        System.out.println(bodyAsString);

        /* //Albo jedna linijka
        Assert.assertTrue(
                httpRequest.get("/80").getBody().asString().contains("\"id\": 80")
        );*/
    }

    //BEST WAY TO WRITE TEST in my opinion
    @Test
    public void testGetAllClients(){
        response = httpRequest
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response();

        System.out.println(response.getBody().asString());

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);
        //JsonObject data = jsonObject.get("data").getAsJsonObject();
        JsonArray data = jsonObject.get("data").getAsJsonArray();
        List<JsonElement> jsonList = new LinkedList<>();
        for( int i = 0; i < data.size() ; i++){
            jsonList.add(data.get(i));
        }

        System.out.println("count of clients: " +  data.size());
        //System.out.println(data.toString());

    }

    @Test
    public void testClient80idWithOutPreSet() {
        given()
                .header("Content-Type", "application/json")
                .and()
                .header("X-Ninja-Token", "pwamm76crdewrh9ldxssghfcebjdw2pl")
        .expect()
                .body("data.id", equalTo(80))
                .when().get("http://79.137.68.21/api/v1/clients/80")
        .print();
    }

    @Test
    public void testAllClientsLoading() {
        given()
                .header("Content-Type", "application/json")
                .and()
                .header("X-Ninja-Token", "pwamm76crdewrh9ldxssghfcebjdw2pl").
        when()
                .get("http://79.137.68.21/api/v1/clients").
        then().assertThat().statusCode(200).extract().response().print();
    }

    @Test
    public void testClientPost(){
        String nameOfClient = "JavaRestAsuredTest00";
        String cityOfClient = "Krakow";

        JsonObject jsonClient = new JsonObject();
        jsonClient.addProperty("name", nameOfClient);
        jsonClient.addProperty("city", cityOfClient);

        response = httpRequest.body(jsonClient.toString())
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .body("data.name", equalTo(nameOfClient))
                .body("data.city", equalTo(cityOfClient))
                .extract()
                .response();

        response.print();

    }

    @Test
    public void testClientDelete(){
        String numberOfClientToDelete = "93";
        response = httpRequest
                .delete("/" + numberOfClientToDelete)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response();
        response.print();
    }

    @DataProvider(name="clientsToGet")
    public Object[][] createTestDataRecords() {
        return new Object[][] {
                {80},
                {85},
                {90},
                {93}
        };
    }

    @Test(dataProvider="clientsToGet")
    public void testGetClientsById(int clientId) {
        String clientUri = "/" + String.valueOf(clientId);
        response = httpRequest
                .get(clientUri)
                .then()
                .assertThat()
                .statusCode(200)
                .assertThat()
                .body("data.id", equalTo(clientId))
                .extract()
                .response();

        response.print();

    }

}