package ch.coop.memory;

import android.graphics.Bitmap;

public class Word {
    private String word;
private Bitmap bitmap;
private String bitmapString;
    public Word(String word, Bitmap bitmap) {
        this.word = word;
        this.bitmap = bitmap;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getBitmapString() {
        return bitmapString;
    }

    public void setBitmapString(String bitmapString) {
        this.bitmapString = bitmapString;
    }
}
