package io.github.zpf9705.core;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * <p>
 *     system utils
 * </p>
 *
 * @author zpf
 * @since 1.1.0
 */
public final class SystemUtils {

    private static final String PROJECT_PATH = "user.dir";

    private static final String SLASH = "/";

    private static final String currentProjectPath;

    static {
        currentProjectPath = System.getProperty(PROJECT_PATH);
    }

    /**
     * Create project relative path to the file
     *
     * @param specifyFolder The specified folder
     * @return target folder
     */
    public static File createRelativePathSpecifyFolder(String specifyFolder) {
        if (StringUtils.isBlank(specifyFolder)) return null;
        String path;
        if (currentProjectPath.endsWith(SLASH)) {
            path = currentProjectPath + specifyFolder;
        } else {
            path = currentProjectPath + SLASH + specifyFolder;
        }
        // if exist just return
        if (FileUtil.exist(path)) {
            return new File(path);
        }
        // no exist mkdir
        return FileUtil.mkdir(path);
    }

    /**
     * Create a project and get the path relative paths files
     *
     * @param specifyFolder The specified folder
     * @return relative path
     */
    public static String createRelativePathSpecifyFolderName(String specifyFolder) {
        String rPath = null;
        File folder = createRelativePathSpecifyFolder(specifyFolder);
        if (folder != null) {
            String path = folder.getPath();
            if (folder.getName().endsWith(SLASH)) {
                rPath = path;
            } else {
                rPath = path + SLASH;
            }
        }
        return rPath;
    }
}
