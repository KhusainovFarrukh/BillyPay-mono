package kh.farrukh.billypay.apis.bill;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Page<Bill> findAllByOwner_Id(long ownerId, Pageable pageable);

    boolean existsByAccountNumber(String accountNumber);
}
