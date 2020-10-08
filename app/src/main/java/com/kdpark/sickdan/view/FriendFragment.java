package com.kdpark.sickdan.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.DialogFriendAddBinding;
import com.kdpark.sickdan.databinding.FragmentFriendBinding;
import com.kdpark.sickdan.model.dto.RelationshipStatus;
import com.kdpark.sickdan.view.control.friend.FriendExpandAdapter;
import com.kdpark.sickdan.view.control.friend.FriendItem;
import com.kdpark.sickdan.view.control.friend.FriendListAdapter;
import com.kdpark.sickdan.viewmodel.FriendViewModel;
import com.kdpark.sickdan.viewmodel.common.Event;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

public class FriendFragment extends Fragment {

    private FragmentFriendBinding binding;
    private FriendViewModel viewModel;

    public FriendFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(FriendViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initData();
        initView();
        initObserver();

        viewModel.getMyCode();
        viewModel.requestFriendList();
    }

    private void initData() {}

    private void initView() {
        binding.frgFriendRcvFriends.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.frgFriendRcvFriends.setAdapter(createAdapter(new ArrayList<>()));

        binding.frgFriendImgAdd.setOnClickListener(v -> {
            showFriendAddDialog();
        });

        binding.frgFriendImgClipboard.setOnClickListener(v -> {
            String code = binding.frgFriendTvMycode.getText().toString();
            if (code.length() == 0) return;
            ClipboardManager clipboardManager = (ClipboardManager)requireActivity().getSystemService(CLIPBOARD_SERVICE);

            ClipData clipData = ClipData.newPlainText("code", code);
            clipboardManager.setPrimaryClip(clipData);

            Toast.makeText(requireActivity(), "클립보드에 복사되었습니다", Toast.LENGTH_SHORT).show();
        });
    }

    private void initObserver() {
        viewModel.friendList.observe(getViewLifecycleOwner(), friendList -> {
            FriendExpandAdapter adapter = createAdapter(friendList);
            binding.frgFriendRcvFriends.setAdapter(adapter);
            adapter.toggleGroup(0);
        });

        viewModel.myCode.observe(requireActivity(), s -> binding.frgFriendTvMycode.setText(s));

        viewModel.friendAcceptComplete.observe(requireActivity(), booleanEvent -> viewModel.requestFriendList());

        viewModel.showToast.observe(requireActivity(), msg ->
                Toast.makeText(requireContext(), msg.getValueIfNotHandledOrNull(), Toast.LENGTH_SHORT).show());
    }

    private FriendExpandAdapter createAdapter(List<? extends ExpandableGroup<FriendItem>> groups) {
        FriendExpandAdapter adapter = new FriendExpandAdapter(requireContext(), groups);
        adapter.setOnCalendarClick(relationship -> {
            Intent intent = new Intent(requireContext(), FriendMainActivity.class);
            intent.putExtra("memberId", relationship.getId());
            intent.putExtra("displayName", relationship.getDisplayName());
            requireActivity().startActivity(intent);
        });

        adapter.setOnAcceptClick(relationship -> {
            viewModel.acceptFriendRequest(relationship.getId());
        });

        return adapter;
    }

    private void showFriendAddDialog() {
        FriendAddDialog dialog = new FriendAddDialog(requireActivity());

        DialogFriendAddBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(dialog.getContext()),
                R.layout.dialog_friend_add, null, false);
        dialog.setContentView(dialogBinding.getRoot());

        DisplayMetrics dm = requireActivity().getApplicationContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        WindowManager.LayoutParams wm = dialog.getWindow().getAttributes();
        wm.width = (int) (width * 0.85);
        wm.height = (int) (height * 0.5);

        // setting
        dialogBinding.dlgAddImgClose.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.dlgAddImgSearch.setOnClickListener(v -> {
            String code = dialogBinding.dlgAddEdCode.getText().toString();

            if (code.length() == 0) {
                Toast.makeText(requireActivity(), "코드를 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.searchByCode(code);
        });

        viewModel.searchResult.observe(requireActivity(), friendSearchDto -> {
            String text = String.format("%s (%s)", friendSearchDto.getDisplayName(), friendSearchDto.getEmail());
            dialogBinding.dlgAddTvResult.setText(text);

            RelationshipStatus status = friendSearchDto.getStatus();

            String btnCaption = "";
            boolean isAble = false;
            switch (status) {
                case FRIEND:
                    btnCaption = "이미 친구입니다";
                    break;
                case REQUESTED:
                    btnCaption = "요청 수락하기";
                    isAble = true;
                    dialogBinding.dlgAddBtnAdd.setOnClickListener(v -> viewModel.acceptFriendRequest(friendSearchDto.getId()));
                    break;
                case REQUESTING:
                    btnCaption = "이미 요청중 입니다";
                    break;
                case SELF:
                    btnCaption = "본인은 추가할 수 없습니다";
                    break;
                case NONE:
                    btnCaption = "요청하기";
                    isAble = true;
                    dialogBinding.dlgAddBtnAdd.setOnClickListener(v -> viewModel.sendFriendRequest());
                    break;
            }
            dialogBinding.dlgAddBtnAdd.setText(btnCaption);
            dialogBinding.dlgAddBtnAdd.setEnabled(isAble);
            dialogBinding.dlgAddClResult.setVisibility(View.VISIBLE);
        });

        viewModel.friendAddComplete.observe(getViewLifecycleOwner(), e -> {
            if (e.getValueIfNotHandledOrNull() != null) {
                dialog.dismiss();
                viewModel.requestFriendList();
            }
        });

        dialog.show();
    }
}