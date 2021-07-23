/*
 * Decompiled with CFR <Could not determine version>.
 */
package org.spongepowered.asm.util;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.spongepowered.asm.lib.signature.SignatureReader;
import org.spongepowered.asm.lib.signature.SignatureVisitor;
import org.spongepowered.asm.lib.signature.SignatureWriter;
import org.spongepowered.asm.lib.tree.ClassNode;

public class ClassSignature {
    protected static final String OBJECT = "java/lang/Object";
    private final Map<TypeVar, TokenHandle> types = new LinkedHashMap<TypeVar, TokenHandle>();
    private Token superClass = new Token("java/lang/Object");
    private final List<Token> interfaces = new ArrayList<Token>();
    private final Deque<String> rawInterfaces = new LinkedList<String>();

    ClassSignature() {
    }

    private ClassSignature read(String signature) {
        if (signature == null) return this;
        try {
            new SignatureReader(signature).accept(new SignatureParser());
            return this;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }

    protected TypeVar getTypeVar(String varName) {
        TypeVar typeVar;
        Iterator<TypeVar> iterator = this.types.keySet().iterator();
        do {
            if (!iterator.hasNext()) return null;
        } while (!(typeVar = iterator.next()).matches(varName));
        return typeVar;
    }

    protected TokenHandle getType(String varName) {
        TypeVar typeVar;
        Iterator<TypeVar> iterator = this.types.keySet().iterator();
        do {
            if (iterator.hasNext()) continue;
            TokenHandle handle = new TokenHandle();
            this.types.put(new TypeVar(varName), handle);
            return handle;
        } while (!(typeVar = iterator.next()).matches(varName));
        return this.types.get(typeVar);
    }

    protected String getTypeVar(TokenHandle handle) {
        TokenHandle typeHandle;
        TypeVar typeVar;
        Iterator<Map.Entry<TypeVar, TokenHandle>> iterator = this.types.entrySet().iterator();
        do {
            if (!iterator.hasNext()) return handle.token.asType();
            Map.Entry<TypeVar, TokenHandle> type = iterator.next();
            typeVar = type.getKey();
            typeHandle = type.getValue();
            if (handle == typeHandle) return "T" + typeVar + ";";
        } while (handle.asToken() != typeHandle.asToken());
        return "T" + typeVar + ";";
    }

    protected void addTypeVar(TypeVar typeVar, TokenHandle handle) throws IllegalArgumentException {
        if (this.types.containsKey(typeVar)) {
            throw new IllegalArgumentException("TypeVar " + typeVar + " is already present on " + this);
        }
        this.types.put(typeVar, handle);
    }

    protected void setSuperClass(Token superClass) {
        this.superClass = superClass;
    }

    public String getSuperClass() {
        return this.superClass.asType(true);
    }

    protected void addInterface(Token iface) {
        if (!iface.isRaw()) {
            String raw = iface.asType(true);
            ListIterator<Token> iter = this.interfaces.listIterator();
            while (iter.hasNext()) {
                Token intrface = iter.next();
                if (!intrface.isRaw() || !intrface.asType(true).equals(raw)) continue;
                iter.set(iface);
                return;
            }
        }
        this.interfaces.add(iface);
    }

    public void addInterface(String iface) {
        this.rawInterfaces.add(iface);
    }

    protected void addRawInterface(String iface) {
        Token intrface;
        Token token = new Token(iface);
        String raw = token.asType(true);
        Iterator<Token> iterator = this.interfaces.iterator();
        do {
            if (iterator.hasNext()) continue;
            this.interfaces.add(token);
            return;
        } while (!(intrface = iterator.next()).asType(true).equals(raw));
    }

    public void merge(ClassSignature other) {
        try {
            HashSet<String> typeVars = new HashSet<String>();
            for (TypeVar typeVar : this.types.keySet()) {
                typeVars.add(typeVar.toString());
            }
            other.conform(typeVars);
        }
        catch (IllegalStateException ex) {
            ex.printStackTrace();
            return;
        }
        for (Map.Entry<TypeVar, TokenHandle> type : other.types.entrySet()) {
            this.addTypeVar(type.getKey(), type.getValue());
        }
        Iterator<Object> ex = other.interfaces.iterator();
        while (ex.hasNext()) {
            Token iface = (Token)ex.next();
            this.addInterface(iface);
        }
    }

    private void conform(Set<String> typeVars) {
        Iterator<TypeVar> iterator = this.types.keySet().iterator();
        while (iterator.hasNext()) {
            TypeVar typeVar = iterator.next();
            String name = this.findUniqueName(typeVar.getOriginalName(), typeVars);
            typeVar.rename(name);
            typeVars.add(name);
        }
    }

    private String findUniqueName(String typeVar, Set<String> typeVars) {
        String name;
        if (!typeVars.contains(typeVar)) {
            return typeVar;
        }
        if (typeVar.length() == 1 && (name = this.findOffsetName(typeVar.charAt(0), typeVars)) != null) {
            return name;
        }
        name = this.findOffsetName('T', typeVars, "", typeVar);
        if (name != null) {
            return name;
        }
        name = this.findOffsetName('T', typeVars, typeVar, "");
        if (name != null) {
            return name;
        }
        name = this.findOffsetName('T', typeVars, "T", typeVar);
        if (name != null) {
            return name;
        }
        name = this.findOffsetName('T', typeVars, "", typeVar + "Type");
        if (name == null) throw new IllegalStateException("Failed to conform type var: " + typeVar);
        return name;
    }

    private String findOffsetName(char c, Set<String> typeVars) {
        return this.findOffsetName(c, typeVars, "", "");
    }

    private String findOffsetName(char c, Set<String> typeVars, String prefix, String suffix) {
        String name = String.format("%s%s%s", prefix, Character.valueOf(c), suffix);
        if (!typeVars.contains(name)) {
            return name;
        }
        if (c <= '@') return null;
        if (c >= '[') return null;
        int s = c - 64;
        while (s + 65 != c) {
            name = String.format("%s%s%s", prefix, Character.valueOf((char)(s + 65)), suffix);
            if (!typeVars.contains(name)) {
                return name;
            }
            ++s;
            s %= 26;
        }
        return null;
    }

    public SignatureVisitor getRemapper() {
        return new SignatureRemapper();
    }

    public String toString() {
        while (this.rawInterfaces.size() > 0) {
            this.addRawInterface(this.rawInterfaces.remove());
        }
        StringBuilder sb = new StringBuilder();
        if (this.types.size() > 0) {
            boolean valid = false;
            StringBuilder types = new StringBuilder();
            for (Map.Entry<TypeVar, TokenHandle> type : this.types.entrySet()) {
                String bound = type.getValue().asBound();
                if (bound.isEmpty()) continue;
                types.append(type.getKey()).append(':').append(bound);
                valid = true;
            }
            if (valid) {
                sb.append('<').append(types).append('>');
            }
        }
        sb.append(this.superClass.asType());
        Iterator<Token> valid = this.interfaces.iterator();
        while (valid.hasNext()) {
            Token iface = valid.next();
            sb.append(iface.asType());
        }
        return sb.toString();
    }

    public ClassSignature wake() {
        return this;
    }

    public static ClassSignature of(String signature) {
        return new ClassSignature().read(signature);
    }

    public static ClassSignature of(ClassNode classNode) {
        if (classNode.signature == null) return ClassSignature.generate(classNode);
        return ClassSignature.of(classNode.signature);
    }

    public static ClassSignature ofLazy(ClassNode classNode) {
        if (classNode.signature == null) return ClassSignature.generate(classNode);
        return new Lazy(classNode.signature);
    }

    private static ClassSignature generate(ClassNode classNode) {
        ClassSignature generated = new ClassSignature();
        generated.setSuperClass(new Token(classNode.superName != null ? classNode.superName : OBJECT));
        Iterator<String> iterator = classNode.interfaces.iterator();
        while (iterator.hasNext()) {
            String iface = iterator.next();
            generated.addInterface(new Token(iface));
        }
        return generated;
    }

    class SignatureRemapper
    extends SignatureWriter {
        private final Set<String> localTypeVars = new HashSet<String>();

        SignatureRemapper() {
        }

        @Override
        public void visitFormalTypeParameter(String name) {
            this.localTypeVars.add(name);
            super.visitFormalTypeParameter(name);
        }

        @Override
        public void visitTypeVariable(String name) {
            TypeVar typeVar;
            if (!this.localTypeVars.contains(name) && (typeVar = ClassSignature.this.getTypeVar(name)) != null) {
                super.visitTypeVariable(typeVar.toString());
                return;
            }
            super.visitTypeVariable(name);
        }
    }

    class SignatureParser
    extends SignatureVisitor {
        private FormalParamElement param;

        SignatureParser() {
            super(327680);
        }

        @Override
        public void visitFormalTypeParameter(String name) {
            this.param = new FormalParamElement(name);
        }

        @Override
        public SignatureVisitor visitClassBound() {
            return this.param.visitClassBound();
        }

        @Override
        public SignatureVisitor visitInterfaceBound() {
            return this.param.visitInterfaceBound();
        }

        @Override
        public SignatureVisitor visitSuperclass() {
            return new SuperClassElement();
        }

        @Override
        public SignatureVisitor visitInterface() {
            return new InterfaceElement();
        }

        class InterfaceElement
        extends TokenElement {
            InterfaceElement() {
            }

            @Override
            public void visitEnd() {
                ClassSignature.this.addInterface(this.token);
            }
        }

        class SuperClassElement
        extends TokenElement {
            SuperClassElement() {
            }

            @Override
            public void visitEnd() {
                ClassSignature.this.setSuperClass(this.token);
            }
        }

        class BoundElement
        extends TokenElement {
            private final TokenElement type;
            private final boolean classBound;

            BoundElement(TokenElement type, boolean classBound) {
                this.type = type;
                this.classBound = classBound;
            }

            @Override
            public void visitClassType(String name) {
                this.token = this.type.token.addBound(name, this.classBound);
            }

            @Override
            public void visitTypeArgument() {
                this.token.addTypeArgument('*');
            }

            @Override
            public SignatureVisitor visitTypeArgument(char wildcard) {
                return new TypeArgElement(this, wildcard);
            }
        }

        class TypeArgElement
        extends TokenElement {
            private final TokenElement type;
            private final char wildcard;

            TypeArgElement(TokenElement type, char wildcard) {
                this.type = type;
                this.wildcard = wildcard;
            }

            @Override
            public SignatureVisitor visitArrayType() {
                this.type.setArray();
                return this;
            }

            @Override
            public void visitBaseType(char descriptor) {
                this.token = this.type.addTypeArgument(descriptor).asToken();
            }

            @Override
            public void visitTypeVariable(String name) {
                TokenHandle token = ClassSignature.this.getType(name);
                this.token = this.type.addTypeArgument(token).setWildcard(this.wildcard).asToken();
            }

            @Override
            public void visitClassType(String name) {
                this.token = this.type.addTypeArgument(name).setWildcard(this.wildcard).asToken();
            }

            @Override
            public void visitTypeArgument() {
                this.token.addTypeArgument('*');
            }

            @Override
            public SignatureVisitor visitTypeArgument(char wildcard) {
                return new TypeArgElement(this, wildcard);
            }

            @Override
            public void visitEnd() {
            }
        }

        class FormalParamElement
        extends TokenElement {
            private final TokenHandle handle;

            FormalParamElement(String param) {
                this.handle = ClassSignature.this.getType(param);
                this.token = this.handle.asToken();
            }
        }

        abstract class TokenElement
        extends SignatureElement {
            protected Token token;
            private boolean array;

            TokenElement() {
            }

            public Token getToken() {
                if (this.token != null) return this.token;
                this.token = new Token();
                return this.token;
            }

            protected void setArray() {
                this.array = true;
            }

            private boolean getArray() {
                boolean array = this.array;
                this.array = false;
                return array;
            }

            @Override
            public void visitClassType(String name) {
                this.getToken().setType(name);
            }

            @Override
            public SignatureVisitor visitClassBound() {
                this.getToken();
                return new BoundElement(this, true);
            }

            @Override
            public SignatureVisitor visitInterfaceBound() {
                this.getToken();
                return new BoundElement(this, false);
            }

            @Override
            public void visitInnerClassType(String name) {
                this.token.addInnerClass(name);
            }

            @Override
            public SignatureVisitor visitArrayType() {
                this.setArray();
                return this;
            }

            @Override
            public SignatureVisitor visitTypeArgument(char wildcard) {
                return new TypeArgElement(this, wildcard);
            }

            Token addTypeArgument() {
                return this.token.addTypeArgument('*').asToken();
            }

            IToken addTypeArgument(char symbol) {
                return this.token.addTypeArgument(symbol).setArray(this.getArray());
            }

            IToken addTypeArgument(String name) {
                return this.token.addTypeArgument(name).setArray(this.getArray());
            }

            IToken addTypeArgument(Token token) {
                return this.token.addTypeArgument(token).setArray(this.getArray());
            }

            IToken addTypeArgument(TokenHandle token) {
                return this.token.addTypeArgument(token).setArray(this.getArray());
            }
        }

        abstract class SignatureElement
        extends SignatureVisitor {
            public SignatureElement() {
                super(327680);
            }
        }

    }

    class TokenHandle
    implements IToken {
        final Token token;
        boolean array;
        char wildcard;

        TokenHandle() {
            this(new Token());
        }

        TokenHandle(Token token) {
            this.token = token;
        }

        @Override
        public IToken setArray(boolean array) {
            this.array |= array;
            return this;
        }

        @Override
        public IToken setWildcard(char wildcard) {
            if ("+-".indexOf(wildcard) <= -1) return this;
            this.wildcard = wildcard;
            return this;
        }

        @Override
        public String asBound() {
            return this.token.asBound();
        }

        @Override
        public String asType() {
            StringBuilder sb = new StringBuilder();
            if (this.wildcard > '\u0000') {
                sb.append(this.wildcard);
            }
            if (!this.array) return sb.append(ClassSignature.this.getTypeVar(this)).toString();
            sb.append('[');
            return sb.append(ClassSignature.this.getTypeVar(this)).toString();
        }

        @Override
        public Token asToken() {
            return this.token;
        }

        public String toString() {
            return this.token.toString();
        }

        public TokenHandle clone() {
            return new TokenHandle(this.token);
        }
    }

    static class Token
    implements IToken {
        static final String SYMBOLS = "+-*";
        private final boolean inner;
        private boolean array;
        private char symbol = '\u0000';
        private String type;
        private List<Token> classBound;
        private List<Token> ifaceBound;
        private List<IToken> signature;
        private List<IToken> suffix;
        private Token tail;

        Token() {
            this(false);
        }

        Token(String type) {
            this(type, false);
        }

        Token(char symbol) {
            this();
            this.symbol = symbol;
        }

        Token(boolean inner) {
            this(null, inner);
        }

        Token(String type, boolean inner) {
            this.inner = inner;
            this.type = type;
        }

        Token setSymbol(char symbol) {
            if (this.symbol != '\u0000') return this;
            if (SYMBOLS.indexOf(symbol) <= -1) return this;
            this.symbol = symbol;
            return this;
        }

        Token setType(String type) {
            if (this.type != null) return this;
            this.type = type;
            return this;
        }

        boolean hasClassBound() {
            if (this.classBound == null) return false;
            return true;
        }

        boolean hasInterfaceBound() {
            if (this.ifaceBound == null) return false;
            return true;
        }

        @Override
        public IToken setArray(boolean array) {
            this.array |= array;
            return this;
        }

        @Override
        public IToken setWildcard(char wildcard) {
            if ("+-".indexOf(wildcard) != -1) return this.setSymbol(wildcard);
            return this;
        }

        private List<Token> getClassBound() {
            if (this.classBound != null) return this.classBound;
            this.classBound = new ArrayList<Token>();
            return this.classBound;
        }

        private List<Token> getIfaceBound() {
            if (this.ifaceBound != null) return this.ifaceBound;
            this.ifaceBound = new ArrayList<Token>();
            return this.ifaceBound;
        }

        private List<IToken> getSignature() {
            if (this.signature != null) return this.signature;
            this.signature = new ArrayList<IToken>();
            return this.signature;
        }

        private List<IToken> getSuffix() {
            if (this.suffix != null) return this.suffix;
            this.suffix = new ArrayList<IToken>();
            return this.suffix;
        }

        IToken addTypeArgument(char symbol) {
            if (this.tail != null) {
                return this.tail.addTypeArgument(symbol);
            }
            Token token = new Token(symbol);
            this.getSignature().add(token);
            return token;
        }

        IToken addTypeArgument(String name) {
            if (this.tail != null) {
                return this.tail.addTypeArgument(name);
            }
            Token token = new Token(name);
            this.getSignature().add(token);
            return token;
        }

        IToken addTypeArgument(Token token) {
            if (this.tail != null) {
                return this.tail.addTypeArgument(token);
            }
            this.getSignature().add(token);
            return token;
        }

        IToken addTypeArgument(TokenHandle token) {
            if (this.tail != null) {
                return this.tail.addTypeArgument(token);
            }
            TokenHandle handle = token.clone();
            this.getSignature().add(handle);
            return handle;
        }

        Token addBound(String bound, boolean classBound) {
            if (!classBound) return this.addInterfaceBound(bound);
            return this.addClassBound(bound);
        }

        Token addClassBound(String bound) {
            Token token = new Token(bound);
            this.getClassBound().add(token);
            return token;
        }

        Token addInterfaceBound(String bound) {
            Token token = new Token(bound);
            this.getIfaceBound().add(token);
            return token;
        }

        Token addInnerClass(String name) {
            this.tail = new Token(name, true);
            this.getSuffix().add(this.tail);
            return this.tail;
        }

        public String toString() {
            return this.asType();
        }

        @Override
        public String asBound() {
            StringBuilder sb = new StringBuilder();
            if (this.type != null) {
                sb.append(this.type);
            }
            if (this.classBound != null) {
                for (Token token : this.classBound) {
                    sb.append(token.asType());
                }
            }
            if (this.ifaceBound == null) return sb.toString();
            Iterator<Token> iterator = this.ifaceBound.iterator();
            while (iterator.hasNext()) {
                Token token;
                token = iterator.next();
                sb.append(':').append(token.asType());
            }
            return sb.toString();
        }

        @Override
        public String asType() {
            return this.asType(false);
        }

        public String asType(boolean raw) {
            StringBuilder sb = new StringBuilder();
            if (this.array) {
                sb.append('[');
            }
            if (this.symbol != '\u0000') {
                sb.append(this.symbol);
            }
            if (this.type == null) {
                return sb.toString();
            }
            if (!this.inner) {
                sb.append('L');
            }
            sb.append(this.type);
            if (!raw) {
                if (this.signature != null) {
                    sb.append('<');
                    for (IToken token : this.signature) {
                        sb.append(token.asType());
                    }
                    sb.append('>');
                }
                if (this.suffix != null) {
                    for (IToken token : this.suffix) {
                        sb.append('.').append(token.asType());
                    }
                }
            }
            if (this.inner) return sb.toString();
            sb.append(';');
            return sb.toString();
        }

        boolean isRaw() {
            if (this.signature != null) return false;
            return true;
        }

        String getClassType() {
            if (this.type == null) return ClassSignature.OBJECT;
            String string = this.type;
            return string;
        }

        @Override
        public Token asToken() {
            return this;
        }
    }

    static interface IToken {
        public static final String WILDCARDS = "+-";

        public String asType();

        public String asBound();

        public Token asToken();

        public IToken setArray(boolean var1);

        public IToken setWildcard(char var1);
    }

    static class TypeVar
    implements Comparable<TypeVar> {
        private final String originalName;
        private String currentName;

        TypeVar(String name) {
            this.currentName = this.originalName = name;
        }

        @Override
        public int compareTo(TypeVar other) {
            return this.currentName.compareTo(other.currentName);
        }

        public String toString() {
            return this.currentName;
        }

        String getOriginalName() {
            return this.originalName;
        }

        void rename(String name) {
            this.currentName = name;
        }

        public boolean matches(String originalName) {
            return this.originalName.equals(originalName);
        }

        public boolean equals(Object obj) {
            return this.currentName.equals(obj);
        }

        public int hashCode() {
            return this.currentName.hashCode();
        }
    }

    static class Lazy
    extends ClassSignature {
        private final String sig;
        private ClassSignature generated;

        Lazy(String sig) {
            this.sig = sig;
        }

        @Override
        public ClassSignature wake() {
            if (this.generated != null) return this.generated;
            this.generated = ClassSignature.of(this.sig);
            return this.generated;
        }
    }

}

