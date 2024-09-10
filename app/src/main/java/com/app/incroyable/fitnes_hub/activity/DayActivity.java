package com.app.incroyable.fitnes_hub.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.adapter.IndividualDayAdapter;
import com.app.incroyable.fitnes_hub.database.DatabaseOperations;
import com.app.incroyable.fitnes_hub.model.WorkoutData;

import java.util.ArrayList;
import java.util.HashMap;

public class DayActivity extends AppCompatActivity implements IndividualDayAdapter.clickMethod {

    private RecyclerView recyclerView;
    private LinearLayout buttonTwo;
    private LinearLayoutManager layoutManager;
    private String day;
    private float progress;
    private HashMap<String, Integer> exerciseResourceMap;
    private HashMap<String, Integer> exerciseDescMap;
    private int currentDayIndex = -1;
    private ArrayList<WorkoutData> workoutDataList;
    private Intent intent;

    private final int[] dayArrays = {
            R.array.day1, R.array.day2, R.array.day3, R.array.day4, R.array.day5,
            R.array.day6, R.array.day7, R.array.day8, R.array.day9, R.array.day10,
            R.array.day11, R.array.day12, R.array.day13, R.array.day14, R.array.day15,
            R.array.day16, R.array.day17, R.array.day18, R.array.day19, R.array.day20,
            R.array.day21, R.array.day22, R.array.day23, R.array.day24, R.array.day25,
            R.array.day26, R.array.day27, R.array.day28, R.array.day29, R.array.day30
    };
    private final int[] cycleArrays = {
            R.array.day1_cycles, R.array.day2_cycles, R.array.day3_cycles, R.array.day4_cycles,
            R.array.day5_cycles, R.array.day6_cycles, R.array.day7_cycles, R.array.day8_cycles,
            R.array.day9_cycles, R.array.day10_cycles, R.array.day11_cycles, R.array.day12_cycles,
            R.array.day13_cycles, R.array.day14_cycles, R.array.day15_cycles, R.array.day16_cycles,
            R.array.day17_cycles, R.array.day18_cycles, R.array.day19_cycles, R.array.day20_cycles,
            R.array.day21_cycles, R.array.day22_cycles, R.array.day23_cycles, R.array.day24_cycles,
            R.array.day25_cycles, R.array.day26_cycles, R.array.day27_cycles, R.array.day28_cycles,
            R.array.day29_cycles, R.array.day30_cycles
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        initializeViews();
        initializeData();

        setupToolbar();
        workoutDataList = prepareAdapterData();
        IndividualDayAdapter adapter = new IndividualDayAdapter(this, workoutDataList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        buttonTwo.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(), MainExerciseActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("workoutDataList", workoutDataList);
            intent.putExtras(bundle);
            intent.putExtra("day", day);
            DatabaseOperations databaseOperations = new DatabaseOperations(getApplicationContext());
            progress = databaseOperations.getDayProgress(day);
            intent.putExtra(NotificationCompat.CATEGORY_PROGRESS, progress);
            startActivity(intent);
        });
    }

    private void handleItemClick(int position) {
        if (position < workoutDataList.size()) {
            Intent intent = new Intent(getApplicationContext(), ExcDetailsActivity.class);
            WorkoutData data = workoutDataList.get(position);
            Bundle bundle = new Bundle();
            bundle.putIntArray("framesIdArray", data.getWorkoutList());
            bundle.putString("excName", data.getExcName());
            bundle.putInt("excNameDescResId", exerciseDescMap.get(data.getExcName()));
            bundle.putInt("excCycle", data.getExcCycles());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private ArrayList<WorkoutData> prepareAdapterData() {
        ArrayList<WorkoutData> dataList = new ArrayList<>();
        String[] exerciseNames = getResources().getStringArray(dayArrays[currentDayIndex]);
        int[] cycles = getResources().getIntArray(cycleArrays[currentDayIndex]);

        for (int i = 0; i < exerciseNames.length; i++) {
            TypedArray resourceArray = getResources().obtainTypedArray(exerciseResourceMap.get(exerciseNames[i]));
            int length = resourceArray.length();
            int[] frameIds = new int[length];
            WorkoutData workoutData = new WorkoutData();
            for (int j = 0; j < length; j++) {
                frameIds[j] = resourceArray.getResourceId(j, -1);
            }
            workoutData.setExcName(exerciseNames[i]);
            workoutData.setExcDescResId(exerciseDescMap.get(exerciseNames[i]));
            workoutData.setExcCycles(cycles[i]);
            workoutData.setPosition(i);
            workoutData.setWorkoutList(frameIds);
            dataList.add(workoutData);
        }
        return dataList;
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerAllDaysList);
        buttonTwo = findViewById(R.id.buttonTwo);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    private void initializeData() {
        exerciseResourceMap = new HashMap<>();
        exerciseDescMap = new HashMap<>();
        populateExerciseMaps();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            day = extras.getString("day");
            currentDayIndex = extras.getInt("day_num");
            progress = extras.getFloat(NotificationCompat.CATEGORY_PROGRESS);
        }
    }

    private void populateExerciseMaps() {
        initializeExerciseResourceMap();
        initializeExerciseDescMap();
    }

    private void initializeExerciseResourceMap() {
        exerciseResourceMap.put("bicycle squat", R.array.aerobics_bicycle_squat);
        exerciseResourceMap.put("shadow boxing", R.array.aerobics_shadow_boxing);
        exerciseResourceMap.put("glute bend", R.array.aerobics_glute_bend);
        exerciseResourceMap.put("leg twisting", R.array.aerobics_leg_twisting);
        exerciseResourceMap.put("jump squat", R.array.aerobics_jump_squat);
        exerciseResourceMap.put("waving the feet", R.array.aerobics_waving_the_feet);
        exerciseResourceMap.put("forward punch", R.array.aerobics_forward_punch);
        exerciseResourceMap.put("jumping lunge", R.array.aerobics_jumping_lunge);
        exerciseResourceMap.put("jumping jacks", R.array.aerobics_jumping_jacks);
        exerciseResourceMap.put("high knees", R.array.aerobics_high_knees);
        exerciseResourceMap.put("forward slant", R.array.aerobics_forward_slant);
        exerciseResourceMap.put("legs up", R.array.aerobics_legs_up);
        exerciseResourceMap.put("backward lunges", R.array.aerobics_backward_lunges);
        exerciseResourceMap.put("forward lunges", R.array.aerobics_forward_lunges);
        exerciseResourceMap.put("knee raising", R.array.aerobics_knee_raising);
        exerciseResourceMap.put("butt kicks", R.array.aerobics_butt_kicks);
        exerciseResourceMap.put("side punches", R.array.aerobics_side_punches);
        exerciseResourceMap.put("bicycle crunches", R.array.aerobics_bicycle_crunches);
        exerciseResourceMap.put("single leg up", R.array.aerobics_single_leg_up);
        exerciseResourceMap.put("basic squats", R.array.aerobics_basic_squats);
        exerciseResourceMap.put("hip stretch", R.array.aerobics_hip_stretch);
        exerciseResourceMap.put("knee shaft", R.array.aerobics_knee_shaft);
        exerciseResourceMap.put("standing trunk twists", R.array.aerobics_standing_trunk_twists);
        exerciseResourceMap.put("swimming", R.array.aerobics_swimming);
        exerciseResourceMap.put("wide squats", R.array.aerobics_wide_squats);
    }

    private void initializeExerciseDescMap() {
        exerciseDescMap.put("bicycle squat", R.string.desc_aerobics_bicycle_squat);
        exerciseDescMap.put("shadow boxing", R.string.desc_shadow_boxing);
        exerciseDescMap.put("glute bend", R.string.desc_aerobics_glute_bend);
        exerciseDescMap.put("leg twisting", R.string.desc_aerobics_leg_twisting);
        exerciseDescMap.put("jump squat", R.string.desc_aerobics_jump_squat);
        exerciseDescMap.put("waving the feet", R.string.desc_aerobics_waving_the_feet);
        exerciseDescMap.put("forward punch", R.string.desc_aerobics_forward_punch);
        exerciseDescMap.put("jumping lunge", R.string.desc_aerobics_jumping_lunge);
        exerciseDescMap.put("jumping jacks", R.string.desc_aerobics_jumping_jacks);
        exerciseDescMap.put("high knees", R.string.desc_aerobics_high_knees);
        exerciseDescMap.put("forward slant", R.string.desc_aerobics_forward_slant);
        exerciseDescMap.put("legs up", R.string.desc_aerobics_legs_up);
        exerciseDescMap.put("backward lunges", R.string.desc_aerobics_backward_lunges);
        exerciseDescMap.put("forward lunges", R.string.desc_aerobics_forward_lunges);
        exerciseDescMap.put("knee raising", R.string.desc_aerobics_knee_raising);
        exerciseDescMap.put("butt kicks", R.string.desc_aerobics_butt_kicks);
        exerciseDescMap.put("side punches", R.string.desc_aerobics_side_punches);
        exerciseDescMap.put("bicycle crunches", R.string.desc_aerobics_bicycle_crunches);
        exerciseDescMap.put("single leg up", R.string.desc_aerobics_single_leg_up);
        exerciseDescMap.put("basic squats", R.string.desc_aerobics_basic_squats);
        exerciseDescMap.put("hip stretch", R.string.desc_aerobics_hip_stretch);
        exerciseDescMap.put("knee shaft", R.string.desc_aerobics_knee_shaft);
        exerciseDescMap.put("standing trunk twists", R.string.desc_aerobics_standing_trunk_twists);
        exerciseDescMap.put("swimming", R.string.desc_aerobics_swimming);
        exerciseDescMap.put("wide squats", R.string.desc_aerobics_wide_squats);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.mtoolbar);
        TextView title = toolbar.findViewById(R.id.mtoolbar_title);
        title.setText(day);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void clickCall(int pos) {
        handleItemClick(pos);
    }
}

