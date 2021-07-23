/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.Component;
import com.lukflug.panelstudio.Container;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.settings.AnimatedToggleable;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.theme.Renderer;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class CollapsibleContainer
extends FocusableComponent
implements Toggleable {
    protected Container container;
    protected AnimatedToggleable open;
    protected Toggleable toggle;
    protected int childHeight = 0;
    protected int containerHeight = 0;
    protected boolean scroll = false;
    protected int scrollPosition = 0;

    public CollapsibleContainer(String title, String description, Renderer renderer, Toggleable open, Animation animation, Toggleable toggle) {
        super(title, description, renderer);
        this.container = new Container(title, null, renderer);
        this.open = new AnimatedToggleable(open, animation);
        this.toggle = toggle;
    }

    public void addComponent(Component component) {
        this.container.addComponent(component);
    }

    @Override
    public void render(Context context) {
        this.getHeight(context);
        this.renderer.renderBackground(context, this.hasFocus(context));
        super.render(context);
        this.renderer.renderTitle(context, this.title, this.hasFocus(context), this.isActive(), this.open.getValue() != 0.0);
        if (this.open.getValue() != 0.0) {
            boolean onTop;
            Context subContext = this.getSubContext(context, this.open.getValue() == 1.0);
            this.container.getHeight(subContext);
            Rectangle rect = this.getClipRect(context, subContext.getSize().height);
            boolean bl = onTop = this.open.getValue() == 1.0;
            if (rect != null) {
                onTop = rect.contains(context.getInterface().getMouse());
                context.getInterface().window(rect);
            }
            subContext = this.getSubContext(context, onTop);
            this.container.render(subContext);
            if (rect != null) {
                context.getInterface().restore();
            }
            if (subContext.isHovered()) {
                context.setDescription(subContext.getDescription());
            }
            context.setHeight(this.getRenderHeight(subContext.getSize().height));
            this.scrollPosition = this.renderer.renderScrollBar(context, this.hasFocus(context), this.isActive(), this.scroll, this.childHeight, this.scrollPosition);
            if (this.scrollPosition > this.childHeight - this.containerHeight) {
                this.scrollPosition = this.childHeight - this.containerHeight;
            }
            if (this.scrollPosition < 0) {
                this.scrollPosition = 0;
            }
        }
        this.renderer.renderBorder(context, this.hasFocus(context), this.isActive(), this.open.getValue() != 0.0);
    }

    @Override
    public void handleButton(Context context, int button) {
        context.setHeight(this.renderer.getHeight(this.open.getValue() != 0.0));
        if (context.isClicked() && button == 0) {
            if (this.toggle != null) {
                this.toggle.toggle();
            }
        } else if (context.isHovered() && button == 1 && context.getInterface().getButton(1)) {
            this.open.toggle();
        }
        if (this.open.getValue() != 1.0) {
            super.handleButton(context, button);
            return;
        }
        Context subContext = this.getSubContext(context, true);
        this.container.getHeight(subContext);
        context.setHeight(this.getRenderHeight(subContext.getSize().height));
        this.updateFocus(context, button);
        boolean onTop = true;
        Rectangle rect = this.getClipRect(context, subContext.getSize().height);
        if (rect != null) {
            onTop = rect.contains(context.getInterface().getMouse());
        }
        subContext = this.getSubContext(context, onTop);
        this.container.handleButton(subContext, button);
        context.setHeight(this.getRenderHeight(subContext.getSize().height));
        if (!subContext.focusReleased()) return;
        context.releaseFocus();
    }

    @Override
    public void handleKey(Context context, int scancode) {
        if (this.open.getValue() == 1.0) {
            Context subContext = this.getSubContext(context, true);
            this.container.handleKey(subContext, scancode);
            context.setHeight(this.getRenderHeight(subContext.getSize().height));
            return;
        }
        super.handleKey(context, scancode);
    }

    @Override
    public void handleScroll(Context context, int diff) {
        if (this.open.getValue() != 1.0) {
            super.handleKey(context, diff);
            return;
        }
        Context subContext = this.getSubContext(context, true);
        this.container.handleKey(subContext, diff);
        context.setHeight(this.getRenderHeight(subContext.getSize().height));
        if (!subContext.isHovered()) return;
        this.scrollPosition += diff;
        if (this.scrollPosition > this.childHeight - this.containerHeight) {
            this.scrollPosition = this.childHeight - this.containerHeight;
        }
        if (this.scrollPosition >= 0) return;
        this.scrollPosition = 0;
    }

    @Override
    public void getHeight(Context context) {
        if (this.open.getValue() != 0.0) {
            Context subContext = this.getSubContext(context, true);
            this.container.getHeight(subContext);
            context.setHeight(this.getRenderHeight(subContext.getSize().height));
            return;
        }
        super.getHeight(context);
    }

    @Override
    public void enter(Context context) {
        if (this.open.getValue() == 1.0) {
            Context subContext = this.getSubContext(context, true);
            this.container.enter(subContext);
            context.setHeight(this.getRenderHeight(subContext.getSize().height));
            return;
        }
        super.enter(context);
    }

    @Override
    public void exit(Context context) {
        if (this.open.getValue() == 1.0) {
            Context subContext = this.getSubContext(context, true);
            this.container.exit(subContext);
            context.setHeight(this.getRenderHeight(subContext.getSize().height));
            return;
        }
        super.exit(context);
    }

    protected boolean isActive() {
        if (this.toggle != null) return this.toggle.isOn();
        return true;
    }

    protected int getContainerOffset() {
        boolean bl;
        if (this.scrollPosition > this.childHeight - this.containerHeight) {
            this.scrollPosition = this.childHeight - this.containerHeight;
        }
        if (this.scrollPosition < 0) {
            this.scrollPosition = 0;
        }
        if (this.open.getValue() != 0.0) {
            bl = true;
            return (int)((double)(this.renderer.getHeight(bl) - this.scrollPosition) - (1.0 - this.open.getValue()) * (double)this.containerHeight);
        }
        bl = false;
        return (int)((double)(this.renderer.getHeight(bl) - this.scrollPosition) - (1.0 - this.open.getValue()) * (double)this.containerHeight);
    }

    protected int getScrollHeight(int childHeight) {
        return childHeight;
    }

    protected int getRenderHeight(int childHeight) {
        boolean bl;
        this.childHeight = childHeight;
        this.containerHeight = this.getScrollHeight(childHeight);
        boolean bl2 = this.scroll = childHeight > this.containerHeight;
        if (this.scrollPosition > childHeight - this.containerHeight) {
            this.scrollPosition = childHeight - this.containerHeight;
        }
        if (this.scrollPosition < 0) {
            this.scrollPosition = 0;
        }
        if (this.open.getValue() != 0.0) {
            bl = true;
            return (int)((double)this.containerHeight * this.open.getValue() + (double)this.renderer.getHeight(bl) + (double)this.renderer.getBottomBorder());
        }
        bl = false;
        return (int)((double)this.containerHeight * this.open.getValue() + (double)this.renderer.getHeight(bl) + (double)this.renderer.getBottomBorder());
    }

    protected Rectangle getClipRect(Context context, int height) {
        boolean bl;
        boolean bl2 = this.open.getValue() != 0.0;
        if (this.open.getValue() != 0.0) {
            bl = true;
            return new Rectangle(context.getPos().x + this.renderer.getLeftBorder(this.scroll), context.getPos().y + this.renderer.getHeight(bl2), context.getSize().width - this.renderer.getLeftBorder(this.scroll) - this.renderer.getRightBorder(this.scroll), this.getRenderHeight(height) - this.renderer.getHeight(bl) - this.renderer.getBottomBorder());
        }
        bl = false;
        return new Rectangle(context.getPos().x + this.renderer.getLeftBorder(this.scroll), context.getPos().y + this.renderer.getHeight(bl2), context.getSize().width - this.renderer.getLeftBorder(this.scroll) - this.renderer.getRightBorder(this.scroll), this.getRenderHeight(height) - this.renderer.getHeight(bl) - this.renderer.getBottomBorder());
    }

    @Override
    public void toggle() {
        this.open.toggle();
        if (this.open.isOn()) return;
        this.container.releaseFocus();
    }

    @Override
    public boolean isOn() {
        return this.open.isOn();
    }

    protected Context getSubContext(Context context, boolean onTop) {
        return new Context(context, this.renderer.getLeftBorder(this.scroll), this.renderer.getRightBorder(this.scroll), this.getContainerOffset(), this.hasFocus(context), onTop);
    }
}

