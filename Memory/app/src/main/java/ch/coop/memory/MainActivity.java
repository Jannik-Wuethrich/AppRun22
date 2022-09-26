package ch.coop.memory;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private List<Word> words = new ArrayList<>();
    private List<Pair> pairs = new ArrayList<>();
    private Pair currentPair = new Pair();
    private CardView currentCardView;

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
                    String code = result.getContents();
                    Toast.makeText(this, "Scanned: " + code, Toast.LENGTH_LONG).show();
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            String logMsg = intent.getStringExtra("SCAN_RESULT");
            System.out.println(logMsg);
            if (!words.stream().anyMatch(x -> x.getWord().equals(logMsg))) {
                words.add(new Word(logMsg));
                words.add(new Word(""));
            } else {
                words.add(new Word(logMsg));
            }
            System.out.println(words);
            if (currentCardView != null) {
                MaterialButton button = (MaterialButton)    currentCardView.getChildAt(1);
                button.setText(logMsg);
            }
            updateRec();
        }

    }

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

}
