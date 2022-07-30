import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static io.restassured.RestAssured.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.class)
public class RestAssured {

    Response response;
    Faker FAKER;
    JSONObject jsonObject;
    FakeValuesService fakeValuesService;

    public RestAssured(){
        baseURI = "https://petstore.swagger.io/v2/";
        this.FAKER = new Faker();
        this.jsonObject = new JSONObject();
        this.fakeValuesService =  new FakeValuesService(
                new Locale("en-GB"), new RandomService());
    }

    // Store classımdaki dataları referanslandırdım ve faker kütüphanesiyle fake data üretimi sağlandı.
    // generateOrder bana post işlemlerimde data üretimi sağlar.
    public Store generateOrder(){
        int id = 5858;
        int petId = FAKER.number().randomDigitNotZero();
        int quantity = FAKER.number().randomDigit();
        String shipDate = fakeValuesService.bothify("####-##-29T13:##:##.####");
        String status = FAKER.witcher().character();
        boolean complete = true;
        return new Store(id,petId,quantity,shipDate,status,complete);
    }

    // Post requesti atarak yeni bir order oluşturdum. Dataları generateOrder ve Store classı arasındaki köprüden çektim.
    // log().all() özelliği sayesinde print işlemi yapmama gerek kalmaz , request atmadan önceki ve sonraki bütün bilgileri
    // görebilirim. Sunucunun bana 200 döndürmesini beklerim.
    @Order(1)
    @Test
    public void postRequestStore(){

        Map<String,Object> storeData = new HashMap<>();
        storeData.put("id",generateOrder().getId());
        storeData.put("petId",generateOrder().getPetId());
        storeData.put("quantity",generateOrder().getQuantity());
        storeData.put("shipDate",generateOrder().getShipDate());
        storeData.put("status",generateOrder().getStatus());
        storeData.put("complete",generateOrder().isComplete());

         this.response =
                given().
                        log().all().
                        accept(ContentType.JSON).
                        header("Content-Type","application/json").
                        header("Connection","keep-alive").
                        body(jsonObject.toJSONString(storeData)).
                when().
                        post("store/order");


        this.response.
                then().
                        log().all().
                        statusCode(200);

        Assert.assertEquals(200,response.getStatusCode());
        Assert.assertEquals("HTTP/1.1 200 OK",response.getStatusLine());

    }

    // Post attığım data endpointe düştü mü? Bunun kontrolü için EP'ye get requesti atarım.
    // log().all() sayesinde get attığım EP'deki body'i json formatında ekrana basabilirim. 200 Dönmesini beklerim.
    @Order(2)
    @Test
    public void getRequest(){
         this.response =
                given().
                        log().all().
                        accept(ContentType.JSON).
                        header("Content-Type","application/json").
                        header("Connection","keep-alive").
                        param("id",5858).
                        param("complete",true).
                        get("store/order/"+generateOrder().getId());


            this.response.
                then().
                        log().all().
                        statusCode(200);

            Assert.assertEquals(200,response.getStatusCode());
            Assert.assertEquals("HTTP/1.1 200 OK",response.getStatusLine());
    }


    // Order oluşturuldu ve get EP'si ile order kontrol edildi. Ama şimdi envanterde sipariş sonrası
    // Azalma var mı o kontrol edildi , envantere get atarak 200 döndürmesini bekledim ve ekrana body yazdırıldı
    @Order(3)
    @Test
    public void getInventoryAfterPost(){
        this.response =
                given().
                        log().all().
                        accept(ContentType.JSON).
                        header("Content-Type","application/json").
                        header("Connection","keep-alive").
                        get("store/inventory");
        this.response.
                then().
                        log().all().
                        statusCode(200);

        Assert.assertEquals(200,response.getStatusCode());
        Assert.assertEquals("HTTP/1.1 200 OK",response.getStatusLine());

    }

    // Oluşturduğum orderı EP'ye delete requesti atarak silmek istiyorum. Delete işlemi apide doğru çalışıyorsa
    // 200 döndürmesini beklerim.
    @Order(4)
    @Test
    public void deleteRequest(){
        this.response =
                given().
                        log().all().
                        accept(ContentType.JSON).
                        header("Content-Type","application/json").
                        header("Connection","keep-alive").
                        delete("store/order/"+generateOrder().getId());

        this.response.
                then().
                        log().all().
                        statusCode(200);

        Assert.assertEquals(200,response.getStatusCode());
        Assert.assertEquals("HTTP/1.1 200 OK",response.getStatusLine());
    }

    // Delete işlemi yapıldıktan sonra envantere tekrar get requesti atarak silinen order için
    // envanterde ürün sayısı tekrar artmış mı onu kontrol ederim. 200 dönmesini beklerim.
    @Order(5)
    @Test
    public void afterDeleteGetOrder(){
        this.response =
                given().
                        log().all().
                        accept(ContentType.JSON).
                        header("Content-Type","application/json").
                        header("Connection","keep-alive").
                        get("store/inventory");
        this.response.
                then().
                log().all().
                statusCode(200);

        Assert.assertEquals(200,response.getStatusCode());
        Assert.assertEquals("HTTP/1.1 200 OK",response.getStatusLine());
    }
}
