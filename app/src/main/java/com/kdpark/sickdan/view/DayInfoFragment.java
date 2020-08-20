package com.kdpark.sickdan.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kdpark.sickdan.databinding.FragmentDayInfoBinding;
import com.kdpark.sickdan.view.control.meallist.MealAdapter;
import com.kdpark.sickdan.view.control.meallist.MealItemMoveCallback;
import com.kdpark.sickdan.viewmodel.DailyDetailViewModel;

public class DayInfoFragment extends Fragment {

    private FragmentDayInfoBinding binding;
    private MealAdapter adapter;

    public DayInfoFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDayInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         DailyDetailViewModel viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(DailyDetailViewModel.class);

         adapter = new MealAdapter(getContext());

         ItemTouchHelper.Callback callback = new MealItemMoveCallback(adapter);
         ItemTouchHelper helper = new ItemTouchHelper(callback);
         helper.attachToRecyclerView(binding.frgDayinfoRcvMeals);

         adapter.setOnDragStartListener(helper::startDrag);

         binding.frgDayinfoRcvMeals.setAdapter(adapter);
         binding.frgDayinfoRcvMeals.setLayoutManager(new LinearLayoutManager(getContext()));

         viewModel.getWalkCount().observe(getViewLifecycleOwner(), walkCount ->
                 binding.frgDayinfoTvWalkcount.setText(String.valueOf(walkCount)));

         viewModel.getBodyWeight().observe(getViewLifecycleOwner(), bodyWeight ->
                 binding.frgDayinfoTvWeight.setText(String.valueOf(bodyWeight)));

         viewModel.getMealList().observe(getViewLifecycleOwner(), mealItems -> {
             adapter.setList(mealItems);
             adapter.notifyDataSetChanged();
         });
    }
}