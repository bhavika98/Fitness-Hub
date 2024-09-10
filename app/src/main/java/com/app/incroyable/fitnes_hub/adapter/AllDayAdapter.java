package com.app.incroyable.fitnes_hub.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.incroyable.fitnes_hub.R;
import com.app.incroyable.fitnes_hub.library.NumberProgressBar;
import com.app.incroyable.fitnes_hub.model.WorkoutData;

import java.util.List;

public class AllDayAdapter extends RecyclerView.Adapter<AllDayAdapter.ViewHolder> {

    private final List<WorkoutData> workoutDataList;
    private final clickMethod clickMethod;
    private final Activity activity;

    public AllDayAdapter(Activity activity, List<WorkoutData> workoutDataList) {
        this.workoutDataList = workoutDataList;
        this.activity = activity;
        this.clickMethod = (clickMethod) activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.adapter_day_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.cardView.setOnClickListener(v -> clickMethod.clickCall(position));

        viewHolder.progressBar.setMax(100);
        if ((position + 1) % 4 == 0 && position <= 27) {
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.restImageView.setVisibility(View.VISIBLE);
            viewHolder.dayText.setText(R.string.rest_day);
        } else {
            viewHolder.restImageView.setVisibility(View.GONE);
            viewHolder.dayText.setText(workoutDataList.get(position).getDay());
            viewHolder.progressBar.setVisibility(View.VISIBLE);

            int progress = (int) workoutDataList.get(position).getProgress();
            viewHolder.progressBar.setProgress(progress >= 99 ? 100 : progress);
        }
    }

    @Override
    public int getItemCount() {
        return workoutDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayText;
        NumberProgressBar progressBar;
        CardView cardView;
        ImageView restImageView;

        public ViewHolder(View view) {
            super(view);
            dayText = view.findViewById(R.id.row_day);
            cardView = view.findViewById(R.id.cardviewone);
            restImageView = view.findViewById(R.id.restImageView);
            progressBar = view.findViewById(R.id.progressbar);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface clickMethod {
        void clickCall(int pos);
    }
}
