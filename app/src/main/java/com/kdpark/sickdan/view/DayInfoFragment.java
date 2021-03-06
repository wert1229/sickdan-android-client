package com.kdpark.sickdan.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.FragmentDayInfoBinding;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.util.ImageUtil;
import com.kdpark.sickdan.util.SharedDataUtil;
import com.kdpark.sickdan.view.control.meallist.MealAdapter;
import com.kdpark.sickdan.view.control.meallist.MealItem;
import com.kdpark.sickdan.viewmodel.DailyDetailViewModel;
import com.kdpark.sickdan.viewmodel.common.BundleViewModelFactory;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class DayInfoFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private FragmentDayInfoBinding binding;
    private DailyDetailViewModel viewModel;
    private MealAdapter adapter;

    private Uri imageUri;
    private String imageFilePath;
    private Long tempId;
    private boolean isWeightEditMode = false;

    public DayInfoFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDayInfoBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity(),
                BundleViewModelFactory.getInstance(requireActivity().getApplication(), getArguments()))
                .get(DailyDetailViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initView();
        initObserver();
    }

    private void initData() {}

    private void initView() {
        adapter = new MealAdapter(getContext());

        adapter.setOnManipulateMealListener(new MealAdapter.OnManipulateMealListener() {
            @Override
            public void onAddConfirmed(MealItem item) {
                viewModel.addMeal(item.getDescription(), item.getCategory());
            }

            @Override
            public void onEditConfirmed(MealItem item) {
                viewModel.editMealDesc(item.getId(), item.getDescription());
            }

            @Override
            public void onDeleteConfirmed(MealItem item) {
                viewModel.deleteMeal(item.getId());
            }
        });

        adapter.setOnManipulatePhotoListener(item -> {
            tempId = item.getId();
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = ImageUtil.createImageFile(requireContext());
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }

                if (photoFile != null) {
                    imageFilePath = photoFile.getAbsolutePath();
                    imageUri = FileProvider.getUriForFile(requireContext(),
                            requireActivity().getPackageName(),
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });


        if (viewModel.getMode() == CalendarUtil.MODE_PUBLIC) {
            binding.frgDayinfoImgEdit.setVisibility(View.INVISIBLE);
            adapter.setEditable(false);
        }

        binding.frgDayinfoRcvMeals.setAdapter(adapter);
        binding.frgDayinfoRcvMeals.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.frgDayinfoImgEdit.setOnClickListener(v -> {
            isWeightEditMode = !isWeightEditMode;

            int resource = isWeightEditMode ? R.drawable.ic_confirm : R.drawable.ic_edit;
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), resource, null);
            ((ImageView) v).setImageDrawable(drawable);

            binding.frgDayinfoTvWeight.setVisibility(isWeightEditMode ? View.INVISIBLE : View.VISIBLE);
            binding.frgDayinfoEdWeight.setVisibility(isWeightEditMode ? View.VISIBLE : View.INVISIBLE);

            if (isWeightEditMode) {
                binding.frgDayinfoEdWeight.post(() -> {
                    binding.frgDayinfoEdWeight.requestFocus();
                    binding.frgDayinfoEdWeight.setSelection(binding.frgDayinfoEdWeight.getText().length());
                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(binding.frgDayinfoEdWeight, 0);
                });
            } else {
                Editable text = binding.frgDayinfoEdWeight.getText();
                binding.frgDayinfoTvWeight.setText(text);

                viewModel.editWeight(Double.parseDouble(text.toString()));
            }
        });

        binding.frgDayinfoImgComment.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CommentActivity.class);
            intent.putExtra("date", viewModel.getDate());
            long memberId = viewModel.getMode() == CalendarUtil.MODE_PUBLIC ? viewModel.getMemberId()
                    : Long.parseLong(SharedDataUtil.getData(SharedDataUtil.AUTH_MEMBER_ID, false));

            intent.putExtra("memberId", memberId);

            requireActivity().startActivity(intent);
        });

        binding.frgDayinfoImgLike.setOnClickListener(v -> {
            viewModel.toggleLike();
        });

    }

    private void initObserver() {
        viewModel.walkCount.observe(getViewLifecycleOwner(), walkCount ->
                binding.frgDayinfoTvWalkcount.setText(String.valueOf(walkCount)));

        viewModel.bodyWeight.observe(getViewLifecycleOwner(), bodyWeight -> {
            binding.frgDayinfoTvWeight.setText(String.valueOf(bodyWeight));
            binding.frgDayinfoEdWeight.setText(String.valueOf(bodyWeight));
        });

        viewModel.commentCount.observe(requireActivity(), commentCount ->
                binding.frgDayinfoTvComment.setText(String.valueOf(commentCount)));

        viewModel.likeCount.observe(requireActivity(), likeCount ->
                binding.frgDayinfoTvLike.setText(String.valueOf(likeCount)));

        viewModel.mealList.observe(getViewLifecycleOwner(), mealItems -> {
            adapter.setList(mealItems);
            adapter.notifyDataSetChanged();
        });

        viewModel.toastEvent.observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(requireActivity(), msg.getValueIfNotHandledOrNull(), Toast.LENGTH_SHORT).show());

        viewModel.isLiked.observe(requireActivity(), isLiked -> {
            int resource;
            if (isLiked)
                resource = R.drawable.ic_like_filled;
            else
                resource = R.drawable.ic_like_empty;

            binding.frgDayinfoImgLike.setImageDrawable(ResourcesCompat.getDrawable(getResources(), resource, null));
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            viewModel.addPhoto(tempId, ImageUtil.convertFileToMultipart(imageFilePath));
        }
    }
}