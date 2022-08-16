package kh.farrukh.billypay.apis.bill;

import kh.farrukh.billypay.global.paging.PagingResponse;

public interface BillService {

    PagingResponse<Bill> getBills(long userId, int pageNumber, int pageSize);

    Bill getBillById(long userId, long id);

    Bill addBill(long userId, BillDTO billDTO);

    Bill updateBill(long userId, long id, BillDTO billDTO);

    void deleteBillById(long userId, long id);
}
