package ch.apprun.pixelmaler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.apprun.pixelmaler.model.PixelModel;

public class MainActivity extends Activity {

    private DrawingView drawingView;
    private ImageButton currentBrush;
    private  int GRID_ROWS = 13;
    private  int GRID_COLUMNS = GRID_ROWS;

    public void eraseClicked(View view) {
        if (view != currentBrush) {
            ImageButton imgView = (ImageButton) view;
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.selected));
            currentBrush.setImageDrawable(null);
            currentBrush = (ImageButton) view;
        }
        drawingView.draw(new Canvas());
        drawingView.setErase(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Grösse auswählen");

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Weiter", (dialog, which) -> {
        GRID_ROWS = Integer.parseInt(input.getText().toString());
        dialog.cancel();
            drawingView = findViewById(R.id.drawing);
            drawingView.setGridSize(GRID_ROWS);
            currentBrush = findViewById(R.id.defaultColor);
            currentBrush.setImageDrawable(getResources().getDrawable(R.drawable.selected));
            String color = currentBrush.getTag().toString();
            drawingView.setColor(color);

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                drawingView = findViewById(R.id.drawing);
                drawingView.setGridSize(13);

                currentBrush = (ImageButton) findViewById(R.id.defaultColor);
                currentBrush.setImageDrawable(getResources().getDrawable(R.drawable.selected));
                String color = currentBrush.getTag().toString();
                drawingView.setColor(color);
            }
        });

    }

    private void onCreateNewDrawingAction() {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("New Drawing");
        newDialog.setMessage("Start a new drawing?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                drawingView.startNew();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add("New");
        menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                onCreateNewDrawingAction();
                return true;
            }
        });

        menuItem = menu.add("Log");
        menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    log();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        return true;
    }

    public void paintClicked(View view) {
        if (view != currentBrush) {
            ImageButton imgView = (ImageButton) view;
            String color = view.getTag().toString();
            drawingView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.selected));
            currentBrush.setImageDrawable(null);
            currentBrush = (ImageButton) view;
        }
        drawingView.setErase(false);
    }

    private void onLogAction() {
        // TODO

        Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG);
    }

    public void log() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONArray pixels = new JSONArray();
        PixelModel[][] canvasAsArray = drawingView.getCanvasAsArray();

        for (int row = 0; row < canvasAsArray.length; row++) {
            for (int col = 0; col < canvasAsArray[row].length; col++) {
                if (canvasAsArray[row][col] == null) {
                } else {
                    PixelModel pixelModel = canvasAsArray[row][col];
                    if (!Integer.toHexString(pixelModel.getColor().getColor()).equals("ffffffff")) {
                        JSONObject jobject = new JSONObject();
                        jobject.put("y", col);
                        jobject.put("x", row);
                        try {
                            // TODO check color format
                            jobject.put("color", "#" + Integer.toHexString(pixelModel.getColor().getColor()));
                            pixels.put(jobject);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                }
            }
        }
        Intent intent = new Intent("ch.apprun.intent.LOG");
        jsonObject.put("pixels", pixels);
        jsonObject.put("task", "Pixelmaler");
        intent.putExtra("ch.apprun.logmessage", jsonObject.toString());
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Send successful", Toast.LENGTH_SHORT).show();


    }
}
