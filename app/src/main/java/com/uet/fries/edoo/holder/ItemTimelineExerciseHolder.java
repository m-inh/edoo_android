package com.uet.fries.edoo.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uet.fries.edoo.R;
import com.uet.fries.edoo.activities.TimelineActivity;
import com.uet.fries.edoo.adapter.TimeLineAdapter;
import com.uet.fries.edoo.models.ItemTimeLineExercise;

/**
 * Created by tmq on 28/09/2016.
 */

public class ItemTimelineExerciseHolder extends AbstractHolder {
    private Context mContext;
    private TextView tvTitle, tvSummary,
            tvRemainingTime, tvCreateTime;
    private ImageView ivSeen;

    private ItemTimeLineExercise itemTimeLineExercise;

    public ItemTimelineExerciseHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();

        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvSummary = (TextView) itemView.findViewById(R.id.tv_summary);
        tvRemainingTime = (TextView) itemView.findViewById(R.id.tv_remaining_time);
        tvCreateTime = (TextView) itemView.findViewById(R.id.tv_create_time);
        ivSeen = (ImageView) itemView.findViewById(R.id.iv_marker_seen);

        createListener(itemView);
    }

    private void createListener(View itemView) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemTimeLineExercise.setIsSeen(true);
                TimelineActivity timelineActivity = (TimelineActivity) mContext;
                timelineActivity.startPostDetailActivity(itemTimeLineExercise);
            }
        });
    }

    public void setItemTimeLineExercise(ItemTimeLineExercise itemTimeLineExercise) {
        this.itemTimeLineExercise = itemTimeLineExercise;

        setDataFromItemTimeline();
    }

    private void setDataFromItemTimeline(){
        setRemainingTime(itemTimeLineExercise.getRemainingTime());
        setTitle(itemTimeLineExercise.getTitle());
        setSummary(itemTimeLineExercise.getSummary());
        setSeen(itemTimeLineExercise.isSeen());
        setCreateTime(itemTimeLineExercise.getCreateAt());
    }

    @Override
    public int getViewHolderType() {
        return AbstractHolder.TYPE_EXERCISE;
    }

    /**
     * When time has passed, TextView remaining_time is GONE
     */
    public void timeHasPassed() {
        tvRemainingTime.setVisibility(View.GONE);
    }

    public void setRemainingTime(String time) {
//        tvRemainingTime.setText(mContext.getString(R.string.txt_remaining_time) + time);
        tvRemainingTime.setText(time);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setSummary(String summary) {
        tvSummary.setText(summary);
    }

    public void setSeen(boolean isSeen) {
        if (isSeen) ivSeen.setVisibility(View.INVISIBLE);
        else ivSeen.setVisibility(View.VISIBLE);
    }

    public void setCreateTime(String time){
        tvCreateTime.setText(time);
    }
}
