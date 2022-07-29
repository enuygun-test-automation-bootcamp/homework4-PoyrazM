import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Burada request atacağım api için lazım olan dataların keylerini topladım ve encapsulatedladım.
// Lombok librarysini kullanarak getter , setter , constructor kodlarının otomatik oluşturulması sağlandı.

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Store {
    private int id;
    private int petId;
    private int quantity;
    private String shipDate;
    private String status;
    private boolean complete;
}


