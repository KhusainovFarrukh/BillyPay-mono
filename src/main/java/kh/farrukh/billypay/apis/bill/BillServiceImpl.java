package kh.farrukh.billypay.apis.bill;

import kh.farrukh.billypay.apis.user.UserRepository;
import kh.farrukh.billypay.global.exception.custom.exceptions.BadRequestException;
import kh.farrukh.billypay.global.exception.custom.exceptions.DuplicateResourceException;
import kh.farrukh.billypay.global.exception.custom.exceptions.NotEnoughPermissionException;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import kh.farrukh.billypay.global.paging.PagingResponse;
import kh.farrukh.billypay.security.utils.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static kh.farrukh.billypay.global.checkers.Checkers.checkPageNumber;
import static kh.farrukh.billypay.global.checkers.Checkers.checkUserId;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final UserRepository userRepository;

    @Override
    public PagingResponse<Bill> getBills(Long ownerId, int page, int pageSize) {
        checkPageNumber(page);
        if (ownerId == null && CurrentUserUtils.isAdmin(userRepository)) {
            return new PagingResponse<>(billRepository.findAll(
                PageRequest.of(page - 1, pageSize))
            );
        } else if (ownerId != null && CurrentUserUtils.isAdminOrAuthor(ownerId, userRepository)) {
            checkUserId(userRepository, ownerId);
            return new PagingResponse<>(billRepository.findAllByOwner_Id(
                ownerId, PageRequest.of(page - 1, pageSize)
            ));
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    @Override
    public Bill getBillById(long id) {
        Bill bill = billRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Bill", "id", id)
        );
        if (!CurrentUserUtils.isAdminOrAuthor(bill.getOwner().getId(), userRepository)) {
            throw new NotEnoughPermissionException();
        }
        return bill;
    }

    @Override
    public Bill addBill(BillDTO billDto) {
        if (billDto.getOwnerId() == null) {
            throw new BadRequestException("Owner ID");
        }
        if (!CurrentUserUtils.isAdminOrAuthor(billDto.getOwnerId(), userRepository)) {
            throw new NotEnoughPermissionException();
        }
        if (billRepository.existsByAccountNumber(billDto.getAccountNumber())) {
            throw new DuplicateResourceException("Bill", "account number", billDto.getAccountNumber());
        }
        return billRepository.save(new Bill(billDto, userRepository));
    }

    @Override
    public Bill updateBill(long id, BillDTO billDto) {
        Bill existingBill = billRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Bill", "id", id)
        );

        if (!CurrentUserUtils.isAdminOrAuthor(existingBill.getOwner().getId(), userRepository)) {
            throw new NotEnoughPermissionException();
        }

        if (!billDto.getAccountNumber().equals(existingBill.getAccountNumber()) &&
            billRepository.existsByAccountNumber(billDto.getAccountNumber())) {
            throw new DuplicateResourceException("Bill", "account number", billDto.getAccountNumber());
        }

        existingBill.setAddress(billDto.getAddress());
        existingBill.setAccountNumber(billDto.getAccountNumber());
        existingBill.setType(billDto.getType());
        existingBill.setPrice(billDto.getPrice());

        return billRepository.save(existingBill);
    }

    @Override
    public void deleteBillById(long id) {
        Bill bill = billRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Bill", "id", id)
        );

        if (!CurrentUserUtils.isAdminOrAuthor(bill.getOwner().getId(), userRepository)) {
            throw new NotEnoughPermissionException();
        }

        billRepository.deleteById(id);
    }
}
