package com.kaya.blockreader;

import android.app.Application;

import com.kaya.blockreader.utils.AppUtils;
import com.kaya.blockreader.utils.ListDataSaveUtil;

public class blockApplication extends Application {
    private static blockApplication mblockApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mblockApplication=this;
        ListDataSaveUtil.init(this);
        AppUtils.init(this);
    }

    public synchronized static blockApplication getInstance(){
        if(mblockApplication == null){
            mblockApplication = new blockApplication();
        }
        return mblockApplication;
    }
}
