import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;

public class UpdateUser extends LoginUser{

    public User putGenerator(){
        String username = FAKER.name().username();
        String firstName = FAKER.name().name();
        String lastName = FAKER.name().lastName();
        String email = FAKER.internet().emailAddress();
        String password = FAKER.internet().password();
        String phone = FAKER.phoneNumber().phoneNumber();
        int userStatus = FAKER.number().randomDigit();
        return new User(username,firstName,lastName,email,password,phone,userStatus);
    }

    @Test(priority = 4)
    public void put(){

        getRequest().put("firstName",putGenerator().getUsername());
        getRequest().put("lastName",putGenerator().getLastName());
        getRequest().put("email",putGenerator().getEmail());


                REQUEST.log().all().
                        body(getRequest().toJSONString()).
                when().
                        put("/user/"+getRequest().get("username")).
                then().
                        log().all().
                        statusCode(200);

    }
}
