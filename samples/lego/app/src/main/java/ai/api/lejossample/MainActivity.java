/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package ai.api.lejossample;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.ui.AIButton;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import java.io.IOException;

import lejos.hardware.Audio;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.TextLCD;
import lejos.remote.ev3.RemoteRequestEV3;
import lejos.robotics.RegulatedMotor;

public class MainActivity extends ActionBarActivity {

    static final String TAG = MainActivity.class.getName();

    private static final int REQUEST_AUDIO_PERMISSIONS_ID = 33;

    private AIButton aiButton;
    private TextView commandText;

    private RemoteRequestEV3 ev3;

    private RegulatedMotor motorA;
    private RegulatedMotor motorB;
    private RegulatedMotor motorC;
    private RegulatedMotor motorD;

    private Audio audio;
    private TextLCD lcd;
    private GraphicsLCD graphicsLCD;
    private Button connectButton;

    private TextToSpeech ttsEngine;
    private volatile boolean ttsReady = false;

    private EditText addressEdit;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        commandText = (TextView) findViewById(R.id.commandText);
        connectButton = (Button) findViewById(R.id.connectButton);
        addressEdit = (EditText) findViewById(R.id.addressEdit);

        aiButton = (AIButton) findViewById(R.id.micButton);

        handler = new Handler(Looper.getMainLooper());

        initTTS();
    }

    private void initTTS() {
        ttsEngine = new TextToSpeech(this, new OnInitListener() {
            @Override
            public void onInit(int initStatus) {
                if (initStatus == TextToSpeech.SUCCESS) {
                    ttsEngine.setLanguage(Locale.US);
                    ttsReady = true;
                } else {
                    Log.d(TAG, "Can't initialize TextToSpeech");
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAudioRecordPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ev3 != null) {
            new ConnectTask().execute("disconnect");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String address = settings.getString("pref_ipAddress", "");
        addressEdit.setText(address);

        final AIConfiguration config = new AIConfiguration(
                settings.getString("pref_apiKey", "a07bc2127c61497fad437dd8c197f951"),
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiButton.initialize(config);
        aiButton.setResultsListener(new AIButton.AIButtonListener() {
            @Override
            public void onResult(AIResponse aiResponse) {
                if (!aiResponse.isError()) {
                    Log.i(TAG, aiResponse.getResult().getResolvedQuery());
                    commandText.setText(aiResponse.getResult().getAction());
                    processResponse(aiResponse);
                } else {
                    commandText.setText(aiResponse.getStatus().getErrorDetails());
                }
            }

            @Override
            public void onError(AIError aiError) {
                commandText.setText(aiError.getMessage());
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (ttsEngine != null) {
            ttsEngine.shutdown();
            ttsEngine = null;
            ttsReady = false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        initTTS();
    }

    protected void checkAudioRecordPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_AUDIO_PERMISSIONS_ID);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSIONS_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void processResponse(final AIResponse aiResponse) {

        if (aiResponse != null) {
            new ControlTask().execute(aiResponse);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connectButton_onClick(View view) {
        if (ev3 == null) {
            String address = addressEdit.getText().toString();
            if (!TextUtils.isEmpty(address)) {

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("pref_ipAddress", address);
                editor.apply();

                new ConnectTask().execute("connect", address);
            }
        } else {
            new ConnectTask().execute("disconnect");
        }
    }

    public void settings_onClick(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void stopMotor(RegulatedMotor regulatedMotor) {
        regulatedMotor.setSpeed(0);
        regulatedMotor.stop(true);
    }

    private void fltMotor(RegulatedMotor regulatedMotor) {
        regulatedMotor.setSpeed(0);
        regulatedMotor.flt(true);
    }

    private class ConnectTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... cmd) {
            switch (cmd[0]) {
                case "connect":
                    try {
                        ev3 = new RemoteRequestEV3(cmd[1]);
                        motorA = ev3.createRegulatedMotor("A", 'L');
                        motorB = ev3.createRegulatedMotor("B", 'L');
                        motorC = ev3.createRegulatedMotor("C", 'L');
                        motorD = ev3.createRegulatedMotor("D", 'L');

                        lcd = ev3.getTextLCD();

                        audio = ev3.getAudio();
                        audio.systemSound(3);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                connectButton.setText("Disconnect");
                            }
                        });

                    } catch (IOException e) {
                        Log.e(TAG, "Can't connect to robot");
                        return 1;
                    }
                    break;

                case "disconnect":
                    if (ev3 != null) {

                        try {
                            audio.systemSound(2);

                            stopMotor(motorA);
                            motorA.close();

                            stopMotor(motorB);
                            motorB.close();

                            fltMotor(motorC);
                            motorC.close();

                            fltMotor(motorD);
                            motorD.close();

                        } catch (Exception e) {
                            Log.e(TAG, "Can't release motors", e);
                        } finally {
                            ev3.disConnect();
                            ev3 = null;
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            connectButton.setText("Connect");
                        }
                    });

                    break;
            }

            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                Toast.makeText(MainActivity.this, "Could not connect to EV3", Toast.LENGTH_LONG).show();
            } else if (result == 2) {
                Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class ControlTask extends AsyncTask<AIResponse, Integer, Integer> {

        private String errorMessage;

        protected Integer doInBackground(AIResponse... cmd) {

            Log.d(TAG, "Start response processing");

            if (ev3 == null) {
                return 2;
            }

            AIResponse command = cmd[0];

            if (command == null || command.isError()) {
                return 0;
            }

            try {
                ev3.getAudio().systemSound(1);

                if (!TextUtils.isEmpty(command.getResult().getFulfillment().getSpeech())) {
                    lcd.clear();
                    String speech = command.getResult().getFulfillment().getSpeech();

                    Log.d(TAG, "Write speech " + speech);

                    final int BASE_LINE = 4;
                    if (speech.length() > 15) {

                        lcd.drawString(speech.substring(0, 15), 0, BASE_LINE);

                        if (speech.length() > 15) {
                            lcd.drawString(speech.substring(15, Math.min(speech.length(), 30)), 0, BASE_LINE + 1);
                        }

                        if (speech.length() > 30) {
                            lcd.drawString(speech.substring(30, Math.min(speech.length(), 45)), 0, BASE_LINE + 2);
                        }

                        if (speech.length() > 45) {
                            lcd.drawString(speech.substring(45, Math.min(speech.length(), 60)), 0, BASE_LINE + 3);
                        }

                        if (speech.length() > 60) {
                            lcd.drawString(speech.substring(60, Math.min(speech.length(), 75)), 0, BASE_LINE + 4);
                        }

                        if (speech.length() > 75) {
                            lcd.drawString(speech.substring(75), 0, BASE_LINE + 5);
                        }

                    } else {
                        lcd.drawString(speech, 0, BASE_LINE);
                    }

                    lcd.refresh();

                    Log.d(TAG, "Speak " + speech);

                    if (ttsReady) {
                        ttsEngine.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
                    }

                }

                switch (command.getResult().getAction().toLowerCase()) {
                    case "move":
                        doMove(command);
                        break;

                    default:
                        tryNextStep(command);
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception while command execution", e);
                errorMessage = e.getMessage();
                return 3;
            }

            return 0;
        }

        private void tryNextStep(AIResponse command) {

            Log.d(TAG, "tryNextStep");

            if (command != null && !command.isError() && command.getResult() != null) {

                Log.d(TAG, "Command != null - executing command");

                if (command.getResult().getParameters() != null && !command.getResult().getParameters().isEmpty()) {
                    final float nextDelay = command.getResult().getIntParameter("nextDelay", 500);

                    try {
                        Thread.sleep(Math.round(Math.abs(nextDelay)));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    AIResponse nextCommand = null;
                    final String nextStep = command.getResult().getStringParameter("nextStep");
                    if (!TextUtils.isEmpty(nextStep)) {
                        try {
                            nextCommand = aiButton.textRequest(nextStep);
                        } catch (AIServiceException e) {
                            e.printStackTrace();
                        }
                    }

                    if (nextCommand != null) {
                        new ControlTask().execute(nextCommand);
                    }
                }
            }
        }

        private void doMove(AIResponse command) {

            Log.d(TAG, "doMove");

            final Result result = command.getResult();

            final float k = result.getFloatParameter("k", 1);
            final float nextDelay = result.getIntParameter("nextDelay", 0) * k;

            final float CANONICAL_POWER = 100.0f;

            final int aPower = result.getIntParameter("a_power");
            final float aTime = result.getFloatParameter("a_time") * k;

            final int bPower = result.getIntParameter("b_power");
            //final float bTime = result.getIntParameter("b.time") * k;

            final int cPower = result.getIntParameter("c_power");
            //final float cTime = result.getIntParameter("c.time") * k;

            final int dPower = result.getIntParameter("d_power");
            //final float dTime = result.getIntParameter("d.time") * k;

            Log.d(TAG, "Go motor A " + aPower);
            motorGo(motorA, aPower);

            Log.d(TAG, "Go motor B " + bPower);
            motorGo(motorB, bPower);

            Log.d(TAG, "Go motor C " + cPower);
            motorGo(motorC, cPower);

            Log.d(TAG, "Go motor D " + dPower);
            motorGo(motorD, dPower);

            try {
                int roundedTime = Math.round(aTime * 1000 + nextDelay);
                Log.d(TAG, "First sleep time: " + roundedTime);
                if (roundedTime > 0 && roundedTime < Integer.MAX_VALUE - 10000) {
                    Log.d(TAG, "First sleep " + roundedTime);
                    Thread.sleep(roundedTime);
                    Log.d(TAG, "End first sleep");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AIResponse nextCommand = null;
            final String nextStep = result.getStringParameter("nextStep");
            Log.d(TAG, "Next step command: " + nextStep);
            if (!TextUtils.isEmpty(nextStep)) {
                try {
                    nextCommand = aiButton.textRequest(nextStep);
                    Log.d(TAG, "Next step received");
                } catch (AIServiceException e) {
                    e.printStackTrace();
                }
            }

            try {
                int roundedTime = Math.round(Math.abs(nextDelay));
                Log.d(TAG, "Second sleep time: " + roundedTime);
                if (roundedTime > 0 && roundedTime < Integer.MAX_VALUE - 10000) {
                    Log.d(TAG, "Second sleep " + roundedTime);
                    Thread.sleep(roundedTime);
                    Log.d(TAG, "End second sleep");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Trying stop all motors");

            Log.d(TAG, "Try stop A");
            stopMotor(motorA);
            Log.d(TAG, "A stopped");

            Log.d(TAG, "Try stop B");
            stopMotor(motorB);
            Log.d(TAG, "B stopped");

            Log.d(TAG, "Try stop C");
            fltMotor(motorC);
            Log.d(TAG, "C stopped");

            Log.d(TAG, "Try stop D");
            fltMotor(motorD);
            Log.d(TAG, "D stopped");

            if (nextCommand != null) {
                Log.d(TAG, "Executing next command");
                new ControlTask().execute(nextCommand);
            }

        }

        private void motorGo(RegulatedMotor regulatedMotor, int power) {
            if (regulatedMotor != null) {

                Log.d(TAG, "motorGo " + power);

                if (power > 0) {
                    regulatedMotor.setSpeed(power);
                    regulatedMotor.forward();
                } else if (power == 0) {
                    stopMotor(regulatedMotor);
                } else {
                    regulatedMotor.setSpeed(Math.abs(power));
                    regulatedMotor.backward();
                }
            }
        }

        protected void onPostExecute(Integer result) {
            if (result == 1) {
                Toast.makeText(MainActivity.this, "Could not connect to EV3", Toast.LENGTH_LONG).show();
            } else if (result == 2) {
                Toast.makeText(MainActivity.this, "Not connected", Toast.LENGTH_LONG).show();
            }
            else if (result == 3) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

}
