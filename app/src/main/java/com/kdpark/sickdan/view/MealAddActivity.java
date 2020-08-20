package com.kdpark.sickdan.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivityMealAddBinding;
import com.kdpark.sickdan.model.dto.MealCategory;
import com.kdpark.sickdan.viewmodel.DailyDetailViewModel;
import com.kdpark.sickdan.viewmodel.Event;

public class MealAddActivity extends AppCompatActivity {

    private ActivityMealAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meal_add);

        String date = getIntent().getStringExtra(DailyDetailActivity.ADD_MEAL_DATE);

        DailyDetailViewModel viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(DailyDetailViewModel.class);

        viewModel.getCloseActivity().observe(this, booleanEvent -> {
            setResult(RESULT_OK);
            finish();
        });

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, getCategoryList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.actAddmealSpCategory.setAdapter(adapter);
        binding.actAddmealBtnCreate.setOnClickListener(v -> {
            String description = binding.actAddmealEdDescription.getText().toString();
            String category = binding.actAddmealSpCategory.getSelectedItem().toString();

            viewModel.addMeal(date, description, MealCategory.valueOf(category));
        });

    }

    private String[] getCategoryList() {
        MealCategory[] values = MealCategory.values();
        String[] list = new String[values.length];

        for (int i = 0; i < values.length ; i++) {
            list[i] = values[i].name();
        }

        return list;
    }
}