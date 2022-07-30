import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UniRest {
    Faker FAKER;
    FakeValuesService fakeValuesService;

    public UniRest(){
        this.FAKER = new Faker();
        this.fakeValuesService = new FakeValuesService(
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
    @Order(1)
    @Test
    public void postRequestStore() throws UnirestException, JsonProcessingException {

        Map<String,Object> storeData = new HashMap<>();
        storeData.put("id",generateOrder().getId());
        storeData.put("petId",generateOrder().getPetId());
        storeData.put("quantity",generateOrder().getQuantity());
        storeData.put("shipDate",generateOrder().getShipDate());
        storeData.put("status",generateOrder().getStatus());
        storeData.put("complete",generateOrder().isComplete());

        // Mapper sayesinde string tanımlanan keyleri , json objelerine convertleme işlemi yapıldı.Bu sayede json
        // formatlı body sayesinde apiye post attım. Ve clientin bana 200 döndürmesini bekledim.

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(storeData);

        HttpResponse<JsonNode> response =
        Unirest
                .post("https://petstore.swagger.io/v2/store/order")
                .header("Content-Type","application/json")
                .body(jsonString)
                .asJson();

        Assert.assertEquals(200,response.getStatus());
        Assert.assertEquals(5858,response.getBody().getObject().get("id"));
    }


    // Post attığım data endpointe düştü mü? Bunun kontrolü için EP'ye get requesti atarım.
    // Bana 200 döndürmesini beklerim. UniRestte loglama özelliğini bulamadığım için println ile ekrana body bastırıldı
    @Order(2)
    @Test
    public void getRequest() throws UnirestException {
        Unirest.setTimeouts(0, 0);

        HttpResponse<JsonNode> response =
                Unirest
                        .get("https://petstore.swagger.io/v2/store/order/5858")
                        .header("accept", "application/json")
                        .asJson();

        Assert.assertEquals(200,response.getStatus());
        Assert.assertEquals(5858,response.getBody().getObject().get("id"));
        System.out.println("Response Body :\t"+response.getBody());
        System.out.println("Response Status :\t"+response.getStatus()+response.getStatusText());


    }

    // Order oluşturuldu ve get EP'si ile order kontrol edildi. Ama şimdi envanterde sipariş sonrası
    // Azalma var mı o kontrol edildi , envantere get atarak 200 döndürmesini bekledim ve ekrana body yazdırıldı
    @Order(3)
    @Test
    public void getInventoryAfterPost() throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<JsonNode> response =
                Unirest
                        .get("https://petstore.swagger.io/v2/store/inventory")
                        .asJson();

        Assert.assertEquals(200,response.getStatus());
        Assert.assertEquals(7,response.getBody().getObject().get("sold"));
        System.out.println(response.getBody());
    }

    // Oluşturduğum orderı EP'ye delete requesti atarak silmek istiyorum. Delete işlemi apide doğru çalışıyorsa
    // 200 döndürmesini beklerim.
    @Order(4)
    @Test
    public void deleteRequest() throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<JsonNode> response =
                Unirest
                        .delete("https://petstore.swagger.io/v2/store/order/5858")
                        .header("accept", "application/json")
                        .asJson();

        Assert.assertEquals(200,response.getStatus());
    }

    // Delete işlemi yapıldıktan sonra envantere tekrar get requesti atarak silinen order için
    // envanterde ürün sayısı tekrar artmış mı onu kontrol ederim. 200 dönmesini beklerim.

    @Order(5)
    @Test
    public void afterDeleteGetOrder() throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<JsonNode> response =
                Unirest
                        .get("https://petstore.swagger.io/v2/store/inventory")
                        .header("accept", "application/json")
                        .asJson();

        Assert.assertEquals(200,response.getStatus());
        Assert.assertEquals(7,response.getBody().getObject().get("sold"));
        System.out.println(response.getBody());
    }
}
