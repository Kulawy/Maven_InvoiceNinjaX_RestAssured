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

    private static final String HEADER_OPTION_01 = "Content-Type";
    private static final String HEADER_OPTION_02 = "application/json";
    private static final String HEADER_VAL = "X-Ninja-Token";
    private static final String TOKEN = "pwamm76crdewrh9ldxssghfcebjdw2pl";
    private static final String ID = "/80";
    private static final int STATUS_OK = 200;


    private static final String numberOfClientToDelete = "93";

    private StringBuilder sB;
    private RestAssured xRA;
    private RequestSpecification httpRequest;
    private Response response;



    @BeforeMethod
    public void setUp() {
        xRA = new RestAssured();
        sB = new StringBuilder();
        xRA.baseURI = "http://79.137.68.21/api/v1/clients";
        httpRequest = xRA.given();
        httpRequest.header(HEADER_OPTION_01, HEADER_OPTION_02).and().header(HEADER_VAL, TOKEN);
    }

    @AfterMethod
    public void tearDown() {
        xRA = null;
        httpRequest = null;
        response = null;
    }

    @Test
    public void testClientById() {
        String bodyAsString = httpRequest.get(ID).getBody().asString();
        Assert.assertTrue(bodyAsString.contains(sB.append("\"id\": ").append(ID).toString()));
        System.out.println(bodyAsString);

    }

    @Test
    public void testGetAllClients(){
        response = httpRequest
                .get()
                .then()
                .assertThat()
                .statusCode(STATUS_OK)
                .extract()
                .response();

        System.out.println(response.getBody().asString());
        JsonArray jA = getJsonArrayFromBody();
        System.out.println(sB.append("count of clients: ").append(jA.size()).toString());
        System.out.println(jA.get(0));

    }

    private JsonArray getJsonArrayFromBody(){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(response.getBody().asString(), JsonObject.class);
        JsonArray dataJsonArray = jsonObject.get("data").getAsJsonArray();
        return dataJsonArray;
    }

    @Test
    public void testClientIdWithOutPreSet() {
        String addressToSend = "http://79.137.68.21/api/v1/clients/";

        given()
                .header(HEADER_OPTION_01, HEADER_OPTION_02)
                .and()
                .header(HEADER_VAL, TOKEN)
        .expect()
                .body("data.id", equalTo(ID))
                .when().get(sB.append(addressToSend).append(ID).toString())
        .print();
    }

    @Test
    public void testAllClientsLoading() {
        String addressToSend = "http://79.137.68.21/api/v1/clients";
        given()
                .header(HEADER_OPTION_01, HEADER_OPTION_02)
                .and()
                .header(HEADER_VAL, TOKEN).
        when()
                .get(addressToSend).
        then().assertThat().statusCode(STATUS_OK).extract().response().print();
    }

    @Test
    public void testClientPost(){
        String nameOfClient = "JavaRestAssuredTest00";
        String cityOfClient = "Krakow";
        String propName = "data.name";
        String propCity = "data.city";

        JsonObject jsonClient = new JsonObject();
        jsonClient.addProperty("name", nameOfClient);
        jsonClient.addProperty("city", cityOfClient);

        response = httpRequest.body(jsonClient.toString())
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(STATUS_OK)
                .body(propName, equalTo(nameOfClient))
                .body(propCity, equalTo(cityOfClient))
                .extract()
                .response();
        response.print();

    }

    @Test
    public void testClientDelete(){
        response = httpRequest
                .delete("/" + numberOfClientToDelete)
                .then()
                .assertThat()
                .statusCode(STATUS_OK)
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
        String valToCompare = "data.id";

        response = httpRequest
                .get(sB.append("/").append(clientId).toString())
                .then()
                .assertThat()
                .statusCode(STATUS_OK)
                .assertThat()
                .body( valToCompare, equalTo(clientId))
                .extract()
                .response();
        response.print();

    }

}