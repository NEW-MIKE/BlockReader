package com.kaya.blockreader.blockapi;

import android.content.Context;
/*task :
* 1，对文本进行处理：空格，空行，
* 2，对于当前的阅读位置的记录
* 3，回到原来的阅读的位置
* 4，对大文件的读取的优化*/
public class ReadBlockManager {
    private int mode = 100;
    private int cnt =0;
    OpenFile openFile = new OpenFile("","");
    public String NextBlockReader(String valuein) throws Exception {
        String value = valuein;
        String outPut = "";
        if(cnt > value.length()){
            return "";
        }
        if(cnt+mode > value.length()){
            outPut = value.substring(cnt,value.length());
        }
        else {
            outPut = value.substring(cnt,cnt+mode);
        }
        cnt+=mode;
        return outPut;
    }

    public String PreBlockReader(Context context) throws Exception {
/*        String value = openFile.getTxtValue(context);
        String outPut = "";
        if(cnt == 0) {
            return "";
        }
        if(cnt - mode == 0){
            outPut = value.substring(0,mode);
        }
        else {
            outPut = value.substring(cnt-mode,cnt);
        }
        cnt -= mode;*/
        return "outPut";
    }

    private void SaveCurrentBlock(){

    }
}
