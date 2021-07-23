/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Structure
 */
package club.minnced.discord.rpc;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DiscordRichPresence
extends Structure {
    private static final List<String> FIELD_ORDER = Collections.unmodifiableList(Arrays.asList("state", "details", "startTimestamp", "endTimestamp", "largeImageKey", "largeImageText", "smallImageKey", "smallImageText", "partyId", "partySize", "partyMax", "matchSecret", "joinSecret", "spectateSecret", "instance"));
    public String state;
    public String details;
    public long startTimestamp;
    public long endTimestamp;
    public String largeImageKey;
    public String largeImageText;
    public String smallImageKey;
    public String smallImageText;
    public String partyId;
    public int partySize;
    public int partyMax;
    public String matchSecret;
    public String joinSecret;
    public String spectateSecret;
    public byte instance;

    public DiscordRichPresence(String encoding) {
        this.setStringEncoding(encoding);
    }

    public DiscordRichPresence() {
        this("UTF-8");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiscordRichPresence)) {
            return false;
        }
        DiscordRichPresence presence = (DiscordRichPresence)((Object)o);
        if (this.startTimestamp != presence.startTimestamp) return false;
        if (this.endTimestamp != presence.endTimestamp) return false;
        if (this.partySize != presence.partySize) return false;
        if (this.partyMax != presence.partyMax) return false;
        if (this.instance != presence.instance) return false;
        if (!Objects.equals(this.state, presence.state)) return false;
        if (!Objects.equals(this.details, presence.details)) return false;
        if (!Objects.equals(this.largeImageKey, presence.largeImageKey)) return false;
        if (!Objects.equals(this.largeImageText, presence.largeImageText)) return false;
        if (!Objects.equals(this.smallImageKey, presence.smallImageKey)) return false;
        if (!Objects.equals(this.smallImageText, presence.smallImageText)) return false;
        if (!Objects.equals(this.partyId, presence.partyId)) return false;
        if (!Objects.equals(this.matchSecret, presence.matchSecret)) return false;
        if (!Objects.equals(this.joinSecret, presence.joinSecret)) return false;
        if (!Objects.equals(this.spectateSecret, presence.spectateSecret)) return false;
        return true;
    }

    public int hashCode() {
        return Objects.hash(this.state, this.details, this.startTimestamp, this.endTimestamp, this.largeImageKey, this.largeImageText, this.smallImageKey, this.smallImageText, this.partyId, this.partySize, this.partyMax, this.matchSecret, this.joinSecret, this.spectateSecret, this.instance);
    }

    protected List<String> getFieldOrder() {
        return FIELD_ORDER;
    }
}

