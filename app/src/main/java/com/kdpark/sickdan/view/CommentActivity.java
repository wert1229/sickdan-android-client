package com.kdpark.sickdan.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.databinding.ActivityCommentBinding;
import com.kdpark.sickdan.view.control.comment.CommentAdapter;
import com.kdpark.sickdan.viewmodel.CommentViewModel;

public class CommentActivity extends AppCompatActivity {

    private ActivityCommentBinding binding;
    private CommentViewModel viewModel;
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comment);
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(CommentViewModel.class);

        initData();
        initView();
        initObserver();

        viewModel.getComments();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();

        viewModel.setMemberId(bundle.getLong("memberId", 0));

        String date = bundle.getString("date");
        viewModel.setDate(date);
    }

    private void initView() {
        binding.actCommentImgSend.setOnClickListener(v -> {
            String description = binding.actCommentEdComment.getText().toString();
            if (!description.isEmpty())
                viewModel.writeComment(description);
        });

        binding.actCommentImgClose.setOnClickListener(v -> finish());

        adapter = new CommentAdapter(this);
        adapter.setOnReplyClick((comment, isRoot) -> {
            Long parentId = isRoot ? comment.getId() : comment.getParentCommentId();
            viewModel.setReplyInfo(parentId, comment.getDisplayName());
        });

        binding.actCommentRcvComment.setLayoutManager(new LinearLayoutManager(this));
        binding.actCommentRcvComment.setAdapter(adapter);
        binding.actCommentImgTargetClose.setOnClickListener(v -> viewModel.clearReplyInfo());
    }

    private void initObserver() {
        viewModel.comments.observe(this, comments -> adapter.setList(comments));

        viewModel.replyInfo.observe(this, replyInfo -> {
            if (replyInfo == null) {
                binding.actCommentClTarget.setVisibility(View.GONE);
            } else {
                binding.actCommentClTarget.setVisibility(View.VISIBLE);
                binding.actCommentTvTarget.setText(replyInfo.second);
            }
        });
    }
}