package com.example.nk.temperatureconvertor;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText inputTemperatureEditText;
    private TextView outputTemperatureTextView;
    private EditText historyEditText;
    private RadioGroup radioGroup;
    private double temperatureInF;
    private double temperatureInC;
    private Button convertButton;
    private static String savedKey = "outputTemperatureTextViewValue";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setListenerForRadioButton();

        //Set FtoC checked by default.
        radioGroup.check(R.id.radioButtonFtoC);

    }

    private void initializeViews() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        inputTemperatureEditText = (EditText) findViewById(R.id.editTextInputTemperature);
        outputTemperatureTextView = (TextView) findViewById(R.id.textViewOutputTemperature);
        historyEditText = (EditText) findViewById(R.id.editTextHistory);
        convertButton = (Button) findViewById(R.id.buttonConvert);
    }

    private void setListenerForRadioButton() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                inputTemperatureEditText.setText("");
                outputTemperatureTextView.setText("");
            }

        });
    }

    public void convertButtonClickAction(View view) {
        if (inputTemperatureEditText.getText().toString().trim().equals("")) {
            Toast.makeText(MainActivity.this, "Please provide an Input", Toast.LENGTH_SHORT).show();
        } else {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            switch (checkedId) {
                case R.id.radioButtonFtoC:
                    //Toast.makeText(MainActivity.this, "FtoC selected", Toast.LENGTH_SHORT).show();
                    convertFtoC();
                    break;
                case R.id.radioButtonCtoF:
                    //Toast.makeText(MainActivity.this, "CtoF selected", Toast.LENGTH_SHORT).show();
                    convertCtoF();
                    break;
            }
        }
    }

    private void convertFtoC() {
        temperatureInF = Double.parseDouble(inputTemperatureEditText.getText().toString());
        temperatureInC = (temperatureInF - 32.0) / 1.8;
        temperatureInC = roundDecimal(temperatureInC);
        outputTemperatureTextView.setText("" + temperatureInC);
        historyEditText.setText("F to C: " + temperatureInF + " -> " + temperatureInC + "\n" + historyEditText.getText().toString());
    }

    private void convertCtoF() {
        temperatureInC = Double.parseDouble(inputTemperatureEditText.getText().toString());
        temperatureInF = (temperatureInC * 1.8) + 32;
        temperatureInF = roundDecimal(temperatureInF);
        outputTemperatureTextView.setText("" + temperatureInF);
        historyEditText.setText("C to F: " + temperatureInF + " -> " + temperatureInC + "\n" + historyEditText.getText().toString());
    }

    private static double roundDecimal(double value) {
        return (double) Math.round(value * (int) Math.pow(10, 1)) / (int) Math.pow(10, 1);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(savedKey, outputTemperatureTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        outputTemperatureTextView.setText(savedInstanceState.getString(savedKey));
    }
}
