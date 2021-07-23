/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.lukflug.panelstudio;

import com.lukflug.panelstudio.Component;
import com.lukflug.panelstudio.Interface;
import com.lukflug.panelstudio.PanelConfig;
import java.awt.Point;

public interface FixedComponent
extends Component {
    public Point getPosition(Interface var1);

    public void setPosition(Interface var1, Point var2);

    public int getWidth(Interface var1);

    public void saveConfig(Interface var1, PanelConfig var2);

    public void loadConfig(Interface var1, PanelConfig var2);
}

