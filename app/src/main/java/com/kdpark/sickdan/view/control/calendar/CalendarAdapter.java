package com.kdpark.sickdan.view.control.calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.kdpark.sickdan.R;
import com.kdpark.sickdan.util.CalendarUtil;
import com.kdpark.sickdan.util.SharedDataUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import lombok.Getter;

@Getter
public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private RecyclerView parent;
    private List<CalendarCell> list;
    private int calendarLineCount;
    private onCalendarCellClickListener listener;

    public interface onCalendarCellClickListener {
        void onClick(DayViewHolder viewHolder, CalendarCell cell);
    }

    public CalendarAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public CalendarAdapter(Context context, List<CalendarCell> list) {
        super();
        this.context = context;
        this.list = list;
        this.calendarLineCount = getCalendarLineCount();
    }

    public void setOnCellClickListener(onCalendarCellClickListener listener) {
        this.listener = listener;
    }

    public void setList(List<CalendarCell> list) {
        this.list = list;
        this.calendarLineCount = getCalendarLineCount();
        notifyDataSetChanged();
    }

    private int getCalendarLineCount() {
        if (list.isEmpty()) return 0;

        String date = list.get(list.size() / 2).getDate();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        calendar.set(Calendar.DATE, 1);

        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);

        return (calendar.getActualMaximum(Calendar.DATE) + firstDayOfMonth - 1 + 6) / 7;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parent = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;

        if (viewType == CalendarCellType.EMPTY.getInt()) {
            view = LayoutInflater.from(context).inflate(R.layout.calendar_empty, parent, false);
            holder = new EmptyViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.calendar_day, parent, false);
            holder = new DayViewHolder(view);
        }

        view.getLayoutParams().height = parent.getMeasuredHeight() / calendarLineCount;

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        holder.itemView.getLayoutParams().height = parent.getMeasuredHeight() / calendarLineCount;

        String date = list.get(position).getDate();
        String displayDate = date.charAt(6) == '0' ? date.substring(7) : date.substring(6);

        int viewType = getItemViewType(position);

        if (viewType == CalendarCellType.EMPTY.getInt()) {
            EmptyViewHolder viewHolder = (EmptyViewHolder) holder;
            viewHolder.day.setText(displayDate);
        } else {
            DayViewHolder viewHolder = (DayViewHolder) holder;
            viewHolder.day.setText(displayDate);

            String today = CalendarUtil.calendarToString(Calendar.getInstance(), "yyyyMMdd");

            if (list.get(position).getBodyWeight() <= 0.0)
                viewHolder.weightLayout.setVisibility(View.GONE);
            else
                viewHolder.weight.setText(String.valueOf(list.get(position).getBodyWeight()));

            if (list.get(position).getWalkCount() <= 0)
                viewHolder.walkCountLayout.setVisibility(View.GONE);
            else
                viewHolder.walkCount.setText(String.valueOf(list.get(position).getWalkCount()));

            if (date.equals(today)) {
                Drawable todayBg = ResourcesCompat.getDrawable(context.getResources(), R.drawable.bg_calendar_today, null);
                viewHolder.day.setBackground(todayBg);
                viewHolder.day.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.defaultWhite, null));

                String todayWalkCount = SharedDataUtil.getData(SharedDataUtil.TODAY_COUNT, false);
                if (todayWalkCount.isEmpty() || "0".equals(todayWalkCount))
                    viewHolder.walkCountLayout.setVisibility(View.GONE);
                else {
                    viewHolder.walkCountLayout.setVisibility(View.VISIBLE);
                    viewHolder.walkCount.setText(todayWalkCount);
                }
            }

            viewHolder.itemView.setOnClickListener(v -> listener.onClick(viewHolder, list.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType().getInt();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {

        TextView day;
        TextView weight;
        TextView walkCount;
        LinearLayout weightLayout;
        LinearLayout walkCountLayout;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            this.day = itemView.findViewById(R.id.lay_daycell_tv_day);
            this.weight = itemView.findViewById(R.id.lay_daycell_tv_weight);
            this.walkCount = itemView.findViewById(R.id.lay_daycell_tv_walkcount);
            this.weightLayout = itemView.findViewById(R.id.lay_daycell_ll_weight);
            this.walkCountLayout = itemView.findViewById(R.id.lay_daycell_ll_walkcount);
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {

        TextView day;

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.day = itemView.findViewById(R.id.lay_daycell_tv_day);
        }
    }
}
