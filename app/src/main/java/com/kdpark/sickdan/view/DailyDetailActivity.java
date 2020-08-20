package com.kdpark.sickdan.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivityDailyDetailBinding;
import com.kdpark.sickdan.viewmodel.DailyDetailViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DailyDetailActivity extends AppCompatActivity {

    public static final int ADD_MEAL_REQUEST_CODE = 1;
    public static final String ADD_MEAL_DATE = "date";
    public static final String ADD_MEAL_DESCRIPTION = "description";
    public static final String ADD_MEAL_CATEGORY = "category";

    private ActivityDailyDetailBinding binding;
    private DailyDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_detail);

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(DailyDetailViewModel.class);

        String date = getIntent().getStringExtra("date");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        calendar.set(Calendar.DATE, Integer.parseInt(date.substring(6)));

        viewModel.setDate(calendar);

        viewModel.getCurrentDate().observe(this, calendar1 -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM월 dd일");
            binding.actDetailTvDate.setText(dateFormat.format(calendar1.getTime()));
        });

        binding.actDetailFabAddMeal.setOnClickListener(v -> {
            Intent intent = new Intent(this, MealAddActivity.class);
            intent.putExtra(ADD_MEAL_DATE, date);
            startActivityForResult(intent, ADD_MEAL_REQUEST_CODE);
        });

        viewModel.getToastEvent().observe(this, message ->
                Toast.makeText(getApplicationContext(), message.getValue(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MEAL_REQUEST_CODE) {
            viewModel.setDate(viewModel.getCurrentDate().getValue());
        }
    }
}