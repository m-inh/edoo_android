package com.fries.edoo.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fries.edoo.R;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.helper.SQLiteHandler;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tmq on 20/08/2016.
 */
public class PostWriterTagFragment extends Fragment {
    public static final String TYPE_POST_QUESTION = "question";
    public static final String TYPE_POST_NOTE = "note";
    public static final String TYPE_POST_NOTIFICATION = "notification";
    public static final String TYPE_POST_POLL = "poll";
    private static final String TAG = PostWriterTagFragment.class.getSimpleName();

    private View rootView;
    private SQLiteHandler sqlite;
    private TextView typeQuestion, typeNote, typeNotification, typePoll;
    private ImageView ivLineTypePost;
    private SwitchCompat scIncognitoMode;
    private String typePost;
    private TextView oldType;
    private FloatingActionButton fabAddTagPost;
    private CircleImageView ivAvatar;
    private TextView txtUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_post_tag_incognito, null);

        //get data from Mainactivity
//        Intent mIntent = getIntent();
//        this.idLop = mIntent.getStringExtra("class_id");

        sqlite = new SQLiteHandler(getContext());

        initViews();
        setData();
        return rootView;
    }

    private void setData() {
        typePost = TYPE_POST_NOTE;
        oldType = typeNote;

        typeQuestion.setOnClickListener(clickTypePost);
        typeNote.setOnClickListener(clickTypePost);
        typeNotification.setOnClickListener(clickTypePost);
        typePoll.setOnClickListener(clickTypePost);
        scIncognitoMode.setOnCheckedChangeListener(checkIncognitoMode);
        fabAddTagPost.setOnClickListener(clickTypePost);

        typeNote.setTextSize(14f);

        if (!getIsTeacher()) {
            typeNotification.setVisibility(View.GONE);
        }

        setDataUser(true);
    }

    private void setDataUser(boolean isUser) {
        if (isUser) {
            HashMap<String, String> user = sqlite.getUserDetails();
            String urlAvatar = user.get("avatar");
            Picasso.with(getContext())
                    .load(urlAvatar).fit()
                    .placeholder(R.mipmap.ic_user)
                    .error(R.mipmap.ic_user).into(ivAvatar);
            txtUser.setText(user.get("name"));
        } else {
            ivAvatar.setImageResource(R.drawable.ic_incognito_mode);
            txtUser.setText(R.string.anonymous);
        }
    }

    private void initViews() {
        typeQuestion = (TextView) rootView.findViewById(R.id.txt_type_post_question);
        typeNote = (TextView) rootView.findViewById(R.id.txt_type_post_note);
        typeNotification = (TextView) rootView.findViewById(R.id.txt_type_post_notification);
        typePoll = (TextView) rootView.findViewById(R.id.txt_type_post_poll);
        ivLineTypePost = (ImageView) rootView.findViewById(R.id.iv_line_type_post);
        scIncognitoMode = (SwitchCompat) rootView.findViewById(R.id.sc_incognito_mode_post);
        fabAddTagPost = (FloatingActionButton) rootView.findViewById(R.id.fab_add_tag_post);

        ivAvatar = (CircleImageView) rootView.findViewById(R.id.iv_post_writer_avatar);
        txtUser = (TextView) rootView.findViewById(R.id.txt_post_writer_user);
    }

    /**
     * Receive event click to choose type of Post: Question, Note, Notification, Poll
     */
    View.OnClickListener clickTypePost = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int idColor = 0;
            switch (view.getId()) {
                case R.id.fab_add_tag_post:
                    Toast.makeText(getContext(), "Add Tag...", Toast.LENGTH_SHORT).show();
                    return;
                case R.id.txt_type_post_question:
                    typePost = TYPE_POST_QUESTION;
                    idColor = R.color.type_post_question;
                    break;
                case R.id.txt_type_post_note:
                    typePost = TYPE_POST_NOTE;
                    idColor = R.color.type_post_note;
                    break;
                case R.id.txt_type_post_notification:
                    typePost = TYPE_POST_NOTIFICATION;
                    idColor = R.color.type_post_notification;
                    break;
                case R.id.txt_type_post_poll:
                    typePost = TYPE_POST_POLL;
                    idColor = R.color.type_post_poll;
                    break;
            }
            oldType.setTextSize(12f);
            oldType = (TextView) view;
            oldType.setTextSize(14f);

            YoYo.with(Techniques.ZoomInUp)
                    .duration(700)
                    .playOn(ivLineTypePost);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ivLineTypePost.setBackgroundColor(getActivity().getResources().getColor(idColor, getActivity().getTheme()));
            } else {
                ivLineTypePost.setBackgroundColor(getActivity().getResources().getColor(idColor));
            }
        }
    };

    CompoundButton.OnCheckedChangeListener checkIncognitoMode = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
            YoYo.with(Techniques.SlideOutRight)
                    .withListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setDataUser(!b);
                            YoYo.with(Techniques.SlideInRight)
                                    .duration(500)
                                    .playOn(txtUser);
                            YoYo.with(Techniques.FlipInX)
                                    .duration(1000)
                                    .playOn(ivAvatar);
                        }

                        @Override
                        public void onAnimationStart(Animator animation) {}
                        @Override
                        public void onAnimationCancel(Animator animation) {}
                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    })
                    .duration(500)
                    .playOn(txtUser);
            YoYo.with(Techniques.FlipOutY)
                    .duration(1000)
                    .playOn(ivAvatar);
        }
    };

    // ---------------------------------------------------------------------------------------------
    public String getTypePost(){
        return typePost;
    }

    public boolean getIsIncognitoPost(){
        return scIncognitoMode.isChecked();
    }

    public boolean getIsTeacher(){
        return sqlite.getUserDetails().get("type").equalsIgnoreCase("teacher");
    }

}
