package com.example.xoapit.piratenews.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
    private ArrayList<String> mFonts;
    private Spinner mSpinnerFont;
    private SeekBar mSeekBarSize;
    private RadioButton mRbLight, mRbDark;
    private Button mBtnOk;

    private String font = "Helvetica";
    private String theme = "light";
    private int fontSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkAndSetSetting();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SettingActivity.this.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        initToolbar();

        mSeekBarSize = (SeekBar) findViewById(R.id.seekBarSize);
        mSeekBarSize.setMax(10);
        mSeekBarSize.setProgress(fontSize);

        mRbLight = (RadioButton) findViewById(R.id.rbLight);
        mRbDark = (RadioButton) findViewById(R.id.rbDark);
        if (theme.equals("light")) {
            mRbLight.setChecked(true);
        } else {
            mRbDark.setChecked(true);
        }

        mSpinnerFont = (Spinner) findViewById(R.id.spinerFont);
        mFonts = new ArrayList<>();
        initFonts();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, mFonts
        );
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        mSpinnerFont.setAdapter(arrayAdapter);
        mSpinnerFont.setSelection(mFonts.indexOf(font));

        mBtnOk = (Button) findViewById(R.id.btnOk);
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String size = String.valueOf(mSeekBarSize.getProgress());
                String font = mFonts.get(mSpinnerFont.getSelectedItemPosition());
                String theme = "light";
                if (mRbDark.isChecked()) theme = "dark";
                writeSettingToFile(size + "-" + font + "-" + theme, getBaseContext());

                final Intent intent = new Intent(getApplication(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void initFonts() {
        mFonts.add("Helvetica");
        mFonts.add("Courier");
        mFonts.add("Arimo");
        mFonts.add("Montserrat");
        mFonts.add("Anton");
        mFonts.add("Lobster");
        mFonts.add("Pacifico");
        mFonts.add("Pattaya");
        mFonts.add("Open Sans Condensed");
    }

    private void writeSettingToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("setting.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readSettingFromFile(Context context) {
        String data = "";
        try {
            InputStream inputStream = context.openFileInput("setting.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                data = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("SettingActivity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("SettingActivity", "Can not read file: " + e.toString());
        }
        return data;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }

    private void checkAndSetSetting() {
        try {
            String settingInfo = readSettingFromFile(getBaseContext());
            String arrSetting[] = settingInfo.split("-");
            String themeSetting = arrSetting[2];
            String sizeSetting = arrSetting[0];
            String fontSetting = arrSetting[1];
            fontSize = Integer.parseInt(sizeSetting);
            font = fontSetting;
            if (themeSetting.equals("dark")) {
                setTheme(R.style.DarkTheme);
                theme = "dark";
            } else {
                setTheme(R.style.LightTheme);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolbar() {
        Toolbar toolbarSetting = (Toolbar) findViewById(R.id.toobarSetting);
        setSupportActionBar(toolbarSetting);
        getSupportActionBar().setTitle(Html.fromHtml("<b>Setting</b>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
