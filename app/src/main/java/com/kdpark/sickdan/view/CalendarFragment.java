package com.kdpark.sickdan.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kdpark.sickdan.databinding.FragmentCalendarBinding;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.view.control.calendar.CalendarSlideAdapter;
import com.kdpark.sickdan.viewmodel.CalendarViewModel;

import java.util.Calendar;
import java.util.Locale;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private CalendarViewModel viewModel;
    private CalendarSlideAdapter adapter;

    public CalendarFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(CalendarViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initView();
        initObserver();

        binding.frgCalendarVpSwipe.post(()
                -> binding.frgCalendarVpSwipe.setCurrentItem(CalendarSlideAdapter.NUM_PAGES / 2, false));
        adapter.triggerSlideComplete();
    }

    private void initData() {
        viewModel.setMonth(Calendar.getInstance());
        viewModel.setMode(getArguments().getInt(CalendarUtil.VIEW_MODE_KEY));
        viewModel.setMemberId(getArguments().getLong("memberId"));
    }

    private void initView() {
        adapter = new CalendarSlideAdapter(requireActivity(), CalendarSlideAdapter.NUM_PAGES / 2, Calendar.getInstance());
        adapter.setOnSlideComplete((pos, calendar) -> viewModel.setCalendarData(calendar));

        binding.frgCalendarVpSwipe.setOffscreenPageLimit(1);
        binding.frgCalendarVpSwipe.setAdapter(adapter);
        binding.frgCalendarVpSwipe.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) return;
                adapter.setNowPos(position);
                viewModel.setMonth(adapter.getCurrent());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state != ViewPager2.SCROLL_STATE_IDLE) return;
                adapter.triggerSlideComplete();
            }
        });
    }

    private void initObserver() {
        viewModel.currentDate.observe(getViewLifecycleOwner(), currentDate -> {
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH) + 1;
            String text = String.format(Locale.getDefault(),"%d년 %d월", year, month);

            binding.frgCalendarTvYearMonth.setText(text);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}