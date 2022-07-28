import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;



public class CreateUser extends Base{

    User user;


    public User generateUser(){
        String username = FAKER.name().username();
        String firstName = FAKER.name().name();
        String lastName = FAKER.name().lastName();
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password();
        String phone = FAKER.phoneNumber().phoneNumber();
        int userStatus = FAKER.number().randomDigit();
        return new User(username,firstName,lastName,email,password,phone,userStatus);
    }



    @Test(priority = 1)
    public void postEndpoint(){

        getRequest().put("username",generateUser().getUsername());
        getRequest().put("firstName",generateUser().getFirstName());
        getRequest().put("lastName",generateUser().getLastName());
        getRequest().put("email",generateUser().getEmail());
        getRequest().put("password",generateUser().getPassword());
        getRequest().put("phone",generateUser().getPhone());
        getRequest().put("userStatus",generateUser().getUserStatus());


        Response post =
                REQUEST.log().all().
                        body(getRequest().toJSONString()).
                when().
                        post("/user");

                post.
                then().
                        log().all().
                        statusCode(200);




    }
}
