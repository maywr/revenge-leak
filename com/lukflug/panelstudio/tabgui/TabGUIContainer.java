/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio.tabgui;

import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.tabgui.TabGUIComponent;
import com.lukflug.panelstudio.tabgui.TabGUIRenderer;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class TabGUIContainer
implements TabGUIComponent {
    protected String title;
    protected TabGUIRenderer renderer;
    protected List<TabGUIComponent> components;
    protected boolean childOpen = false;
    protected int selected = 0;
    protected Animation selectedAnimation = null;

    public TabGUIContainer(String title, TabGUIRenderer renderer, Animation animation) {
        this.title = title;
        this.renderer = renderer;
        this.components = new ArrayList<TabGUIComponent>();
        if (animation == null) return;
        animation.initValue(this.selected);
        this.selectedAnimation = animation;
    }

    public void addComponent(TabGUIComponent component) {
        this.components.add(component);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void render(Context context) {
        this.getHeight(context);
        int offset = this.selected * this.renderer.getHeight();
        if (this.selectedAnimation != null) {
            offset = (int)(this.selectedAnimation.getValue() * (double)this.renderer.getHeight());
        }
        this.renderer.renderBackground(context, offset, this.renderer.getHeight());
        int i = 0;
        do {
            if (i >= this.components.size()) {
                if (!this.childOpen) return;
                this.components.get(this.selected).render(this.getSubContext(context));
                return;
            }
            TabGUIComponent component = this.components.get(i);
            this.renderer.renderCaption(context, component.getTitle(), i, this.renderer.getHeight(), component.isActive());
            ++i;
        } while (true);
    }

    @Override
    public void handleButton(Context context, int button) {
        this.getHeight(context);
    }

    @Override
    public void handleKey(Context context, int scancode) {
        this.getHeight(context);
        if (this.renderer.isEscapeKey(scancode)) {
            this.childOpen = false;
            return;
        }
        if (this.childOpen) {
            this.components.get(this.selected).handleKey(this.getSubContext(context), scancode);
            return;
        }
        if (this.renderer.isUpKey(scancode)) {
            if (--this.selected < 0) {
                this.selected = this.components.size() - 1;
            }
            if (this.selectedAnimation == null) return;
            this.selectedAnimation.setValue(this.selected);
            return;
        }
        if (!this.renderer.isDownKey(scancode)) {
            if (!this.renderer.isSelectKey(scancode)) return;
            if (!this.components.get(this.selected).select()) return;
            this.childOpen = true;
            return;
        }
        if (++this.selected >= this.components.size()) {
            this.selected = 0;
        }
        if (this.selectedAnimation == null) return;
        this.selectedAnimation.setValue(this.selected);
    }

    @Override
    public void handleScroll(Context context, int diff) {
        this.getHeight(context);
    }

    @Override
    public void getHeight(Context context) {
        context.setHeight(this.renderer.getHeight() * this.components.size());
    }

    @Override
    public void enter(Context context) {
        this.getHeight(context);
    }

    @Override
    public void exit(Context context) {
        this.getHeight(context);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public boolean select() {
        return true;
    }

    protected Context getSubContext(Context context) {
        Point p = context.getPos();
        p.translate(context.getSize().width + this.renderer.getBorder(), this.selected * this.renderer.getHeight());
        return new Context(context.getInterface(), context.getSize().width, p, context.hasFocus(), context.onTop());
    }

    @Override
    public void releaseFocus() {
    }
}

