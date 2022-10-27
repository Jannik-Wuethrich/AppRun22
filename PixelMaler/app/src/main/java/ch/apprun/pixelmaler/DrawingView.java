package ch.apprun.pixelmaler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Die DrawingView ist für die Darstellung und Verwaltung der Zeichenfläche
 * zuständig.
 */
public class DrawingView extends View {

    private static final int GRID_SIZE = 13;
    private static final int GRID_COLUMNS = 11;

    private Path drawPath = new Path();
    private Paint drawPaint = new Paint();
    private Paint linePaint = new Paint();
    private boolean isErasing = false;
    private Canvas currentCavas;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        linePaint.setColor(0xFF666666);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(1.0f);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        final int maxX = canvas.getWidth();
        final int maxY = canvas.getHeight();

        final int stepSizeX = (int) Math.ceil((double) maxX / GRID_SIZE);
        final int stepSizeY = (int) Math.ceil((double) maxY / GRID_SIZE);

        // TODO Zeichne das Gitter
        int distance_width = getWidth() / GRID_COLUMNS;
        int distance_height = getHeight() / GRID_COLUMNS;
       for(int x = 1; x < GRID_COLUMNS + 1; x++){

            canvas.drawLine(distance_width * x,0,distance_width * x,getHeight(), linePaint);
        }
        for(int x = 1; x < GRID_COLUMNS + 1; x++){

            canvas.drawLine(0,distance_height * x,getWidth(),distance_height * x,  linePaint);
        }
        // Zeichnet einen Pfad der dem Finger folgt
        canvas.drawPath(drawPath, drawPaint);
        currentCavas = canvas;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);

                // TODO wir müssen uns die berührten Punkte zwischenspeichern
                drawPaint.getColor();
                currentCavas.drawPaint(new Paint());
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);

                // TODO wir müssen uns die berührten Punkte zwischenspeichern

                break;
            case MotionEvent.ACTION_UP:

                // TODO Jetzt können wir die zwischengespeicherten Punkte auf das
                // Gitter umrechnen und zeichnen, bzw. löschen, falls wir isErasing
                // true ist (optional)

                drawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void startNew() {

        // TODO Gitter löschen

        invalidate();
    }

    public void setErase(boolean isErase) {
        isErasing = isErase;
        if (isErasing) {
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        } else {
            drawPaint.setXfermode(null);
        }
    }

    public boolean isErasing() {
        return isErasing;
    }

    public void setColor(String color) {
        invalidate();
        drawPaint.setColor(Color.parseColor(color));
    }
}
