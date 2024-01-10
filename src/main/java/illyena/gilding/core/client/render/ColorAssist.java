package illyena.gilding.core.client.render;

import net.minecraft.util.DyeColor;

import java.awt.*;
import java.util.Arrays;

@SuppressWarnings("unused")
public class ColorAssist {
    public static final Color[] JEB_COLORS = Arrays.stream(DyeColor.values()).filter(c -> c != DyeColor.BLACK && c != DyeColor.WHITE).map(ColorAssist::toColor).toArray(Color[]::new);
    private final float totalTime;
    private final Color[] colors;
    private final float segmentTime;
    public final boolean isContinuous;
    private boolean inProgress = false;


    public ColorAssist(float totalTicks, Color... colors) {
        this.totalTime = totalTicks;
        this.colors = colors;
        this.segmentTime = totalTicks / (this.colors.length - 1);
        this.isContinuous = false;
    }

    public ColorAssist(float segmentTime, boolean continuous, Color... colors) {
        this.colors = colors;
        this.segmentTime = segmentTime;
        this.totalTime = segmentTime * (this.colors.length - 1);
        this.isContinuous = continuous;

    }

    public static float[] getJebRGB(float tick, float tickDelta) {
        float[] color;

        float segmentTime = 25.0f;
        int previousColorIndex = (int) (Math.floor(tick / segmentTime) % DyeColor.values().length);
        int nextIndex = previousColorIndex + 1 < DyeColor.values().length ? previousColorIndex + 1 : 0;

        float[] color1 = DyeColor.values()[previousColorIndex].getColorComponents();
        float[] color2 = DyeColor.values()[nextIndex].getColorComponents();
        float colorFactor = ((tick % segmentTime) + tickDelta) / segmentTime;

        float red   = color1[0] * (1.0F - colorFactor) + color2[0] * colorFactor;
        float green = color1[1] * (1.0F - colorFactor) + color2[1] * colorFactor;
        float blue  = color1[2] * (1.0F - colorFactor) + color2[2] * colorFactor;
        color = new float[]{red, green, blue};

        return color;

    }

    public static int getJebInt(int ticks, float tickDelta) {
        float[] jeb = getJebRGB(ticks, tickDelta);
        return new Color(jeb[0], jeb[1], jeb[2]).getRGB();
    }

    public float[] getChromaticFadeColorRGB(float tick, float tickDelta) {
        if (!this.isContinuous) {
            tick = tick % this.totalTime;
        }

        float[] color = this.colors[0].getColorComponents(null);

        int previousColorIndex = (int) (Math.floor(tick / this.segmentTime) % this.colors.length);
        int nextIndex = previousColorIndex + 1 < this.colors.length ? previousColorIndex + 1 : 0;

        if (this.inProgress) {
            float[] color1 = this.colors[previousColorIndex].getColorComponents(null);
            float[] color2 = this.colors[nextIndex].getColorComponents(null);
            float colorFactor = ((tick % this.segmentTime) + tickDelta) / this.segmentTime;

            float red   = color1[0] * (1.0f - colorFactor) + color2[0] * colorFactor;
            float green = color1[1] * (1.0f - colorFactor) + color2[1] * colorFactor;
            float blue  = color1[2] * (1.0f - colorFactor) + color2[2] * colorFactor;
            color = new float[]{red, green, blue};

        } else {
            this.inProgress = true;
        }

        return color;
    }

    public int getChromaticFadeColorInt(int tick, float tickDelta) {
        float[] currentColor = getChromaticFadeColorRGB(tick, tickDelta);
        return new Color(currentColor[0], currentColor[1], currentColor[2]).getRGB();
    }

    public static Color toColor(DyeColor dyeColor) {
        return new Color(dyeColor.getColorComponents()[0], dyeColor.getColorComponents()[1], dyeColor.getColorComponents()[2]);
    }

}
