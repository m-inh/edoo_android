package com.fries.edoo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fries.edoo.R;
import com.fries.edoo.activities.ProfileActivity;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.holder.AbstractHolder;
import com.fries.edoo.models.ItemUser;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tmq on 27/08/2016.
 */
public class ProfileAdapter extends RecyclerView.Adapter<AbstractHolder> {
    private static final String TAG = ProfileAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<ItemInfoProfile> arrInfo;
    private ItemUser user;

    public ProfileAdapter(Context context, ItemUser user) {
        mContext = context;
        arrInfo = new ArrayList<>();

        this.user = user;
        setDataUser(user);
    }

    public void setDataUser(ItemUser user) {
        Resources res = mContext.getResources();
        if (user.isTeacher()) {
            arrInfo.add(new ItemInfoProfile(user.getCode(), res.getString(R.string.hint_mssv), "code"));
            arrInfo.add(new ItemInfoProfile(user.getRegularClass(), res.getString(R.string.lopkhoahoc), "regular_class"));
        } else {
            arrInfo.add(new ItemInfoProfile(user.getCode(), res.getString(R.string.hint_msgv), "code"));
            arrInfo.add(new ItemInfoProfile(user.getRegularClass(), res.getString(R.string.covanlop), "regular_class"));
        }
        arrInfo.add(new ItemInfoProfile(user.getBirthday(), res.getString(R.string.txt_birthday), "birthday"));
        arrInfo.add(new ItemInfoProfile(user.getEmail(), res.getString(R.string.txt_email), "email"));
        arrInfo.add(new ItemInfoProfile(user.getDescription(), res.getString(R.string.txt_description), "description"));
        arrInfo.add(new ItemInfoProfile(user.getFavorite(), res.getString(R.string.txt_favorite), "favorite"));
    }

    public void updateAvatar(String urlAvatar) {
        user.setAvatar(urlAvatar);
        notifyDataSetChanged();
    }

    public void updateDataInfo(String description, String favorite){
        user.setDescription(description);
        user.setFavorite(favorite);
        setDataUser(user);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        else return 1;
    }

    @Override
    public AbstractHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
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
        if (position == 0) {
            ItemHeaderProfileHolder header = (ItemHeaderProfileHolder) holder;
            header.setData();
        } else {
            ItemInfoProfileHolder info = (ItemInfoProfileHolder) holder;
            info.setData(arrInfo.get(position - 1), position);
        }
    }

    @Override
    public int getItemCount() {
        return arrInfo.size() + 1;
    }

    public ItemUser getUser() {
        return user;
    }

    // ---------------------------- Class Item -----------------------------------------------------

    public class ItemHeaderProfileHolder extends AbstractHolder {
        private CircleImageView ivAvatar;
        private TextView tvName, tvPointCount;

        public ItemHeaderProfileHolder(View itemView) {
            super(itemView);

            ivAvatar = (CircleImageView) itemView.findViewById(R.id.iv_avatar_profile);
            tvName = (TextView) itemView.findViewById(R.id.tv_name_profile);
            tvPointCount = (TextView) itemView.findViewById(R.id.tv_point_count_profile);
        }

        public void setData() {
            Picasso.with(mContext)
                    .load(user.getAvatar()).fit()
                    .placeholder(R.mipmap.ic_user)
                    .error(R.mipmap.ic_user).into(ivAvatar);
            tvName.setText(user.getName());
            tvPointCount.setText("" + user.getPointCount());
        }

        @Override
        public int getViewHolderType() {
            return 0;
        }
    }

    // ----------------------------------------------------------------
    public class ItemInfoProfileHolder extends AbstractHolder implements View.OnClickListener {
        private TextView content, hint;
        private ImageView edit;
        private int position;

        public ItemInfoProfileHolder(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.tv_info_content_profile);
            hint = (TextView) itemView.findViewById(R.id.tv_info_content_hint_profile);
            edit = (ImageView) itemView.findViewById(R.id.iv_edit_info_profile);
        }

        public void setData(ItemInfoProfile info, int pos) {
            position = pos - 1;
            content.setText(info.getInfoContent());
            hint.setText(info.getInfoHint());

            if (pos > 4) edit.setVisibility(View.VISIBLE);

            edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(hint.getText());
            View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_info_profile, null);
            final EditText edtContent = (EditText) layout.findViewById(R.id.edt_info_profile);
            edtContent.setHint(hint.getText());
            edtContent.setText(content.getText());
            builder.setView(layout);

            builder.setPositiveButton(mContext.getResources().getString(R.string.txt_save), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateInfo(edtContent.getText().toString());
                }
            });
            builder.setNegativeButton(mContext.getResources().getString(R.string.txt_cancel), null);

            builder.show();
        }

        private void updateInfo(String newContent) {
            ItemInfoProfile item = arrInfo.get(position);
            item.setInfoContent(newContent);

            String des = user.getDescription();
            String favo = user.getFavorite();
            des = des.isEmpty() ? "..." : des;      // If description is empty, description = value_default = "..."
            favo = favo.isEmpty() ? "..." : favo;

            JSONObject params = new JSONObject();
            try {
                params.put("description", des);
                params.put("favorite", favo);
                params.put(item.getInfoKey(), item.getInfoContent());
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            Log.i(TAG, position + ", Update = " + params.toString());
            ((ProfileActivity) mContext).updateProfile(params);
        }

        @Override
        public int getViewHolderType() {
            return 1;
        }
    }

    // ------ Item -------
    public class ItemInfoProfile {
        private String infoContent, infoHint, infoKey;

        public ItemInfoProfile(String content, String hint, String id) {
            infoContent = content;
            infoHint = hint;
            infoKey = id;
        }

        public String getInfoContent() {
            return infoContent;
        }

        public String getInfoHint() {
            return infoHint;
        }

        public String getInfoKey() {
            return infoKey;
        }

        public void setInfoContent(String content) {
            infoContent = content;
        }
    }
}
