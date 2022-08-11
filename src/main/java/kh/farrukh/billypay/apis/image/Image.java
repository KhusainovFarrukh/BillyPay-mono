package kh.farrukh.billypay.apis.image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kh.farrukh.billypay.global.base.entities.EntityWithId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static kh.farrukh.billypay.apis.image.Constants.SEQUENCE_NAME_IMAGE_ID;
import static kh.farrukh.billypay.apis.image.Constants.TABLE_NAME_IMAGE;
import static kh.farrukh.billypay.global.base.entities.EntityWithId.GENERATOR_NAME;

/**
 * Image is a simple entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = TABLE_NAME_IMAGE)
@SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME_IMAGE_ID)
public class Image extends EntityWithId {

    @JsonIgnore
    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] content;
}
