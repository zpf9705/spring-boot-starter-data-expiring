package io.github.zpf9705.expiring.core.persistence;

/**
 * The cache persistence operation interface for {@link PersistenceSolver}
 *
 * @author zpf
 * @since 3.0.0
 */
@SuppressWarnings("rawtypes")
public interface Dispose {

    /**
     * Rear cache persistence operations
     *
     * @param solver must not be {@literal null}
     * @param args   can  be {@literal null}
     */
    void dispose(/*@NonNull*/PersistenceSolver solver, /*@Nullable*/Object[] args);

    int indexOne = 0;

    int indexTwo = 1;

    int indexThree = 2;

    int indexFour = 3;

    int lengthSi = 1;

    int lengthSimple = 2;

    int lengthGan = 3;

    int lengthDlg = 4;
}
