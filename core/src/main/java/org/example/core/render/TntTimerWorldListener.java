package org.example.core.render;

import java.util.Locale;
import net.labymod.api.Laby;
import net.labymod.api.client.Minecraft;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.format.TextColor;
import net.labymod.api.client.entity.Entity;
import net.labymod.api.client.entity.item.PrimedTnt;
import net.labymod.api.client.entity.player.ClientPlayer;
import net.labymod.api.client.options.Perspective;
import net.labymod.api.client.render.RenderPipeline;
import net.labymod.api.client.render.matrix.Stack;
import net.labymod.api.client.world.ClientWorld;
import net.labymod.api.client.world.MinecraftCamera;
import net.labymod.api.event.Phase;
import net.labymod.api.event.Subscribe;
import net.labymod.api.event.client.render.world.RenderWorldEvent;
import net.labymod.api.laby3d.Laby3D;
import net.labymod.api.util.math.vector.DoubleVector3;
import org.example.core.TntTimerAddon;
import org.example.core.TntTimerConfiguration;
import org.joml.Matrix4f;

public class TntTimerWorldListener {

  private static final float LABEL_SCALE = 0.016666668F * 1.6F;
  private static final double MAX_DIST_SQ = 64.0 * 64.0;
  private static final int FULL_BRIGHT = 0xF000F0;
  private static final float FUSE_FOR_GRADIENT = 80f;

  private final TntTimerAddon addon;

  public TntTimerWorldListener(TntTimerAddon addon) {
    this.addon = addon;
  }

  @Subscribe
  public void onRenderWorld(RenderWorldEvent event) {
    Minecraft mcEarly = Laby.labyAPI().minecraft();
    boolean mc1165 = minecraftVersionStartsWith(mcEarly, "1.16.5");
    if (mc1165) {
      if (event.phase() != Phase.PRE) {
        return;
      }
    } else if (event.phase() != Phase.POST) {
      return;
    }
    TntTimerConfiguration cfg = addon.configuration();
    if (!cfg.enabled().get()) {
      return;
    }
    Minecraft mc = mcEarly;
    if (!mc.isIngame()) {
      return;
    }
    ClientWorld world = mc.clientWorld();
    ClientPlayer player = mc.getClientPlayer();
    if (world == null || player == null) {
      return;
    }
    float pt = event.getPartialTicks();
    MinecraftCamera camera = event.camera();
    DoubleVector3 cam = camera.renderPosition();
    RenderPipeline pipeline = Laby.labyAPI().renderPipeline();
    boolean frontThird = mc.options().perspective() == Perspective.THIRD_PERSON_FRONT;
    float pitch = camera.getPitch() * (frontThird ? -1f : 1f);
    Stack stack = event.stack();

    Laby3D laby3d = Laby.references().laby3D();
    laby3d.storeStates();
    try {
      for (Entity entity : world.getEntities()) {
        if (!(entity instanceof PrimedTnt tnt)) {
          continue;
        }
        if (entity.getDistanceSquared(player) > MAX_DIST_SQ) {
          continue;
        }
        float ticks = tnt.getFuse() - pt;
        if (ticks < 1f) {
          continue;
        }
        float h = (float) entity.axisAlignedBoundingBox().getHeight();
        var cur = entity.position();
        var prev = entity.previousPosition();
        double x = prev.lerpX(cur, pt) - cam.getX();
        double y =
            prev.lerpY(cur, pt) - cam.getY() + h + 0.5 + (mc1165 ? 0.12 : 0.0);
        double z = prev.lerpZ(cur, pt) - cam.getZ();
        String text = String.format(Locale.US, "%.2f", ticks / 20f);
        Component line = coloredLine(text, ticks, cfg);
        float w = pipeline.textRenderer().getWidth(line);

        stack.push();
        stack.translate((float) x, (float) y, (float) z);
        stack.rotate(-camera.getYaw(), 0f, 1f, 0f);
        stack.rotate(pitch, 1f, 0f, 0f);
        stack.scale(-LABEL_SCALE, -LABEL_SCALE, LABEL_SCALE);
        Matrix4f pose = stack.getProvider().getPose();
        pipeline
            .textRenderer()
            .render(pose, line, -w / 2f, 0f, 0xFFFFFFFF, FULL_BRIGHT, FULL_BRIGHT, FULL_BRIGHT);
        stack.pop();
      }
    } finally {
      laby3d.restoreStates();
    }
  }

  private static boolean minecraftVersionStartsWith(Minecraft mc, String prefix) {
    if (mc == null || prefix == null) {
      return false;
    }
    String v = mc.getVersion();
    return v != null && v.startsWith(prefix);
  }

  private static Component coloredLine(String text, float fuseTicks, TntTimerConfiguration cfg) {
    if (!cfg.dynamicColors().get()) {
      int rgb = cfg.staticTextColor().get();
      int r = (rgb >> 16) & 0xFF;
      int g = (rgb >> 8) & 0xFF;
      int b = rgb & 0xFF;
      return Component.text(text, TextColor.color(r, g, b));
    }
    float t = Math.min(Math.max(fuseTicks, 0f) / FUSE_FOR_GRADIENT, 1f);
    int r = Math.round(255f * (1f - t));
    int g = Math.round(255f * t);
    return Component.text(text, TextColor.color(r, g, 0));
  }
}
