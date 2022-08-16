package kh.farrukh.billypay.apis.bill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.billypay.apis.user.AppUser;
import kh.farrukh.billypay.apis.user.UserRepository;
import kh.farrukh.billypay.global.base.entities.EntityWithId;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;

import static kh.farrukh.billypay.apis.bill.Constants.SEQUENCE_NAME_BILL_ID;
import static kh.farrukh.billypay.apis.bill.Constants.TABLE_NAME_BILL;
import static kh.farrukh.billypay.global.base.entities.EntityWithId.GENERATOR_NAME;

@Entity
@Table(name = TABLE_NAME_BILL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_BILL_ID)
public class Bill extends EntityWithId {

    private String address;

    @JsonProperty("account_number")
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private BillType type;

    private Double price;

    @JsonIgnoreProperties("bills")
    @ManyToOne(optional = false)
    private AppUser owner;

    public Bill(BillDTO billDTO) {
        BeanUtils.copyProperties(billDTO, this);
    }
}
