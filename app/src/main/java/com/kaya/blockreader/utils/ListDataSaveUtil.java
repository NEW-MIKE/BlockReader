package com.kaya.blockreader.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaya.blockreader.model.bookshelf_item;

import java.util.ArrayList;
import java.util.List;

public class ListDataSaveUtil {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static ListDataSaveUtil listDataSaveUtil;

    public static String SP_KEY_SET_MODE_READ = "ID_SET_MODE_READ";
    public static String SP_KEY_SET_PROGRESS_READ = "ID_SET_PROGRESS_READ";

    public static void init(Context mContext){
        preferences = mContext.getSharedPreferences(bookshelf_item.BOOK_PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    public synchronized static ListDataSaveUtil getInstance() {
        if(listDataSaveUtil == null){
            listDataSaveUtil = new ListDataSaveUtil();
        }
        return listDataSaveUtil;
    }

    /**
     * 保存List
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0) {
            return;
        }

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        editor.putString(tag, strJson);
        editor.commit();

    }

    /**
     * 获取List
     * @param tag
     * @return
     */
    public <T> List<T> getDataList(String tag) {
        List<T> datalist=new ArrayList<T>();
        String strJson = preferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<bookshelf_item>>() {
        }.getType());
        return datalist;

    }
    public void SetReadMode(int mode){
        editor.putInt(SP_KEY_SET_MODE_READ,mode).commit();
    }
    public int GetReadMode(){
        return preferences.getInt(SP_KEY_SET_MODE_READ,5);
    }
    public void SetReadProgress(String bookid,int progress){
        editor.putInt(bookid,progress).commit();
    }
    public int GetReadProgress(String bookid){
        return preferences.getInt(bookid,0);
    }
}