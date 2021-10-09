package com.kaya.blockreader.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.kaya.blockreader.R;
import com.kaya.blockreader.utils.ListDataSaveUtil;
import com.kaya.blockreader.utils.StringUtils;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class ReadPagerAdapter extends PagerAdapter {
    private ArrayList<String> data;
    private Context context;

    private ArrayList<String> getData(){
        return data;
    }
    public ReadPagerAdapter(Context context,ArrayList<String> data){
        this.data = data;
        this.context = context;
    }
    @Override
    public int getCount() {
        return getData().size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View viewl = inflater.inflate(R.layout.view_pager_content,null);
        final TextView vcTextView = ((TextView)viewl.findViewById(R.id.vc_text));
        final TextView bcTextView = ((TextView)viewl.findViewById(R.id.back_text));
        vcTextView.setText(""+getData().get(position));
        bcTextView.setText(StringUtils.getBlockValue(getData().get(position),4));
        vcTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipAnimation(vcTextView,bcTextView);
            }
        });
        bcTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipAnimationBackToPre(bcTextView,vcTextView);
            }
        });
        container.addView(viewl);
        return viewl;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);;
    }


    private void flipAnimation(TextView textview1,TextView textview2) {
        final TextView visibletext;
        final TextView invisibletext;
        //逻辑判断
        if (textview1.getVisibility() == View.GONE) {
            visibletext = textview2;
            invisibletext = textview1;
        } else {
            invisibletext = textview2;
            visibletext = textview1;
        }
        //LinearInterpolator()     其变化速率恒定
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visibletext, "rotationY", 0f, 90f);
        visToInvis.setDuration(500);
        //AccelerateInterpolator()    其变化开始速率较慢，后面加速
        visToInvis.setInterpolator(new AccelerateInterpolator());
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisibletext, "rotationY",
                -90f, 0f);
        invisToVis.setDuration(500);
        //DecelerateInterpolator()   其变化开始速率较快，后面减速
        invisToVis.setInterpolator(new DecelerateInterpolator());
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                visibletext.setVisibility(View.GONE);
                invisToVis.start();
                invisibletext.setVisibility(View.VISIBLE);
            }
        });
        visToInvis.start();
    }


    private void flipAnimationBackToPre(TextView textview1,TextView textview2) {
        final TextView visibletext;
        final TextView invisibletext;
        //逻辑判断
        if (textview1.getVisibility() == View.GONE) {
            visibletext = textview2;
            invisibletext = textview1;
        } else {
            invisibletext = textview2;
            visibletext = textview1;
        }
        //LinearInterpolator()     其变化速率恒定
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visibletext, "rotationY", 0f, -90f);
        visToInvis.setDuration(500);
        //AccelerateInterpolator()    其变化开始速率较慢，后面加速
        visToInvis.setInterpolator(new AccelerateInterpolator());
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisibletext, "rotationY",
                90f,0f);
        invisToVis.setDuration(500);
        //DecelerateInterpolator()   其变化开始速率较快，后面减速
        invisToVis.setInterpolator(new DecelerateInterpolator());
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                visibletext.setVisibility(View.GONE);
                invisToVis.start();
                invisibletext.setVisibility(View.VISIBLE);
            }
        });
        visToInvis.start();
    }
}
