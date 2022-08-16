package kh.farrukh.billypay.apis.user;

import kh.farrukh.billypay.global.paging.PagingResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * A base interface for service of User endpoints
 * <p>
 * Methods implemented in UserServiceImpl
 */
public interface UserService extends UserDetailsService {

    PagingResponse<AppUser> getUsers(int page, int pageSize);

    AppUser getUserById(Long id);

    AppUser updateUser(long id, AppUserDTO appUserDto);

    void deleteUser(long id);

    AppUser setUserRole(long id, UserRoleDTO roleDto);

    AppUser setUserImage(long id, UserImageDTO imageDto);

    AppUser setUserPassword(long id, UserPasswordDTO passwordDto);
}
