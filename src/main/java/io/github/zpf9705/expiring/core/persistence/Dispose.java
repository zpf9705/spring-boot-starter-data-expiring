package io.github.zpf9705.expiring.core.persistence;

/**
 * The cache persistence operation interface for {@link ExpirePersistenceUtils}
 *
 * @author zpf
 * @since 3.0.0
 */
public interface Dispose {

    int indexOne = 0;

    int indexTwo = 1;

    int indexThree = 2;

    int indexFour = 3;

    int lengthSi = 1;

    int lengthSimple = 2;

    int lengthGan = 3;

    int lengthDlg = 4;

    void dispose(Object[] args);
}
