package kh.farrukh.billypay.apis.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String name;
    private String email;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String password;
    @JsonProperty("image_id")
    private Long imageId;
}
