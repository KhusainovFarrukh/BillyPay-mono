package kh.farrukh.billypay.apis.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Login request user to deserialize Spring Security login endpoint request body
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignInRequest {
    @NotBlank
    @JsonProperty("phone_number")
    private String phoneNumber;
    @NotBlank
    private String password;
}
