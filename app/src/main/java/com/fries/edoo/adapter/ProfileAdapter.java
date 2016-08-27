package com.fries.edoo.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fries.edoo.R;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.holder.AbstractHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tmq on 27/08/2016.
 */
public class ProfileAdapter extends RecyclerView.Adapter<AbstractHolder> {
    private static final String TAG = ProfileAdapter.class.getSimpleName();
    private Context mContext;
    private HashMap<String, String> user;
    private ArrayList<ItemInfoProfile> arrInfo;

    public ProfileAdapter(Context context){
        mContext = context;

        user = new SQLiteHandler(mContext).getUserDetails();

        setDataInfo();
    }
    private void setDataInfo(){
        arrInfo = new ArrayList<>();
//        email, lop, mssv, type
        boolean isTeacher = user.get("type").equalsIgnoreCase("teacher");
        Resources res = mContext.getResources();
        if (!isTeacher){
            arrInfo.add(new ItemInfoProfile(user.get("mssv"), res.getString(R.string.hint_mssv)));
            arrInfo.add(new ItemInfoProfile(user.get("lop"), res.getString(R.string.lopkhoahoc)));
        }else {
            arrInfo.add(new ItemInfoProfile(user.get("mssv"), res.getString(R.string.hint_msgv)));
            arrInfo.add(new ItemInfoProfile(user.get("lop"), res.getString(R.string.covanlop)));
        }
        arrInfo.add(new ItemInfoProfile(user.get("email"), res.getString(R.string.hint_email)));
        arrInfo.add(new ItemInfoProfile("11/07/1995", "Ngày sinh"));
        arrInfo.add(new ItemInfoProfile("Yêu công nghệ, yêu zai đẹp, thích màu hồng, sống thủy chung, ...", "Giới thiệu"));
        arrInfo.add(new ItemInfoProfile("Yêu công nghệ, yêu zai đẹp, thích màu hồng, sống thủy chung, ...", "Sở thích"));
//        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0) return 0;
        else return 1;
    }

    @Override
    public AbstractHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case 0:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_header_profile, parent, false);
                return new ItemHeaderProfileHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_info_profile, parent, false);
                return new ItemInfoProfileHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(AbstractHolder holder, int position) {
        if (position==0){
            ItemHeaderProfileHolder header = (ItemHeaderProfileHolder) holder;
            header.setData(user.get("avatar"), user.get("name"));
        } else {
            ItemInfoProfileHolder info = (ItemInfoProfileHolder) holder;
            info.setData(arrInfo.get(position-1));
        }
    }

    @Override
    public int getItemCount() {
        return arrInfo.size()+1;
    }


    // ---------------------------- Class Item -----------------------------------------------------

    public class ItemHeaderProfileHolder extends AbstractHolder {
        private CircleImageView ivAvatar;
        private TextView tvName;

        public ItemHeaderProfileHolder(View itemView) {
            super(itemView);

            ivAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar_profile);
            tvName = (TextView) itemView.findViewById(R.id.tv_name_profile);
//            TextView tvCountStar = (TextView) itemView.findViewById(R.id.tv_count_star_profile);
        }

        public void setData(String urlAvatar, String name){
            Picasso.with(mContext)
                    .load(urlAvatar).fit()
                    .placeholder(R.mipmap.ic_user)
                    .error(R.mipmap.ic_user).into(ivAvatar);
            tvName.setText(name);
        }

        @Override
        public int getViewHolderType() {
            return 0;
        }
    }

    public class ItemInfoProfileHolder extends AbstractHolder {
        private TextView content, hint;

        public ItemInfoProfileHolder(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.tv_info_content_profile);
            hint = (TextView) itemView.findViewById(R.id.tv_info_content_hint_profile);
        }

        public void setData(ItemInfoProfile info){
            content.setText(info.getInfoContent());
            hint.setText(info.getInfoHint());
        }

        @Override
        public int getViewHolderType() {
            return 1;
        }
    }

    // ------ Item -------
    public class ItemInfoProfile{
        private String infoContent, infoHint;
        public ItemInfoProfile(String content, String hint){
            infoContent = content;
            infoHint = hint;
        }

        public String getInfoContent() {
            return infoContent;
        }

        public String getInfoHint() {
            return infoHint;
        }
    }
}
