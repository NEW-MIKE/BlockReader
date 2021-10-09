package com.kaya.blockreader.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kaya.blockreader.R;
import com.kaya.blockreader.adapter.ReadPagerAdapter;
import com.kaya.blockreader.blockapi.OpenFile;
import com.kaya.blockreader.blockapi.ReadBlockManager;
import com.kaya.blockreader.utils.ListDataSaveUtil;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class ReadActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private ReadBlockManager readBlockManager;
    private String input ;
    private String bookid;
    private int ReadPosition;
    private OpenFile openFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        ReadPosition = 1;
        Glide.with(this)
                .asGif()
                .load(R.drawable.readbkg)
                .into((ImageView) findViewById(R.id.read_imageview));

        viewPager = findViewById(R.id.pager);
        final LayoutInflater inflater = getLayoutInflater();
        final ArrayList<String> datas = new ArrayList<>();
        Intent getIntent = getIntent();
        input = getIntent.getStringExtra("param1");
        bookid = getIntent.getStringExtra("bookid");
        openFile = new OpenFile(input,bookid);

        readBlockManager = new ReadBlockManager();
        try {
            datas.add(openFile.LinesTest());
            datas.add(openFile.LinesTest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ReadPagerAdapter adapter = new ReadPagerAdapter(ReadActivity.this,datas);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (position == datas.size()-1){
                    try {
                        datas.add(openFile.LinesTest());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                }
                ReadPosition = position;
                Log.d("TAG", "viewPager.getChildCount(): "+viewPager.getChildCount());
                Log.d("TAG", "position: "+position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ListDataSaveUtil.getInstance().SetReadProgress(bookid,openFile.getReadLocations().get(ReadPosition));
    }

    public static void actionStart(Context context, String data1, String bookid) {
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra("param1", data1);
        intent.putExtra("bookid", bookid);
        context.startActivity(intent);
    }
}