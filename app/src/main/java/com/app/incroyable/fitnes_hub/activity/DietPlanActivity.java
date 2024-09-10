package com.app.incroyable.fitnes_hub.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.fragment.DietPlanFragment;

public class DietPlanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_plan);

        if (savedInstanceState == null) {
            initFragment();
        }
    }

    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentview, new DietPlanFragment());
        transaction.commit();
    }
}

