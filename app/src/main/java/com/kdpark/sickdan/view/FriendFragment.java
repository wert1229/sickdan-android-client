package com.kdpark.sickdan.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kdpark.sickdan.databinding.FragmentFriendBinding;
import com.kdpark.sickdan.model.dto.FriendSearchDto;
import com.kdpark.sickdan.model.dto.MemberRelationshipDto;
import com.kdpark.sickdan.view.control.friend.FriendListAdapter;
import com.kdpark.sickdan.viewmodel.FriendMainViewModel;
import com.kdpark.sickdan.viewmodel.FriendViewModel;

import java.util.List;

public class FriendFragment extends Fragment {

    private FragmentFriendBinding binding;
    private FriendViewModel viewModel;
    private FriendListAdapter adapter;

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
    }

    private void initData() {}

    private void initView() {
        adapter = new FriendListAdapter(requireContext());
        adapter.setOnCellClickListener(relationship -> {
            Intent intent = new Intent(requireContext(), FriendMainActivity.class);
            intent.putExtra("memberId", relationship.getId());
            intent.putExtra("displayName", relationship.getDisplayName());
            requireActivity().startActivity(intent);
        });

        binding.frgFriendRcvFriends.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.frgFriendRcvFriends.setAdapter(adapter);

        binding.frgFriendEdEmail.setOnFocusChangeListener((v, hasFocus) -> {
            String content = ((EditText) v).getText().toString();

            if (hasFocus) return;
            if (content.isEmpty()) return;

            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(v.getWindowToken(), 0);

            viewModel.searchByEmail(content);
        });

        binding.frgFriendBtnRequest.setOnClickListener(v -> viewModel.sendFriendRequest());
    }

    private void initObserver() {
        viewModel.getFriendList().observe(getViewLifecycleOwner(), friendList -> {
            adapter.setList(friendList);
            adapter.notifyDataSetChanged();
        });

        viewModel.getSearchResult().observe(getViewLifecycleOwner(), searchResult ->
                binding.frgFriendTvName.setText(searchResult.getDisplayName()));

        viewModel.getFriendAddComplete().observe(getViewLifecycleOwner(), e -> {
            if (e.getValueIfNotHandledOrNull() != null) viewModel.requestFriendList();
        });

        viewModel.requestFriendList();
    }
}