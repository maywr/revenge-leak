/*
 * Decompiled with CFR <Could not determine version>.
 */
package com.rianix.revenge.setting;

import com.rianix.revenge.module.Module;
import com.rianix.revenge.setting.Setting;
import java.util.ArrayList;
import java.util.Iterator;

public class SettingsManager {
    public ArrayList<Setting> settings = new ArrayList();

    public ArrayList<Setting> getSettingsInMod(Module mod) {
        ArrayList<Setting> sets = new ArrayList<Setting>();
        Iterator<Setting> iterator = this.settings.iterator();
        while (iterator.hasNext()) {
            Setting s = iterator.next();
            if (s.getMod() != mod) continue;
            sets.add(s);
        }
        return sets;
    }

    public void setSettings(ArrayList<Setting> settings) {
        this.settings = settings;
    }
}

