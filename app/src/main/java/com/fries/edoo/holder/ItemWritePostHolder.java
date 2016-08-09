package com.fries.edoo.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fries.edoo.MainActivity;
import com.fries.edoo.R;
import com.fries.edoo.app.AppManager;

/**
 * Created by Tdh4vn on 11/21/2015.
 */
public class ItemWritePostHolder extends AbstractHolder {
    private ImageView imgAvatar;
    private TextView txtView;

    private String idLop;
    private String keyLopType;

    public ItemWritePostHolder(View itemView) {
        super(itemView);
        imgAvatar = (ImageView) itemView.findViewById(R.id.avatar);
        txtView = (TextView) itemView.findViewById(R.id.textView2);
        txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = ((MainActivity) AppManager.getInstance().getMainContext());
                mainActivity.startPostWriterActivity(idLop,keyLopType);
            }
        });
    }

    public ItemWritePostHolder(View itemView, String idLop, String keyLopType){
        this(itemView);
        this.idLop = idLop;
        this.keyLopType = keyLopType;
    }

    @Override
    public int getViewHolderType() {
        int viewHolderType = 0;
        return viewHolderType;
    }

    public ImageView getImgAvatar() {
        return imgAvatar;
    }

    public void setImgAvatar(ImageView imgAvatar) {
        this.imgAvatar = imgAvatar;
    }

    public TextView getTxtEdit() {
        return txtView;
    }

    public void setTxtEdit(TextView txtView) {
        this.txtView = txtView;
    }
}
