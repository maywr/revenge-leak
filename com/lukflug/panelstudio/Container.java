/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import com.lukflug.panelstudio.Component;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.theme.Renderer;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Container
extends FocusableComponent {
    protected List<Component> components = new ArrayList<Component>();
    private String tempDescription;

    public Container(String title, String description, Renderer renderer) {
        super(title, description, renderer);
    }

    public void addComponent(Component component) {
        this.components.add(component);
    }

    @Override
    public void render(Context context) {
        this.tempDescription = null;
        this.doComponentLoop(context, (subContext, component) -> {
            component.render(subContext);
            if (!subContext.isHovered()) return;
            if (subContext.getDescription() == null) return;
            this.tempDescription = subContext.getDescription();
        });
        if (this.tempDescription == null) {
            this.tempDescription = this.description;
        }
        context.setDescription(this.tempDescription);
    }

    @Override
    public void handleButton(Context context, int button) {
        this.getHeight(context);
        this.updateFocus(context, button);
        this.doComponentLoop(context, (subContext, component) -> {
            component.handleButton(subContext, button);
            if (!subContext.focusReleased()) return;
            context.releaseFocus();
        });
    }

    @Override
    public void handleKey(Context context, int scancode) {
        this.doComponentLoop(context, (subContext, component) -> component.handleKey(subContext, scancode));
    }

    @Override
    public void handleScroll(Context context, int diff) {
        this.doComponentLoop(context, (subContext, component) -> component.handleScroll(subContext, diff));
    }

    @Override
    public void getHeight(Context context) {
        this.doComponentLoop(context, (subContext, component) -> component.getHeight(subContext));
    }

    @Override
    public void enter(Context context) {
        this.doComponentLoop(context, (subContext, component) -> component.enter(subContext));
    }

    @Override
    public void exit(Context context) {
        this.doComponentLoop(context, (subContext, component) -> component.exit(subContext));
    }

    @Override
    public void releaseFocus() {
        super.releaseFocus();
        Iterator<Component> iterator = this.components.iterator();
        while (iterator.hasNext()) {
            Component component = iterator.next();
            component.releaseFocus();
        }
    }

    @Override
    protected void handleFocus(Context context, boolean focus) {
        if (focus) return;
        this.releaseFocus();
    }

    protected Context getSubContext(Context context, int posy) {
        return new Context(context, this.renderer.getBorder(), this.renderer.getBorder(), posy, this.hasFocus(context), true);
    }

    protected void doComponentLoop(Context context, LoopFunction function) {
        int posy = this.renderer.getOffset();
        Iterator<Component> iterator = this.components.iterator();
        do {
            if (!iterator.hasNext()) {
                context.setHeight(posy);
                return;
            }
            Component component = iterator.next();
            Context subContext = this.getSubContext(context, posy);
            function.loop(subContext, component);
            posy += subContext.getSize().height + this.renderer.getOffset();
        } while (true);
    }

    protected static interface LoopFunction {
        public void loop(Context var1, Component var2);
    }

}

