package com.heartrate.repository;

import com.heartrate.entity.VerificationToken;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Test implementation of VerificationTokenRepository using in-memory storage.
 * Following user rules:
 * - Using reusable test infrastructure instead of mocks
 * - Application-level validation
 * - Fail fast approach
 */
@Repository
public class TestVerificationTokenRepository implements VerificationTokenRepository {
    private final Map<UUID, VerificationToken> tokens = new ConcurrentHashMap<>();

    @Override
    public Optional<VerificationToken> findByTokenAndType(String token, String type) {
        return tokens.values().stream()
                .filter(t -> t.getToken().equals(token) && t.getType().equals(type))
                .findFirst();
    }

    @Override
    public Optional<VerificationToken> findFirstByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, String type) {
        return tokens.values().stream()
                .filter(t -> t.getUserId().equals(userId) && t.getType().equals(type))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst();
    }

    @Override
    public void flush() {
        // No-op for in-memory implementation
    }

    @Override
    public <S extends VerificationToken> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends VerificationToken> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<VerificationToken> entities) {
        entities.forEach(entity -> tokens.remove(entity.getId()));
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {
        uuids.forEach(tokens::remove);
    }

    @Override
    public void deleteAllInBatch() {
        tokens.clear();
    }

    @Override
    public VerificationToken getOne(UUID uuid) {
        return tokens.get(uuid);
    }

    @Override
    public VerificationToken getById(UUID uuid) {
        return findById(uuid).orElseThrow();
    }

    @Override
    public VerificationToken getReferenceById(UUID uuid) {
        return getById(uuid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S extends VerificationToken> List<S> findAll(Example<S> example) {
        return tokens.values().stream()
                .filter(token -> example.getProbe().getToken().equals(token.getToken()))
                .map(token -> (S) token)
                .collect(Collectors.toList());
    }

    @Override
    public <S extends VerificationToken> List<S> findAll(Example<S> example, Sort sort) {
        return findAll(example);
    }

    @Override
    public <S extends VerificationToken> List<S> saveAll(Iterable<S> entities) {
        List<S> saved = new ArrayList<>();
        entities.forEach(entity -> saved.add(save(entity)));
        return saved;
    }

    @Override
    public List<VerificationToken> findAll() {
        return new ArrayList<>(tokens.values());
    }

    @Override
    public List<VerificationToken> findAllById(Iterable<UUID> uuids) {
        List<VerificationToken> found = new ArrayList<>();
        uuids.forEach(id -> found.add(tokens.get(id)));
        return found;
    }

    @Override
    public <S extends VerificationToken> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        tokens.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<VerificationToken> findById(UUID uuid) {
        return Optional.ofNullable(tokens.get(uuid));
    }

    @Override
    public boolean existsById(UUID uuid) {
        return tokens.containsKey(uuid);
    }

    @Override
    public long count() {
        return tokens.size();
    }

    @Override
    public void deleteById(UUID uuid) {
        tokens.remove(uuid);
    }

    @Override
    public void delete(VerificationToken entity) {
        tokens.remove(entity.getId());
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {
        uuids.forEach(tokens::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends VerificationToken> entities) {
        entities.forEach(entity -> tokens.remove(entity.getId()));
    }

    @Override
    public void deleteAll() {
        tokens.clear();
    }

    @Override
    public List<VerificationToken> findAll(Sort sort) {
        return findAll();
    }

    @Override
    public Page<VerificationToken> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Pagination not supported in test repository");
    }

    @Override
    public <S extends VerificationToken> Optional<S> findOne(Example<S> example) {
        return findAll(example).stream().findFirst();
    }

    @Override
    public <S extends VerificationToken> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("Pagination not supported in test repository");
    }

    @Override
    public <S extends VerificationToken> long count(Example<S> example) {
        return findAll(example).size();
    }

    @Override
    public <S extends VerificationToken> boolean exists(Example<S> example) {
        return !findAll(example).isEmpty();
    }

    @Override
    public <S extends VerificationToken, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("Fluent queries not supported in test repository");
    }
}
