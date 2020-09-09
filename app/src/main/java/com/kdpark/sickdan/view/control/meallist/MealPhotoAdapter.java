package com.kdpark.sickdan.view.control.meallist;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kdpark.sickdan.R;
import com.kdpark.sickdan.model.dto.MealPhotoDto;

import java.util.List;

import lombok.Getter;

@Getter
public class MealPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<MealPhotoDto> list;
    private Handler handler;

    public MealPhotoAdapter(Context context, List<MealPhotoDto> list) {
        this.context = context;
        this.list = list;
        this.handler = new Handler();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_photo_item, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        PhotoViewHolder viewHolder = (PhotoViewHolder) holder;
        Glide.with(context)
                .load(list.get(pos).getFileUrl())
                .apply(new RequestOptions().override(300, 300))
                .into(viewHolder.photo);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.photo = itemView.findViewById(R.id.lay_mealphoto_img_photo);
        }
    }
}
