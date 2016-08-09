package com.fries.edoo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fries.edoo.R;
import com.fries.edoo.models.ItemLopMonHoc;

import java.util.ArrayList;

/**
 * Created by TMQ on 20-Nov-15.
 */
public class TableSubjectAdapter extends BaseAdapter {
    private static final String TAG = "TableSubjectAdapter";
    // So item toi da trong bang Thoi khoa bieu
    private static final int MAX_SIZE = 77;
    // 6 ngay trong tuan <=> 6 cot
    private static final int SIZE_COL = 6;
    // Danh sach cac mau dung de danh dau cac mon hoc
    private static final int [] COLOR_ITEM = new int[] {
            R.color.bg_item_subject_0,
            R.color.bg_item_subject_1,
            R.color.bg_item_subject_2,
            R.color.bg_item_subject_3,
            R.color.bg_item_subject_4,
            R.color.bg_item_subject_5,
            R.color.bg_item_subject_6,
            R.color.bg_item_subject_7,
            R.color.bg_item_subject_8,
            R.color.bg_item_subject_9,
            R.color.bg_item_subject_10
    };


    private int [] listSubjectInTable;
    private ArrayList<ItemLopMonHoc> listSubject = new ArrayList<>();
    private Context mContext;
    private LayoutInflater lf;

    public TableSubjectAdapter(Context context){
        mContext = context;
        lf = LayoutInflater.from(mContext);
    }

    public void setListSubject(ArrayList<ItemLopMonHoc> list){
        listSubject = list;
        convertSubjectToTable();
    }

    private void getSubjectData(){
        listSubject = new ArrayList<>();

        listSubject.add(new ItemLopMonHoc("Xác suất thống kê", "MAT1111 1", "303 G2", "Lê Phê Đô", 3, 3, 90, 1));
        listSubject.add(new ItemLopMonHoc("Toán rời rạc", "MAT2222 3", "313 G2", "Đặng Thanh Hải", 16, 3, 90, 1));
        listSubject.add(new ItemLopMonHoc("Lý thuyết thông tin", "INT2222 3", "313 G2", "Đặng Thanh Hải", 22, 2, 90, 1));
        listSubject.add(new ItemLopMonHoc("Lập trình hướng đối tượng", "INT1111 1", "303 G2", "Lê Phê Đô", 28, 2, 90, 1));

        convertSubjectToTable();
    }
    private void convertSubjectToTable(){
        listSubjectInTable = new int[MAX_SIZE];
        for (int i=0; i<MAX_SIZE; i++){
            listSubjectInTable[i] = -1;
        }
        for ( int i = 0; i<listSubject.size(); i++){
            ItemLopMonHoc item = listSubject.get(i);
            int viTri = item.getViTri();    // Lay vi tri cua mon hoc
            int soTiet = item.getSoTiet();

            for (int j=0; j<soTiet; j++){
                int x = (viTri+j - 1) / 10;
                int y = (viTri+j - 1) % 10;
                if (y>=5) y++;

                int viTriMoi = y*SIZE_COL + x;

                listSubjectInTable[viTriMoi] = i;
            }
        }
    }

    @Override
    public int getCount() {
        return MAX_SIZE;
    }

    @Override
    public ItemLopMonHoc getItem(int position) {
        if (listSubjectInTable[position]==-1) return null;
        return listSubject.get(listSubjectInTable[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null){
            view = lf.inflate(R.layout.item_subject_in_table, null);
        }

        if (position>=30 && position<=35) {
            TextView txtName = (TextView)   view.findViewById(R.id.txtItemNameSubject);
            txtName.setBackgroundColor(mContext.getResources().getColor(COLOR_ITEM[10]));
            txtName.setText("-");
            return view;
        }

        if (listSubjectInTable[position] == -1) return view;

        Animation myAni = AnimationUtils.loadAnimation(mContext, R.anim.anim_show_item_subject);
        view.startAnimation(myAni);

        ItemLopMonHoc item = listSubject.get(listSubjectInTable[position]);

        TextView txtName = (TextView)   view.findViewById(R.id.txtItemNameSubject);
        txtName.setText(item.getTenVietTat());
        txtName.setBackgroundColor(mContext.getResources().getColor(COLOR_ITEM[listSubjectInTable[position]]));

        return view;
    }

    //---------------------------------------------------------------------------------------




}
