package com.example.androidparticlestarter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;
import io.particle.android.sdk.cloud.exceptions.ParticleCloudException;
import io.particle.android.sdk.utils.Async;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {
    private final String TAG="";
    private final String PARTICLE_USERNAME = "pintu.chaudhary9@gmail.com";
    private final String PARTICLE_PASSWORD = "Qwerty@123";
    private final String DEVICE_ID = "2d0033000447363333343435";
    private long subscriptionId;
    private ParticleDevice mDevice;
    TextView startTimer;
    long startTime = 0;
    int seconds = 0;
    TextView timeChangeLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParticleCloudSDK.init(this.getApplicationContext());
        getDeviceFromCloud();
        startTimer = (TextView) findViewById(R.id.timerText);

        Button b = (Button) findViewById(R.id.startButton);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().equals("stop")) {
                    timerHandler.removeCallbacks(timerRunnable);
                    b.setText("start");

                } else {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
                        @Override
                        public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                            Log.d(TAG, "Availble functions: " + mDevice.getFunctions());
                            List<String> functionParameters = new ArrayList<String>();
                            functionParameters.add(Long.toString(startTime));
                            try {
                                mDevice.callFunction("stime", functionParameters);

                            } catch (ParticleDevice.FunctionDoesNotExistException e1) {
                                e1.printStackTrace();
                            }


                            return -1;
                        }

                        @Override
                        public void onSuccess(Object o) {
                            // put your success message here
                            Log.d(TAG, "Success!");
                        }

                        @Override
                        public void onFailure(ParticleCloudException exception) {
                            // put your error handling code here
                            Log.d(TAG, exception.getBestMessage());
                        }
                    });



                    b.setText("stop");
                }
            }
        });

        SeekBar seekBar = findViewById(R.id.seekBar);

        seekBar.setProgress(1);
        seekBar.setMax(20);

        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        int progress = seekBar.getProgress();
        this.timeChangeLabel = findViewById(R.id.scrollTime);
        this.timeChangeLabel.setText("" + progress);



    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            timeChangeLabel.setText("" + progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };


    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 100;

            Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
                @Override
                public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                    Log.d(TAG, "Availble functions: " + mDevice.getFunctions());
                    List<String> functionParameters = new ArrayList<String>();
                    functionParameters.add(Integer.toString(seconds));
                    try {
                        mDevice.callFunction("mtime", functionParameters);

                    } catch (ParticleDevice.FunctionDoesNotExistException e1) {
                        e1.printStackTrace();
                    }


                    return -1;
                }

                @Override
                public void onSuccess(Object o) {
                    // put your success message here
                    Log.d(TAG, "Success!");
                }

                @Override
                public void onFailure(ParticleCloudException exception) {
                    // put your error handling code here
                    Log.d(TAG, exception.getBestMessage());
                }
            });

            if(seconds >= 7) {
                Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
                    @Override
                    public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                        Log.d(TAG, "Availble functions: " + mDevice.getFunctions());
                        List<String> functionParameters = new ArrayList<String>();
                        functionParameters.add("Exit");
                        try {
                            mDevice.callFunction("etime", functionParameters);

                        } catch (ParticleDevice.FunctionDoesNotExistException e1) {
                            e1.printStackTrace();
                        }


                        return -1;
                    }

                    @Override
                    public void onSuccess(Object o) {
                        // put your success message here
                        Log.d(TAG, "Success!");
                    }

                    @Override
                    public void onFailure(ParticleCloudException exception) {
                        // put your error handling code here
                        Log.d(TAG, exception.getBestMessage());
                    }
                });
            }




            startTimer.setText(String.format("%d",seconds));

            timerHandler.postDelayed(this, 1000);


        }
    };


    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
        Button b = (Button)findViewById(R.id.startButton);
        b.setText("start");
    }


    public void getDeviceFromCloud() {


        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {

            @Override
            public Object callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                particleCloud.logIn("pintu.chaudhary9@gmail.com", "Qwerty@123");
                mDevice = particleCloud.getDevice(DEVICE_ID);
                return -1;
            }

            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Successfully got device from Cloud");
            }

            @Override
            public void onFailure(ParticleCloudException exception) {
                Log.d(TAG, exception.getBestMessage());
            }
        });
    }

}
