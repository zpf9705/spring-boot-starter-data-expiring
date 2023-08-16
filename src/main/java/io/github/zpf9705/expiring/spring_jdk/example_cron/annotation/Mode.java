package io.github.zpf9705.expiring.spring_jdk.example_cron.annotation;

/**
 * How to register the existence of timed task method running objects
 *
 * @author zpf
 * @since 3.1.5
 */
public enum Mode {

    /**
     * Calling methods in the form of spring proxy objects uses spring singleton objects,
     * provided that the method object needs to be manually added to the spring container.
     * You can refer to annotations such as {@link org.springframework.stereotype.Component}.
     */
    PROXY,

    /**
     * Create an object in the form of an empty construction instantiation to make corresponding
     * method calls, provided that the object is guaranteed to have an empty construction, and
     * this form does not require adding the object to the spring container.
     */
    INSTANCE
}
