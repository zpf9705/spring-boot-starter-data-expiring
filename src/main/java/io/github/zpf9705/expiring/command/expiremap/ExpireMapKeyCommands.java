package io.github.zpf9705.expiring.command.expiremap;

import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapConnection;
import io.github.zpf9705.expiring.core.PersistenceExec;
import io.github.zpf9705.expiring.core.PersistenceExecTypeEnum;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * About the key {@code key} operating instructions
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapKeyCommands implements ExpireKeyCommands {

    private final ExpireMapConnection expireMapConnection;

    public ExpireMapKeyCommands(ExpireMapConnection expireMapConnection) {
        this.expireMapConnection = expireMapConnection;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#delete(byte[]...)
     */
    @Nullable
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.REMOVE)
    public Long delete(byte[]... keys) {
        return this.expireMapConnection.deleteReturnSuccessNum(keys);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#deleteType(byte[]...)
     */
    @Override
    public Map<byte[], byte[]> deleteType(byte[] key) {
        return this.expireMapConnection.deleteSimilarKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#deleteAll()
     */
    @Override
    public Boolean deleteAll() {
        return this.expireMapConnection.reboot();
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#hasKey(byte[])
     */
    @Override
    public Boolean hasKey(byte[] key) {
        return this.expireMapConnection.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpiration(byte[])
     */
    @Override
    public Long getExpiration(byte[] key) {
        return this.expireMapConnection.getExpirationWithKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpiration(byte[], TimeUnit)
     */
    @Override
    public Long getExpiration(byte[] key, TimeUnit unit) {
        return this.expireMapConnection.getExpirationWithDuration(key, unit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpectedExpiration(byte[])
     */
    @Override
    public Long getExpectedExpiration(byte[] key) {
        return this.expireMapConnection.getExpectedExpirationWithKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpectedExpiration(byte[], TimeUnit)
     */
    @Override
    public Long getExpectedExpiration(byte[] key, TimeUnit unit) {
        return this.expireMapConnection.getExpectedExpirationWithUnit(key, unit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#setExpiration(byte[], Long, TimeUnit)
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.SET_E)
    public void setExpiration(byte[] key, Long duration, TimeUnit timeUnit) {
        this.expireMapConnection.setExpirationDuration(key, duration, timeUnit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#resetExpiration(byte[])
     */
    @Override
    @PersistenceExec(PersistenceExecTypeEnum.REST)
    public Boolean resetExpiration(byte[] key) {
        return this.expireMapConnection.resetExpirationWithKey(key);
    }
}
