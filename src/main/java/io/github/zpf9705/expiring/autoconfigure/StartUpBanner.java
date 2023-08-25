package io.github.zpf9705.expiring.autoconfigure;

/**
 * Component log printing characters and version number acquisition interface
 *
 * @author zpf
 * @since 3.0.0
 */
public interface StartUpBanner {

    /**
     * Method for obtaining component log concatenated string
     *
     * @return Using print banner
     */
    String getBanner();

    /**
     * Method of obtaining component version number
     *
     * @return Using print version
     */
    String getLeftSign();
}
