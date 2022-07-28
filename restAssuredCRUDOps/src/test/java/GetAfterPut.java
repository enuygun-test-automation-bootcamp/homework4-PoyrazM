import org.json.simple.JSONObject;
import org.testng.annotations.Test;

public class GetAfterPut extends Base{


    @Test(priority = 5)
    public void getRequestAfterPut(){
        REQUEST.
                get("/user/"+getRequest().get("username")).
                then().
                statusCode(200);
    }


}
