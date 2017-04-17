package com.example.xoapit.piratenews.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.xoapit.piratenews.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SettingActivity extends AppCompatActivity {
    private ArrayList<String> fonts;
    private Spinner spinnerFont;
    private SeekBar seekBarSize;
    private RadioButton rbLight, rbDark;
    private Button btnOk;

    private String font="Helvetica";
    private int fontSize=0;
    private String theme="light";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            String settingInfo = readFromFile(getBaseContext());
            String arrSetting[] = settingInfo.split("-");
            String themeSetting=arrSetting[2];
            String sizeSetting=arrSetting[0];
            String fontSetting=arrSetting[1];
            fontSize=Integer.parseInt(sizeSetting);
            font=fontSetting;
            if (themeSetting.equals("dark")) {
                setTheme(R.style.DarkTheme);
                theme="dark";
            }else{
                setTheme(R.style.LightTheme);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        fonts=new ArrayList<>();
        Toolbar toolbarSetting=(Toolbar)findViewById(R.id.toobarSetting);
        setSupportActionBar(toolbarSetting);
        getSupportActionBar().setTitle(Html.fromHtml("<b>Setting</b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        seekBarSize=(SeekBar)findViewById(R.id.seekBarSize);
        spinnerFont=(Spinner)findViewById(R.id.spinerFont);
        btnOk=(Button)findViewById(R.id.btnOk);
        rbLight=(RadioButton)findViewById(R.id.rbLight);
        rbDark=(RadioButton)findViewById(R.id.rbDark);
        if(theme.equals("light")){
            rbLight.setChecked(true);
        }else{
            rbDark.setChecked(true);
        }
        seekBarSize.setMax(10);
        seekBarSize.setProgress(fontSize);

        initFont();
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,fonts
        );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinnerFont.setAdapter(arrayAdapter);
        spinnerFont.setSelection(fonts.indexOf(font));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String size=String.valueOf(seekBarSize.getProgress());
                String font= fonts.get(spinnerFont.getSelectedItemPosition());
                String theme="light";
                if(rbDark.isChecked()) theme="dark";
                writeToFile(size+"-"+font+"-"+theme,getBaseContext());
                Toast.makeText(getBaseContext(),"Setting Changed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initFont(){
        fonts.add("Helvetica");
        fonts.add("Courier");
        fonts.add("Arimo");
        fonts.add("Montserrat");
        fonts.add("Anton");
        fonts.add("Lobster");
        fonts.add("Pacifico");
        fonts.add("Pattaya");
        fonts.add("Open Sans Condensed");
    }

    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("setting.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(Context context) {
        String data = "";
        try {
            InputStream inputStream = context.openFileInput("setting.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                data = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("SettingActivity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("SettingActivity", "Can not read file: " + e.toString());
        }
        return data;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
