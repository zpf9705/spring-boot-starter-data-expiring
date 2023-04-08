package io.github.zpf9705.expiring.core.persistence;

import cn.hutool.core.io.FileUtil;
import io.github.zpf9705.expiring.core.error.PersistenceException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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

    public void deserializeWithPath(@Nullable String path) {

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
     * @return If the real path to the file del , and does fail
     */
    public boolean del0(String fileName) {
        boolean c = true;
        if (exist0(fileName)) {
            c = del(fileName);
        }
        return c;
    }
}
