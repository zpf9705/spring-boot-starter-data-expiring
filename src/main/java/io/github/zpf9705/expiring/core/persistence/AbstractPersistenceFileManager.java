package io.github.zpf9705.expiring.core.persistence;

import cn.hutool.core.io.FileUtil;
import io.github.zpf9705.expiring.util.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;


/**
 * Cache persistence operation file operations abstract classes
 * Provides about persistent files to add and delete functions
 * The main function of provider comes from {@link cn.hutool.Hutool}
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractPersistenceFileManager extends FileUtil {

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

    /**
     * Write single file line
     *
     * @param json write context
     * @param file write file
     */
    public void writeSingleFileLine(String json, File file) {
        if (StringUtils.simpleIsBlank(json)) {
            return;
        }
        appendLines(Collections.singletonList(json), file, StandardCharsets.UTF_8);
    }
}
