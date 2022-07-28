import com.beust.jcommander.Parameterized;
import org.testng.annotations.Test;

public class Get extends Base{

    @Test(priority = 2)
    public void get(){

        REQUEST.
                get("/user/"+getRequest().get("username")).
                then().
                statusCode(200);
    }

}
