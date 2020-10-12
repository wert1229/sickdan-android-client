package com.kdpark.sickdan.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivityDailyDetailBinding;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.viewmodel.DailyDetailViewModel;
import com.kdpark.sickdan.viewmodel.common.BundleViewModelFactory;

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
                BundleViewModelFactory.getInstance(getApplication(), getIntent().getExtras()))
                .get(DailyDetailViewModel.class);

        initData();
        initView();
        initObserver();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.act_detail_fl_dayinfo, new DayInfoFragment()).commitAllowingStateLoss();
    }

    private void initData() {
        viewModel.loadData();
        viewModel.isLiked();
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
        viewModel.currentDate.observe(this, calendar -> {
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DATE);
            binding.actDetailTvTitle.setText(String.format(Locale.getDefault(),"%d월 %d일", month, day));
        });

        viewModel.toastEvent.observe(this, message ->
                Toast.makeText(getApplicationContext(), message.getValue(), Toast.LENGTH_SHORT).show());
    }
}