package ch.coop.morseencoder;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ch.coop.morseencoder.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // initialize variables
    EditText etinput;
    FlashView etoutput;
    Button btnEncode,
            btnDecode,
            btnclear;
    MorseEncoder morseEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        morseEncoder = new MorseEncoder();

        // Assign variables
        etinput = findViewById(R.id.etinput);
        etoutput = findViewById(R.id.etoutput);
        btnDecode = findViewById(R.id.btndecode);
        btnEncode = findViewById(R.id.btnencode);
        btnclear = findViewById(R.id.btnclear);


        btnEncode.setOnClickListener(v -> {

            // When button encode is clicked then the
            // following lines inside this curly
            // braces will be executed

            // to get the input as string which the user wants to encode
            String input = etinput.getText().toString();

            String output = "";

            // variable used to compute the output
            // to get the length of the input string
            int l = input.length();

            // variables used in loops
            int i, j;

            try {
                List<Primitive> primitiveList = morseEncoder.textToCode(input);
                for (Primitive primitiv : primitiveList) {
                    if (primitiv.isLightOn()) {
                        //   etoutput.setBackgroundColor(Color.WHITE);
                        //  lightOn(primitiv.getSignalLengthInDits());
                        etoutput.setDuration(primitiv.getSignalLengthInDits());
                        // etoutput.flashOn();
                        etoutput.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                       Thread.sleep(primitiv.getSignalLengthInDits() * 500);
                    etoutput.setBackgroundColor(ContextCompat.getColor(this, R.color.black));

                        //   findViewById(R.layout.activity_main).invalidate();

                    } else {
                        Thread.sleep(primitiv.getSignalLengthInDits() * 500);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // to display the output
            // etoutput.setText(output);
        });
        btnclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When button clear is clicked then the
                // following lines inside this curly
                // braces will be executed

                // to clear the etinput
                etinput.setText("");

                // to clear etoutput
                //   etoutput.setText("");
            }
        });
        btnDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When button decode is clicked then the
                // following lines inside this curly
                // braces will be executed

                // to get the input given by the user as string
                String input1 = etinput.getText().toString();


            }
        });
    }

    private void lightOn(int dur) throws InterruptedException {
        etoutput.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        // etoutput.postInvalidate();
        //   etoutput.requestLayout();
        Thread.sleep(dur * 500);
        etoutput.setBackgroundResource(R.color.black);


    }
}