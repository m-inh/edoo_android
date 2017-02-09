package com.uet.fries.edoo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.uet.fries.edoo.R;
import com.uet.fries.edoo.helper.SQLiteHandler;
import com.uet.fries.edoo.models.ITimelineBase;
import com.uet.fries.edoo.utils.CommonVLs;
import com.nineoldandroids.animation.Animator;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tmq on 20/08/2016.
 */
public class PostWriterTagFragment extends Fragment {
    private static final String TAG = PostWriterTagFragment.class.getSimpleName();

    private View rootView;
    private SQLiteHandler sqlite;
    private TextView typeQuestion, typeNote, typeNotification, typePoll, typeExercise;
    private TextView tvPickDate, tvPickTime;
    private ImageView ivLineTypePost;
    private SwitchCompat scIncognitoMode;
    private String typePost = "";
    private boolean isIncognito = false;
    //    private FloatingActionButton fabAddTagPost;
    private CircleImageView ivAvatar;
    private TextView txtUser;

    public static PostWriterTagFragment newInstance(String typePost, boolean isIncognito) {
        PostWriterTagFragment writer = new PostWriterTagFragment();
        writer.typePost = typePost;
        writer.isIncognito = isIncognito;
        return writer;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_post_tag_incognito, null);

        sqlite = new SQLiteHandler(getContext());

        initViews();
        setData();
        return rootView;
    }

    private void setData() {
        typeQuestion.setOnClickListener(clickTypePost);
        typeNote.setOnClickListener(clickTypePost);
        typeNotification.setOnClickListener(clickTypePost);
        typePoll.setOnClickListener(clickTypePost);
        typeExercise.setOnClickListener(clickTypePost);
        scIncognitoMode.setOnCheckedChangeListener(checkIncognitoMode);

        // Type post default for Teacher and Student
        if (typePost.equals("")) {
            typePost = ITimelineBase.TYPE_POST_QUESTION;
        } else {
            int idType = 0;
            int type = 0;
            switch (typePost) {
                case ITimelineBase.TYPE_POST_QUESTION:
                    idType = R.drawable.ic_type_post_question;
                    type = R.id.tv_type_post_question;
                    break;
                case ITimelineBase.TYPE_POST_NOTE:
                    idType = R.drawable.ic_type_post_note;
                    type = R.id.tv_type_post_note;
                    break;
                case ITimelineBase.TYPE_POST_NOTIFICATION:
                    idType = R.drawable.ic_type_post_notification;
                    type = R.id.tv_type_post_notification;
                    break;
                case ITimelineBase.TYPE_POST_POLL:
                    idType = R.drawable.ic_type_post_poll;
                    type = R.id.tv_type_post_poll;
                    break;
                case ITimelineBase.TYPE_POST_EXERCISE:
                    type = R.id.tv_type_post_exercise;
                    break;
            }
            setModeWritePost(type != R.id.tv_type_post_exercise);
            setStatusClickTypePost(idType, type);
        }

        setDataUser(!isIncognito);
        scIncognitoMode.setChecked(isIncognito);
        if (!getIsTeacher()) {
            typeNotification.setVisibility(View.INVISIBLE);
            typeExercise.setVisibility(View.INVISIBLE);
        } else {
//            scIncognitoMode.setVisibility(View.GONE);
        }


        // Pick Date, Time
        setOnClickDateTime();
    }

    private void setDataUser(boolean isUser) {
        if (isUser) {
            HashMap<String, String> user = sqlite.getUserDetails();
            String urlAvatar = user.get("avatar");
            Picasso.with(getContext())
                    .load(urlAvatar).fit()
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user).into(ivAvatar);
            txtUser.setText(user.get("name"));
        } else {
            ivAvatar.setImageResource(R.drawable.ic_incognito_mode);
            txtUser.setText(R.string.icognito);
        }
    }

    private void initViews() {
        typeQuestion = (TextView) rootView.findViewById(R.id.tv_type_post_question);
        typeNote = (TextView) rootView.findViewById(R.id.tv_type_post_note);
        typeNotification = (TextView) rootView.findViewById(R.id.tv_type_post_notification);
        typePoll = (TextView) rootView.findViewById(R.id.tv_type_post_poll);
        typeExercise = (TextView) rootView.findViewById(R.id.tv_type_post_exercise);
        ivLineTypePost = (ImageView) rootView.findViewById(R.id.iv_line_type_post);
        scIncognitoMode = (SwitchCompat) rootView.findViewById(R.id.sc_incognito_mode_post);
        tvPickDate = (TextView) rootView.findViewById(R.id.tv_pick_date);
        tvPickTime = (TextView) rootView.findViewById(R.id.tv_pick_time);
//        fabAddTagPost = (FloatingActionButton) rootView.findViewById(R.id.fab_add_tag_post);

        ivAvatar = (CircleImageView) rootView.findViewById(R.id.iv_post_writer_avatar);
        txtUser = (TextView) rootView.findViewById(R.id.txt_post_writer_user);
    }

    /**
     * Receive event click to choose type of Post: Question, Note, Notification, Poll
     */
    View.OnClickListener clickTypePost = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int idType = 0;
            int type = 0;
            switch (view.getId()) {
                case R.id.tv_type_post_question:
                    typePost = ITimelineBase.TYPE_POST_QUESTION;
                    idType = R.drawable.ic_type_post_question;
                    type = R.id.tv_type_post_question;
                    break;
                case R.id.tv_type_post_note:
                    typePost = ITimelineBase.TYPE_POST_NOTE;
                    idType = R.drawable.ic_type_post_note;
                    type = R.id.tv_type_post_note;
                    break;
                case R.id.tv_type_post_notification:
                    typePost = ITimelineBase.TYPE_POST_NOTIFICATION;
                    idType = R.drawable.ic_type_post_notification;
                    type = R.id.tv_type_post_notification;
                    break;
                case R.id.tv_type_post_poll:
                    typePost = ITimelineBase.TYPE_POST_POLL;
                    idType = R.drawable.ic_type_post_poll;
                    type = R.id.tv_type_post_poll;
                    break;
                case R.id.tv_type_post_exercise:
                    typePost = ITimelineBase.TYPE_POST_EXERCISE;
                    type = R.id.tv_type_post_exercise;
                    break;
            }
            setModeWritePost(type != R.id.tv_type_post_exercise);
            setStatusClickTypePost(idType, type);
        }
    };

    private void setStatusClickTypePost(int idType, int type) {
        typeQuestion.setBackgroundResource(R.drawable.bg_btn_border_empty);
        typeNote.setBackgroundResource(R.drawable.bg_btn_border_empty);
        typePoll.setBackgroundResource(R.drawable.bg_btn_border_empty);
        typeNotification.setBackgroundResource(R.drawable.bg_btn_border_empty);
        typeExercise.setBackgroundResource(R.drawable.bg_btn_border_empty);

        typeQuestion.setTextColor(CommonVLs.getColor(R.color.black_87, getContext()));
        typeNote.setTextColor(CommonVLs.getColor(R.color.black_87, getContext()));
        typePoll.setTextColor(CommonVLs.getColor(R.color.black_87, getContext()));
        typeNotification.setTextColor(CommonVLs.getColor(R.color.black_87, getContext()));
        typeExercise.setTextColor(CommonVLs.getColor(R.color.black_87, getContext()));

        rootView.findViewById(type).setBackgroundResource(R.drawable.bg_btn_border_fill);
        ((TextView) rootView.findViewById(type)).setTextColor(CommonVLs.getColor(R.color.white, getContext()));

        YoYo.with(Techniques.ZoomInUp)
                .duration(700)
                .playOn(ivLineTypePost);
        if (idType != 0) ivLineTypePost.setImageResource(idType);
        else ivLineTypePost.setImageResource(R.color.white);
    }

    private void setModeWritePost(boolean isWritePost) {
        LinearLayout llIncognito = (LinearLayout) rootView.findViewById(R.id.ll_mode_incognito);
        LinearLayout llDeadline = (LinearLayout) rootView.findViewById(R.id.ll_deadline);
        if (isWritePost) {
            llIncognito.setVisibility(View.VISIBLE);
            llDeadline.setVisibility(View.GONE);
        } else {    // Choose time for Deadline
            llIncognito.setVisibility(View.GONE);
            llDeadline.setVisibility(View.VISIBLE);
            scIncognitoMode.setChecked(false);
        }
    }

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
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    })
                    .duration(500)
                    .playOn(txtUser);
            YoYo.with(Techniques.FlipInX)
                    .duration(1000)
                    .playOn(ivAvatar);
        }
    };

    private void setOnClickDateTime() {
        final Calendar now = Calendar.getInstance();
        now.setTimeInMillis(now.getTimeInMillis() + 24*3600*1000);
        int year = now.get(Calendar.YEAR);
        String month = getNumber(now.get(Calendar.MONTH) + 1);
        String day = getNumber(now.get(Calendar.DAY_OF_MONTH));
        String hour = "21";
        String minutes = "00";
        tvPickTime.setText(hour + ":" + minutes);
        tvPickDate.setText(day + "/" + month + "/" + year);

        tvPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        onDateSetListener,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(CommonVLs.getColor(R.color.colorPrimary, getContext()));
                dpd.setTitle("Ngày nộp bài");
                dpd.setMinDate(now);

                dpd.show(getActivity().getFragmentManager(), TAG + "DatePicker");
            }
        });

        tvPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        onTimeSetListener,
                        21,
                        0,
                        true
                );
                tpd.enableMinutes(true);
                tpd.setAccentColor(CommonVLs.getColor(R.color.colorPrimary, getContext()));
                tpd.setTitle("Giờ nộp bài");
                tpd.show(getActivity().getFragmentManager(), TAG + "TimePicker");
            }
        });
    }

    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear++;
            tvPickDate.setText(getNumber(dayOfMonth) + "/" + getNumber(monthOfYear) + "/" + year);
        }
    };
    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            tvPickTime.setText(getNumber(hourOfDay) + ":" + getNumber(minute));
        }
    };

    private String getNumber(int num) {
        return (num < 10 ? "0" + num : "" + num);
    }

    public String getTimestamp() {
        if (!typePostIsExercise()) return "0";

        String dateTime = tvPickDate.getText().toString() + "T" + tvPickTime.getText().toString() + ":00.000Z";
        String timestamp = "1";

        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy'T'hh:mm:ss.SSS").parse(dateTime);
            timestamp = date.getTime() + "";

        } catch (ParseException e) {
            e.printStackTrace();
        }
//        Log.i(TAG, "timestamp = " + timestamp);
        return timestamp;
    }

    // ---------------------------------------------------------------------------------------------
    public String getTypePost() {
        return typePost;
    }

    public boolean typePostIsExercise() {
        return getTypePost().equals(ITimelineBase.TYPE_POST_EXERCISE);
    }

    public boolean getIsIncognitoPost() {
        return scIncognitoMode.isChecked();
    }

    public boolean getIsTeacher() {
        return sqlite.getUserDetails().get("type").equalsIgnoreCase("teacher");
    }

}
