package com.kdpark.sickdan.view.control.meallist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.model.dto.MealCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class MealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements MealItemMoveCallback.ItemTouchHelperAdapter {

    private Context context;
    private List<MealItem> list;
    private Map<MealCategory, Integer> categoryCountMap;

    private onDragStartListener onDragStartListener;

    //TODO: 리스너들 추가하기
    public interface onDragStartListener {
        void onDragStart(MealItemViewHolder viewHolder);
    }

    public MealAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public MealAdapter(Context context, List<MealItem> list) {
        super();
        this.context = context;
        this.list = getListWithSection(list);
    }

    public void setList(List<MealItem> list) {
        this.list = getListWithSection(list);
    }

    public void setOnDragStartListener(MealAdapter.onDragStartListener onDragStartListener) {
        this.onDragStartListener = onDragStartListener;
    }

    private List<MealItem> getListWithSection(List<MealItem> list) {
        categoryCountMap = new HashMap<>();

        for (int i = 0; i < list.size(); i++) {
            MealCategory category = list.get(i).getCategory();
            if (!categoryCountMap.containsKey(category))
                categoryCountMap.put(category, 0);

            categoryCountMap.put(category, categoryCountMap.get(category) + 1);
        }

        list.add(new MealItem("", MealCategory.BREAKFAST, MealCellType.SECTION));
        list.add(new MealItem("", MealCategory.LUNCH, MealCellType.SECTION));
        list.add(new MealItem("", MealCategory.DINNER, MealCellType.SECTION));
        list.add(new MealItem("", MealCategory.SNACK, MealCellType.SECTION));

        Collections.sort(list, (m1, m2) -> m1.getCategory().compareTo(m2.getCategory()) != 0
                   ? m1.getCategory().compareTo(m2.getCategory())
                   : m1.getType().compareTo(m2.getType())
        );

        return list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;

        if (viewType == MealCellType.SECTION.getInt()) {
            view = LayoutInflater.from(context).inflate(R.layout.meal_list_section, parent, false);
            holder = new MealSectionViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.meal_list_item, parent, false);
            holder = new MealItemViewHolder(view);
        }

        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType().getInt();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == MealCellType.SECTION.getInt()) {
            MealSectionViewHolder viewHolder = (MealSectionViewHolder) holder;
            viewHolder.title.setText(list.get(position).getCategory().toString());
        } else {
            MealItemViewHolder viewHolder = (MealItemViewHolder) holder;
            viewHolder.description.setText(list.get(position).getDescription());
            viewHolder.handler.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    onDragStartListener.onDragStart(viewHolder);
                return false;
            });
        }

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onItemMove(int fromPos, int targetPos) {
        if (targetPos == 0) return;

        MealItem fromItem = list.remove(fromPos);
        fromItem.setCategory(list.get(targetPos - 1).getCategory());
        list.add(targetPos, fromItem);
        Log.d("PKD", fromItem.getCategory().toString());
        notifyItemMoved(fromPos, targetPos);
    }

    @Override
    public void onItemDismiss(int pos) {

    }

    public class MealItemViewHolder extends RecyclerView.ViewHolder {
        TextView description;
        ImageView handler;

        public MealItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.description = itemView.findViewById(R.id.lay_mealitem_tv_description);
            this.handler = itemView.findViewById(R.id.lay_mealitem_img_handler);
        }
    }

    public class MealSectionViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public MealSectionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.lay_mealsection_tv_title);
        }
    }
}