package com.example.iosfileuploader.core.service.validator;


import com.example.iosfileuploader.domain.entity.AbstractEntity;

import java.time.OffsetDateTime;
import java.util.List;

public interface EntityValidator<E extends AbstractEntity> {
    void correctForCreate(E entity);
    void correctForUpdate(E entity);
    void correctForBatchSave(List<E> entities);

    default void correctCreatedAt(E entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(OffsetDateTime.now());
        }
    }
}
