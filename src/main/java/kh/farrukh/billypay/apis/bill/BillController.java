package kh.farrukh.billypay.apis.bill;

import kh.farrukh.billypay.global.paging.PagingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static kh.farrukh.billypay.apis.bill.Constants.ENDPOINT_BILL;

@RestController
@RequestMapping(ENDPOINT_BILL)
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @GetMapping
    public ResponseEntity<PagingResponse<Bill>> getBills(
        @RequestParam(name = "owner_id", required = false) Long ownerId,
        @RequestParam(name = "page", defaultValue = "1") int page,
        @RequestParam(name = "page_size", defaultValue = "10") int pageSize
    ) {
        return new ResponseEntity<>(billService.getBills(ownerId, page, pageSize), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Bill> getBillById(
        @PathVariable long id
    ) {
        return new ResponseEntity<>(billService.getBillById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Bill> addBill(
        @Valid @RequestBody BillDTO billDto
    ) {
        return new ResponseEntity<>(billService.addBill(billDto), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Bill> updateBill(
        @PathVariable long id,
        @Valid @RequestBody BillDTO billDto
    ) {
        return new ResponseEntity<>(billService.updateBill(id, billDto), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable long id) {
        billService.deleteBillById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
