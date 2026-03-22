package org.example.core;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.color.ColorPickerWidget.ColorPickerSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;

@ConfigName("settings")
public class TntTimerConfiguration extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> dynamicColors = new ConfigProperty<>(true);

  @SettingRequires(value = "dynamicColors", invert = true)
  @ColorPickerSetting
  private final ConfigProperty<Integer> staticTextColor = new ConfigProperty<>(0xFFFFFF);

  public ConfigProperty<Boolean> enabled() {
    return enabled;
  }

  public ConfigProperty<Boolean> dynamicColors() {
    return dynamicColors;
  }

  public ConfigProperty<Integer> staticTextColor() {
    return staticTextColor;
  }
}
