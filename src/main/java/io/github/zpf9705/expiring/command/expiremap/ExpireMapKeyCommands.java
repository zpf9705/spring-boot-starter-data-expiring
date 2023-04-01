package io.github.zpf9705.expiring.command.expiremap;

import io.github.zpf9705.expiring.command.ExpireKeyCommands;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapConnectionSlot;
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

    private final ExpireMapConnectionSlot slot;

    public ExpireMapKeyCommands(ExpireMapConnectionSlot slot) {
        this.slot = slot;
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#delete(byte[]...)
     */
    @Nullable
    @Override
    public Long delete(byte[]... keys) {
        return this.slot.deleteReturnSuccessNum(keys);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#deleteType(byte[]...)
     */
    @Override
    public Map<byte[], byte[]> deleteType(byte[] key) {
        return this.slot.deleteSimilarKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#deleteAll()
     */
    @Override
    public Boolean deleteAll() {
        return this.slot.reboot();
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#hasKey(byte[])
     */
    @Override
    public Boolean hasKey(byte[] key) {
        return this.slot.containsKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpiration(byte[])
     */
    @Override
    public Long getExpiration(byte[] key) {
        return this.slot.getExpirationWithKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpiration(byte[], TimeUnit)
     */
    @Override
    public Long getExpiration(byte[] key, TimeUnit unit) {
        return this.slot.getExpirationWithDuration(key, unit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpectedExpiration(byte[])
     */
    @Override
    public Long getExpectedExpiration(byte[] key) {
        return this.slot.getExpectedExpirationWithKey(key);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#getExpectedExpiration(byte[], TimeUnit)
     */
    @Override
    public Long getExpectedExpiration(byte[] key, TimeUnit unit) {
        return this.slot.getExpectedExpirationWithUnit(key, unit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#setExpiration(byte[], Long, TimeUnit)
     */
    @Override
    public void setExpiration(byte[] key, Long duration, TimeUnit timeUnit) {
        this.slot.setExpirationDuration(key, duration, timeUnit);
    }

    /*
     * (non-Javadoc)
     * @see io.github.zpf9705.expiring.command.ExpireKeyCommands#resetExpiration(byte[])
     */
    @Override
    public Boolean resetExpiration(byte[] key) {
        return this.slot.resetExpirationWithKey(key);
    }
}
