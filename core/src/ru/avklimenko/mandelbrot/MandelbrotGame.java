package ru.avklimenko.mandelbrot;

import com.badlogic.gdx.Game;

public class MandelbrotGame extends Game {

    @Override
    public void create() {
        setScreen(new MandelbrotScreen());
    }
}
