package io.github.zpf9705.expiring.command.expiremap;

import io.github.zpf9705.expiring.command.ExpireStringCommands;
import io.github.zpf9705.expiring.connection.expiremap.ExpireMapConnectionSlot;

import java.util.concurrent.TimeUnit;

/**
 * Normal operation instruction
 *
 * @author zpf
 * @since 3.0.0
 */
public class ExpireMapStringCommands implements ExpireStringCommands {

    private final ExpireMapConnectionSlot slot;

    public ExpireMapStringCommands(ExpireMapConnectionSlot slot) {
        this.slot = slot;
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#set(Object, Object)
     */
    @Override
    public Boolean set(byte[] key, byte[] value) {
        return this.slot.put(key, value);
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#setE(Object, Object,Long,TimeUnit)
     */
    @Override
    public Boolean setE(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        return this.slot.putDuration(key, value, duration, unit);
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#setNX(Object, Object)
     */
    @Override
    public Boolean setNX(byte[] key, byte[] value) {
        return this.slot.putIfAbsent(key, value);
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#setEX(Object, Object,Long,TimeUnit)
     */
    @Override
    public Boolean setEX(byte[] key, byte[] value, Long duration, TimeUnit unit) {
        return this.slot.putIfAbsentDuration(key, value, duration, unit);
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#get(Object)
     */
    @Override
    public byte[] get(byte[] key) {
        return this.slot.getVal(key);
    }

    /*
     * (non-Javadoc)
     * io.github.zpf9705.expiring.command.ExpireStringCommands#getAndSet(Object, Object)
     */
    @Override
    public byte[] getAndSet(byte[] key, byte[] newValue) {
        return this.slot.replace(key, newValue);
    }
}
