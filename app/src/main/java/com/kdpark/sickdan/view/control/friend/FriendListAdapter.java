package com.kdpark.sickdan.view.control.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.model.dto.MemberDto;
import com.kdpark.sickdan.model.dto.enums.RelationshipStatus;

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MemberDto.MemberRelationship> list;
    private OnCalendarClick onCalendarClick;
    private OnAcceptClick onAcceptClick;

    public interface OnCalendarClick {
        void onClick(MemberDto.MemberRelationship relationship);
    }

    public interface OnAcceptClick {
        void onClick(MemberDto.MemberRelationship relationship);
    }

    public FriendListAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public FriendListAdapter(Context context, List<MemberDto.MemberRelationship> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<MemberDto.MemberRelationship> list) {
        this.list = list;
    }

    public void setOnAcceptClick(OnAcceptClick onAcceptClick) {
        this.onAcceptClick = onAcceptClick;
    }

    public void setOnCalendarClick(OnCalendarClick onCalendarClick) {
        this.onCalendarClick = onCalendarClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_list_item, parent, false);
        RecyclerView.ViewHolder holder = new FriendViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        FriendViewHolder viewHolder = (FriendViewHolder) holder;

        RelationshipStatus status = list.get(pos).getStatus();

        switch (status) {
            case REQUESTED:
                viewHolder.calendar.setVisibility(View.INVISIBLE);
                viewHolder.accept.setVisibility(View.VISIBLE);
                break;
            case FRIEND:
                viewHolder.calendar.setVisibility(View.VISIBLE);
                viewHolder.accept.setVisibility(View.INVISIBLE);
                break;
            default:
                viewHolder.calendar.setVisibility(View.INVISIBLE);
                viewHolder.accept.setVisibility(View.INVISIBLE);
        }

        viewHolder.name.setText(String.format("%s(%s)", list.get(pos).getDisplayName(), status.getDesc()));
        viewHolder.calendar.setOnClickListener(v -> onCalendarClick.onClick(list.get(pos)));
        viewHolder.accept.setOnClickListener(v -> onAcceptClick.onClick(list.get(pos)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView calendar;
        ImageView accept;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.lay_frienditem_tv_name);
            this.calendar = itemView.findViewById(R.id.lay_frienditem_btn_calendar);
            this.accept = itemView.findViewById(R.id.lay_frienditem_btn_accept);
        }
    }
}
