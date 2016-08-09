package com.fries.edoo.models;

/**
 * Created by TMQ on 21-Nov-15.
 */
public class ItemLopMonHoc {
    private String ten;
    private String maLMH;
    private String diaDiem;
    private String giangVien;
    private int viTri;
    private int soTiet;
    private int soSV;
    private int nhomTH;

    public ItemLopMonHoc(String ten, String maLMH, String diaDiem, String giangVien, int viTri, int soTiet, int soSV, int nhomTH){
        this.ten        = ten;
        this.maLMH      = maLMH;
        this.diaDiem    = diaDiem;
        this.giangVien  = giangVien;
        this.viTri      = viTri;
        this.soTiet     = soTiet;
        this.soSV       = soSV;
        this.nhomTH     = nhomTH;
    }

    public String getTen(){
        return ten;
    }
    public String getMaLMH(){
        return maLMH;
    }
    public String getDiaDiem(){
        return diaDiem;
    }
    public String getGiangVien(){
        return giangVien;
    }
    public int getViTri(){
        return viTri;
    }
    public int getSoTiet(){
        return soTiet;
    }
    public int getSoSV(){
        return soSV;
    }
    public int getNhomTH(){
        return nhomTH;
    }

    public String getTenVietTat(){
        if (ten == null) {
            return "";
        }
        String vietTat = "";
        String temp[] = ten.split(" ");
        for (int i = 0; i < temp.length; i++) {
//            Log.i("ItemLopMonHoc", "ok: " + temp[i]);
            if (temp[i] != null && temp[i].length() > 0) {
                vietTat += temp[i].charAt(0);
            }
        }
        vietTat = vietTat.toUpperCase();
        return vietTat;
    }
}
