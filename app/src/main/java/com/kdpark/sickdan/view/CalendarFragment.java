package com.kdpark.sickdan.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kdpark.sickdan.databinding.FragmentCalendarBinding;
import com.kdpark.sickdan.view.control.calendar.CalendarAdapter;
import com.kdpark.sickdan.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;

public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;
    private CalendarAdapter adapter;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainViewModel viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(MainViewModel.class);

        adapter = new CalendarAdapter(requireContext());
        adapter.setOnCellClickListener(cell -> {
            Intent intent = new Intent(requireContext(), DailyDetailActivity.class);
            intent.putExtra("date", cell.getDate());
            requireActivity().startActivity(intent);
        });

        binding.frgCalendarRcvCalendar.setLayoutManager(new GridLayoutManager(requireActivity(), 7));
        binding.frgCalendarRcvCalendar.setAdapter(adapter);

        binding.frgCalendarBtnPrev.setOnClickListener(v -> viewModel.setPrevMonth());
        binding.frgCalendarBtnNext.setOnClickListener(v -> viewModel.setNextMonth());

        viewModel.getCurrentDate().observe(requireActivity(), currentDate -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM");
            binding.frgCalendarTvYearMonth.setText(dateFormat.format(currentDate.getTime()));
        });

        viewModel.getDailyList().observe(requireActivity(), dailyList -> {
            adapter.setList(dailyList);
            adapter.notifyDataSetChanged();
        });

        viewModel.setCurrentMonth();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}