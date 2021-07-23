/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.json.simple.parser;

public class Yytoken {
    public static final int TYPE_VALUE = 0;
    public static final int TYPE_LEFT_BRACE = 1;
    public static final int TYPE_RIGHT_BRACE = 2;
    public static final int TYPE_LEFT_SQUARE = 3;
    public static final int TYPE_RIGHT_SQUARE = 4;
    public static final int TYPE_COMMA = 5;
    public static final int TYPE_COLON = 6;
    public static final int TYPE_EOF = -1;
    public int type = 0;
    public Object value = null;

    public Yytoken(int type, Object value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        switch (this.type) {
            case 0: {
                sb.append("VALUE(").append(this.value).append(")");
                return sb.toString();
            }
            case 1: {
                sb.append("LEFT BRACE({)");
                return sb.toString();
            }
            case 2: {
                sb.append("RIGHT BRACE(})");
                return sb.toString();
            }
            case 3: {
                sb.append("LEFT SQUARE([)");
                return sb.toString();
            }
            case 4: {
                sb.append("RIGHT SQUARE(])");
                return sb.toString();
            }
            case 5: {
                sb.append("COMMA(,)");
                return sb.toString();
            }
            case 6: {
                sb.append("COLON(:)");
                return sb.toString();
            }
            case -1: {
                sb.append("END OF FILE");
            }
        }
        return sb.toString();
    }
}

