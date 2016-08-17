package com.fries.edoo.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TMQ on 21-Nov-15.
 */
public class ItemLopMonHoc {
    private static final String TAG = ItemLopMonHoc.class.getSimpleName();
//    private String ten;
//    private String maLMH;
//    private String diaDiem;
//    private String giangVien;
//    private int viTri;
//    private int soTiet;
//    private int soSV;
//    private int nhomTH;
//
//    public ItemLopMonHoc(String ten, String maLMH, String diaDiem, String giangVien, int viTri, int soTiet, int soSV, int nhomTH){
//        this.ten        = ten;
//        this.maLMH      = maLMH;
//        this.diaDiem    = diaDiem;
//        this.giangVien  = giangVien;
//        this.viTri      = viTri;
//        this.soTiet     = soTiet;
//        this.soSV       = soSV;
//        this.nhomTH     = nhomTH;
//    }
//
//    public String getTen(){
//        return ten;
//    }
//    public String getMaLMH(){
//        return maLMH;
//    }
//    public String getDiaDiem(){
//        return diaDiem;
//    }
//    public String getGiangVien(){
//        return giangVien;
//    }
//    public int getViTri(){
//        return viTri;
//    }
//    public int getSoTiet(){
//        return soTiet;
//    }
//    public int getSoSV(){
//        return soSV;
//    }
//    public int getNhomTH(){
//        return nhomTH;
//    }
//
//    public String getTenVietTat(){
//        if (ten == null) {
//            return "";
//        }
//        String vietTat = "";
//        String temp[] = ten.split(" ");
//        for (int i = 0; i < temp.length; i++) {
////            Log.i("ItemLopMonHoc", "ok: " + temp[i]);
//            if (temp[i] != null && temp[i].length() > 0) {
//                vietTat += temp[i].charAt(0);
//            }
//        }
//        vietTat = vietTat.toUpperCase();
//        return vietTat;
//    }

    private String classId,
            code,
            name,
            type,               // Has 3 type: Subject, Regular, Fit
            semester,           // Semester: I , II
            teacherName,
            address,
            period;             // Exp: 1-2, 3-5
    private int id,
            creditCount,
            studentCount,       // The number of Student
            dayOfWeek;          // Monday -> 2, Tus -> 3, ...


    public ItemLopMonHoc(String code, String name, String teacherName, String address, String period,
                         int creditCount, int studentCount, int dayOfWeek) {
        this.code = code;
        this.name = name;
        this.teacherName = teacherName;
        this.address = address;
        this.period = period;
        this.creditCount = creditCount;
        this.studentCount = studentCount;
        this.dayOfWeek = dayOfWeek;
    }

    public ItemLopMonHoc(String classId, String code, String name, String type, String semester, String teacherName, String address, String period,
                         int id, int creditCount, int studentCount, int dayOfWeek) {
        this.classId = classId;
        this.code = code;
        this.name = name;
        this.type = type;
        this.semester = semester;
        this.teacherName = teacherName;
        this.address = address;
        this.period = period;
        this.id = id;
        this.creditCount = creditCount;
        this.studentCount = studentCount;
        this.dayOfWeek = dayOfWeek;
    }

    public ItemLopMonHoc(JSONObject lesson) {
        try {
            this.classId = lesson.getString("class_id");
            this.code = lesson.getString("code");
            this.name = lesson.getString("name");
            this.type = lesson.getString("type");
            this.semester = lesson.getString("semester");
            this.teacherName = lesson.getString("teacher_name");
            this.address = lesson.getString("address");
            this.period = lesson.getString("period");
            this.id = lesson.getInt("id");
            this.creditCount = lesson.getInt("credit_count");
            this.studentCount = lesson.getInt("student_count");
            this.dayOfWeek = lesson.getInt("day_of_week");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "Error parse String -> JsonObject");
        }
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getAddress() {
        return address;
    }

    public String getPeriod() {
        return period;
    }

    /**
     * @return the number of lesson
     * Eg: 4-5 -> 2,        6-9 -> 4
     */
    public int getLengthOfPeriod(){
        int posDivider = period.indexOf("-");
        String start = period.substring(0, posDivider);
        String end = period.substring(posDivider+1, period.length());
        return Integer.parseInt(end) - Integer.parseInt(start) + 1;
    }

    /**
     * @return start position of period
     * Eg: 4-5 -> 4,        6-9 -> 6
     */
    public int getPosOfPeriod(){
        Log.i(TAG, "getPosOfPeriod = " + period.substring(period.indexOf("-")));
        return Integer.parseInt(period.substring(0, period.indexOf("-")));
    }

    public int getCreditCount() {
        return creditCount;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * @return First Character of Name
     * Eg: "Dai so" -> "D"
     */
    public String getAcronymOfName() {
        if (name == null) {
            return "";
        }
        String acronym = "";
        String temp[] = name.split(" ");
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != null && temp[i].length() > 0) {
                acronym += temp[i].charAt(0);
            }
        }
        acronym = acronym.toUpperCase();
        return acronym;
    }
}
