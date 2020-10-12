package com.kdpark.sickdan.view.control.comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.model.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<CommentDto.Comment> list;
    private OnReplyClick onReplyClick;

    public interface OnReplyClick {
        void onClick(CommentDto.Comment commentDto, boolean isRoot);
    }

    public CommentAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public CommentAdapter(Context context, List<CommentDto.Comment> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<CommentDto.Comment> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setOnReplyClick(OnReplyClick onReplyClick) {
        this.onReplyClick = onReplyClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        ItemViewHolder viewHolder = (ItemViewHolder) holder;

        viewHolder.displayName.setText(list.get(pos).getDisplayName());
        viewHolder.description.setText(list.get(pos).getDescription());
        viewHolder.time.setText(list.get(pos).getCreatedDateTime());
        viewHolder.reply.setOnClickListener(v -> {
            boolean isRoot = list.get(pos).getParentCommentId() == null;
            onReplyClick.onClick(list.get(pos), isRoot);
        });

        if (list.get(pos).getReplies() != null) {
            CommentAdapter adapter = new CommentAdapter(context, list.get(pos).getReplies());
            adapter.setOnReplyClick(this.onReplyClick);
            viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            viewHolder.recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView displayName;
        TextView description;
        TextView time;
        Button reply;
        RecyclerView recyclerView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.displayName = itemView.findViewById(R.id.lay_comment_tv_displayname);
            this.description = itemView.findViewById(R.id.lay_comment_tv_description);
            this.time = itemView.findViewById(R.id.lay_comment_tv_time);
            this.reply = itemView.findViewById(R.id.lay_comment_btn_reply);
            this.recyclerView = itemView.findViewById(R.id.lay_comment_rcv_reply);
        }
    }
}
