package io.github.zpf9705.core;


/**
 * <p>
 *   Persistent abnormal
 * <p>
 *
 * @author zpf
 * @since 1.1.0
 */
public class PersistenceException extends Exception {

    private static final long serialVersionUID = 8520468345693528171L;

    public PersistenceException(String detailMsg) {
        super(detailMsg);
    }
}
