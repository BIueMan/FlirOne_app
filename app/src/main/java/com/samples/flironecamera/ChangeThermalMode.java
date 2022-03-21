package com.samples.flironecamera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeThermalMode extends AppCompatActivity {

    private static final String TAG = "ChangeModeActivity";
    private String ThermalMode = "";
    private TextView modeStatus;
    // todo: clear instend here and in the main, from transform data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_thermal_mode);
        // get data
        Intent intent =getIntent();
        ThermalMode = intent.getStringExtra("ThermalMode");

        // crete list
        ListView list = (ListView) findViewById(R.id.ModeList);
        Log.d(TAG, "onCreate: Started");

        // create list of fusion mode
        FusionModesList fusion_mode = new FusionModesList();
        final ArrayList<String> modes = new ArrayList<>(fusion_mode.List);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, modes);
        list.setAdapter(adapter);

        //crate text
        modeStatus = findViewById(R.id.ModeStatus);
        modeStatus.setText("SelectedMode: " + ThermalMode);

        // onclick list
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: Mode - " + modes.get(i));
                ThermalMode = modes.get(i);
                modeStatus.setText("SelectedMode: " + ThermalMode);
                textHendler.update_line_txt(textHendler.Data_Path, textHendler.Data_txt, textHendler.Fusion_Mode_Line, ThermalMode);
            }
        });
    }

    public void finish(View v){
        Intent changeIntent = new Intent();
        changeIntent.putExtra("ThermalMode", ThermalMode);

        setResult(RESULT_OK, changeIntent);
        finish();
    }
}