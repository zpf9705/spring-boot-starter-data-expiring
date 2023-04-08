package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.error.PersistenceException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.File;

/**
 * Persistence Cache recovery collection of a variety of ways
 *
 * @author zpf
 * @since 3.0.0
 */
public interface PersistenceFactory {

    /**
     * Get Factory name
     *
     * @return a factory name
     */
    String getFactoryName();

    /**
     * Restore memory within a path
     *
     * @param path Persistence path
     */
    void deserializeWithPath(@Nullable String path) throws PersistenceException;

    /**
     * Restore memory within a file
     *
     * @param file Persistence file
     * @throws PersistenceException Persistence ex
     */
    void deserializeWithFile(@NonNull File file) throws PersistenceException;

    /**
     * Restore memory of read file buff
     *
     * @param buffer file read buff
     * @throws PersistenceException Persistence ex
     */
    void deserializeWithString(@NonNull StringBuilder buffer) throws PersistenceException;
}
