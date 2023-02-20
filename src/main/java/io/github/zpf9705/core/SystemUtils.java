package io.github.zpf9705.core;

import cn.hutool.core.io.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.TimeZone;

/**
 * <p>
 * 【System utils】
 * Here you can get relevant variables of the system
 * Also can obtain relevant configuration of engineering {@link ExpireMapCacheProperties}
 * But you need setting your configuration in system property
 * .....
 * System.setProperty(...) {@link System#setProperty(String, String)}
 * And get it {@link System#getProperty(String)}
 * </p>
 *
 * @author zpf
 * @since 1.1.4
 */
public final class SystemUtils {

    /**
     * The current working directory of the user
     */
    public static final String PROJECT_PATH = "user.dir";

    /**
     * The current main working directory of the user
     * @since 1.1.5
     */
    public static final String PROJECT_HOME= "user.home";

    private static final String SLASH = "/";

    private static final String currentProjectPath;

    static {
        /*
        * Static access to relevant variables of the system
        * */
        currentProjectPath = System.getProperty(PROJECT_PATH);
    }

    /**
     * set system key and value constant or configuration or cache value
     *
     * @param key   set a value as a key
     * @param value set a value as a key of value
     * @since 1.1.5
     */
    public static void setProperty(String key, String value) {
        if (key == null || value == null){
            return;
        }
        System.setProperty(key, value);
    }

    /**
     * get system key and value constant or configuration or cache value
     *
     * @param key  value of key
     * @return You call parameter value of key value
     * @since 1.1.5
     */
    public static String getProperty(String key) {
        if (key == null){
            return null;
        }
        return System.getProperty(key);
    }

    /**
     * get current project path
     *
     * @return path
     */
    public static String getCurrentProjectPath() {
        return currentProjectPath;
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
        if (currentProjectPath.endsWith(SLASH) || specifyFolder.startsWith(SLASH)) {
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
