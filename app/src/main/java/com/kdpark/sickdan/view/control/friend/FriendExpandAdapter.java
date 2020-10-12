package com.kdpark.sickdan.view.control.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.model.dto.enums.RelationshipStatus;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class FriendExpandAdapter extends ExpandableRecyclerViewAdapter<FriendExpandAdapter.SectionViewHolder, FriendExpandAdapter.ItemViewHolder> {

    private Context context;

    private OnAcceptClick onAcceptClick;
    private OnCalendarClick onCalendarClick;

    public void setOnAcceptClick(OnAcceptClick onAcceptClick) {
        this.onAcceptClick = onAcceptClick;
    }

    public void setOnCalendarClick(OnCalendarClick onCalendarClick) {
        this.onCalendarClick = onCalendarClick;
    }

    public interface OnCalendarClick {
        void onClick(FriendItem item);
    }

    public interface OnAcceptClick {
        void onClick(FriendItem item);
    }

    public FriendExpandAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.context = context;
    }

    @Override
    public SectionViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_list_section, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public ItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(SectionViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.name.setText(group.getTitle());
    }

    @Override
    public void onBindChildViewHolder(ItemViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final FriendItem item = ((FriendSection) group).getItems().get(childIndex);

        RelationshipStatus status = item.getStatus();

        switch (status) {
            case REQUESTED:
                holder.calendar.setVisibility(View.INVISIBLE);
                holder.accept.setVisibility(View.VISIBLE);
                break;
            case FRIEND:
                holder.calendar.setVisibility(View.VISIBLE);
                holder.accept.setVisibility(View.INVISIBLE);
                break;
            default:
                holder.calendar.setVisibility(View.INVISIBLE);
                holder.accept.setVisibility(View.INVISIBLE);
        }

        holder.name.setText(String.format("%s (%s)", item.getDisplayName(), item.getEmail()));
        holder.calendar.setOnClickListener(v -> onCalendarClick.onClick(item));
        holder.accept.setOnClickListener(v -> onAcceptClick.onClick(item));
    }

    public static class SectionViewHolder extends GroupViewHolder {

        TextView name;
        ImageView arrow;

        public SectionViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.lay_friendsection_tv_name);
            arrow = itemView.findViewById(R.id.lay_friendsection_btn_expand);
        }

        @Override
        public void expand() {
            RotateAnimation rotate =
                    new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }

        @Override
        public void collapse() {
            RotateAnimation rotate =
                    new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setFillAfter(true);
            arrow.setAnimation(rotate);
        }
    }

    public static class ItemViewHolder extends ChildViewHolder {

        TextView name;
        ImageView accept;
        ImageView calendar;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.lay_frienditem_tv_name);
            accept = itemView.findViewById(R.id.lay_frienditem_btn_accept);
            calendar = itemView.findViewById(R.id.lay_frienditem_btn_calendar);
        }
    }
}
