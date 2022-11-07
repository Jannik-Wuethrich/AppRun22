package ch.apprun.pixelmaler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;

import ch.apprun.pixelmaler.model.PixelModel;

/**
 * Die DrawingView ist für die Darstellung und Verwaltung der Zeichenfläche
 * zuständig.
 */
public class DrawingView extends View {

    // private static final int GRID_ROWS = 13;
    //private static final int GRID_COLUMNS = 13;
    private int GRID_ROWS = 13;
    private int GRID_COLUMNS = GRID_ROWS;
    private static final float GRID_STROKE_WIDTH = 1.0f;

    private PixelModel[][] canvasAsArray = new PixelModel[GRID_ROWS][GRID_COLUMNS];

    private Path drawPath = new Path();
    private Paint drawPaint = new Paint();
    private Paint linePaint = new Paint();
    private boolean isErasing = false;
    private int stepSizeX = 0;
    private int stepSizeY = 0;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        linePaint.setColor(0xFF666666);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(GRID_STROKE_WIDTH);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int maxX = getWidth();
        final int maxY = getHeight();

        int gridAllocationSize = (int) (2 * GRID_STROKE_WIDTH);
        stepSizeX = (int) (Math.ceil((double) maxX / GRID_ROWS) - gridAllocationSize);
        stepSizeY = (int) (Math.ceil((double) maxY / GRID_ROWS) - gridAllocationSize);

        // TODO Zeichne das Gitter
        int distance_width = getWidth() / GRID_COLUMNS;
        int distance_height = getHeight() / GRID_ROWS;

        // TODO loop through array and draw rectangles if there are any
        for (int row = 0; row < canvasAsArray.length; row++) {
            for (int col = 0; col < canvasAsArray[row].length; col++) {
                if (canvasAsArray[row][col] == null) {
                } else {
                    PixelModel pixelModel = canvasAsArray[row][col];
                    canvas.drawRect(pixelModel.getRectangle(), pixelModel.getColor());
                }
            }
        }

        // Draw Vertical Lines
        for (int x = 0; x < GRID_COLUMNS + 1; x++) {
            if (x == 0) {
                canvas.drawLine(0, 0, 0, getHeight(), linePaint);

            } else {
                canvas.drawLine(distance_width * x + GRID_STROKE_WIDTH, 0, distance_width * x + GRID_STROKE_WIDTH, getHeight(), linePaint);
            }
        }

        // Draw Horizontal Lines
        for (int y = 0; y < GRID_ROWS + 1; y++) {
            if (y == 0) {
                canvas.drawLine(0, 0, getWidth(), 0, linePaint);

            } else {
                canvas.drawLine(0, distance_height * y, getWidth(), distance_height * y, linePaint);
            }
        }
        // Zeichnet einen Pfad der dem Finger folgt
        canvas.drawPath(drawPath, drawPaint);


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        int left = (int) (Math.floor(touchX / stepSizeX) * (stepSizeX + GRID_STROKE_WIDTH) + 2 );
        int top = (int) (Math.floor(touchY / stepSizeY) * (stepSizeY + GRID_STROKE_WIDTH) );
        int right = left + stepSizeX;
        int bottom = top + stepSizeY;
        Rect rect;
        Paint paint;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                rect = new Rect(left, top, right, bottom);

                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.FILL);
                if (isErasing) {
                    paint.setColor(Color.WHITE);
                } else {
                    paint.setColor(drawPaint.getColor());
                }


                // TODO calc array Position
                int arrayPositionX = (int) Math.floor(touchX / stepSizeX);
                int arrayPositionY = (int) Math.floor(touchY / stepSizeY);

                // TODO before putting anything check if the position exists in array
                if (canvasAsArray.length > arrayPositionX && canvasAsArray.length > arrayPositionY) {
                    canvasAsArray[arrayPositionX][arrayPositionY] = new PixelModel(rect, paint);
                } else {
                    Toast.makeText(getContext(), "Fehler ", Toast.LENGTH_LONG);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);

                rect = new Rect(left, top, right, bottom);

                paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setStyle(Paint.Style.FILL);
                if (isErasing) {
                    paint.setColor(Color.WHITE);
                } else {
                    paint.setColor(drawPaint.getColor());
                }


                // TODO calc array Position
                arrayPositionX = (int) Math.floor(touchX / stepSizeX);
                arrayPositionY = (int) Math.floor(touchY / stepSizeY);

                // TODO before putting anything check if the position exists in array
                if (canvasAsArray.length > arrayPositionX && canvasAsArray.length > arrayPositionY) {
                    canvasAsArray[arrayPositionX][arrayPositionY] = new PixelModel(rect, paint);
                } else {
                    Toast.makeText(getContext(), "Fehler ", Toast.LENGTH_LONG);
                }
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
        canvasAsArray = new PixelModel[GRID_ROWS][GRID_COLUMNS];
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


    public PixelModel[][] getCanvasAsArray() {
        return canvasAsArray;
    }

    public void setGridSize(int number) {
        GRID_ROWS = number;
        GRID_COLUMNS = number;
        canvasAsArray = new PixelModel[number][number];
    }
}
