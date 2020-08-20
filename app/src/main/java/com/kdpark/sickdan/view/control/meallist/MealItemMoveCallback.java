package com.kdpark.sickdan.view.control.meallist;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MealItemMoveCallback extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter adapter;

    public MealItemMoveCallback(ItemTouchHelperAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int flagDrag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int flagSwipe = ItemTouchHelper.START | ItemTouchHelper.END;

        return makeMovementFlags(flagDrag, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override public boolean isLongPressDragEnabled() {
        return true; //롱터치 입력허용
    }
    @Override public boolean isItemViewSwipeEnabled() {
        return false;
    }

    public interface ItemTouchHelperAdapter {
        void onItemMove(int fromPos, int targetPos);
        void onItemDismiss(int pos);
    }
}
