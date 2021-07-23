/*
 * Decompiled with CFR <Could not determine version>.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 */
package org.spongepowered.asm.mixin.transformer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.lib.tree.FieldNode;
import org.spongepowered.asm.mixin.transformer.ClassInfo;
import org.spongepowered.asm.mixin.transformer.MixinApplicatorStandard;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;
import org.spongepowered.asm.mixin.transformer.TargetClassContext;

class MixinApplicatorInterface
extends MixinApplicatorStandard {
    MixinApplicatorInterface(TargetClassContext context) {
        super(context);
    }

    @Override
    protected void applyInterfaces(MixinTargetContext mixin) {
        Iterator<String> iterator = mixin.getInterfaces().iterator();
        while (iterator.hasNext()) {
            String interfaceName = iterator.next();
            if (this.targetClass.name.equals(interfaceName) || this.targetClass.interfaces.contains(interfaceName)) continue;
            this.targetClass.interfaces.add(interfaceName);
            mixin.getTargetClassInfo().addInterface(interfaceName);
        }
    }

    @Override
    protected void applyFields(MixinTargetContext mixin) {
        Iterator<Map.Entry<FieldNode, ClassInfo.Field>> iterator = mixin.getShadowFields().iterator();
        do {
            if (!iterator.hasNext()) {
                this.mergeNewFields(mixin);
                return;
            }
            Map.Entry<FieldNode, ClassInfo.Field> entry = iterator.next();
            FieldNode shadow = entry.getKey();
            this.logger.error("Ignoring redundant @Shadow field {}:{} in {}", new Object[]{shadow.name, shadow.desc, mixin});
        } while (true);
    }

    @Override
    protected void applyInitialisers(MixinTargetContext mixin) {
    }

    /*
     * Exception decompiling
     */
    @Override
    protected void prepareInjections(MixinTargetContext mixin) {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[DOLOOP]], but top level block is 0[TRYBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:906)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:797)
        // org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:225)
        // org.benf.cfr.reader.Driver.doJar(Driver.java:109)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        // the.bytecode.club.bytecodeviewer.decompilers.CFRDecompiler.decompileToZip(CFRDecompiler.java:311)
        // the.bytecode.club.bytecodeviewer.gui.MainViewerGUI$14$1$7.run(MainViewerGUI.java:1287)
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    protected void applyInjections(MixinTargetContext mixin) {
    }
}

