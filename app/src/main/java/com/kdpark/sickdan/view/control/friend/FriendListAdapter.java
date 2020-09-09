package com.kdpark.sickdan.view.control.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.model.dto.MemberRelationshipDto;
import com.kdpark.sickdan.view.control.meallist.MealAdapter;
import com.kdpark.sickdan.view.control.meallist.MealCellType;

import java.util.ArrayList;
import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MemberRelationshipDto> list;
    private onCellClickListener listener;

    public interface onCellClickListener {
        void onCalendarClick(MemberRelationshipDto relationship);
    }

    public FriendListAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public FriendListAdapter(Context context, List<MemberRelationshipDto> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(List<MemberRelationshipDto> list) {
        this.list = list;
    }

    public void setOnCellClickListener(onCellClickListener listener) {
        this.listener = listener;
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

        viewHolder.name.setText(String.format("%s(%s)", list.get(pos).getDisplayName(), list.get(pos).getStatus()));
        viewHolder.calendar.setOnClickListener(v -> listener.onCalendarClick(list.get(pos)));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageButton calendar;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.lay_frienditem_tv_name);
            this.calendar = itemView.findViewById(R.id.lay_frienditem_btn_calendar);
        }
    }
}
