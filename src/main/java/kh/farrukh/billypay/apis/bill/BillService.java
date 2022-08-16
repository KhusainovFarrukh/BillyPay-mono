package kh.farrukh.billypay.apis.bill;

import kh.farrukh.billypay.global.paging.PagingResponse;

public interface BillService {

    PagingResponse<Bill> getBills(Long ownerId, int pageNumber, int pageSize);

    Bill getBillById(long id);

    Bill addBill(BillDTO billDTO);

    Bill updateBill(long id, BillDTO billDTO);

    void deleteBillById(long id);
}
