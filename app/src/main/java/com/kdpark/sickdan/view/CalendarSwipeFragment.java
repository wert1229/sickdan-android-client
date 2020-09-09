package com.kdpark.sickdan.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.FragmentCalendarBinding;
import com.kdpark.sickdan.databinding.FragmentCalendarSwipeBinding;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.view.control.calendar.CalendarAdapter;
import com.kdpark.sickdan.view.control.calendar.CalendarCell;
import com.kdpark.sickdan.viewmodel.CalendarViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CalendarSwipeFragment extends Fragment {

    private FragmentCalendarSwipeBinding binding;
    private CalendarViewModel viewModel;
    private CalendarAdapter adapter;
    private String yyyymm;

    public CalendarSwipeFragment() {}

    public static CalendarSwipeFragment newInstance(String yyyymm) {
        CalendarSwipeFragment fragment = new CalendarSwipeFragment();

        Bundle bundle = new Bundle();
        bundle.putString("date", yyyymm);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarSwipeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(CalendarViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initView();
        initObserver();
    }

    private void initData() {
        yyyymm = getArguments().getString("date");
    }

    private void initView() {
        adapter = new CalendarAdapter(requireContext());
        adapter.setOnCellClickListener((viewHolder, cell) -> {
            Intent intent = new Intent(requireContext(), DailyDetailActivity.class);
            intent.putExtra("date", cell.getDate());
            intent.putExtra("memberId", viewModel.getMemberId());
            intent.putExtra(CalendarUtil.VIEW_MODE_KEY, viewModel.getMode());

            requireActivity().startActivity(intent);
        });

        binding.frgCalendarRcvCalendar.setLayoutManager(new GridLayoutManager(requireActivity(), 7));
        binding.frgCalendarRcvCalendar.setAdapter(adapter);

        adapter.setList(CalendarUtil.getDefaultOfMonth(yyyymm));
    }

    private void initObserver() {
        viewModel.dailyList.observe(getViewLifecycleOwner(), currMonthList -> {
            if (CalendarUtil.calendarToString(viewModel.currentDate.getValue(), "yyyyMM").equals(yyyymm))
                adapter.setList(currMonthList);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}