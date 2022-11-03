package ch.apprun.pixelmaler.model;

import android.graphics.Paint;
import android.graphics.Rect;

public class PixelModel {

    private Rect rectangle;
    private Paint color;

    public PixelModel(Rect rectangle, Paint color) {
        this.rectangle = rectangle;
        this.color = color;
    }

    public Rect getRectangle() {
        return rectangle;
    }

    public Paint getColor() {
        return color;
    }

}
