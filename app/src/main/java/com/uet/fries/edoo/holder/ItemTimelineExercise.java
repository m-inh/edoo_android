package com.uet.fries.edoo.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uet.fries.edoo.R;

/**
 * Created by tmq on 28/09/2016.
 */

public class ItemTimelineExercise extends AbstractHolder {
    private Context mContext;
    private TextView tvTitle, tvSummary,
            tvRemainingTime, tvCreateTime;
    private ImageView ivSeen;

    public ItemTimelineExercise(View itemView) {
        super(itemView);
        mContext = itemView.getContext();

        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvSummary = (TextView) itemView.findViewById(R.id.tv_summary);
        tvRemainingTime = (TextView) itemView.findViewById(R.id.tv_remaining_time);
        tvCreateTime = (TextView) itemView.findViewById(R.id.tv_create_time);
        ivSeen = (ImageView) itemView.findViewById(R.id.iv_marker_seen);
    }

    @Override
    public int getViewHolderType() {
        return 2;
    }

    /**
     * When time has passed, TextView remaining_time is GONE
     */
    public void timeHasPassed() {
        tvRemainingTime.setVisibility(View.GONE);
    }

    public void setRemainingTime(String time) {
        tvRemainingTime.setText(mContext.getString(R.string.txt_remaining_time) + time);
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
}
