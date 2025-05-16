package com.example.iosfileuploader.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "processed_url_error_log")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProcessedUrlErrorLog extends AbstractEntity {
    @NonNull
    String serviceName;
    @OneToOne
    ProcessedFile processedFile;
    String stackTrace;
    String errorMessage;
}
