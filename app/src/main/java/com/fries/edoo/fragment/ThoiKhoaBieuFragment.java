package com.fries.edoo.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fries.edoo.R;
import com.fries.edoo.adapter.TableSubjectAdapter;
import com.fries.edoo.app.AppConfig;
import com.fries.edoo.app.AppController;
import com.fries.edoo.communication.RequestServer;
import com.fries.edoo.helper.SQLiteHandler;
import com.fries.edoo.models.ItemLopMonHoc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by TMQ on 20-Nov-15.
 */
public class ThoiKhoaBieuFragment extends Fragment implements AdapterView.OnItemClickListener{
    private static final String TAG = ThoiKhoaBieuFragment.class.getSimpleName();
    private Context mContext;
    private View rootView;
    private GridView gridSubject;
    private ArrayList<ItemLopMonHoc> listSubject = new ArrayList<>();

    private TableSubjectAdapter adapter;
    private Dialog dialogInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.table_subject, null);
        mContext = getActivity();

        initViews();

        getDataFromServer();

        return rootView;
    }

    private void initViews(){
        gridSubject = (GridView)    rootView.findViewById(R.id.gridSubject);
        gridSubject.setOnItemClickListener(this);

        dialogInfo = new Dialog(mContext, R.style.DialogNoActionBar);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemLopMonHoc item = adapter.getItem(position);
        if (item==null) {
//            Toast.makeText(mContext, "Trống", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialogInfo(item);
    }

    private void showDialogInfo(ItemLopMonHoc item){
        dialogInfo.setContentView(R.layout.dialog_item_subject_info);

        dialogInfo.setTitle("Thông tin:");

        // InitViews
        TextView ten    = (TextView)    dialogInfo.findViewById(R.id.dialogTenMH);
        TextView maLMH  = (TextView)    dialogInfo.findViewById(R.id.dialogMaMH);
        TextView gv     = (TextView)    dialogInfo.findViewById(R.id.dialogGiangVien);
        TextView tgian  = (TextView)    dialogInfo.findViewById(R.id.dialogThoiGian);
        TextView diaDiem= (TextView)    dialogInfo.findViewById(R.id.dialogDiaDiem);

        // Set for Views
        ten.setText(item.getName());
        maLMH.setText(item.getCode());
        gv.setText(item.getTeacherName());
        diaDiem.setText(item.getAddress());
        tgian.setText("Thứ " + item.getDayOfWeek() + "\t\tTiết " + item.getPeriod());

        dialogInfo.show();
    }

    //----------------------------------------------------------------------------------------------

    private static final String URL_REQUEST = AppConfig.URL_GET_TKB;

    private void getDataFromServer(){
        listSubject.clear();

        RequestServer requestServer = new RequestServer(getActivity(), Request.Method.GET, URL_REQUEST);
        requestServer.setListener(new RequestServer.ServerListener() {
            @Override
            public void onReceive(boolean error, JSONObject response, String message) throws JSONException {
                try {
                    if (error) return;

                    JSONArray listSubjects = response.getJSONObject("data").getJSONArray("classes");
                    for (int i=0; i<listSubjects.length(); i++){
                        JSONObject subject = listSubjects.getJSONObject(i);

                        JSONArray lessons = subject.getJSONArray("lessions");
                        for (int j = 0; j < lessons.length(); j++) {
                            JSONObject lesson = lessons.getJSONObject(j);
                            listSubject.add(new ItemLopMonHoc(lesson));
                        }
                    }

                    adapter = new TableSubjectAdapter(mContext);
                    adapter.setListSubject(listSubject);
                    gridSubject.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        requestServer.sendRequest("get timetable");


    }

}
