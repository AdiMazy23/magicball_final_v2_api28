package com.example.hw3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.StrictMath.abs;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Context mContext;
    ImageView ball;
    TextView answerTextView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private List<String> answers;
    private boolean layoutReady;
    private ConstraintLayout mainContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        ball = findViewById(R.id.ballImageView);
        answerTextView = findViewById(R.id.insideBallTextView);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        layoutReady = false;

        mainContainer = findViewById(R.id.sensor_container);

        answers = Arrays.asList(getResources().getStringArray(R.array.answers));

        mainContainer.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {

                mainContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutReady = true;
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (abs(event.values[0]) > 0 && layoutReady) {
            handleAccelerationSensor(event.values[0] + event.values[1] + event.values[2]);
        }
    }

    private void handleAccelerationSensor(float value) {
        Animation shakeAnimation = createShakeAnimation(value);
        ball.startAnimation(shakeAnimation);
    }

    private Animation createShakeAnimation(final float value) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shakeanimation);

        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                ball.setImageDrawable(getResources().getDrawable(R.drawable.hw3ball_empty));
                answerTextView.setText(getRandomAnswer(value));
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        return animation;
    }

    private String getRandomAnswer(float value) {

        Random rand = new Random();
        int rand_int1 = rand.nextInt(1000);

        int index = (int) abs(value * rand_int1);
        String s = answers.get(index % answers.size());

        if (s != null && !s.isEmpty()) {
            return s;
        }

        return "Error !";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSensor != null) {
            mSensorManager.registerListener(this, mSensor, 10000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mSensor != null) {
            mSensorManager.unregisterListener(this, mSensor);
        }
    }
}
