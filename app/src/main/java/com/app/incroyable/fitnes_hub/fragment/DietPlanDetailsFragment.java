package com.app.incroyable.fitnes_hub.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.model.RepeatData;
import com.app.incroyable.fitnes_hub.model.WeightDay;
import com.app.incroyable.fitnes_hub.utils.FitnessDatabase;

import java.util.ArrayList;
import java.util.List;

public class DietPlanDetailsFragment extends Fragment {

    TextView d_0;
    TextView d_1;
    TextView day_txt;
    ArrayList<RepeatData> diet_plan_detail_array = new ArrayList();
    TextView finished_txt;
    TextView l_0;
    TextView l_1;
    TextView li_0;
    TextView li_1;
    ImageView li_1_dot;
    TextView m_0;
    TextView m_1;

    class C14522 implements OnClickListener {
        C14522() {
        }

        public void onClick(View view) {
            Log.e("value", String.valueOf(new FitnessDatabase(DietPlanDetailsFragment.this.getContext()).updateDietPlanDayValue(DietPlanFragment.day_name, String.valueOf("true"))));
            getActivity().onBackPressed();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_plan_detail, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.exc_details_layout_mtoolbar);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        this.day_txt = (TextView) view.findViewById(R.id.day_txt);
        this.finished_txt = (TextView) view.findViewById(R.id.finished_txt);
        this.m_0 = (TextView) view.findViewById(R.id.m_0);
        this.m_1 = (TextView) view.findViewById(R.id.m_1);
        this.li_0 = (TextView) view.findViewById(R.id.li_0);
        this.li_1 = (TextView) view.findViewById(R.id.li_1);
        this.l_0 = (TextView) view.findViewById(R.id.l_0);
        this.l_1 = (TextView) view.findViewById(R.id.l_1);
        this.d_0 = (TextView) view.findViewById(R.id.d_0);
        this.d_1 = (TextView) view.findViewById(R.id.d_1);
        this.li_1_dot = (ImageView) view.findViewById(R.id.li_1_dot);
        get_detail();
        this.finished_txt.setOnClickListener(new C14522());
        return view;
    }

    public void get_detail() {
        this.diet_plan_detail_array.clear();
        FitnessDatabase db = new FitnessDatabase(getContext());
        List<WeightDay> w_da_no = db.getDietPlanDays();
        if (w_da_no != null) {
            for (WeightDay cn : w_da_no) {
                String day_no = cn.getDay_no();
                String day_value = cn.getDay_value();
                if (day_no.equals(DietPlanFragment.day_name)) {
                    if (day_value.equals("true")) {
                        this.finished_txt.setText("Finished");
                    } else {
                        this.finished_txt.setText("Finish");
                    }
                }
            }
        }
        List<RepeatData> rep_data_models = db.getAllDiet();
        if (rep_data_models != null) {
            for (RepeatData cn2 : rep_data_models) {
                String rep_day_no = cn2.getRep_day_no();
                String morning_meal_0 = cn2.getMorning_meal_0();
                String morning_meal_1 = cn2.getMorning_meal_1();
                String light_meal_0 = cn2.getLight_meal_0();
                String light_meal_1 = cn2.getLight_meal_1();
                String lunch_0 = cn2.getLunch_0();
                String lunch_1 = cn2.getLunch_1();
                String dinner_0 = cn2.getDinner_0();
                String dinner_1 = cn2.getDinner_1();
                if (rep_day_no.equals(DietPlanFragment.day_name)) {
                    this.diet_plan_detail_array.add(new RepeatData(rep_day_no, morning_meal_0, morning_meal_1, light_meal_0, light_meal_1, lunch_0, lunch_1, dinner_0, dinner_1));
                    this.day_txt.setText("" + getString(R.string.day) + " " + (DietPlanFragment.pos + 1));
                    break;
                }
            }
        }
        this.m_0.setText(((RepeatData) this.diet_plan_detail_array.get(0)).getMorning_meal_0());
        this.m_1.setText(((RepeatData) this.diet_plan_detail_array.get(0)).getMorning_meal_1());
        this.li_0.setText(((RepeatData) this.diet_plan_detail_array.get(0)).getLight_meal_0());
        this.li_1.setText(((RepeatData) this.diet_plan_detail_array.get(0)).getLight_meal_1());
        this.l_0.setText(((RepeatData) this.diet_plan_detail_array.get(0)).getLunch_0());
        this.l_1.setText(((RepeatData) this.diet_plan_detail_array.get(0)).getLunch_1());
        this.d_0.setText(((RepeatData) this.diet_plan_detail_array.get(0)).getDinner_0());
        this.d_1.setText(((RepeatData) this.diet_plan_detail_array.get(0)).getDinner_1());
        if (((RepeatData) this.diet_plan_detail_array.get(0)).getLight_meal_1().isEmpty()) {
            this.li_1_dot.setVisibility(View.GONE);
        } else {
            this.li_1_dot.setVisibility(View.VISIBLE);
        }
    }
}
