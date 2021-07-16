package ru.avklimenko.mandelbrot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import static com.badlogic.gdx.Input.Keys.*;
import static ru.avklimenko.mandelbrot.Mandelbrot.fillGrid;


public class MandelbrotScreen implements Screen {
    private final SpriteBatch batch;
    private final Pixmap pixmap = new Pixmap(Config.SIZE, Config.SIZE, Pixmap.Format.RGBA8888);
    private final Pixmap mPixmap = new Pixmap(Config.SIZE, Config.SIZE, Pixmap.Format.RGBA8888);
    private final Pixmap dPixmap = new Pixmap(Config.SIZE, Config.SIZE, Pixmap.Format.RGBA8888);
    private int count = 0;

    private final Texture bgTexture = new Texture(pixmap);
    private int x0 = -1;
    private int y0 = -1;
    private int x1 = -1;
    private int y1 = -1;
    private double reMin = -2.;
    private double reMax = 2.;
    private double imMin = -2.;
    private double imMax = 2.;
    private Mode mode = Mode.EXPLORE;
    private Complex finalPoint;
    private double finalWidth;

    private final int[][] grid = new int[Config.SIZE][Config.SIZE];

    public MandelbrotScreen() {
        batch = new SpriteBatch();
        addInputProcessor();
        drawGrid();
    }


    private void drawGrid() {
        mPixmap.setColor(Color.CLEAR);
        mPixmap.fill();
        fillGrid(grid, reMin, reMax, imMin, imMax);
        for (int y = 0; y < grid.length; ++y) {
            for (int x = 0; x < grid[y].length; ++x) {
                mPixmap.drawPixel(x, y, grid[y][x]);
            }
        }
    }

    private double translate(double min, double max, int i) {
        return min + i * (max - min) / Config.SIZE;
    }

    public void addInputProcessor() {
        Gdx.input.setInputProcessor(new InputAdapter(){
            public boolean touchDown(int x, int y, int pointer, int button) {
                x0 = x;
                y0 = y;
                return true;
            }

            public boolean keyUp(int keyCode) {
                switch (keyCode) {
                    case NUM_2:
                    case NUMPAD_2:
                    case NUM_3:
                    case NUMPAD_3:
                    case NUM_4:
                    case NUMPAD_4:
                    case NUM_5:
                    case NUMPAD_5:
                    case NUM_6:
                    case NUMPAD_6:
                    case NUM_7:
                    case NUMPAD_7:
                    case NUM_8:
                    case NUMPAD_8:
                    case NUM_9:
                    case NUMPAD_9:
                        int pow = keyCode - 7;
                        if (pow > 9) {
                            pow -= 137;
                        }
                        Mandelbrot.setPower(pow);
                        drawGrid();
                        break;
                    case ESCAPE:
                        reMin = -2;
                        reMax = 2;
                        imMin = -2;
                        imMax = 2;
                        drawGrid();
                        break;
                    case NUMPAD_SUBTRACT:
                    case MINUS:
                        double re = (reMax + reMin) / 2;
                        double dre = (re - reMin) * 2;
                        double im = (imMax + imMin) / 2;

                        reMin = re - dre;
                        reMax = re + dre;
                        imMin = im - dre;
                        imMax = im + dre;
                        drawGrid();
                        break;
                    case NUMPAD_ADD:
                    case PLUS:
                        re = (reMax + reMin) / 2;
                        dre = (re - reMin) / 2;
                        im = (imMax + imMin) / 2;
                        reMin = re - dre;
                        reMax = re + dre;
                        imMin = im - dre;
                        imMax = im + dre;
                        drawGrid();
                        break;
                    case A:
                        mode = Mode.ANIMATE;
                        finalPoint = new Complex((reMax + reMin) / 2, (imMax + imMin) / 2);
                        finalWidth = reMax - reMin;
                        reMin = -2;
                        reMax = 2;
                        imMin = -2;
                        imMax = 2;
                        break;
                    case E:
                        mode = Mode.EXPLORE;
                        break;
                }

                return true;
            }

            public boolean touchUp(int x,int y, int pointer, int button){
                dPixmap.setColor(Color.CLEAR);
                dPixmap.fill();
                if (x0 != x1 && y0 != y1) {
                    double reMinNew = translate(reMin, reMax, x0);
                    double reMaxNew = translate(reMin, reMax, x1);
                    double imMinNew = translate(imMin, imMax, y0);
                    double imMaxNew = translate(imMin, imMax, y1);
                    reMin = Math.min(reMaxNew, reMinNew);
                    reMax = Math.max(reMaxNew, reMinNew);
                    imMin = Math.min(imMaxNew, imMinNew);
                    imMax = imMin + reMax - reMin;
                    drawGrid();
                }
                x0 = -1;
                y0 = -1;
                x1 = -1;
                y1 = -1;
                return true;
            }

            public boolean touchDragged (int x, int y, int pointer) {
                x1 = x;
                y1 = y;
                return true;
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (mode == Mode.ANIMATE) {
            double cr = (reMax + reMin) / 2;
            double ci = (imMax + imMin) / 2;
            double w = reMax - reMin;
            w *= 0.99;
            if (w < finalWidth) {
                mode = Mode.EXPLORE;
            }
            double dr = (finalPoint.re - cr) * 0.01;
            double di = (finalPoint.im - ci) * 0.01;
            cr += dr;
            ci += di;
            reMin = cr - w / 2;
            reMax = reMin + w;
            imMin = ci - w / 2;
            imMax = imMin + w;
            drawGrid();
        }

        ScreenUtils.clear(Color.BLACK);
        pixmap.setColor(Color.CLEAR);
        pixmap.fill();

        if (x0 > -1 && y0 > -1 && x1 > -1 && y1 > -1) {
            dPixmap.setColor(Color.CLEAR);
            dPixmap.fill();

            dPixmap.setColor(Config.GRID_COLOR);
            int w = x1 - x0;
            dPixmap.drawRectangle(x0, y0, w, w);
        }
        pixmap.drawPixmap(mPixmap, 0, 0);
        pixmap.drawPixmap(dPixmap, 0, 0);

        bgTexture.draw(pixmap, 0, 0);
        batch.begin();

        batch.draw(bgTexture, 0, 0);
        batch.end();

        if (mode == Mode.ANIMATE) {
            var filename = String.format("output/%07d.png", ++count);
            PixmapIO.writePNG(new FileHandle(filename), pixmap);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
