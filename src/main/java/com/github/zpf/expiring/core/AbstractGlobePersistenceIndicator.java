package com.github.zpf.expiring.core;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 *    Abstract class persistence instructions
 * <p>
 *
 * @author zpf
 * @since 1.1.0
 */
public abstract class AbstractGlobePersistenceIndicator<K, V> extends FileUtil {

    public void serial() {
        // do nothing
    }

    public void deserialize(String path) {
        // do nothing
    }

    public boolean exist0(String filePath) {
        return exist(filePath);
    }

    public File touch0(String filePath) {
        if (del0(filePath)) {
            return touch(filePath);
        }
        return null;
    }

    public boolean del0(String fileName) {
        boolean c = true;
        if (exist0(fileName)) {
            c = del(fileName);
        }
        //true 确实为文件不存在
        return c;
    }

    public abstract void deserializeO(String path);

    public abstract void deserialize0(File file) throws PersistenceException;

    public abstract void deserialize0(StringBuilder buffer) throws PersistenceException;

    public abstract void deserialize0(ExpireTemplate<K, V> template,
                                      ExpireGlobePersistence.Persistence<K, V> persistence, String writePath)
            throws PersistenceException;

    public abstract Long condition(LocalDateTime now, LocalDateTime expire, TimeUnit unit) throws PersistenceException;
}
