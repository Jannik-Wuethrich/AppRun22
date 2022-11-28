package ch.coop.morseencoder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.view.View;

import androidx.core.content.ContextCompat;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etinput;
    private View etoutput;
    private Button btnEncode,
            btnLog,
            btnclear;
    private MorseEncoder morseEncoder;
    private long system = SystemClock.uptimeMillis();
    private long DIT_DURATION_MILLISECONDS = 500;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        morseEncoder = new MorseEncoder();
        HandlerThread handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        etinput = findViewById(R.id.etinput);
        etoutput = findViewById(R.id.etoutput);
        btnLog = findViewById(R.id.btnlog);
        btnEncode = findViewById(R.id.btnencode);
        btnclear = findViewById(R.id.btnclear);
        btnEncode.setOnClickListener(v -> {
            try {
                morse(etinput.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etinput.setText("");
            }
        });
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input1 = etinput.getText().toString();
                log(input1);
            }
        });
    }


    private void morse(String word) throws Exception {
        MorseEncoder morseEncoder = new MorseEncoder();
        View v = findViewById(R.id.etoutput);
        List<Primitive> code = morseEncoder.textToCode(word.toUpperCase());
        for (Primitive p : code) {
            if (p.isLightOn()) {
                handler.post(new Morse(Color.WHITE, p.getSignalLengthInDits(), v));
                handler.post(new Morse(Color.BLACK, 0, v));
            } else {
                handler.post(new Morse(Color.BLACK, p.getSignalLengthInDits(), v));
            }
        }

    }

    private void log(String solution) {
        JSONObject jsonObject = new JSONObject();
        Intent intent = new Intent("ch.apprun.intent.LOG");
        try {
            jsonObject.put("task", "Morseencoder");
            jsonObject.put("solution", solution);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        intent.putExtra("ch.apprun.logmessage", jsonObject.toString());
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Send successful", Toast.LENGTH_SHORT).show();
    }
}