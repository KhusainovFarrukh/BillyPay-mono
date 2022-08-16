package kh.farrukh.billypay.apis.stats;

import kh.farrukh.billypay.apis.bill.Bill;
import kh.farrukh.billypay.apis.bill.BillRepository;
import kh.farrukh.billypay.apis.user.UserRepository;
import kh.farrukh.billypay.global.exception.custom.exceptions.NotEnoughPermissionException;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import kh.farrukh.billypay.global.paging.PagingResponse;
import kh.farrukh.billypay.security.utils.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static kh.farrukh.billypay.global.checkers.Checkers.checkPageNumber;
import static kh.farrukh.billypay.global.checkers.Checkers.checkStatsId;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final BillRepository billRepository;
    private final UserRepository userRepository;

    @Override
    public PagingResponse<Stats> getStatsList(Long billId, int page, int pageSize) {
        checkPageNumber(page);
        if (billId == null && CurrentUserUtils.isAdmin(userRepository)) {
            return new PagingResponse<>(statsRepository.findAll(
                PageRequest.of(page - 1, pageSize))
            );
        } else if (billId != null) {
            Bill bill = billRepository.findById(billId).orElseThrow(
                () -> new ResourceNotFoundException("Bill", "id", billId)
            );
            if (CurrentUserUtils.isAdminOrAuthor(bill.getOwner().getId(), userRepository)) {
                return new PagingResponse<>(statsRepository.findAllByBill_Id(
                    billId, PageRequest.of(page - 1, pageSize)
                ));
            } else {
                throw new NotEnoughPermissionException();
            }
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    @Override
    public Stats getStatsById(long id) {
        Stats stats = statsRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Stats", "id", id)
        );
        if (!CurrentUserUtils.isAdminOrAuthor(stats.getBill().getOwner().getId(), userRepository)) {
            throw new NotEnoughPermissionException();
        }
        return stats;
    }

    @Override
    public Stats addStats(StatsDTO statsDto) {
        Bill bill = billRepository.findById(statsDto.getBillId()).orElseThrow(
            () -> new ResourceNotFoundException("Bill", "id", statsDto.getBillId())
        );
        if (!CurrentUserUtils.isAdminOrAuthor(bill.getOwner().getId(), userRepository)) {
            throw new NotEnoughPermissionException();
        }
        return statsRepository.save(new Stats(statsDto, billRepository));
    }

    @Override
    public Stats updateStats(long id, StatsDTO statsDto) {
        Stats existingStats = statsRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Stats", "id", id)
        );

        if (!CurrentUserUtils.isAdminOrAuthor(existingStats.getBill().getOwner().getId(), userRepository)) {
            throw new NotEnoughPermissionException();
        }

        existingStats.setAmount(statsDto.getAmount());
        existingStats.setTotalPrice(existingStats.getAmount() * existingStats.getBill().getPrice());
        existingStats.setStartDate(statsDto.getStartDate());
        existingStats.setEndDate(statsDto.getEndDate());

        return statsRepository.save(existingStats);
    }

    @Override
    public void deleteStatsById(long id) {
        Stats stats = statsRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Stats", "id", id)
        );
        if (!CurrentUserUtils.isAdminOrAuthor(stats.getBill().getOwner().getId(), userRepository)) {
            throw new NotEnoughPermissionException();
        }
        statsRepository.deleteById(id);
    }
}
