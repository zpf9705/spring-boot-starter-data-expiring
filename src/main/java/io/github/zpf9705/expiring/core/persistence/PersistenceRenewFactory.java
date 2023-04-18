package io.github.zpf9705.expiring.core.persistence;

import io.github.zpf9705.expiring.core.PersistenceException;
import io.github.zpf9705.expiring.core.annotation.CanNull;
import io.github.zpf9705.expiring.core.annotation.NotNull;

import java.io.File;

/**
 * Persistence definition method interface file recovery factory, mainly describe the different factory class cache recovery
 *
 * @author zpf
 * @since 3.0.0
 */
public interface PersistenceRenewFactory {

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
    void deserializeWithPath(@CanNull String path) throws PersistenceException;

    /**
     * Restore memory within a file
     *
     * @param file Persistence file
     * @throws PersistenceException Persistence ex
     */
    void deserializeWithFile(@NotNull File file) throws PersistenceException;

    /**
     * Restore memory of read file buff
     *
     * @param buffer file read buff
     * @throws PersistenceException Persistence ex
     */
    void deserializeWithString(@NotNull StringBuilder buffer) throws PersistenceException;
}
