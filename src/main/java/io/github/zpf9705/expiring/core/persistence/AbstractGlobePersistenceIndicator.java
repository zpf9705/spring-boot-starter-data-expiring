package io.github.zpf9705.expiring.core.persistence;

import cn.hutool.core.io.FileUtil;
import io.github.zpf9705.expiring.core.error.PersistenceException;
import org.springframework.lang.NonNull;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


/**
 * Here provides a cache persistent {@link ExpireSimpleGlobePersistence} all process
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractGlobePersistenceIndicator extends FileUtil implements PersistenceFactory {

    /**
     * Serial cache persistence
     */
    public void serial() {
        // do nothing
    }

    /**
     * Deserialize cache persistence
     *
     * @param path persistence path
     */
    public void deserialize(@NonNull String path) {
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
     * Delete a file within fileName
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
     * Restore the cache time remaining
     *
     * @param now    now time
     * @param expire expiring time
     * @param unit   time unit
     * @return The rest of the corresponding amount per unit time
     * @throws PersistenceException Persistence ex
     */
    public abstract Long condition(LocalDateTime now, LocalDateTime expire, TimeUnit unit) throws PersistenceException;
}
