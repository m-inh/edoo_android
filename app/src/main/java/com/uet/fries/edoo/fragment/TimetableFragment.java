package com.uet.fries.edoo.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;

import com.uet.fries.edoo.R;
import com.uet.fries.edoo.activities.MainActivity;
import com.uet.fries.edoo.app.AppConfig;
import com.uet.fries.edoo.communication.RequestServer;
import com.uet.fries.edoo.helper.PrefManager;
import com.uet.fries.edoo.helper.SQLiteHandler;
import com.uet.fries.edoo.models.ItemLop;
import com.uet.fries.edoo.models.ItemLopMonHoc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TMQ on 20-Nov-15.
 */
public class TimetableFragment extends Fragment {
    private static final String TAG = TimetableFragment.class.getSimpleName();
    private static final int TT_WIDTH = 6;
    private static final int TT_HEIGHT = 12;
    private Context mContext;
    private View rootView;
    private ArrayList<ItemLopMonHoc> listSubject;
    private LinearLayout[] columns;
    private int[][] listSubjectInTable;

    private PrefManager sessionMgr;
    private SQLiteHandler sqlite;

    private boolean firstLoading = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!firstLoading) {
            startAnim();
            return rootView;
        }

        firstLoading = false;
        rootView = inflater.inflate(com.uet.fries.edoo.R.layout.fragment_timetable, null);
        mContext = getActivity();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        rootView.setLayoutParams(params);

        sessionMgr = new PrefManager(mContext);
        sqlite = new SQLiteHandler(mContext);

        listSubject = new ArrayList<>();
        listSubjectInTable = new int[TT_WIDTH][TT_HEIGHT];
        columns = new LinearLayout[6];

        initViews();

//        if (!sessionMgr.isSaveClass())
            getDataFromServer();
//        else
//            getDataFromSQLite();

        return rootView;
    }

    private void initViews() {
        columns[0] = (LinearLayout) rootView.findViewById(com.uet.fries.edoo.R.id.tt_col_2);
        columns[1] = (LinearLayout) rootView.findViewById(com.uet.fries.edoo.R.id.tt_col_3);
        columns[2] = (LinearLayout) rootView.findViewById(com.uet.fries.edoo.R.id.tt_col_4);
        columns[3] = (LinearLayout) rootView.findViewById(com.uet.fries.edoo.R.id.tt_col_5);
        columns[4] = (LinearLayout) rootView.findViewById(com.uet.fries.edoo.R.id.tt_col_6);
        columns[5] = (LinearLayout) rootView.findViewById(com.uet.fries.edoo.R.id.tt_col_7);
    }

    private void showDialogInfo(final ItemLopMonHoc item, int bgItem) {
        final Dialog dialogInfo = new Dialog(mContext, com.uet.fries.edoo.R.style.DialogNoActionBar);
        View view = LayoutInflater.from(mContext).inflate(com.uet.fries.edoo.R.layout.dialog_item_subject_info, null);
        dialogInfo.setContentView(view);
        view.setBackgroundResource(bgItem);

        // InitViews
        TextView ten = (TextView) dialogInfo.findViewById(com.uet.fries.edoo.R.id.dialogTenMH);
        TextView maLMH = (TextView) dialogInfo.findViewById(com.uet.fries.edoo.R.id.dialogMaMH);
        TextView gv = (TextView) dialogInfo.findViewById(com.uet.fries.edoo.R.id.dialogGiangVien);
        TextView tgian = (TextView) dialogInfo.findViewById(com.uet.fries.edoo.R.id.dialogThoiGian);
        TextView diaDiem = (TextView) dialogInfo.findViewById(com.uet.fries.edoo.R.id.dialogDiaDiem);
        Button directToClass = (Button) dialogInfo.findViewById(com.uet.fries.edoo.R.id.btn_direct_to_class_dialog);

        // Set for Views
        ten.setText(item.getName());
        maLMH.setText(item.getCode());
        gv.setText(item.getTeacherName());
        diaDiem.setText(item.getAddress());
        tgian.setText("Thứ " + item.getDayOfWeek() + "\t\tTiết " + item.getPeriod());
        directToClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity mainActivity = (MainActivity) mContext;
                ItemLop itemLop = new ItemLop(item.getName(), item.getClassId(), item.getCode(), item.getTeacherName(), item.getStudentCount());
                mainActivity.goToTimeLine(itemLop, LopFragment.KEY_LOP_MON_HOC);
                dialogInfo.dismiss();
            }
        });

        dialogInfo.show();
    }

    //----------------------------------------------------------------------------------------------

    private void getDataFromServer() {
        listSubject.clear();

        String URL_REQUEST = AppConfig.URL_GET_TKB;
        RequestServer requestServer = new RequestServer(getActivity(), Request.Method.GET, URL_REQUEST);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                try {
                    if (error) return;

                    JSONArray listSubjects = response.getJSONObject("data").getJSONArray("classes");
                    for (int i = 0; i < listSubjects.length(); i++) {
                        JSONObject subject = listSubjects.getJSONObject(i);

                        JSONArray lessons = subject.getJSONArray("lessions");
                        for (int j = 0; j < lessons.length(); j++) {
                            JSONObject lesson = lessons.getJSONObject(j);
                            listSubject.add(new ItemLopMonHoc(lesson));
//                            Log.i(TAG, i+ " ----- " + lesson.toString());
                        }
                    }

                    setDataForTimeTable();

//                    saveClassesToSQLite();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        requestServer.sendRequest("get timetable");
    }

    private void getDataFromSQLite() {
        ArrayList<HashMap<String, String>> arrClasses = sqlite.getAllClasses();
        listSubject.clear();

        for (HashMap<String, String> hashClass : arrClasses) {
            listSubject.add(new ItemLopMonHoc(hashClass));
        }

        setDataForTimeTable();
    }

    // ---------------------------------------------------------------------------------------------
    private void saveClassesToSQLite() {
        for (ItemLopMonHoc c : listSubject) {
            sqlite.addClass(c.getId(), c.getClassId(), c.getCode(), c.getName(), c.getType(), c.getTeacherName(), c.getAddress(), c.getPeriod(),
                    c.getDayOfWeek(), c.getCreditCount(), c.getStudentCount());
        }
        sessionMgr.setIsSaveClass(true);
    }


    private void setDataForTimeTable() {
        convertSubjectToTable();
//        int[] colorsBackground = getResources().getIntArray(R.array.arr_color_cell_background);
        TypedArray bgCell = getResources().obtainTypedArray(R.array.arr_bg_cell_background);
        TypedArray bgItem = getResources().obtainTypedArray(R.array.arr_background_item);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < listSubjectInTable[0].length; j++) {
                int posSubject = listSubjectInTable[i][j];
                int space = 1;
                while (j + space < listSubjectInTable[0].length && listSubjectInTable[i][j + space] == posSubject) {
                    space++;
                }
                if (posSubject >= 0) {
                    addView(columns[i], bgCell.getResourceId(idColorForItem(posSubject), 0),
                            bgItem.getResourceId(idColorForItem(posSubject), 0), space, listSubject.get(posSubject));
                } else {
                    addEmptyView(columns[i], space);
                }
                j += (space - 1);
            }
        }
        bgCell.recycle();
    }

    ArrayList<View> listViewSubject = new ArrayList();

    private void startAnim() {
        Animation myAni = AnimationUtils.loadAnimation(mContext, com.uet.fries.edoo.R.anim.anim_show_item_subject);
        for (View view : listViewSubject) {
            view.startAnimation(myAni);
        }
    }

    private void addView(LinearLayout col, final int bgCellId, final int bgItem, int weight, final ItemLopMonHoc lmh) {
        View view = LayoutInflater.from(mContext).inflate(com.uet.fries.edoo.R.layout.item_cell_timetable, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogInfo(lmh, bgItem);
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, weight);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(10);
        }
        view.setLayoutParams(params);
        view.findViewById(com.uet.fries.edoo.R.id.bg_cell).setBackgroundResource(bgCellId);

        TextView name = (TextView) view.findViewById(com.uet.fries.edoo.R.id.tv_name_subject);
        TextView timeStart = (TextView) view.findViewById(com.uet.fries.edoo.R.id.tv_time_start_subject);
        TextView timeEnd = (TextView) view.findViewById(com.uet.fries.edoo.R.id.tv_time_end_subject);

        name.setText(lmh.getAcronymOfName());
        int pos = lmh.getPosOfPeriod();
        pos = (pos < 6 ? pos : pos + 1) + 6;
        timeStart.setText(pos + ":00");
        timeEnd.setText((pos + lmh.getLengthOfPeriod()) + ":00");

        Animation myAni = AnimationUtils.loadAnimation(mContext, com.uet.fries.edoo.R.anim.anim_show_item_subject);
        view.startAnimation(myAni);

        listViewSubject.add(view);
        col.addView(view);
    }

    private void addEmptyView(LinearLayout col, int weight) {
        View view = LayoutInflater.from(mContext).inflate(com.uet.fries.edoo.R.layout.item_cell_empty_timetable, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, weight);
        view.setLayoutParams(params);
        col.addView(view);
    }

    private void convertSubjectToTable() {
        for (int i = 0; i < listSubjectInTable.length; i++) {
            for (int j = 0; j < listSubjectInTable[0].length; j++) {
                listSubjectInTable[i][j] = -1;
            }
        }
        for (int i = 0; i < listSubject.size(); i++) {
            ItemLopMonHoc item = listSubject.get(i);
            int day = item.getDayOfWeek();
            int posOfPeriod = item.getPosOfPeriod();
            int periodLength = item.getLengthOfPeriod();

            for (int j = 0; j < periodLength; j++) {
                int x = day - 2;
                int y = posOfPeriod + j - 1;
//                Log.i(TAG, "period = " + posOfPeriod + ", x = " + x + ", y = " + y);
                if (y >= 5) y++;

                listSubjectInTable[x][y] = i;
            }
        }
    }

    private int idColorForItem(int indexLesson) {
        for (int i = 0; i < listSubject.size(); i++) {
            if (listSubject.get(indexLesson).getCode().equalsIgnoreCase(listSubject.get(i).getCode())) {
                return i;
            }
        }
        return indexLesson;
    }

}
