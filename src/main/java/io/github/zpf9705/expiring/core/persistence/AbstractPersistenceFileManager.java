package io.github.zpf9705.expiring.core.persistence;

import cn.hutool.core.io.FileUtil;
import io.github.zpf9705.expiring.core.annotation.NotNull;
import io.github.zpf9705.expiring.util.AssertUtils;
import io.github.zpf9705.expiring.util.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;


/**
 * Abstract file relations help manage the class
 * <p>
 * The full path of the records about persistent storage file
 * <ul>
 *     <li>{@link #writePath}</li>
 * </ul>
 * And some related about storage paths suitable method of file operations
 * <dl>
 *     <dt>{@link FileUtil}</dt>
 *     <dd>{@link FileUtil#touch(String)}</dd>
 *     <dd>{@link FileUtil#exist(String)}</dd>
 *     <dd>{@link FileUtil#del(String)}</dd>
 *     <dd>{@link FileUtil#appendLines(Collection, File, String)}</dd>
 * </dl>
 * Very useful file operations packaging tools {@link cn.hutool.Hutool}
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractPersistenceFileManager extends FileUtil {

    /**
     * Check provider persistence path
     *
     * @param persistencePath setting write path
     */
    public static void checkDirectory(@NotNull String persistencePath) {
        if (StringUtils.simpleNotBlank(persistencePath)) {
            //Determine whether to native folders and whether the folder
            if (!isDirectory(persistencePath)) {
                File directory = mkdir(persistencePath);
                if (!directory.exists()) {
                    checkError(persistencePath);
                }
            }
        }
    }

    /**
     * If no found persistence path will check error path and give logger
     *
     * @param persistencePath persistence path
     */
    public static void checkError(String persistencePath) {
        String[] pathArray = persistencePath.split("/");
        AssertUtils.Persistence.notEmpty(pathArray,
                "[" + persistencePath + "] no a path");
        String line = "";
        for (String path : pathArray) {
            if (StringUtils.simpleIsBlank(path)) {
                continue;
            }
            line += "/" + path;
            AssertUtils.Persistence.isTrue(isDirectory(line),
                    "[" + line + "] no a Directory for your file system");
        }
    }

    private String writePath;

    /**
     * Set a {@code writePath} with current write file
     *
     * @param writePath write path
     */
    public void setWritePath(@NotNull String writePath) {
        this.writePath = writePath;
    }

    /**
     * Get current write file path
     *
     * @return file write path
     */
    public String getWritePath() {
        return this.writePath;
    }

    /**
     * Determine whether the specified file path
     *
     * @return If the real path to the file exists, and does not exist
     */
    public boolean existCurrentWritePath() {
        return exist(this.writePath);
    }

    /**
     * touch a file within a filePath
     *
     * @return created file
     */
    public File touchWritePath() {
        if (delWithCurrentWritePath()) {
            return touch(this.writePath);
        }
        return null;
    }

    /**
     * Delete a file within fileName
     *
     * @return If the real path to the file del , and does fail
     */
    public boolean delWithCurrentWritePath() {
        boolean c = true;
        if (existCurrentWritePath()) {
            c = del(this.writePath);
        }
        return c;
    }

    /**
     * Write single file line
     *
     * @param json write context
     */
    public void writeSingleFileLine(String json) {
        if (StringUtils.simpleIsBlank(json)) {
            return;
        }
        File file = touchWritePath();
        if (file == null) {
            return;
        }
        appendLines(Collections.singletonList(json), file, StandardCharsets.UTF_8);
    }
}
