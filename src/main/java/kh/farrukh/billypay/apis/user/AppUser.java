package kh.farrukh.billypay.apis.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.billypay.apis.image.Image;
import kh.farrukh.billypay.apis.image.ImageRepository;
import kh.farrukh.billypay.global.base.entities.EntityWithId;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

import static kh.farrukh.billypay.apis.user.Constants.SEQUENCE_NAME_USER_ID;
import static kh.farrukh.billypay.apis.user.Constants.TABLE_NAME_USER;
import static kh.farrukh.billypay.global.base.entities.EntityWithId.GENERATOR_NAME;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"id", "name", "role", "image", "email", "username"})
@Entity
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_USER_ID)
@Table(name = TABLE_NAME_USER,
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_app_user_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_app_user_phone_number", columnNames = "phone_number")
    })
public class AppUser extends EntityWithId implements UserDetails {

    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "phone_number", nullable = false)
    @JsonProperty("phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @JsonProperty("is_enabled")
    // TODO: 6/7/22 set default to false and implement phone verification OTP
    private boolean isEnabled = true;

    @Column
    @JsonProperty("is_locked")
    private boolean isLocked = false;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @ManyToOne
    @JoinColumn(
        name = "image_id",
        foreignKey = @ForeignKey(name = "fk_image_id_of_app_user")
    )
    private Image image;

    public AppUser(AppUserDTO appUserDto, ImageRepository imageRepository) {
        this.name = appUserDto.getName();
        this.email = appUserDto.getEmail();
        this.password = appUserDto.getPassword();
        this.role = appUserDto.getRole();
        this.image = imageRepository.findById(appUserDto.getImageId()).orElseThrow(
            () -> new ResourceNotFoundException("Image", "id", appUserDto.getImageId())
        );
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return phoneNumber;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}