package com.kdpark.sickdan.view;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivityFriendMainBinding;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.viewmodel.FriendMainViewModel;
import com.kdpark.sickdan.viewmodel.common.BundleViewModelFactory;

public class FriendMainActivity extends AppCompatActivity {

    private ActivityFriendMainBinding binding;
    private FriendMainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend_main);
        viewModel = new ViewModelProvider(this,
                BundleViewModelFactory.getInstance(getApplication(), getIntent().getExtras()))
                .get(FriendMainViewModel.class);

        initData();
        initView();
        initObserver();

        Bundle bundle = new Bundle();
        bundle.putInt(CalendarUtil.VIEW_MODE_KEY, CalendarUtil.MODE_PUBLIC);
        bundle.putLong("memberId", getIntent().getLongExtra("memberId", 0));
        Fragment fragment = new CalendarFragment();
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.act_friend_main_fl_main, fragment).commitAllowingStateLoss();
    }

    private void initData() {}

    private void initView() {
        setSupportActionBar(binding.actFriendMainTbTop);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.actFriendMainTvTitle.setText(getIntent().getStringExtra("displayName"));
        binding.actFriendMainImgClose.setOnClickListener(v -> finish());
    }

    private void initObserver() {}
}