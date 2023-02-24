package io.github.zpf9705.core;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


/**
 * Here provides a cache persistent {@link ExpireGlobePersistence} all process
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractGlobePersistenceIndicator<K, V> extends FileUtil {

    /**
     * serial cache persistence
     */
    public void serial() {
        // do nothing
    }

    /**
     * deserialize cache persistence
     *
     * @param path persistence path
     */
    public void deserialize(String path) {
        // do nothing
    }

    /**
     * Determine whether the specified file path
     *
     * @param filePath Address of the file
     * @return If the real path to the file exists, and does not exist
     */
    public boolean exist0(String filePath) {
        return exist(filePath);
    }

    /**
     * touch a file within a filePath
     *
     * @param filePath Address of the file
     * @return created file
     */
    public File touch0(String filePath) {
        if (del0(filePath)) {
            return touch(filePath);
        }
        return null;
    }

    /**
     * delete a file within fileName
     *
     * @param fileName name of the file
     * @return If the real path to the file del , and does failed
     */
    public boolean del0(String fileName) {
        boolean c = true;
        if (exist0(fileName)) {
            c = del(fileName);
        }
        return c;
    }

    /**
     * Restore memory within a path
     *
     * @param path Persistence path
     */
    public abstract void deserializeO(String path);

    /**
     * Restore memory within a file
     *
     * @param file Persistence file
     * @throws PersistenceException Persistence ex
     */
    public abstract void deserialize0(File file) throws PersistenceException;

    /**
     * Restore memory of read file buff
     *
     * @param buffer file read buff
     * @throws PersistenceException Persistence ex
     */
    public abstract void deserialize0(StringBuilder buffer) throws PersistenceException;

    /**
     * Restore memory of expireTemplate regain this info
     *
     * @param template    {@link ExpireTemplate}
     * @param persistence read file of persistence
     * @param writePath   persistence write path
     * @throws PersistenceException Persistence ex
     */
    public abstract void deserialize0(ExpireTemplate<K, V> template,
                                      ExpireGlobePersistence.Persistence<K, V> persistence, String writePath)
            throws PersistenceException;

    /**
     * Restore the cache time remaining
     *
     * @param now    now time
     * @param expire expiring time
     * @param unit   time unit
     * @return The rest of the corresponding amount per unit time
     * @throws PersistenceException Persistence ex
     */
    public abstract Long condition(LocalDateTime now, LocalDateTime expire, TimeUnit unit)
            throws PersistenceException;
}
