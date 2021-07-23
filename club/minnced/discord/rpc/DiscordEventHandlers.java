/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Callback
 *  com.sun.jna.Structure
 */
package club.minnced.discord.rpc;

import club.minnced.discord.rpc.DiscordUser;
import com.sun.jna.Callback;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DiscordEventHandlers
extends Structure {
    private static final List<String> FIELD_ORDER = Collections.unmodifiableList(Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest"));
    public OnReady ready;
    public OnStatus disconnected;
    public OnStatus errored;
    public OnGameUpdate joinGame;
    public OnGameUpdate spectateGame;
    public OnJoinRequest joinRequest;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiscordEventHandlers)) {
            return false;
        }
        DiscordEventHandlers that = (DiscordEventHandlers)((Object)o);
        if (!Objects.equals(this.ready, that.ready)) return false;
        if (!Objects.equals(this.disconnected, that.disconnected)) return false;
        if (!Objects.equals(this.errored, that.errored)) return false;
        if (!Objects.equals(this.joinGame, that.joinGame)) return false;
        if (!Objects.equals(this.spectateGame, that.spectateGame)) return false;
        if (!Objects.equals(this.joinRequest, that.joinRequest)) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hash(this.ready, this.disconnected, this.errored, this.joinGame, this.spectateGame, this.joinRequest);
    }

    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }

    public static interface OnJoinRequest
    extends Callback {
        public void accept(DiscordUser var1);
    }

    public static interface OnGameUpdate
    extends Callback {
        public void accept(String var1);
    }

    public static interface OnStatus
    extends Callback {
        public void accept(int var1, String var2);
    }

    public static interface OnReady
    extends Callback {
        public void accept(DiscordUser var1);
    }

}

