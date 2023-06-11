package io.github.zpf9705.expiring.util;

import cn.hutool.core.io.FileUtil;
import io.github.zpf9705.expiring.core.annotation.CanNull;

import java.io.File;
import java.util.function.Function;

/**
 * Here is some tips on some API support system of Java classes
 *
 * @author zpf
 * @since 1.1.4
 */
public abstract class SystemUtils {

    /**
     * The current working directory of the user
     */
    public static final String PROJECT_PATH = "user.dir";

    /**
     * The current main working directory of the user
     *
     * @since 1.1.5
     */
    public static final String PROJECT_HOME = "user.home";

    private static final String SLASH = "/";

    private static final String currentProjectPath;

    static {
        /*
         * Static access to relevant variables of the system
         * */
        currentProjectPath = System.getProperty(PROJECT_PATH);
    }

    /**
     * Set system key and value constant or configuration or cache value
     *
     * @param key   must no be {@literal null}
     * @param value must no be {@literal null}
     * @param <K>   generic of {@code key}
     * @param <V>   generic of {@code value}
     * @since 1.1.5
     */
    public static <K, V> void setProperty(K key, V value) {
        if (key == null || value == null) {
            return;
        }
        System.setProperty(key.toString(), value.toString());
    }

    /**
     * Get system key and value constant or configuration or cache value
     *
     * @param key Value of {@code key}
     * @return You call parameter value of key value
     * @since 1.1.5
     */
    public static String getProperty(String key) {
        if (key == null) {
            return null;
        }
        return System.getProperty(key);
    }

    /**
     * Get system key and value constant or configuration or cache value with convert
     *
     * @param key     value of {@code key}
     * @param <C>     convert generic
     * @param convert type
     * @param def     {@literal null} with default value
     * @return You call parameter value of key value
     * @since 1.1.5
     */
    public static <C> C getPropertyWithConvert(String key, Function<String, C> convert, @CanNull C def) {
        if (key == null) {
            return def;
        }
        String property = getProperty(key);
        if (property == null) {
            return def;
        }
        return convert.apply(property);
    }

    /**
     * Get current project path
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
        if (StringUtils.simpleIsBlank(specifyFolder)) return null;
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
