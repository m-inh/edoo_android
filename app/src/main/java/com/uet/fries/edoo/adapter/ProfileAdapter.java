package com.uet.fries.edoo.adapter;

import android.app.Dialog;
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

import com.android.volley.Request;

import com.stfalcon.frescoimageviewer.ImageViewer;
import com.uet.fries.edoo.activities.ProfileActivity;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.holder.AbstractHolder;
import com.uet.fries.edoo.models.ItemUser;
import com.uet.fries.edoo.utils.CommonVLs;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
        arrInfo.clear();
        Resources res = mContext.getResources();
        if (!user.isTeacher()) {
            arrInfo.add(new ItemInfoProfile(user.getCode(), res.getString(com.uet.fries.edoo.R.string.hint_mssv), "code"));
            arrInfo.add(new ItemInfoProfile(user.getRegularClass(), res.getString(com.uet.fries.edoo.R.string.lopkhoahoc), "regular_class"));
        } else {
            arrInfo.add(new ItemInfoProfile(user.getCode(), res.getString(com.uet.fries.edoo.R.string.hint_msgv), "code"));
            arrInfo.add(new ItemInfoProfile(user.getRegularClass(), res.getString(com.uet.fries.edoo.R.string.covanlop), "regular_class"));
        }
        arrInfo.add(new ItemInfoProfile(CommonVLs.convertDate(user.getBirthday()), res.getString(com.uet.fries.edoo.R.string.txt_birthday), "birthday"));
        arrInfo.add(new ItemInfoProfile(user.getEmail(), res.getString(com.uet.fries.edoo.R.string.txt_email), "email"));
        arrInfo.add(new ItemInfoProfile(user.getDescription(), res.getString(com.uet.fries.edoo.R.string.txt_description), "description"));
        arrInfo.add(new ItemInfoProfile(user.getFavorite(), res.getString(com.uet.fries.edoo.R.string.txt_favorite), "favorite"));
        arrInfo.add(new ItemInfoProfile("****", res.getString(com.uet.fries.edoo.R.string.txt_password), "change_password"));
    }

    public void updateAvatar(String urlAvatar) {
        user.setAvatar(urlAvatar);
        notifyDataSetChanged();
    }

    public void updateDataInfo(String description, String favorite) {
        user.setDescription(description);
        user.setFavorite(favorite);
        setDataUser(user);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return AbstractHolder.TYPE_HEADER;
        else return AbstractHolder.TYPE_INFO;
    }

    @Override
    public AbstractHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case AbstractHolder.TYPE_HEADER:
                view = LayoutInflater.from(mContext).inflate(com.uet.fries.edoo.R.layout.item_header_profile, parent, false);
                return new ItemHeaderProfileHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(com.uet.fries.edoo.R.layout.item_info_profile, parent, false);
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

            ivAvatar = (CircleImageView) itemView.findViewById(com.uet.fries.edoo.R.id.iv_avatar_profile);
            tvName = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_name_profile);
            tvPointCount = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_point_count_profile);
        }

        public void setData() {
            Picasso.with(mContext)
                    .load(user.getAvatar()).fit()
                    .placeholder(com.uet.fries.edoo.R.mipmap.ic_user)
                    .error(com.uet.fries.edoo.R.mipmap.ic_user).into(ivAvatar);
            tvName.setText(user.getName());
            tvPointCount.setText("" + user.getPointCount());

            ivAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ImageViewer.Builder(mContext, new String[]{user.getAvatar()})
                            .setStartPosition(0)
                            .show();
                }
            });
        }

        @Override
        public int getViewHolderType() {
            return AbstractHolder.TYPE_HEADER;
        }
    }

    // ----------------------------------------------------------------
    public class ItemInfoProfileHolder extends AbstractHolder implements View.OnClickListener {
        private TextView content, hint;
        private ImageView edit;
        private int position;

        public ItemInfoProfileHolder(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_info_content_profile);
            hint = (TextView) itemView.findViewById(com.uet.fries.edoo.R.id.tv_info_content_hint_profile);
            edit = (ImageView) itemView.findViewById(com.uet.fries.edoo.R.id.iv_edit_info_profile);
        }

        public void setData(ItemInfoProfile info, int pos) {
            position = pos - 1;
            content.setText(info.getInfoContent());
            hint.setText(info.getInfoHint());

            if (pos > 4) {
                edit.setVisibility(View.VISIBLE);
            } else {
                edit.setVisibility(View.INVISIBLE);
            }

            edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ItemInfoProfile item = arrInfo.get(position);
            if (item.getInfoKey().equalsIgnoreCase("change_password")) {
                caseUpdatePassword();
            } else {
                caseUpdateInfo();
            }
        }

        private void caseUpdatePassword() {
            final Dialog dialog = new Dialog(mContext, com.uet.fries.edoo.R.style.DialogInputActionBar);
            dialog.setContentView(com.uet.fries.edoo.R.layout.dialog_change_password);
            dialog.setTitle(com.uet.fries.edoo.R.string.txt_change_password);
            dialog.setCancelable(false);

            final EditText edtOldPass = (EditText) dialog.findViewById(com.uet.fries.edoo.R.id.edt_old_password);
            final EditText edtNewPass = (EditText) dialog.findViewById(com.uet.fries.edoo.R.id.edt_new_password);
            final EditText edtConfirmNewPass = (EditText) dialog.findViewById(com.uet.fries.edoo.R.id.edt_confirm_new__password);
            final TextView tvAlertNotMatch = (TextView) dialog.findViewById(com.uet.fries.edoo.R.id.tv_new_password_not_match);
            final TextView tvAlertOldPassIncorrect = (TextView) dialog.findViewById(com.uet.fries.edoo.R.id.tv_old_password_incorrect);

            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case com.uet.fries.edoo.R.id.btn_cancel_change_pass:
                            dialog.dismiss();
                            break;
                        case com.uet.fries.edoo.R.id.btn_ok_change_pass:
                            String oldPass = edtOldPass.getText().toString();
                            String newPass = edtNewPass.getText().toString();
                            String confirmNewPass = edtConfirmNewPass.getText().toString();

                            if (oldPass.isEmpty() || newPass.isEmpty() || confirmNewPass.isEmpty()) {
                                Toast.makeText(mContext, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                                break;
                            }

                            if (!newPass.equals(confirmNewPass)) {
                                tvAlertNotMatch.setVisibility(View.VISIBLE);
                                break;
                            } else {
                                changePasswordRequest(oldPass, newPass, dialog, tvAlertOldPassIncorrect);
                            }
                            break;
                    }
                }
            };

            View.OnFocusChangeListener onFocusChange = new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        tvAlertNotMatch.setVisibility(View.GONE);
                        tvAlertOldPassIncorrect.setVisibility(View.GONE);
                    }
                }
            };

            edtNewPass.setOnFocusChangeListener(onFocusChange);
            edtConfirmNewPass.setOnFocusChangeListener(onFocusChange);
            dialog.findViewById(com.uet.fries.edoo.R.id.btn_cancel_change_pass).setOnClickListener(onClick);
            dialog.findViewById(com.uet.fries.edoo.R.id.btn_ok_change_pass).setOnClickListener(onClick);

            dialog.show();
        }

        private void caseUpdateInfo() {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, com.uet.fries.edoo.R.style.DialogInput);
            builder.setTitle(hint.getText());
            View layout = LayoutInflater.from(mContext).inflate(com.uet.fries.edoo.R.layout.dialog_edit_info_profile, null);
            final EditText edtContent = (EditText) layout.findViewById(com.uet.fries.edoo.R.id.edt_info_profile);
            edtContent.setHint(hint.getText());
            edtContent.setText(content.getText());
            edtContent.setSelection(content.getText().length());
            builder.setView(layout);
            builder.setCancelable(false);
            builder.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    updateInfo(edtContent.getText().toString());
                }
            });
            builder.setNegativeButton("Hủy", null);

            builder.show();
        }

        // ------------------------ RequestServer --------------------------------------------------
        private void changePasswordRequest(String oldPass, String newPass, final Dialog dialog, final TextView tvAlertOldPassIncorrect) {
            JSONObject json = new JSONObject();
            try {
                json.put("old_password", oldPass);
                json.put("new_password", newPass);
                Log.d(TAG, "old = " + oldPass);
                Log.d(TAG, "new = " + newPass);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final RequestServer requestServer = new RequestServer(mContext, Request.Method.POST, AppConfig.URL_POST_CHANGE_PASS, json);
            requestServer.setListener(new RequestServer.ServerListener() {
                @Override
                public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                    if (!error) {
                        Toast.makeText(mContext, "Thay đổi thành công! Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        requestServer.logout();
                    } else {
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        tvAlertOldPassIncorrect.setVisibility(View.VISIBLE);
                    }
                }
            });

            requestServer.sendRequest("change_password");
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
            return AbstractHolder.TYPE_INFO;
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
