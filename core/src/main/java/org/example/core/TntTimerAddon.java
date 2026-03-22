package org.example.core;

import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;
import org.example.core.render.TntTimerWorldListener;

@AddonMain
public class TntTimerAddon extends LabyAddon<TntTimerConfiguration> {

  protected void enable() {
    registerSettingCategory();
    registerListener(new TntTimerWorldListener(this));
  }

  protected Class<TntTimerConfiguration> configurationClass() {
    return TntTimerConfiguration.class;
  }
}
