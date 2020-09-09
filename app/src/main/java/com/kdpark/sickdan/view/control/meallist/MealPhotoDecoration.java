package com.kdpark.sickdan.view.control.meallist;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MealPhotoDecoration extends RecyclerView.ItemDecoration {
    public static final int ITEM_GAP = 15;
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.right += ITEM_GAP;
    }
}
