package kh.farrukh.billypay.apis.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    @EntityGraph(value = "app_user_with_bills", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select appUser from AppUser appUser where appUser.id = :id")
    Optional<AppUser> findByIdWithBills(Long id);

    @EntityGraph(value = "app_user_with_bills", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select appUser from AppUser appUser")
    List<AppUser> findAllWithBills();

    @EntityGraph(value = "app_user_with_bills", type = EntityGraph.EntityGraphType.LOAD)
    @Query("select appUser from AppUser appUser")
    Page<AppUser> findAllWithBills(Pageable pageable);
}
