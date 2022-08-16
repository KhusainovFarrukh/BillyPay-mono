package kh.farrukh.billypay.apis.bill;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDTO {

    private String address;
    @JsonProperty("account_number")
    private String accountNumber;
    private BillType type;
    private Double price;
    @JsonProperty("owner_id")
    private Long ownerId;
}
