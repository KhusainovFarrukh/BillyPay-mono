package kh.farrukh.billypay.apis.bill;

import kh.farrukh.billypay.apis.user.UserRepository;
import kh.farrukh.billypay.global.exception.custom.exceptions.NotEnoughPermissionException;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import kh.farrukh.billypay.global.paging.PagingResponse;
import kh.farrukh.billypay.security.utils.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static kh.farrukh.billypay.global.checkers.Checkers.*;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final UserRepository userRepository;

    @Override
    public PagingResponse<Bill> getBills(long userId, int page, int pageSize) {
        if (CurrentUserUtils.isAdminOrAuthor(userId, userRepository)) {
            checkUserId(userRepository, userId);
            checkPageNumber(page);
            return new PagingResponse<>(billRepository.findAllByOwner_Id(
                userId, PageRequest.of(page - 1, pageSize)
            ));
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    @Override
    public Bill getBillById(long userId, long id) {
        if (CurrentUserUtils.isAdminOrAuthor(userId, userRepository)) {
            checkUserId(userRepository, userId);
            return billRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Bill", "id", id)
            );
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    @Override
    public Bill addBill(long userId, BillDTO billDto) {
        if (CurrentUserUtils.isAdminOrAuthor(userId, userRepository)) {
            Bill bill = new Bill(billDto);
            bill.setOwner(userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userId))
            );
            return billRepository.save(bill);
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    @Override
    public Bill updateBill(long userId, long id, BillDTO billDto) {
        if (CurrentUserUtils.isAdminOrAuthor(userId, userRepository)) {
            checkUserId(userRepository, userId);
            Bill existingBill = billRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Bill", "id", id)
            );

            existingBill.setAddress(billDto.getAddress());
            existingBill.setAccountNumber(billDto.getAccountNumber());
            existingBill.setType(billDto.getType());
            existingBill.setPrice(billDto.getPrice());

            return billRepository.save(existingBill);
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    @Override
    public void deleteBillById(long userId, long id) {
        if (CurrentUserUtils.isAdminOrAuthor(userId, userRepository)) {
            checkUserId(userRepository, userId);
            checkBillId(billRepository, id);
            billRepository.deleteById(id);
        } else {
            throw new NotEnoughPermissionException();
        }
    }
}
