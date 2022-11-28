package ch.coop.morseencoder;

import android.os.SystemClock;
import android.view.View;

public class Morse implements Runnable {
    private int duration;
    private int color;
    private View view;

    public Morse(int color, int duration, View view) {
        this.view = view;
        this.color = color;
        this.duration = duration * 500;
    }

    @Override
    public void run() {
        view.setBackgroundColor(color);
        SystemClock.sleep(duration);
    }
}