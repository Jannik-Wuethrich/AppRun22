package ch.coop.morseencoder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class FlashView extends View {
    private boolean white = false;
    private int duration = 0;
    private Rect rect = new Rect(100, 500, 500, 2000);
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public FlashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (white) {
            setBackgroundResource(R.color.white);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLUE);
            rect.set(0, 0, 1264, 2039);
            canvas.drawRect(rect, paint);
            try {
                Thread.sleep(duration * 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setBackgroundColor(Color.BLACK);
        } else {
            try {
                Thread.sleep(duration * 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void flashOn() {
        white = true;
        invalidate();
    }

    public void flashOff() {
        white = false;
        invalidate();
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
