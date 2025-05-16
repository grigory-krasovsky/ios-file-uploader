package com.example.iosfileuploader.core.service.impl;

import com.example.iosfileuploader.core.exception.DataBaseException;
import com.example.iosfileuploader.core.exception.enums.ErrorType;
import com.example.iosfileuploader.core.service.CRUDService;
import com.example.iosfileuploader.core.service.validator.EntityValidator;
import com.example.iosfileuploader.domain.entity.AbstractEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.iosfileuploader.core.exception.DataBaseException.ENTITY_IS_ABSENT_MESSAGE;


@AllArgsConstructor
public class AbstractEntityService<E extends AbstractEntity, V extends EntityValidator<E>, R extends JpaRepository<E, UUID>> implements CRUDService<E> {

    protected final V validator;
    protected final R repository;

    @Override
    public E create(E entity) {
        validator.correctForCreate(entity);
        return repository.save(entity);
    }

    @Override
    public E findById(UUID uuid) {
        return repository.findById(uuid).orElseThrow(() -> new DataBaseException(ErrorType.SYSTEM_ERROR, ENTITY_IS_ABSENT_MESSAGE(uuid, this.getClass())));
    }

    @Override
    public List<E> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<E> update(E entity) {
        validator.correctForUpdate(entity);
        return Optional.of(repository.save(entity));
    }

    @Override
    public Boolean delete(UUID uuid) {
        return null;
    }

    @Override
    public Boolean exists(UUID uuid) {
        return repository.existsById(uuid);
    }

    @Override
    public List<E> batchSave(List<E> entities) {
        validator.correctForBatchSave(entities);
        return repository.saveAll(entities);
    }
}
