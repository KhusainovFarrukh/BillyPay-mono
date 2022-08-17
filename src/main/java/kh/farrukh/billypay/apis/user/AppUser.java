package kh.farrukh.billypay.apis.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import kh.farrukh.billypay.apis.auth.SignUpRequest;
import kh.farrukh.billypay.apis.bill.Bill;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    uniqueConstraints = @UniqueConstraint(name = "uk_app_user_phone_number", columnNames = "phone_number"))
@NamedEntityGraph(name = "app_user_with_bills", attributeNodes = @NamedAttributeNode(value = "bills"))
public class AppUser extends EntityWithId implements UserDetails {

    @Column(nullable = false)
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

    @JsonIgnoreProperties("owner")
    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private List<Bill> bills = new ArrayList<>();

    public AppUser(SignUpRequest signUpRequest, PasswordEncoder passwordEncoder, ImageRepository imageRepository) {
        this.name = signUpRequest.getName();
        this.email = signUpRequest.getEmail();
        this.phoneNumber = signUpRequest.getPhoneNumber();
        this.password = passwordEncoder.encode(signUpRequest.getPassword());
        this.role = UserRole.USER;
        this.image = imageRepository.findById(signUpRequest.getImageId()).orElseThrow(
            () -> new ResourceNotFoundException("Image", "id", signUpRequest.getImageId())
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
