package com.kaya.blockreader.utils;

public class StringUtils {
    private static int smodeLength =1;
    public static void setBlockLength(int modeLength){
        smodeLength = modeLength;
    }
    public static String getBlockValue(String value,int modeLengtdh){
        String output = "";
        String blank = "";
        int modeLength = smodeLength;
        int cnt = 0;
        for (int i = 0;i < modeLength;i++){
            blank += "[ ]";
        }
        if (modeLength >= value.length()){
            output = value;
        }
        else
        {
            while (cnt < value.length()){
                if(cnt+modeLength < value.length()){
                    output += value.substring(cnt,cnt+modeLength);
                }
                output += blank;
                cnt += modeLength;
                cnt += modeLength;
            }
        }
        return output;
    }
}
