package ru.avklimenko.mandelbrot;

import com.badlogic.gdx.graphics.Color;

public class Config {
    public static final int SIZE = 1080;
    public static final String TITLE = "∙∙∙Mandelbrot∙∙∙";
    private static final int RR = 31;
    private static final int GG = 27;
    private static final int BB = 23;
    public static final int NN = 5000;

    public static final int[] COLOR = new int[NN];
    public static final Color GRID_COLOR = Color.ORANGE;

    static {
        float delta = 0.01f;
        for (int i = 0; i < NN; ++i) {
            float r = (float) (delta + (0.5 - delta / 2) * (Math.sin(i * 1f / RR) + 1));
            float g = (float) (delta + (0.5 - delta / 2) * (Math.sin(i * 1f / GG) + 1));
            float b = (float) (delta + (0.5 - delta / 2) * (Math.sin(i * 1f / BB) + 1));
            int ir = (int) (r * 256);
            int ig = (int) (g * 256);
            int ib = (int) (b * 256);
            COLOR[NN - i - 1] = 256 * (256 * (ir * 256 + ig) + ib) + 255;
        }
        COLOR[NN - 1] = 0x000000ff;
    }
}
