package ru.avklimenko.mandelbrot;

public class Complex {
    public double re;
    public double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public void square() {
        var tmp = re;
        re = re * re - im * im;
        im = 2 * tmp * im;
    }

    public void pow(int n) {
        double re = this.re;
        double im = this.im;
        switch (n) {
            case 2 -> square();
            case 3 -> {
                square();
                mul(re, im);
            }
            case 4 -> {
                square();
                square();
            }
            case 5 -> {
                square();
                square();
                mul(re, im);
            }
            case 6 -> {
                square();
                square();
                mul(re, im);
                mul(re, im);
            }
            case 7 -> {
                square();
                square();
                mul(re, im);
                mul(re, im);
                mul(re, im);
            }
            case 8 -> {
                square();
                square();
                square();
            }
            case 9 -> {
                square();
                square();
                square();
                mul(re, im);
            }
        }
    }

    public double r2() {
        return re * re + im * im;
    }

    public void add(Complex bc) {
        re = re + bc.re;
        im = im + bc.im;
    }

    public void mul(double xre, double xim) {
        var tmp = re;
        re = re * xre - im * xim;
        im = tmp * xim + im * xre;
    }

    @Override
    public String toString() {
        return " " + re + " i･" + im;
    }
}
