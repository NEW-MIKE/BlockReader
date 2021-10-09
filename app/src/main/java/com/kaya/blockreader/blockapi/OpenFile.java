package com.kaya.blockreader.blockapi;

import android.content.Context;
import android.util.Log;

import com.kaya.blockreader.blockApplication;
import com.kaya.blockreader.utils.ListDataSaveUtil;
import com.kaya.blockreader.utils.ScreenUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Vector;

public class OpenFile {

    private String charset = "UTF-8";
    /**
     * 屏幕宽高
     */
    private int mHeight, mWidth;
    /**
     * 文字区域宽高
     */
    private int mVisibleHeight, mVisibleWidth;
    /**
     * 间距
     */
    private int marginHeight, marginWidth;
    /**
     * 字体大小
     */
    private int mFontSize, mNumFontSize;
    /**
     * 每页行数
     */
    private int mPageLineCount;
    /**
     * 行间距
     **/
    private int mLineSpace;
    /**
     * 字节长度
     */
    private int mbBufferLen;
    /**
     * MappedByteBuffer：高效的文件内存映射
     */
    private MappedByteBuffer mbBuff;
    /**
     * 页首页尾的位置
     */
    private int curEndPos, curBeginPos, tempBeginPos, tempEndPos;
    private Vector<String> mLines = new Vector<>();
    private ArrayList<Integer> ReadLocations = new ArrayList<Integer>();
    private String bookid;

    public OpenFile(String path,String bookid) {
        this(ScreenUtils.getScreenWidth(),ScreenUtils.getScreenHeight(),60);
        this.bookid = bookid;
        curEndPos = ListDataSaveUtil.getInstance().GetReadProgress(bookid);
        openBook(path);
    }

    @Override
    public String toString() {
        return "OpenFile{" +
                "mHeight=" + mHeight +
                ", mWidth=" + mWidth +
                ", mVisibleHeight=" + mVisibleHeight +
                ", mVisibleWidth=" + mVisibleWidth +
                ", marginHeight=" + marginHeight +
                ", marginWidth=" + marginWidth +
                ", mFontSize=" + mFontSize +
                ", mPageLineCount=" + mPageLineCount +
                ", mLineSpace=" + mLineSpace +
                ", mbBufferLen=" + mbBufferLen +
                ", mLines=" + mLines +
                '}';
    }

    public OpenFile(int height, int width, int fontSize) {
        mWidth = width;
        mHeight = height;
        mFontSize = fontSize;
        mLineSpace = mFontSize / 5 * 2;
        mNumFontSize = ScreenUtils.dpToPxInt(16);
        marginWidth = ScreenUtils.dpToPxInt(15);
        marginHeight = ScreenUtils.dpToPxInt(15);
        mVisibleHeight = mHeight - marginHeight * 2 - mNumFontSize * 2 - mLineSpace * 2;
        mVisibleWidth = mWidth - marginWidth * 2;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
    }


    /**
     * 打开书籍文件
     *
     * @param path  阅读章节
     * @return 0：文件不存在或打开失败  1：打开成功
     */
    public int openBook(String path) {
        try {
            File file = new File(path);
            long length = file.length();
            if (length > 10) {
                mbBufferLen = (int) length;
                mbBuff = new RandomAccessFile(file, "r")
                        .getChannel()
                        .map(FileChannel.MapMode.READ_ONLY, 0, length);
                return 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 读取一个换行的内容。从一个位置开始，到最近的一个换行符。此处不是回车。
     *
     * @param curEndPos 当前页结束位置指针
     * @return
     */
    private byte[] readParagraphForward(int curEndPos) {
        byte b0;
        int i = curEndPos;
        while (i < mbBufferLen) {
            b0 = mbBuff.get(i++);
            if (b0 == 0x0a) {
                break;
            }
        }
        int nParaSize = i - curEndPos;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = mbBuff.get(curEndPos + i);
        }
        return buf;
    }

    public String test() throws UnsupportedEncodingException {
        int nParaSize = 1000;
        byte[] buf = new byte[nParaSize];
        for (int i = 0; i < nParaSize; i++) {
            buf[i] = mbBuff.get(curEndPos + i);
        }
        String strParagraph = new String(readParagraphForwardTest(424), charset);
        return strParagraph;
    }
    private byte[] readParagraphForwardTest(int curEndPos) {
        byte b0;
        int i = curEndPos;
        while (i < mbBufferLen) {
            b0 = mbBuff.get(i++);
            if (b0 == 0x0a) {
                break;
            }
        }
        int nParaSize = i - curEndPos;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = mbBuff.get(curEndPos + i);
        }
        return buf;
    }

    /**
     * 读取上一段落
     *
     * @param curBeginPos 当前页起始位置指针
     * @return
     */
    private byte[] readParagraphBack(int curBeginPos) {
        byte b0;
        int i = curBeginPos - 1;
        while (i > 0) {
            b0 = mbBuff.get(i);
            if (b0 == 0x0a && i != curBeginPos - 1) {
                i++;
                break;
            }
            i--;
        }
        int nParaSize = curBeginPos - i;
        byte[] buf = new byte[nParaSize];
        for (int j = 0; j < nParaSize; j++) {
            buf[j] = mbBuff.get(i + j);
        }
        return buf;
    }
    /**
     * 指针移到上一页页首
     */
    public ArrayList<Integer> getReadLocations(){
        return ReadLocations;
    }
    private void pageUp() {
        String strParagraph = "";
        Vector<String> lines = new Vector<>(); // 页面行
        int paraSpace = 0;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        while ((lines.size() < mPageLineCount) && (curBeginPos > 0)) {
            Vector<String> paraLines = new Vector<>(); // 段落行
            byte[] parabuffer = readParagraphBack(curBeginPos); // 1.读取上一个段落

            curBeginPos -= parabuffer.length; // 2.变换起始位置指针
            try {
                strParagraph = new String(parabuffer, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            strParagraph = strParagraph.replaceAll("\r\n", "  ");
            strParagraph = strParagraph.replaceAll("\n", " ");

            while (strParagraph.length() > 0) { // 3.逐行添加到lines
                int paintSize = mVisibleWidth;
                paraLines.add(strParagraph.substring(0, paintSize));
                strParagraph = strParagraph.substring(paintSize);
            }
            lines.addAll(0, paraLines);

            while (lines.size() > mPageLineCount) { // 4.如果段落添加完，但是超出一页，则超出部分需删减
                try {
                    curBeginPos += lines.get(0).getBytes(charset).length; // 5.删减行数同时起始位置指针也要跟着偏移
                    lines.remove(0);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            curEndPos = curBeginPos; // 6.最后结束指针指向下一段的开始处
            paraSpace += mLineSpace;
            mPageLineCount = (mVisibleHeight - paraSpace) / (mFontSize + mLineSpace); // 添加段落间距，实时更新容纳行数
        }
    }

    /**
     * 根据起始位置指针，读取一页内容，一页的内容包含，多少行，这个公式是什么，总共有多少行，
     *
     * @return
     */
    private Vector<String> pageDown() {
        String strParagraph = " ";
        Vector<String> lines = new Vector<>();
        int paraSpace = 0;
        mPageLineCount = mVisibleHeight / (mFontSize + mLineSpace);
        /*
        * 此处的作用是什么呢，就是根据一个段落，读取一个单元，然后将这个单元用来处理，目标，是产生一个页面的显示的内容，具体的操作
        * 是将读取的段落里面的内容，按照行的宽度，逐步转化出来。一行一行的，转换出来后，后序的相关的操作，都是重复这样的过程。对于当下的
        * 的这个数标，做好相关方面的记录，*/
        ReadLocations.add(curEndPos);
        while ((lines.size() < mPageLineCount) && (curEndPos < mbBufferLen)) {
            byte[] parabuffer = readParagraphForward(curEndPos);
            curEndPos += parabuffer.length;
            try {
                strParagraph = new String(parabuffer, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            strParagraph = strParagraph
                    .trim()
                    .replaceAll("\r\n", "  ")
                    .replaceAll("\n", " ");
            // 段落中的换行符去掉，绘制的时候再换行
            while (strParagraph.length() > 0) {
                int paintSize = 12;
                if(strParagraph.length() < paintSize){
                    /*此处获取到的段落里面，长度如果是短语一个段落的话，那就直接赋值*/
                    lines.add(strParagraph);
                    strParagraph="";
                    break;
                }
                else {
                    lines.add(strParagraph.substring(0, paintSize));
                    strParagraph = strParagraph.substring(paintSize);
                }
                if (lines.size() >= mPageLineCount) {
                    break;
                }
            }
            if (strParagraph.length() != 0) {
                try {
                    curEndPos -= (strParagraph).getBytes(charset).length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            paraSpace += mLineSpace;
            mPageLineCount = (mVisibleHeight - paraSpace) / (mFontSize + mLineSpace);
        }
        curEndPos--;
        return lines;
    }

    /**
     * 跳转下一页
     */
    public Vector<String> nextPage() {
        mLines.clear();
        mLines = pageDown();
        return mLines;
    }

    public String LinesTest(){
        mLines.clear();
        mLines = pageDown();
        String output = "";
        for (int i = 0;i < mLines.size(); i ++){
            output += mLines.get(i)+"\n";
        }
        return output;
    }


}
