package ch.coop.memory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final Bitmap PLACEHOLDER_IMAGE = StringToBitMap("Qk1WJwAAAAAAAD4AAAAoAAAAHgEAABYBAAABAAEAAAAAABgnAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP///wD///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf/z//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf/z//P/////////////////////////////////////////+f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P/////////////////////////+////////////////f/z//P/////////////////////////8f///////////////f/z//P/////////////////////////4P///////////////f/z//P/////////////////////////xn///////////////f/z//P/////////////////////////jj///////////////f/z//P/////////////////////////nx///////////////f/z//P/////////////////////////P4///////////////f/z//P////////////////////////+f8f//////////////f/z//P////////////////////////8f+P//////////////f/z//P////////////////////////4//H//////////////f/z//P////////////////////////x//n//////////////f/z//P////////////////////////z//z//////////////f/z//P////////////////////////n//5//////////////f/z//P////////////////////////P//8//////////////f/z//P///////////////////////+P//+f/////////////f/z//P///////////////////////8f///P/////////////f/z//P///////////////////////4////n/////////////f/z//P///////////////////////5////z/////////////f/z//P///////////////////////z////x/////////////f/z//P///////////////////////n////4/////////////f/z//P///////////////////////P////8f////////////f/z//P//////////////////////+P////+P////////////f/z//P//////////////////////+f/////H////////////f/z//P//////////////////////8//////j////////////f/z//P//////////////////////5//////x////////////f/z//P//////////////////////z//////5////////////f/z//P//////////////////////j//////8////////////f/z//P//////////////////////H//////+f///////////f/z//P//////////////////////P///////P///////////f/z//P/////////////////////+f///////n///////////f/z//P/////////////////////8////////z///////////f/z//P/////////////////////5////////x///////////f/z//P/////////////////////x////////4///////////f/z//P/////////////////////j////////8f//////////f/z//P/////////////////////H////////+P//////////f/z//P/////////////////////P/////////H//////////f/z//P////////////////////+f/////////j//////////f/z//P////////////////////8//////////z//////////f/z//P////////////////////4//////////5//////////f/z//P////////////////////x//////////8//////////f/z//P////////////////////z//////////+f/////////f/z//P////////////////////n///////////P/////////f/z//P////////////////////P///////////n/////////f/z//P///////////////////+f///////////z/////////f/z//P///////////////////8f///////////5/////////f/z//P///////////////////4////////////4/////////f/z//P///////////////////5////////////8f////////f/z//P///////////////////z////////////+P//////P/f/z//P///////////////////n/////////////H/////+P/f/z//P///////////////////P/////////////j/////8P/f/z//P//////////////////+P/////////////x/////4P/f/z//P//////////////////+f/////////////5/////xP/f/z//P//////////////////8//////////////8/////jP/f/z//P//////////////////5//////////////+f////HP/f/z//P//////////////////z///////////////P///+PP/f/z//P//////////////////n///////////////n///8fP/f/z//P//////////////////H///////////////z///8/P/f/z//P+f///////////////+P///////////////5///5/P/f/z//P+P///////////////+f///////////////4///z/P/f/z//P+H///////////////8////////////////8f//n/P/f/z//P+H///////////////5////////////////+P//P/P/f/z//P+T///////////////x/////////////////H/+f/P/f/z//P+Z///////////////j/////////////////j/8//P/f/z//P+c///////////////H/////////////////x/5//P/f/z//P+cf//////////////P/////////////////5/x//P/f/z//P+eP/////////////+f/////////////////8/j//P/f/z//P+fH/////////////8//////////////////+PH//P/f/z//P+fn/////////////4///////////////////OP//P/f/z//P+fz/////////////x///////////////////kf//P/f/z//P+f5/////////////z///////////////////w///P/f/z//P+f4/////////////n///////////////////5///P/f/z//P+f8f////////////P///////////////////////P/f/z//P+f+P///////////+f///////////////////////P/f/z//P+f/P///////////8f///////////////////////P/f/z//P+f/n///////////4////////////////////////P/f/z//P+f/z///////////5////////////////////////P/f/z//P+f/x///////////z////////////////////////P/f/z//P+f/5///////////n////////////////////////P/f/z//P+f/8///////////P////////////////////////P/f/z//P+f/+f/////////+P////////////////////////P/f/z//P+f//P/////////8f////////////////////////P/f/z//P+f//n/////////8/////////////////////////P/f/z//P+f//j/////////5/////////////////////////P/f/z//P+f//x/////////z/////////////////////////P/f/z//P+f//5/////////j/////////////////////////P/f/z//P+f//8/////////H/////////////////////////P/f/z//P+f//+f///////+P/////////////////////////P/f/z//P+f///P///////+f/////////////////////////P/f/z//P+f///H///////8//////////////////////////P/f/z//P+f///j///////5//////////////////////////P/f/z//P+f///z///////x//////////////////////////P/f/z//P+f///5///////j//////////////////////////P/f/z//P+f///8///////H//////////////////////////P/f/z//P+f///8f//////P//////////////////////////P/f/z//P+f///+P/////+f//////////////////////////P/f/z//P+f////H/////8///////////////////////////P/f/z//P+f////n/////4///////////////////////////P/f/z//P+f////z/////x///////////////////////////P/f/z//P+f////5/////j///////////////////////////P/f/z//P+f////8/////n///////////////////////////P/f/z//P+f////8f////P///////////////////////////P/f/z//P+f////+f///+f///////////////////////////P/f/z//P+f/////P///8////////////////////////////P/f/z//P+f/////n///4////////////////////////////P/f/z//P+f/////z///5////////////////////////////P/f/z//P+f/////5///z////////////////////////////P/f/z//P+f/////4///n////////////////////////////P/f/z//P+f/////8///P////////////////////////////P/f/z//P+f/////+f/+P////////////////////////////P/f/z//P+f//////P/8f////////////////////////////P/f/z//P+f//////n/8/////////////////////////////P/f/z//P+f//////z/5/////////////////////////////P/f/z//P+f//////x/z/////////////////////////////P/f/z//P+f//////5/n/////////////////////////////P/f/z//P+f//////8/H/////////////////////////////P/f/z//P+f//////+eP/////////////////////////////P/f/z//P+f///////Mf/////////////////////////////P/f/z//P+f///////E//////////////////////////////P/f/z//P+f///////h//////////////////////////////P/f/z//P+f///////z//////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////Af//////////////////P/f/z//P+f/////////////////wAB//////////////////P/f/z//P+f/////////////////A/gf/////////////////P/f/z//P+f////////////////8P/+D/////////////////P/f/z//P+f////////////////w///x/////////////////P/f/z//P+f////////////////D///8f////////////////P/f/z//P+f///////////////+P///+P////////////////P/f/z//P+f///////////////8f////H////////////////P/f/z//P+f///////////////4/////j////////////////P/f/z//P+f///////////////x/////x////////////////P/f/z//P+f///////////////z/////4////////////////P/f/z//P+f///////////////n/////8////////////////P/f/z//P+f///////////////P/////+f///////////////P/f/z//P+f///////////////P/////+f///////////////P/f/z//P+f//////////////+f//////P///////////////P/f/z//P+f//////////////+f//////P///////////////P/f/z//P+f//////////////8///////n///////////////P/f/z//P+f//////////////8///////n///////////////P/f/z//P+f//////////////8///////3///////////////P/f/z//P+f//////////////9///////z///////////////P/f/z//P+f//////////////5///////z///////////////P/f/z//P+f//////////////5///////z///////////////P/f/z//P+f//////////////5///////z///////////////P/f/z//P+f//////////////5///////z///////////////P/f/z//P+f//////////////5///////z///////////////P/f/z//P+f//////////////5///////z///////////////P/f/z//P+f//////////////5///////z///////////////P/f/z//P+f//////////////5///////z///////////////P/f/z//P+f//////////////5///////z///////////////P/f/z//P+f//////////////5///////z///////////////P/f/z//P+f//////////////9///////z///////////////P/f/z//P+f//////////////8///////3///////////////P/f/z//P+f//////////////8///////n///////////////P/f/z//P+f//////////////8///////n///////////////P/f/z//P+f//////////////+f//////P///////////////P/f/z//P+f//////////////+f//////P///////////////P/f/z//P+f///////////////P/////+f///////////////P/f/z//P+f///////////////P/////+f///////////////P/f/z//P+f///////////////n/////8////////////////P/f/z//P+f///////////////j/////4////////////////P/f/z//P+f///////////////x/////5////////////////P/f/z//P+f///////////////4/////j////////////////P/f/z//P+f///////////////8f////H////////////////P/f/z//P+f///////////////+P///+P////////////////P/f/z//P+f////////////////H///8f////////////////P/f/z//P+f////////////////x///w/////////////////P/f/z//P+f////////////////4f//D/////////////////P/f/z//P+f////////////////+B/4P/////////////////P/f/z//P+f/////////////////wAA//////////////////P/f/z//P+f//////////////////Af//////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+f//////////////////////////////////////P/f/z//P+P/////////////////////////////////////+P/f/z//P+AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P//////////////////////////////////////////f/z//P/////////////////////////////////////////+f/z//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAf/z//gAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA//z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////z///////////////////////////////////////////////w=");

    private String lastClickedTileId = "";

    private List<Word> words = new ArrayList<>();
    private String filename = "data123.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // When clicking the PLUS-Button launch the normal QR Scanner
        findViewById(R.id.fab).setOnClickListener(view -> launchScanner(qrLauncher));

        // When clicking the PLUS-Button launch the normal QR Scanner
        findViewById(R.id.send).setOnClickListener(view -> log());

        try (FileInputStream fin123 = openFileInput(filename)) {
            Gson gson = new Gson();
            JSONModal abc = gson.fromJson(readFileInputStream(fin123), JSONModal.class);
            Toast.makeText(getBaseContext(), "file read", Toast.LENGTH_SHORT).show();

            for (Word word : abc.getResults()) {
                word.setBitmap(StringToBitMap(word.getBitmapString()));
                words.add(word);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        updateRec();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private final ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Intent originalIntent = result.getOriginalIntent();
            if (originalIntent == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Toast.makeText(this, "No Permissions", Toast.LENGTH_LONG);
            }
        } else {
            String randomPairIdentifier = String.valueOf(UUID.randomUUID());

            String scannedWord = result.getContents();
            String path = result.getBarcodeImagePath();
            File file = new File(path);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            Toast.makeText(this, "Scanned: " + scannedWord, Toast.LENGTH_LONG).show();

            Word word1 = new Word(scannedWord, bitmap, randomPairIdentifier + "_1");
            Word word2 = new Word("", PLACEHOLDER_IMAGE, randomPairIdentifier + "_2");

            words.add(word1);
            words.add(word2);

            writeDataToFile();
            updateRec();
        }
    });

    private final ActivityResultLauncher<ScanOptions> qrLauncherToUpdate = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String scannedWord = result.getContents();
            String path = result.getBarcodeImagePath();
            File file = new File(path);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            Optional<Word> optionalWordToUpdate = words.stream()
                    .filter(word -> lastClickedTileId.equals(word.getId()))
                    .findAny();

            if (optionalWordToUpdate.isPresent()) {
                Word wordToUpdate = optionalWordToUpdate.get();
                wordToUpdate.setWord(scannedWord);
                wordToUpdate.setBitmap(bitmap);
                wordToUpdate.setBitmapString(bitMapToString(bitmap));
            }

            writeDataToFile();
            updateRec();
        } else {
            Intent originalIntent = result.getOriginalIntent();

            if (originalIntent == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Toast.makeText(this, "No Permissions", Toast.LENGTH_LONG);
            }
        }
    });

    public void launchScanner(ActivityResultLauncher<ScanOptions> qrLauncher) {
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.add("Log");
        menuItem.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.message_button) {

                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
                return false;
            } else if (item.getItemId() == R.id.action_send) {
                log();
            }
            return false;
        });
        return true;
    }

    private void writeDataToFile() {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray wordsArray = new JSONArray();
            for (Word word : words) {
                JSONObject x = new JSONObject();
                x.put("word", word.getWord());
                x.put("bitmapString", bitMapToString(word.getBitmap()));
                x.put("id", word.getId());
                wordsArray.put(x);
            }
            jsonObject.put("results", wordsArray);

            FileOutputStream fOut = openFileOutput(filename, Context.MODE_PRIVATE);
            fOut.write(jsonObject.toString().getBytes());
            fOut.close();

            Toast.makeText(getBaseContext(), "file saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updateRec() {
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView);
        WordsAdapter adapter = new WordsAdapter(words);
        rv.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this /* the activity */, 2);
        rv.setLayoutManager(gridLayoutManager);
        rv.addOnItemTouchListener(new RecyclerItemClickListener(rv.getContext(), rv, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                // Need this or we don't have info for that after scanning
                lastClickedTileId = words.get(position).getId();

                launchScanner(qrLauncherToUpdate);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                // do whatever
            }
        }));
    }

    private void log() {
        List<Pair> pairs = new ArrayList<>();

        // Find words that belong together using the shared base-id
        for (Word word : words) {
            if (word.getId().endsWith("_1")) {
                String baseId = word.getId().substring(0, word.getId().indexOf("_"));

                List<Word> wordPair = words.stream()
                        .filter(myWord -> myWord.getId().contains(baseId))
                        .collect(Collectors.toList());

                if (wordPair.size() == 2) {
                    Pair pair = new Pair();
                    pair.setFirstWord(wordPair.get(0));
                    pair.setSecondWord(wordPair.get(1));

                    pairs.add(pair);
                }
            }
        }

        try {
            JSONArray solutionArray = new JSONArray();

            for (Pair pair : pairs) {
                JSONArray pairArray = new JSONArray();
                pairArray.put(pair.getFirstWord().getWord());
                pairArray.put(pair.getSecondWord().getWord());
                solutionArray.put(pairArray);
            }

            Intent intent = new Intent("ch.apprun.intent.LOG");
            // format depends on app, see logbook format guideline
            JSONObject log = new JSONObject();
            log.put("task", "Memory");
            log.put("solution", solutionArray);
            intent.putExtra("ch.apprun.logmessage", log.toString());
            startActivity(intent);
            Toast.makeText(getBaseContext(), "Send successful", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String readFileInputStream(FileInputStream fis) throws IOException {
        StringBuilder tempBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                tempBuilder.append(line);
                tempBuilder.append("\n");
            }
        }
        fis.close();
        return tempBuilder.toString();
    }

    public String bitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);

            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
