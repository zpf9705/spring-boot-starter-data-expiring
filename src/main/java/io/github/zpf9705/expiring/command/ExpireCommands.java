package io.github.zpf9705.expiring.command;

/**
 * The instruction aggregation interface of {@code Expire} includes key value operations, as well as value operations, etc.
 *
 * @author zpf
 * @since 3.0.0
 */
public interface ExpireCommands extends ExpireStringCommands, ExpireKeyCommands {
}
