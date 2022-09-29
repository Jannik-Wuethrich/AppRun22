package ch.coop.memory;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private List<Word> words = new ArrayList<>();
    private List<Pair> pairs = new ArrayList<>();
    private Pair currentPair = new Pair();
    private CardView currentCardView;
    private String filename = "data.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchScanner();
            }
        });
        try {

            FileInputStream fin = openFileInput(filename);
            int c;
            String temp = "";

            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
            JSONObject x = new JSONObject(temp);
            Gson gson = new Gson();
            JSONModal abc = gson.fromJson(temp, JSONModal.class);
            Toast.makeText(getBaseContext(), "file read", Toast.LENGTH_SHORT).show();
            for (Word word : abc.getResults()
            ) {
                words.add(word);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        updateRec();
      /*  words.add(new Word("Coop"));
        words.add(new Word("Basel"));
        words.add(new Word("BBZBL"));
        words.add(new Word("Pratteln"));*/


    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Toast.makeText(this, "No Permissions", Toast.LENGTH_LONG);
                    }
                } else {
                    String logMsg = result.getContents();
                    String path = result.getBarcodeImagePath();
                    File file = new File(path);
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Toast.makeText(this, "Scanned: " + logMsg, Toast.LENGTH_LONG).show();
                    if (!words.stream().anyMatch(x -> x.getWord().equals(logMsg))) {
                        words.add(new Word(logMsg, bitmap));
                        // words.add(new Word("", getBitmapFromURL("https://cdn4.vectorstock.com/i/thumb-large/19/58/no-image-vector-30371958.jpg")));
                    } else {
                        words.add(new Word(logMsg, bitmap));
                    }
                    System.out.println(words);
                    if (currentCardView != null) {
                        MaterialButton button = (MaterialButton) currentCardView.getChildAt(1);
                        button.setText(logMsg);
                    }
                    try {
                        String filename = "data.json";
                        JSONObject jsonObject = new JSONObject();
                        JSONArray wordsArray = new JSONArray();
                        for (Word word : words
                        ) {
                            JSONObject x = new JSONObject();
                            x.put("word", word.getWord());
                            x.put("image", word.getBitmap());
                            wordsArray.put(x);
                        }
                        jsonObject.put("results", wordsArray);
                        File filetoSave = new File(filename);
                        FileOutputStream fOut = openFileOutput(filename, Context.MODE_PRIVATE);
                        fOut.write(jsonObject.toString().getBytes());
                        fOut.close();

                        Toast.makeText(getBaseContext(), "file saved", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    updateRec();
                }
            });

    public void launchScanner() {
        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setCaptureActivity(MyCaptureActivity.class);
        scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        scanOptions.setOrientationLocked(false);
        scanOptions.addExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, true);
        scanOptions.setBeepEnabled(false);
        scanOptions.setPrompt("Scan a QR code");
        qrLauncher.launch(scanOptions);
    }

    private static final int SCAN_QR_CODE_REQUEST_CODE = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add("Log");
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    /*
        @SuppressLint("MissingSuperCall")
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            if (resultCode == RESULT_OK) {
                String logMsg = intent.getStringExtra("SCAN_RESULT");
                System.out.println(logMsg);

            }

        }
    */
    private void log(String solution) throws JSONException {
        Intent intent = new Intent("ch.apprun.intent.LOG");
// format depends on app, see logbook format guideline
        JSONObject log = new JSONObject();
        log.put("task", "Metalldetektor");
        log.put("solution", solution);
        intent.putExtra("ch.apprun.logmessage", log.toString());
        startActivity(intent);
    }

    private void updateRec() {
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView);
        WordsAdapter adapter = new WordsAdapter(words);
        rv.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this /* the
activity */, 2);
        rv.setLayoutManager(gridLayoutManager);
        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(rv.getContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO check if you really need to use type casting here
                        CardView cardView = (CardView) ((GridLayout) view).getChildAt(0);
                        launchScanner();
                        currentCardView = cardView;
                      /*  if (materialButton.isEnabled()) {
                            materialButton.setEnabled(false);

                            if (currentPair.isFull() || currentPair.getFirstWord() == null) {
                                currentPair = new Pair();
                                currentPair.setFirstWord(words.get(position));

                            } else {
                                currentPair.setSecondWord(words.get(position));
                                pairs.add(currentPair);
                            }
                            System.out.println(pairs);
                            try {
                                log(pairs);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }*/
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    private void log(List<Pair> pairs) throws JSONException {
        JSONArray sol = new JSONArray();
        for (Pair pair : pairs
        ) {
            JSONArray ar = new JSONArray();
            ar.put(pair.getFirstWord().getWord());
            ar.put(pair.getSecondWord().getWord());
            sol.put(ar);
        }
        Intent intent = new Intent("ch.apprun.intent.LOG");
// format depends on app, see logbook format guideline
        JSONObject log = new JSONObject();
        log.put("task", "Memory");
        log.put("solution", sol);
        intent.putExtra("ch.apprun.logmessage", log.toString());
        startActivity(intent);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }

    }
}
