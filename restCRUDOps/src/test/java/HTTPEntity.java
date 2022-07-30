import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;

import static io.restassured.RestAssured.baseURI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.class)
public class HTTPEntity {
    Store store;
    Faker FAKER = new Faker();
    HttpHeaders headers;
    RestTemplate restTemplate;
    ObjectMapper objectMapper;
    FakeValuesService fakeValuesService;

    // Yapıcımı oluşturdum tanımlama ve bütün newleme işlemlerini içinde gerçekleştirdim.
    public HTTPEntity(){
        baseURI = "https://petstore.swagger.io/v2/";
        this.headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        this.restTemplate = new RestTemplate();
        this.store = new Store();
        this.objectMapper = new ObjectMapper();
        this.fakeValuesService = new FakeValuesService
                (new Locale("en-GB"), new RandomService());
    }

    // Store classımdaki dataları referanslandırdım ve faker kütüphanesiyle fake data üretimi sağlandı.
    // generateOrder bana post işlemlerimde data üretimi sağlar.
    public Store generateOrder(){
        int id = 5858;
        int petId = this.FAKER.number().randomDigitNotZero();
        int quantity = this.FAKER.number().randomDigit();
        String shipDate = this.fakeValuesService.bothify("####-##-29T13:##:##.####");
        String status = this.FAKER.witcher().character();
        boolean complete = true;
        return new Store(id,petId,quantity,shipDate,status,complete);
    }

    // Store sınıfımdaki datalarımı generateOrderdaki datalarımla set işlemi yaparım ve daha sonra
    // HttpEntityden yaptığım post işlemini bir string referans tutucuya atarak , referans tutucumu Json formatına çeviririm
    // daha sonra assert işlemi gerçekleştirilir. POST request başarılısıyla 200 döner.
    @Order(1)
    @PostMapping
    @Test
    public void postRequestStore() throws JsonProcessingException {

        this.store.setId(generateOrder().getId());
        this.store.setPetId(generateOrder().getPetId());
        this.store.setQuantity(generateOrder().getQuantity());
        this.store.setShipDate(generateOrder().getShipDate());
        this.store.setStatus(generateOrder().getStatus());
        this.store.setComplete(generateOrder().isComplete());

        HttpEntity<Store> request =
                new HttpEntity<Store>(this.store, this.headers);

        String postResultAsJson =
                restTemplate.postForObject
                        (baseURI+"store/order/",
                        request,
            String.class,
            HttpMethod.POST);

        JsonNode root = this.objectMapper.readTree(postResultAsJson);

        System.out.println("Response Body is :\t"+postResultAsJson);
        assertNotNull(root.path("message").asText(),"5858");
    }

    // POST atarak oluşturduğum orderın End Point'ine get atarak POST işlemi doğru yapılmış mı test ederim.
    // Referans tutucu olayları POST'taki ile aynı şekilde.
    @Order(2)
    @GetMapping
    @Test
    public void getRequest() throws JsonProcessingException {

        String getResultAsJson =
                restTemplate.getForObject
                        (baseURI+"store/order/"+generateOrder().getId(),
                String.class,
                HttpMethod.GET);

        JsonNode root = this.objectMapper.readTree(getResultAsJson);

        System.out.println("Response Body is :\t"+getResultAsJson);
        assertNotNull(root.path("message").asText(),"5858");
    }

    // POST ile order oluşturdum ve GET ile kontrol de ettim ama şimdi sırada sipariş atıldıktan sonra envanterde
    // ürün azalmış mı kontrolü yapılır.
    @Order(3)
    @GetMapping
    @Test
    public void getInventoryAfterPost() throws JsonProcessingException {

        String getInventoryResultsAfterPost =
                restTemplate.getForObject
                        (baseURI+"store/inventory",
                                String.class,
                                HttpMethod.GET);

        JsonNode root = this.objectMapper.readTree(getInventoryResultsAfterPost);

        System.out.println("Request Body is :\t"+getInventoryResultsAfterPost);
        assertNotNull(root);
    }

    // EP'me Delete requesti atarak oluşturduğum orderı silerim ve apide başarılı şekilde DELETE işlemi yapabildiğimi de test
    // etmiş olurum. 200 dönmesini beklerim.
    @Order(4)
    @DeleteMapping
    @Test
    public void deleteRequest() throws JsonProcessingException {

        ResponseEntity<String> deleteResultAsJson =
                restTemplate.exchange
        (baseURI+"store/order/"+generateOrder().getId(),
                HttpMethod.DELETE,
                new HttpEntity<String>("",this.headers),
                String.class);

        assertEquals(200,deleteResultAsJson.getStatusCodeValue());
        assertNotNull(deleteResultAsJson.getStatusCode(),"200 OK");
    }

    // Orderımı DELETE requestiyle sildim , şimdi sırada envanterde iptal edilen order için ürün sayısı tekrar eski
    //haline dönmüş mü kontrolü yapılır.
    @Order(5)
    @GetMapping
    @Test
    public void afterDeleteGetOrder() throws JsonProcessingException {
        String getInventoryResultsAfterDelete =
                restTemplate.getForObject
                        (baseURI+"store/inventory",
                                String.class,
                                HttpMethod.GET);

        JsonNode root = this.objectMapper.readTree(getInventoryResultsAfterDelete);

        System.out.println("Response Body is :\t"+getInventoryResultsAfterDelete);
        assertNotNull(root);
    }
}
