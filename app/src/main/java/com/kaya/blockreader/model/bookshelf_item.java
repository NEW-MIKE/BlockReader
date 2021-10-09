package com.kaya.blockreader.model;

public class bookshelf_item {
    int type;
    String bookname;
    String bookurl;
    String bookid;
    public static final int ADD_TYPE = 1;
    public static final int SHOW_TYEP = 2;
    public static final String BOOK_PREF_NAME = "BOOK_LIB";
    public static final String BOOK_TAG = "BOOK_TAG";

    public int getType() {
        return type;
    }

    public String getBookname() {
        return bookname;
    }

    public String getBookurl() {
        return bookurl;
    }

    public String getBookid() {
        return bookid;
    }

    public bookshelf_item(int type, String bookname, String bookurl, String bookid) {
        this.type = type;
        this.bookname = bookname;
        this.bookurl = bookurl;
        this.bookid = bookid;
    }
}
