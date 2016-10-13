package com.uet.fries.edoo.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Tdh4vn on 11/21/2015.
 */
public abstract class AbstractHolder extends RecyclerView.ViewHolder {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_COMMENT = 1;
    public static final int TYPE_INFO = 2;


    private int viewHolderType;

    public AbstractHolder(View itemView) {
        super(itemView);
    }

    abstract public int getViewHolderType();
}
