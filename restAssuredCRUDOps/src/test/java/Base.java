import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;


import java.io.IOException;
import java.util.Properties;

public class Base {
    public RequestSpecification REQUEST;
    public Faker FAKER = new Faker();

    public Base() {

        try {
            Properties properties = new Properties();
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));


            RestAssured.baseURI = properties.getProperty("baseUri");
        } catch (IOException exception) {
            System.out.println("Properties not configured" + exception.getMessage());
        }
        //basic request setting
        REQUEST =
                RestAssured.
                given().
                        contentType(ContentType.JSON).
                        accept(ContentType.JSON).
                        header("Content-Type","application/json");
    }
    public static JSONObject getRequest(){
        JSONObject request = new JSONObject();
        return request;
    }
}
