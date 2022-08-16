package kh.farrukh.billypay.apis.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.billypay.apis.user.AppUser;
import kh.farrukh.billypay.apis.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"user", "access_token", "access_token_expires", "refresh_token", "refresh_token_expires", "role"})
public class AuthResponse {

    private UserRole role;
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("access_token_expires")
    private String accessTokenExpires;
    @JsonProperty("refresh_token_expires")
    private String refreshTokenExpires;
    private AppUser user;

    public AuthResponse(UserRole role, String accessToken, String refreshToken, String accessTokenExpires, String refreshTokenExpires) {
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpires = accessTokenExpires;
        this.refreshTokenExpires = refreshTokenExpires;
    }
}
