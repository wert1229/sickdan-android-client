package com.kdpark.sickdan.view.control.meallist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    private OnManipulateMealListener onManipulateMealListener;
    private OnManipulatePhotoListener onManipulatePhotoListener;

    public interface OnManipulateMealListener {
        void onAddConfirmed(MealItem item);
        void onEditConfirmed(MealItem item);
        void onDeleteConfirmed(MealItem item);
    }

    public interface OnManipulatePhotoListener {
        void onCameraClick(MealItem item);
    }

    public MealAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public MealAdapter(Context context, List<MealItem> list) {
        super();
        this.context = context;
        categoryCountMap = new HashMap<>();
        this.list = getListWithSection(list);
    }

    public void setList(List<MealItem> list) {
        this.list = getListWithSection(list);
    }

    public void setOnManipulateMealListener(OnManipulateMealListener onManipulateMealListener) {
        this.onManipulateMealListener = onManipulateMealListener;
    }

    public void setOnManipulatePhotoListener(OnManipulatePhotoListener onManipulatePhotoListener) {
        this.onManipulatePhotoListener = onManipulatePhotoListener;
    }

    private List<MealItem> getListWithSection(List<MealItem> list) {
        for (MealCategory category : MealCategory.values()) {
            if (category == MealCategory.NONE) continue;
            list.add(new MealItem(0L, "", category, MealCellType.SECTION, null));
            categoryCountMap.put(category, 0);
        }

        for (int i = 0; i < list.size(); i++) {
            MealCategory category = list.get(i).getCategory();
            if (list.get(i).getType() == MealCellType.SECTION) continue;
            categoryCountMap.computeIfPresent(category, (key, value) -> value + 1);
        }

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
        } else if (viewType == MealCellType.EDIT.getInt() || viewType == MealCellType.NEW.getInt()) {
            view = LayoutInflater.from(context).inflate(R.layout.meal_list_edit, parent, false);
            holder = new MealEditViewHolder(view);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        int viewType = getItemViewType(pos);

        if (viewType == MealCellType.SECTION.getInt()) {
            MealSectionViewHolder viewHolder = (MealSectionViewHolder) holder;
            viewHolder.title.setText(list.get(pos).getCategory().getStr());
            viewHolder.add.setOnClickListener(v -> {
                removeNotConfirmedItem();
                
                MealItem item = list.get(pos);
                MealItem newItem = MealItem.builder()
                        .category(item.getCategory())
                        .description("")
                        .type(MealCellType.NEW)
                        .photos(new ArrayList<>())
                        .build();
                
                int index = pos + categoryCountMap.get(item.getCategory()) + 1;
                list.add(index, newItem);
                notifyDataSetChanged();
            });
        } else if (viewType == MealCellType.EDIT.getInt()) {
            MealEditViewHolder viewHolder = (MealEditViewHolder) holder;

            viewHolder.description.setText(list.get(pos).getDescription());

            viewHolder.description.post(() -> {
                viewHolder.description.requestFocus();
                viewHolder.description.setSelection(viewHolder.description.getText().length());
                InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(viewHolder.description,0);
            });

            viewHolder.confirm.setOnClickListener(v -> {
                String after =  viewHolder.description.getText().toString();
                if (!after.isEmpty()) list.get(pos).setDescription(after);
                
                list.get(pos).setType(MealCellType.ITEM);

                onManipulateMealListener.onEditConfirmed(list.get(pos));
                notifyDataSetChanged();
            });

            viewHolder.delete.setOnClickListener(v -> createConfirmDialog(pos));

        } else if (viewType == MealCellType.NEW.getInt()) {
            MealEditViewHolder viewHolder = (MealEditViewHolder) holder;

            viewHolder.description.setText(list.get(pos).getDescription());

            viewHolder.confirm.setOnClickListener(v -> {
                String after =  viewHolder.description.getText().toString();

                if (after.isEmpty()) {
                    removeNotConfirmedItem();
                    return;
                }

                list.get(pos).setDescription(after);
                list.get(pos).setType(MealCellType.ITEM);

                categoryCountMap.computeIfPresent(list.get(pos).getCategory(), (key, value) -> value + 1);
                onManipulateMealListener.onAddConfirmed(list.get(pos));
                notifyDataSetChanged();
            });

            viewHolder.delete.setOnClickListener(v -> removeNotConfirmedItem());
        } else {
            MealItemViewHolder viewHolder = (MealItemViewHolder) holder;
            viewHolder.description.setText(list.get(pos).getDescription());

            viewHolder.itemView.setOnLongClickListener(v -> {
                removeNotConfirmedItem();
                list.get(pos).setType(MealCellType.EDIT);
                notifyItemChanged(pos);
                return false;
            });

            viewHolder.camera.setOnClickListener(v -> onManipulatePhotoListener.onCameraClick(list.get(pos)));

            MealPhotoAdapter photoAdapter = new MealPhotoAdapter(context, list.get(pos).getPhotos());

            viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context
                    , LinearLayoutManager.HORIZONTAL
                    ,false));
            viewHolder.recyclerView.setAdapter(photoAdapter);
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
        categoryCountMap.computeIfPresent(fromItem.getCategory(), (key, value) -> value - 1);

        fromItem.setCategory(list.get(targetPos - 1).getCategory());
        categoryCountMap.computeIfPresent(fromItem.getCategory(), (key, value) -> value + 1);

        list.add(targetPos, fromItem);
        notifyItemMoved(fromPos, targetPos);
    }

    @Override
    public void onItemDismiss(int pos) {

    }

    public MealItem removeNotConfirmedItem() {
        MealItem currentEditItem = null;

        for (MealItem mealItem : list) {
            if (mealItem.getType() == MealCellType.NEW) {
                currentEditItem = mealItem;
            } else if (mealItem.getType() == MealCellType.EDIT) {
                mealItem.setType(MealCellType.ITEM);
            }
        }

        if (currentEditItem != null) list.remove(currentEditItem);

        notifyDataSetChanged();
        return currentEditItem;
    }

    private void createConfirmDialog(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.meal_delete_message)
                .setPositiveButton(R.string.meal_delete_fire, (dialog, id) ->
                        onManipulateMealListener.onDeleteConfirmed(list.get(pos)))

                .setNegativeButton(R.string.meal_delete_cancel, (dialog, id) -> {
                    // User cancelled the dialog
                });

        builder.create().show();
    }


    public static class MealItemViewHolder extends RecyclerView.ViewHolder {
        TextView description;
        Button camera;
        RecyclerView recyclerView;

        public MealItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.description = itemView.findViewById(R.id.lay_mealitem_tv_description);
            this.camera = itemView.findViewById(R.id.lay_mealitem_ib_camera);
            this.recyclerView = itemView.findViewById(R.id.lay_mealitem_rcv_photo);
        }
    }

    public static class MealEditViewHolder extends RecyclerView.ViewHolder {
        EditText description;
        Button confirm;
        Button delete;

        public MealEditViewHolder(@NonNull View itemView) {
            super(itemView);
            this.description = itemView.findViewById(R.id.lay_mealitem_ed_description);
            this.confirm = itemView.findViewById(R.id.lay_mealitem_ib_confirm);
            this.delete = itemView.findViewById(R.id.lay_mealitem_ib_delete);
        }
    }

    public static class MealSectionViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        Button add;

        public MealSectionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.lay_mealsection_tv_title);
            this.add = itemView.findViewById(R.id.lay_mealsection_ib_add);
        }
    }
}