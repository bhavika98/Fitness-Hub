package com.app.incroyable.fitnes_hub.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.database.DatabaseOperations;
import com.app.incroyable.fitnes_hub.library.FAImageView;
import com.app.incroyable.fitnes_hub.model.WorkoutData;
import com.app.incroyable.fitnes_hub.utils.AbsWomenApplication;
import com.app.incroyable.fitnes_hub.utils.AppUtils;
import com.app.incroyable.fitnes_hub.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainExerciseActivity extends AppCompatActivity implements OnClickListener {

    private CountDownTimer workoutTimer;
    private CountDownTimer restTimer;
    private CountDownTimer mainExerciseTimer;

    private FAImageView animImageFull;
    private FAImageView nextExerciseAnimation;
    private ImageView pauseMainExercise;
    private ImageView resumeMainExercise;
    private FloatingActionButton playPause;
    private ProgressBar progressBar;
    private ProgressBar restTimerProgress;
    private ProgressBar timerProgress;
    private RoundCornerProgressBar progressbar;
    private TextView count;
    private TextView countRestTimer;
    private TextView excDescInReadyToGo;
    private TextView excName;
    private TextView excNameInReadyToGo;
    private TextView nextExerciseName;
    private TextView nextExerciseCycles;
    private TextView pauseRestTime;
    private TextView resumeRestTime;
    private TextView tvProgress;
    private TextView tvProgressMax;

    private CoordinatorLayout readyToGoLayout;
    private RelativeLayout restScreen;
    private LinearLayout hLayoutProgress;

    private DatabaseOperations databaseOperations;
    private ArrayList<WorkoutData> workoutDataList;
    private String packageName;
    private String day;

    private float exerciseFactor = 1.0f;
    private int maxProgressValue = 0;
    private int playPauseState = 0;
    private int mainExcCounter = 1;
    private int exerciseCounter;
    private long exerciseDuration;
    private final long defaultRestDuration = 25000;
    private long durations;
    private boolean isRestSkipped = false;
    private boolean pauseClicked = false;
    private double progress;

    private AbsWomenApplication absWomenApplication;
    private Boolean isMainExerciseRunning = Boolean.FALSE;
    private Boolean isRestTimerRunning = Boolean.FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main_exercise);

        databaseOperations = new DatabaseOperations(this);
        setupToolbar();
        retrieveIntentData();
        initializeFields();
        setupUI();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.mtoolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void retrieveIntentData() {
        Intent intent = getIntent();
        workoutDataList = (ArrayList) intent.getExtras().getSerializable("workoutDataList");
        day = intent.getExtras().getString("day");
        double categoryProgress = intent.getExtras().getFloat(NotificationCompat.CATEGORY_PROGRESS);
        exerciseCounter = (int) categoryProgress / (100 / workoutDataList.size());
    }

    private void initializeFields() {
        packageName = getApplicationContext().getPackageName();
        absWomenApplication = AbsWomenApplication.getInstance();
    }

    private void setupUI() {
        setOnClickListener();
        startAnimation();
        readyToGo(10000);
        initProgressBar();
    }

    private void initProgressBar() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        layoutParams.setMargins(2, 0, 2, 0);

        for (int i = 0; i < workoutDataList.size(); i++) {
            ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setPadding(0, 0, 0, -8);
            progressBar.setLayoutParams(layoutParams);
            progressBar.setId(i);
            progressBar.setScaleY(2.5f);
            progressBar.setBackgroundColor(getResources().getColor(R.color.lightestgrey));
            hLayoutProgress.addView(progressBar);
        }

        updateProgressBars();
    }

    private void updateProgressBars() {
        for (int i = 0; i < exerciseCounter; i++) {
            ProgressBar progressBar = (ProgressBar) hLayoutProgress.findViewById(i);
            if (progressBar != null) {
                WorkoutData currentWorkoutData = (WorkoutData) workoutDataList.get(exerciseCounter);
                int maxProgress = currentWorkoutData.getWorkoutList().length * currentWorkoutData.getExcCycles();

                progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.xml_launch_progress));
                progressBar.setMax(maxProgress);
                progressBar.setProgress(maxProgress);
            }
        }
    }

    private void startMainExerciseTimer(long durationMillis, int cycleIndex, float initialProgress) {
        WorkoutData currentWorkoutData = (WorkoutData) workoutDataList.get(exerciseCounter);
        int frameIndex = 0;

        if (pauseClicked) {
            pauseClicked = false;
            for (int frameId : currentWorkoutData.getWorkoutList()) {
                animImageFull.addImageFrame(frameId);
            }
        } else {
            animImageFull.reset();
            for (int frameId : currentWorkoutData.getWorkoutList()) {
                animImageFull.addImageFrame(frameId);
            }
        }

        int[] workoutFrames = currentWorkoutData.getWorkoutList();
        int frameCount = workoutFrames.length;

        while (frameIndex < frameCount) {
            animImageFull.addImageFrame(workoutFrames[frameIndex]);
            frameIndex++;
        }

        restScreen.setVisibility(View.GONE);
        animImageFull.startAnimation();
        progressbar.setMax((float) ((exerciseDuration / 1000) - 1));
        maxProgressValue = (int) ((exerciseDuration / 1000) - 1);

        progressBar = (ProgressBar) hLayoutProgress.findViewById(exerciseCounter);
        progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.xml_launch_progress));
        progressBar.setMax((((int) exerciseDuration) / 1000) - 1);

        absWomenApplication.addEarCorn(packageName);

        final float progressIncrement = initialProgress;
        final int initialCycleIndex = cycleIndex;

        workoutTimer = new CountDownTimer(durationMillis, 1000) {
            float currentProgress = progressIncrement;
            int currentCycle = initialCycleIndex;
            int lastCycle = 0;

            @Override
            public void onFinish() {
                currentProgress += 1.0f;
                progressbar.setProgress(currentProgress);
                exerciseCounter++;
                animImageFull.stopAnimation();

                if (exerciseCounter < workoutDataList.size()) {
                    restScreen.setVisibility(View.VISIBLE);
                    progressbar.setMax((float) ((WorkoutData) workoutDataList.get(exerciseCounter)).getExcCycles());

                    StringBuilder progressBuilder = new StringBuilder();
                    currentCycle++;
                    progressBuilder.append(currentCycle);
                    tvProgress.setText(progressBuilder.toString());

                    progressBuilder = new StringBuilder();
                    progressBuilder.append(((WorkoutData) workoutDataList.get(exerciseCounter)).getExcCycles());
                    tvProgressMax.setText(progressBuilder.toString());

                    currentProgress = 1.0f;
                    currentCycle = 1;

                    double progress = 100.0d / ((double) workoutDataList.size());
                    progress += databaseOperations.getDayProgress(day);
                    if (progress <= 100.0d) {
                        databaseOperations.updateDayProgress(day, (float) progress);
                    }

                    try {
                        Intent intent = new Intent(AppUtils.WORKOUT_BROADCAST_FILTER);
                        intent.putExtra(AppUtils.KEY_PROGRESS, progress);
                        sendBroadcast(intent);
                    } catch (Throwable e) {
                        Log.e("Error", " => " + e.getMessage());
                    }

                    pauseRestTime.setVisibility(View.VISIBLE);
                    resumeRestTime.setVisibility(View.GONE);
                    startRestTimer(defaultRestDuration);
                } else {
                    double progress = 100.0d / ((double) workoutDataList.size());
                    progress += databaseOperations.getDayProgress(day);
                    if ((int) progress <= 100) {
                        databaseOperations.updateDayProgress(day, (float) progress);
                    }

                    try {
                        Intent intent = new Intent(AppUtils.WORKOUT_BROADCAST_FILTER);
                        intent.putExtra(AppUtils.KEY_PROGRESS, progress);
                        sendBroadcast(intent);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                    restScreen.setVisibility(View.GONE);
                    launchCompletionActivity();
                }

                exerciseFactor = 1.0f;
                mainExcCounter = 1;
            }

            private void launchCompletionActivity() {
                Intent intent = new Intent(getApplicationContext(), CompletionExcActivity.class);
                intent.putExtra("day", day);

                int totalCycles = 0;
                int totalTime = 0;
                for (int index = 0; index < workoutDataList.size(); index++) {
                    WorkoutData data = (WorkoutData) workoutDataList.get(index);
                    totalCycles += data.getExcCycles();
                    totalTime += (data.getWorkoutList().length + Constants.REST_TIME);
                }

                intent.putExtra("totalExc", totalCycles);
                intent.putExtra("totalTime", totalTime);
                startActivity(intent);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                float progressIncrement;
                StringBuilder progressMaxBuilder;
                String exerciseName;

                if (((WorkoutData) workoutDataList.get(exerciseCounter)).getWorkoutList().length <= 2) {
                    if (currentCycle <= ((WorkoutData) workoutDataList.get(exerciseCounter)).getExcCycles()) {
                        currentCycle++;
                    }
                    currentProgress += 1.0f;
                    int progressValue = currentCycle - 1;
                    progressbar.setProgress(progressValue);
                    progressBar.setProgress(progressValue);
                    tvProgress.setText(String.valueOf(progressValue));

                    int secondsRemaining = (int) (millisUntilFinished / 1000) % 60;
                    if (secondsRemaining == 1) {
                        animImageFull.stopAnimation();
                    }

                    exerciseName = ((WorkoutData) workoutDataList.get(exerciseCounter)).getExcName().replace("_", " ").toUpperCase();
                    excName.setText(exerciseName);

                    if (!exerciseName.equalsIgnoreCase("inverted board")) {
                        if (exerciseName.equalsIgnoreCase("unstable sit ups")) {
                            progressMaxBuilder = new StringBuilder("/");
                            progressMaxBuilder.append(((WorkoutData) workoutDataList.get(exerciseCounter)).getExcCycles());
                            tvProgressMax.setText(progressMaxBuilder.toString());
                            durations = millisUntilFinished;
                            mainExcCounter = currentCycle;
                            exerciseFactor = currentProgress;

                            if (lastCycle != currentCycle) {
                                lastCycle = currentCycle;
                                if (lastCycle != 1) {
                                    absWomenApplication.playEarCorn(packageName);
                                }
                            }
                        }
                    }

                    progressMaxBuilder = new StringBuilder("/");
                    progressMaxBuilder.append(((WorkoutData) workoutDataList.get(exerciseCounter)).getExcCycles());
                    progressMaxBuilder.append("s");
                    tvProgressMax.setText(progressMaxBuilder.toString());
                    durations = millisUntilFinished;
                    mainExcCounter = currentCycle;
                    exerciseFactor = currentProgress;

                    if (lastCycle != currentCycle) {
                        lastCycle = currentCycle;
                        if (lastCycle != 1) {
                            absWomenApplication.playEarCorn(packageName);
                        }
                    }
                } else {
                    if (currentProgress % ((float) ((WorkoutData) workoutDataList.get(exerciseCounter)).getWorkoutList().length) == 0.0f) {
                        currentCycle++;
                    }
                    currentProgress += 1.0f;

                    if (maxProgressValue < currentProgress) {
                        animImageFull.stopAnimation();
                    } else {
                        progressbar.setProgress(currentProgress);
                        progressBar.setProgress((int) currentProgress);
                        tvProgress.setText(String.valueOf(currentCycle));
                    }

                    exerciseName = ((WorkoutData) workoutDataList.get(exerciseCounter)).getExcName().replace("_", " ").toUpperCase();
                    excName.setText(exerciseName);

                    if (exerciseName.equalsIgnoreCase("inverted board")) {
                        if (exerciseName.equalsIgnoreCase("unstable sit ups")) {
                            progressMaxBuilder = new StringBuilder("/");
                            progressMaxBuilder.append(((WorkoutData) workoutDataList.get(exerciseCounter)).getExcCycles());
                            tvProgressMax.setText(progressMaxBuilder.toString());
                            durations = millisUntilFinished;
                            mainExcCounter = currentCycle;
                            exerciseFactor = currentProgress;

                            if (lastCycle != currentCycle) {
                                lastCycle = currentCycle;
                                if (lastCycle != 1) {
                                    absWomenApplication.playEarCorn(packageName);
                                }
                            }
                        }
                    }

                    progressMaxBuilder = new StringBuilder("/");
                    progressMaxBuilder.append(((WorkoutData) workoutDataList.get(exerciseCounter)).getExcCycles());
                    progressMaxBuilder.append("s");
                    tvProgressMax.setText(progressMaxBuilder.toString());
                    durations = millisUntilFinished;
                    mainExcCounter = currentCycle;
                    exerciseFactor = currentProgress;

                    if (lastCycle != currentCycle) {
                        lastCycle = currentCycle;
                        if (lastCycle != 1) {
                            absWomenApplication.playEarCorn(packageName);
                        }
                    }
                }
            }
        }.start();
    }

    private void setOnClickListener() {
        initializeViews();
        setOnClickListeners();
        configureProgressBar();
    }

    private void initializeViews() {
        progressbar = findViewById(R.id.progress_one);
        animImageFull = findViewById(R.id.animImageFull);
        tvProgress = findViewById(R.id.tv_progress);
        tvProgressMax = findViewById(R.id.tv_progress_max);
        restScreen = findViewById(R.id.restScreen);
        excName = findViewById(R.id.excName);
        pauseMainExercise = findViewById(R.id.pauseMainExercise);
        resumeMainExercise = findViewById(R.id.resumeMainExercise);
        timerProgress = findViewById(R.id.timerProgress);
        count = findViewById(R.id.counting);
        playPause = findViewById(R.id.floatingPlay);
        excDescInReadyToGo = findViewById(R.id.excDescInReadyToGo);
        excNameInReadyToGo = findViewById(R.id.excNameInReadyToGo);
        pauseRestTime = findViewById(R.id.pauseRestTime);
        resumeRestTime = findViewById(R.id.resumeRestTime);
        restTimerProgress = findViewById(R.id.restTimerProgress);
        countRestTimer = findViewById(R.id.rest_counting);
        nextExerciseName = findViewById(R.id.nextExerciseName);
        nextExerciseCycles = findViewById(R.id.nextExerciseCycles);
        nextExerciseAnimation = findViewById(R.id.nextExerciseAnimation);
        hLayoutProgress = findViewById(R.id.hLayoutProgress);
        readyToGoLayout = findViewById(R.id.readyToGoLayout);
    }

    private void setOnClickListeners() {
        pauseMainExercise.setOnClickListener(this);
        resumeMainExercise.setOnClickListener(this);
        findViewById(R.id.txtSkip).setOnClickListener(this);
        findViewById(R.id.skipRestTime).setOnClickListener(this);
        playPause.setOnClickListener(this);
        pauseRestTime.setOnClickListener(this);
        resumeRestTime.setOnClickListener(this);
    }

    private void configureProgressBar() {
        progressbar.setMax(10);
    }

    private void startAnimation() {
        configureAnimationSettings();
        loadAnimationFrames();
        animImageFull.startAnimation();
    }

    private void configureAnimationSettings() {
        animImageFull.setInterval(1000);
        animImageFull.setLoop(true);
        animImageFull.reset();
    }

    private void loadAnimationFrames() {
        WorkoutData currentWorkoutData = (WorkoutData) workoutDataList.get(exerciseCounter);
        int[] workoutFrames = currentWorkoutData.getWorkoutList();
        for (int frame : workoutFrames) {
            animImageFull.addImageFrame(frame);
        }
    }

    private void startRestTimer(long durationMillis) {
        WorkoutData currentWorkoutData = (WorkoutData) workoutDataList.get(exerciseCounter);
        String exerciseName = currentWorkoutData.getExcName().replace("_", " ").toUpperCase();
        int exerciseCycles = currentWorkoutData.getExcCycles();
        int[] workoutList = currentWorkoutData.getWorkoutList();

        nextExerciseName.setText(exerciseName);
        String cyclesText = "x" + exerciseCycles;
        nextExerciseCycles.setText(cyclesText);

        nextExerciseAnimation.reset();
        for (int frameResourceId : workoutList) {
            nextExerciseAnimation.addImageFrame(frameResourceId);
        }
        nextExerciseAnimation.startAnimation();
        restTimerProgress.setMax((int) (defaultRestDuration / 1000));

        if (durationMillis == defaultRestDuration) {
            absWomenApplication.speak("Take rest");
            String announcement = "the next exercise is " + exerciseName;
            absWomenApplication.speak(announcement);
        }

        mainExerciseTimer = new CountDownTimer(durationMillis, 1000) {

            @Override
            public void onFinish() {
                restScreen.setVisibility(View.GONE);
                isMainExerciseRunning = false;

                int workoutListSize = workoutList.length;
                long cycles = (workoutListSize > 2 ? workoutListSize * exerciseCycles : exerciseCycles) + 1;
                exerciseDuration = cycles * 1000;

                pauseMainExercise.setVisibility(View.VISIBLE);
                resumeMainExercise.setVisibility(View.GONE);

                startMainExerciseTimer(exerciseDuration, mainExcCounter, exerciseFactor);
            }

            @Override
            @SuppressLint({"SetTextI18n"})
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = (millisUntilFinished - 1000) / 1000;
                restTimerProgress.setProgress((int) secondsRemaining);
                countRestTimer.setText(String.valueOf(secondsRemaining));

                if (secondsRemaining < 4) {
                    switch ((int) secondsRemaining) {
                        case 3:
                            absWomenApplication.speak("three");
                            break;
                        case 2:
                            absWomenApplication.speak("two");
                            break;
                        case 1:
                            absWomenApplication.speak("one");
                            break;
                        case 0:
                            if (!isMainExerciseRunning) {
                                absWomenApplication.speak("start");
                                isMainExerciseRunning = true;
                            }
                            break;
                    }
                } else if (!absWomenApplication.isSpeaking()) {
                    absWomenApplication.playEarCorn(packageName);
                }
            }
        }.start();
    }

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    @Override
    public void onBackPressed() {
        pauseClicked = false;
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm")
                .setMessage("Would you like to quit the workout?")
                .setNegativeButton("No", this::onCancelDialogClick)
                .setPositiveButton("Yes", this::onConfirmDialogClick)
                .setCancelable(true)
                .create()
                .show();
    }

    private void onCancelDialogClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

    private void onConfirmDialogClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        try {
            cancelTimers();
            navigateToMainActivity();
            onSuperBackPressed();
        } catch (Throwable e) {
            Log.e("Error", " => " + e.getMessage());
        }
    }

    private void cancelTimers() {
        if (restTimer != null) restTimer.cancel();
        if (workoutTimer != null) workoutTimer.cancel();
        if (mainExerciseTimer != null) mainExerciseTimer.cancel();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void onSuperBackPressed() {
        super.onBackPressed();
    }

    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.floatingPlay) {
            if (playPauseState % 2 == 0) {
                playPause.setImageResource(R.drawable.play);
                restTimer.cancel();
            } else {
                playPause.setImageResource(R.drawable.pause);
                readyToGo(durations);
            }
            playPauseState++;

        } else if (id == R.id.pauseMainExercise) {
            pauseMainExercise.setVisibility(View.GONE); 
            resumeMainExercise.setVisibility(View.VISIBLE);
            workoutTimer.cancel();
            animImageFull.stopAnimation();
            pauseClicked = true;

        } else if (id == R.id.pauseRestTime) {
            pauseRestTime.setVisibility(View.GONE);
            resumeRestTime.setVisibility(View.VISIBLE);
            mainExerciseTimer.cancel();

        } else if (id == R.id.resumeMainExercise) {
            pauseMainExercise.setVisibility(View.VISIBLE);
            resumeMainExercise.setVisibility(View.GONE);
            try {
                startMainExerciseTimer(durations - 1000, mainExcCounter, exerciseFactor);
            } catch (Throwable e) {
                Log.e("Error", " => " + e.getMessage());
            }

        } else if (id == R.id.resumeRestTime) {
            pauseRestTime.setVisibility(View.VISIBLE);
            resumeRestTime.setVisibility(View.GONE);
            startRestTimer(durations);

        } else if (id == R.id.txtSkip) {
            restTimer.cancel();
            restTimer.onFinish();
            if (absWomenApplication.isSpeaking()) {
                absWomenApplication.stop();
            }

        } else if (id == R.id.skipRestTime) {
            if (!isRestSkipped) {
                isRestSkipped = true;
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isRestSkipped = false;
                    }
                }, 1000);
                mainExerciseTimer.cancel();
                mainExerciseTimer.onFinish();
                if (absWomenApplication.isSpeaking()) {
                    absWomenApplication.stop();
                }
                pauseRestTime.setVisibility(View.VISIBLE);
                resumeRestTime.setVisibility(View.GONE);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    public void readyToGo(long durationMillis) {
        WorkoutData currentExerciseData = (WorkoutData) workoutDataList.get(exerciseCounter);
        String exerciseName = currentExerciseData.getExcName().replace("_", " ").toUpperCase();
        String exerciseNameLowerCase = exerciseName.toLowerCase();

        excDescInReadyToGo.setText(((WorkoutData) workoutDataList.get(exerciseCounter)).getExcDescResId());
        excNameInReadyToGo.setText(exerciseName);

        if (durationMillis == 10000) {
            absWomenApplication.speak("ready to go");
            String announcement = "the exercise is " + exerciseNameLowerCase;
            absWomenApplication.speak(announcement);
        }

        timerProgress.setMax(10);
        restTimer = new CountDownTimer(durationMillis, 1000) {

            @Override
            public void onFinish() {
                isRestTimerRunning = false;
                timerProgress.setProgress(0);
                readyToGoLayout.setVisibility(View.GONE);

                int workoutListSize = currentExerciseData.getWorkoutList().length;
                long exerciseCycles = currentExerciseData.getExcCycles();
                long mainExerciseDurationMillis = (workoutListSize > 2 ? workoutListSize * exerciseCycles : exerciseCycles) + 1;
                exerciseDuration = mainExerciseDurationMillis * 1000;

                pauseMainExercise.setVisibility(View.VISIBLE);
                resumeMainExercise.setVisibility(View.GONE);

                startMainExerciseTimer(exerciseDuration, mainExcCounter, exerciseFactor);
            }

            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) millisUntilFinished / 1000;
                timerProgress.setProgress(secondsRemaining);
                count.setText(String.valueOf(secondsRemaining));

                if (secondsRemaining < 4) {
                    if (secondsRemaining == 3) {
                        absWomenApplication.speak("three");
                    } else if (secondsRemaining == 2) {
                        absWomenApplication.speak("two");
                    } else if (secondsRemaining == 1) {
                        absWomenApplication.speak("one");
                    } else if (secondsRemaining == 0 && !isRestTimerRunning) {
                        absWomenApplication.speak("let's start");
                        isRestTimerRunning = true;
                    }
                } else if (!absWomenApplication.isSpeaking()) {
                    absWomenApplication.playEarCorn(packageName);
                }
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pauseMainExercise.setVisibility(View.GONE);
        resumeMainExercise.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (restTimer != null) {
            restTimer.cancel();
        }
        if (workoutTimer != null) {
            workoutTimer.cancel();
        }
        if (mainExerciseTimer != null) {
            mainExerciseTimer.cancel();
        }
        playPauseState--;
        playPause.setImageResource(R.drawable.play);
        resumeMainExercise.setVisibility(View.VISIBLE);
        pauseMainExercise.setVisibility(View.GONE);
        resumeRestTime.setVisibility(View.VISIBLE);
        pauseRestTime.setVisibility(View.GONE);
        animImageFull.stopAnimation();
    }
}
