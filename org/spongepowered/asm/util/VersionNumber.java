/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.util;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionNumber
implements Comparable<VersionNumber>,
Serializable {
    private static final long serialVersionUID = 1L;
    public static final VersionNumber NONE = new VersionNumber();
    private static final Pattern PATTERN = Pattern.compile("^(\\d{1,5})(?:\\.(\\d{1,5})(?:\\.(\\d{1,5})(?:\\.(\\d{1,5}))?)?)?(-[a-zA-Z0-9_\\-]+)?$");
    private final long value;
    private final String suffix;

    private VersionNumber() {
        this.value = 0L;
        this.suffix = "";
    }

    private VersionNumber(short[] parts) {
        this(parts, null);
    }

    private VersionNumber(short[] parts, String suffix) {
        this.value = VersionNumber.pack(parts);
        this.suffix = suffix != null ? suffix : "";
    }

    private VersionNumber(short major, short minor, short revision, short build) {
        this(major, minor, revision, build, null);
    }

    private VersionNumber(short major, short minor, short revision, short build, String suffix) {
        this.value = VersionNumber.pack(major, minor, revision, build);
        this.suffix = suffix != null ? suffix : "";
    }

    public String toString() {
        short[] parts = VersionNumber.unpack(this.value);
        Object[] arrobject = new Object[5];
        arrobject[0] = parts[0];
        arrobject[1] = parts[1];
        Object object = (this.value & Integer.MAX_VALUE) > 0L ? String.format(".%d", parts[2]) : (arrobject[2] = "");
        arrobject[3] = (this.value & 32767L) > 0L ? String.format(".%d", parts[3]) : "";
        arrobject[4] = this.suffix;
        return String.format("%d.%d%3$s%4$s%5$s", arrobject);
    }

    @Override
    public int compareTo(VersionNumber other) {
        if (other == null) {
            return 1;
        }
        long delta = this.value - other.value;
        if (delta > 0L) {
            return 1;
        }
        if (delta >= 0L) return 0;
        return -1;
    }

    public boolean equals(Object other) {
        if (!(other instanceof VersionNumber)) {
            return false;
        }
        if (((VersionNumber)other).value != this.value) return false;
        return true;
    }

    public int hashCode() {
        return (int)(this.value >> 32) ^ (int)(this.value & 0xFFFFFFFFL);
    }

    private static long pack(short ... shorts) {
        return (long)shorts[0] << 48 | (long)shorts[1] << 32 | (long)(shorts[2] << 16) | (long)shorts[3];
    }

    private static short[] unpack(long along) {
        return new short[]{(short)(along >> 48), (short)(along >> 32 & 32767L), (short)(along >> 16 & 32767L), (short)(along & 32767L)};
    }

    public static VersionNumber parse(String version) {
        return VersionNumber.parse(version, NONE);
    }

    public static VersionNumber parse(String version, String defaultVersion) {
        return VersionNumber.parse(version, VersionNumber.parse(defaultVersion));
    }

    private static VersionNumber parse(String version, VersionNumber defaultVersion) {
        if (version == null) {
            return defaultVersion;
        }
        Matcher versionNumberPatternMatcher = PATTERN.matcher(version);
        if (!versionNumberPatternMatcher.matches()) {
            return defaultVersion;
        }
        short[] parts = new short[4];
        int pos = 0;
        while (pos < 4) {
            String part = versionNumberPatternMatcher.group(pos + 1);
            if (part != null) {
                int value = Integer.parseInt(part);
                if (value > 32767) {
                    throw new IllegalArgumentException("Version parts cannot exceed 32767, found " + value);
                }
                parts[pos] = (short)value;
            }
            ++pos;
        }
        return new VersionNumber(parts, versionNumberPatternMatcher.group(5));
    }
}

