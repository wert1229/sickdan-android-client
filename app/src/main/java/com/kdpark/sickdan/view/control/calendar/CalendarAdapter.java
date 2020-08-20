package com.kdpark.sickdan.view.control.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdpark.sickdan.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lombok.Getter;

@Getter
public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private RecyclerView parent;
    private List<CalendarCell> list;
    private int calendarLineCount;
    private onCalendarCellClickListener listener;

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

            if (list.get(position).getBodyWeight() > 0.0)
                viewHolder.weight.setText(String.valueOf(list.get(position).getBodyWeight()));

            if (list.get(position).getWalkCount() > 0)
                viewHolder.walkCount.setText(String.valueOf(list.get(position).getWalkCount()));

            viewHolder.itemView.setOnClickListener(v -> {
                listener.onClick(list.get(position));
            });
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

    public class DayViewHolder extends RecyclerView.ViewHolder {

        TextView day;
        TextView weight;
        TextView walkCount;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            this.day = itemView.findViewById(R.id.lay_daycell_tv_day);
            this.weight = itemView.findViewById(R.id.lay_daycell_tv_weight);
            this.walkCount = itemView.findViewById(R.id.lay_daycell_tv_walkcount);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        TextView day;

        public EmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.day = itemView.findViewById(R.id.lay_daycell_tv_day);
        }
    }

    public interface onCalendarCellClickListener {
        void onClick(CalendarCell cell);
    }
}
