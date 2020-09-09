package com.kdpark.sickdan.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivityDailyDetailBinding;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.viewmodel.DailyDetailViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DailyDetailActivity extends AppCompatActivity {

    private ActivityDailyDetailBinding binding;
    private DailyDetailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_daily_detail);
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(DailyDetailViewModel.class);
        initData();
        initView();
        initObserver();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.act_detail_fl_dayinfo, new DayInfoFragment()).commitAllowingStateLoss();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();

        viewModel.setMode(bundle.getInt(CalendarUtil.VIEW_MODE_KEY, 0));
        viewModel.setMemberId(bundle.getLong("memberId", 0));

        String date = bundle.getString("date");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        calendar.set(Calendar.DATE, Integer.parseInt(date.substring(6)));

        viewModel.setDate(calendar);
    }

    private void initView() {
        setSupportActionBar(binding.actDetailTbTop);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.actDetailImgClose.setOnClickListener(v -> finish());
        binding.actDetailTvDate.setText("리빙포인트 : 많이 먹으면 살이 찔 수 있다          리빙포인트 : 많이 먹으면 살이 찔 수 있다");
        binding.actDetailTvDate.setSelected(true);
    }

    private void initObserver() {
        viewModel.getCurrentDate().observe(this, calendar -> {
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            binding.actDetailTvTitle.setText(String.format(Locale.getDefault(),"%d월 %d일", month, day));
        });

        viewModel.getToastEvent().observe(this, message ->
                Toast.makeText(getApplicationContext(), message.getValue(), Toast.LENGTH_SHORT).show());
    }
}