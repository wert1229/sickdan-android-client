package com.kdpark.sickdan.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivityMainBinding;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.viewmodel.MainViewModel;
import com.kdpark.sickdan.viewmodel.common.BundleViewModelFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    private final long closeDelay = 3500;
    private long lastPressedTime = 1000;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this,
                BundleViewModelFactory.getInstance(getApplication(), getIntent().getExtras()))
                .get(MainViewModel.class);

        initData();
        initView();
        initObserver();

        loadCalendar();

        viewModel.getMyInfo();
    }

    private void loadCalendar() {
        Fragment fragment = new CalendarFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(CalendarUtil.VIEW_MODE_KEY, CalendarUtil.MODE_PRIVATE);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.act_main_fl_main, fragment).commitAllowingStateLoss();
    }

    private void initData() {}

    private void initView() {
        binding.actMainNavBottom.setOnNavigationItemSelectedListener(menuItem -> {
            Fragment fragment;

            switch (menuItem.getItemId()) {
                case R.id.menu_bottom_calendar:
                    fragment = new CalendarFragment();
                    break;
                case R.id.menu_bottom_friend:
                    fragment = new FriendFragment();
                    break;
                case R.id.menu_bottom_statistics:
                    fragment = new FriendFragment();
                    break;
                default:
                    fragment = new FriendFragment();
            }

            Bundle bundle = new Bundle();
            bundle.putInt(CalendarUtil.VIEW_MODE_KEY, CalendarUtil.MODE_PRIVATE);
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.act_main_fl_main, fragment).commitAllowingStateLoss();

            return true;
        });

        setSupportActionBar(binding.actMainTbTop);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initObserver() {
        viewModel.member.observe(this, memberDto -> binding.actMainTvTitle.setText(memberDto.getDisplayName()));
    }

    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);

        if (System.currentTimeMillis() > lastPressedTime + closeDelay) {
            lastPressedTime = System.currentTimeMillis();
            toast.show();
            return;
        }

        toast.cancel();
        finish();
    }
}