import org.testng.annotations.Test;

public class LoginUser extends Base{

    @Test(priority = 3)
    public void getLogin(){
        REQUEST.log().all().
                param("username",getRequest().get("username")).
                param("password",getRequest().get("password"))
                .get("/user/login").then().statusCode(200);
    }

}
