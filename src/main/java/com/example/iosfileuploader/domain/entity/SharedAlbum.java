package com.example.iosfileuploader.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "shared_album")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SharedAlbum extends AbstractEntity {

    @NonNull
    String albumId;
    Boolean albumEnabled;
}
