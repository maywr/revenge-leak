/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.lib.tree;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.lib.AnnotationVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AnnotationNode
extends AnnotationVisitor {
    public String desc;
    public List<Object> values;

    public AnnotationNode(String desc) {
        this(327680, desc);
        if (this.getClass() == AnnotationNode.class) return;
        throw new IllegalStateException();
    }

    public AnnotationNode(int api, String desc) {
        super(api);
        this.desc = desc;
    }

    AnnotationNode(List<Object> values) {
        super(327680);
        this.values = values;
    }

    @Override
    public void visit(String name, Object value) {
        if (this.values == null) {
            this.values = new ArrayList<Object>(this.desc != null ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(name);
        }
        if (value instanceof byte[]) {
            byte[] v = (byte[])value;
            ArrayList<Byte> l = new ArrayList<Byte>(v.length);
            byte[] arrby = v;
            int n = arrby.length;
            int n2 = 0;
            do {
                if (n2 >= n) {
                    this.values.add(l);
                    return;
                }
                byte b = arrby[n2];
                l.add(b);
                ++n2;
            } while (true);
        }
        if (value instanceof boolean[]) {
            boolean[] v = (boolean[])value;
            ArrayList<Boolean> l = new ArrayList<Boolean>(v.length);
            boolean[] arrbl = v;
            int n = arrbl.length;
            int n3 = 0;
            do {
                if (n3 >= n) {
                    this.values.add(l);
                    return;
                }
                boolean b = arrbl[n3];
                l.add(b);
                ++n3;
            } while (true);
        }
        if (value instanceof short[]) {
            short[] v = (short[])value;
            ArrayList<Short> l = new ArrayList<Short>(v.length);
            short[] arrs = v;
            int n = arrs.length;
            int n4 = 0;
            do {
                if (n4 >= n) {
                    this.values.add(l);
                    return;
                }
                short s = arrs[n4];
                l.add(s);
                ++n4;
            } while (true);
        }
        if (value instanceof char[]) {
            char[] v = (char[])value;
            ArrayList<Character> l = new ArrayList<Character>(v.length);
            char[] arrc = v;
            int n = arrc.length;
            int n5 = 0;
            do {
                if (n5 >= n) {
                    this.values.add(l);
                    return;
                }
                char c = arrc[n5];
                l.add(Character.valueOf(c));
                ++n5;
            } while (true);
        }
        if (value instanceof int[]) {
            int[] v = (int[])value;
            ArrayList<Integer> l = new ArrayList<Integer>(v.length);
            int[] arrn = v;
            int n = arrn.length;
            int n6 = 0;
            do {
                if (n6 >= n) {
                    this.values.add(l);
                    return;
                }
                int i = arrn[n6];
                l.add(i);
                ++n6;
            } while (true);
        }
        if (value instanceof long[]) {
            long[] v = (long[])value;
            ArrayList<Long> l = new ArrayList<Long>(v.length);
            long[] arrl = v;
            int n = arrl.length;
            int n7 = 0;
            do {
                if (n7 >= n) {
                    this.values.add(l);
                    return;
                }
                long lng = arrl[n7];
                l.add(lng);
                ++n7;
            } while (true);
        }
        if (value instanceof float[]) {
            float[] v = (float[])value;
            ArrayList<Float> l = new ArrayList<Float>(v.length);
            float[] arrf = v;
            int n = arrf.length;
            int n8 = 0;
            do {
                if (n8 >= n) {
                    this.values.add(l);
                    return;
                }
                float f = arrf[n8];
                l.add(Float.valueOf(f));
                ++n8;
            } while (true);
        }
        if (!(value instanceof double[])) {
            this.values.add(value);
            return;
        }
        double[] v = (double[])value;
        ArrayList<Double> l = new ArrayList<Double>(v.length);
        double[] arrd = v;
        int n = arrd.length;
        int n9 = 0;
        do {
            if (n9 >= n) {
                this.values.add(l);
                return;
            }
            double d = arrd[n9];
            l.add(d);
            ++n9;
        } while (true);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        if (this.values == null) {
            this.values = new ArrayList<Object>(this.desc != null ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(name);
        }
        this.values.add(new String[]{desc, value});
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        if (this.values == null) {
            this.values = new ArrayList<Object>(this.desc != null ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(name);
        }
        AnnotationNode annotation = new AnnotationNode(desc);
        this.values.add(annotation);
        return annotation;
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        if (this.values == null) {
            this.values = new ArrayList<Object>(this.desc != null ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(name);
        }
        ArrayList<Object> array = new ArrayList<Object>();
        this.values.add(array);
        return new AnnotationNode(array);
    }

    @Override
    public void visitEnd() {
    }

    public void check(int api) {
    }

    public void accept(AnnotationVisitor av) {
        if (av == null) return;
        if (this.values != null) {
            for (int i = 0; i < this.values.size(); i += 2) {
                String name = (String)this.values.get(i);
                Object value = this.values.get(i + 1);
                AnnotationNode.accept(av, name, value);
            }
        }
        av.visitEnd();
    }

    static void accept(AnnotationVisitor av, String name, Object value) {
        if (av == null) return;
        if (value instanceof String[]) {
            String[] typeconst = (String[])value;
            av.visitEnum(name, typeconst[0], typeconst[1]);
            return;
        }
        if (value instanceof AnnotationNode) {
            AnnotationNode an = (AnnotationNode)value;
            an.accept(av.visitAnnotation(name, an.desc));
            return;
        }
        if (!(value instanceof List)) {
            av.visit(name, value);
            return;
        }
        AnnotationVisitor v = av.visitArray(name);
        if (v == null) return;
        List array = (List)value;
        int j = 0;
        do {
            if (j >= array.size()) {
                v.visitEnd();
                return;
            }
            AnnotationNode.accept(v, null, array.get(j));
            ++j;
        } while (true);
    }
}

