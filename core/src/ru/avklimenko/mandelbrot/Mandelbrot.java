package ru.avklimenko.mandelbrot;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

import static ru.avklimenko.mandelbrot.Config.COLOR;

public class Mandelbrot {
    private static int pow = 2;
    private static final int NCpu = 12;
    private static final List<RunTask> runnable = new ArrayList<>();

    static {
        for (int i = 0; i < NCpu; i++) {
            runnable.add(new RunTask(i));
        }
    }

    private static class RunTask implements Runnable {
        private static int[][] grid;
        private static double reMin, reMax, imMin, imMax;
        private final int n;

        RunTask(int n) {
            this.n = n;
        }

        public static void setParams(int[][] grid, double reMin, double reMax, double imMin, double imMax) {
            RunTask.grid = grid;
            RunTask.reMin = reMin;
            RunTask.reMax = reMax;
            RunTask.imMin = imMin;
            RunTask.imMax = imMax;
        }

        public void run() {
            for (int y = n; y < Config.SIZE; y += NCpu) {
                fillGrid(grid, reMin, reMax, imMin, imMax, y, y +1);
            }
        }
    }

    public static void setPower(int pow) {
        assert pow > 1 && pow < 10;
        Mandelbrot.pow = pow;
    }

    public static int getN(Complex c0) {
        int n = -1;
        Complex z = new Complex(0, 0);
        while (z.r2() < 4. && n++ < Config.NN - 2) {
            z.pow(pow);
            z.add(c0);
        }
        return Math.min(n, Config.NN);
    }

    private static void fillGrid(int[][] grid, double reMin, double reMax, double imMin, double imMax,
                                 int yMin, int yMax) {
        double dx = (reMax - reMin) / grid[0].length;
        double dy = (imMax - imMin) / grid.length;
        for (int y = yMin; y < yMax; ++y) {
            double im = imMin + y * dy;
            for (int x = 0; x < grid[y].length; ++x) {
                double re = reMin + x * dx;
                Complex c0 = new Complex(re, im);
                grid[y][x] = COLOR[getN(c0)];
            }
        }
    }

    public static void fillGrid(int[][] grid, double reMin, double reMax, double imMin, double imMax) {
        long start = System.currentTimeMillis();

        RunTask.setParams(grid, reMin, reMax, imMin, imMax);
        List<Thread> threads = new ArrayList<>();
        runnable.forEach(r -> threads.add(new Thread(r)));
        threads.forEach(t -> {
                    t.setPriority(Thread.MAX_PRIORITY);
                    t.start();
        });
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        long end = System.currentTimeMillis();
        String zoom = String.format("%.2f", 4.0 / (reMax - reMin));
        Gdx.graphics.setTitle(Config.TITLE + " (" + (end - start) + " ms. zoom: " + zoom + ", pow:" + pow + ")");
    }
}
