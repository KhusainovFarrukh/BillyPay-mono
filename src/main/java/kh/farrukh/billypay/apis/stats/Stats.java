package kh.farrukh.billypay.apis.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import kh.farrukh.billypay.apis.bill.Bill;
import kh.farrukh.billypay.apis.bill.BillRepository;
import kh.farrukh.billypay.global.base.entities.EntityWithId;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

import static kh.farrukh.billypay.apis.stats.Constants.SEQUENCE_NAME_STATS_ID;
import static kh.farrukh.billypay.apis.stats.Constants.TABLE_NAME_STATS;
import static kh.farrukh.billypay.global.base.entities.EntityWithId.GENERATOR_NAME;

@Entity
@Table(name = TABLE_NAME_STATS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_STATS_ID)
public class Stats extends EntityWithId {

    @NotNull
    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @NotNull
    private Double amount;

    @NotNull
    @JsonProperty("total_price")
    private Double totalPrice;

    @JsonIgnoreProperties("stats")
    @ManyToOne(optional = false)
    private Bill bill;

    public Stats(StatsDTO billDTO, BillRepository billRepository) {
        BeanUtils.copyProperties(billDTO, this);
        this.bill = billRepository.findById(billDTO.getBillId()).orElseThrow(
            () -> new ResourceNotFoundException("Bill", "id", billDTO.getBillId())
        );
        this.totalPrice = this.amount * this.bill.getPrice();
    }
}
