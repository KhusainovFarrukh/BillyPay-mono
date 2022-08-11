package kh.farrukh.billypay.global.exception.custom.exceptions;

import kh.farrukh.billypay.global.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static kh.farrukh.billypay.global.exception.ExceptionMessages.EXCEPTION_NOT_ENOUGH_PERMISSION;

@Getter
public class NotEnoughPermissionException extends ApiException {

    public NotEnoughPermissionException() {
        super(
            "You don't have enough permission",
            HttpStatus.FORBIDDEN,
            EXCEPTION_NOT_ENOUGH_PERMISSION,
            new Object[]{}
        );
    }
}
