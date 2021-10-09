package com.kaya.blockreader.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kaya.blockreader.R;
import com.kaya.blockreader.adapter.BookListAdapter;
import com.kaya.blockreader.model.bookshelf_item;
import com.kaya.blockreader.utils.FileUtils;
import com.kaya.blockreader.utils.ListDataSaveUtil;
import com.kaya.blockreader.utils.StringUtils;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallbackWithBeforeParam;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView booklistRecycleView;
    private BookListAdapter bookListAdapter;
    private List<bookshelf_item> databooklist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .onExplainRequestReason(new ExplainReasonCallbackWithBeforeParam() {
                    @Override
                    public void onExplainReason(ExplainScope scope, List<String> deniedList, boolean beforeRequest) {
                        scope.showRequestReasonDialog(deniedList, "即将申请的权限是程序必须依赖的权限", "我已明白");
                    }
                })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        if (allGranted) {
                        } else {
                            Toast.makeText(MainActivity.this, "您拒绝了如下权限：" + deniedList, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextAppearance(this, R.style.Toolbar_TitleText);
        setSupportActionBar(myToolbar);
        booklistRecycleView = findViewById(R.id.book_list_recyclerview);
        databooklist = new ArrayList<>();
        if(ListDataSaveUtil.getInstance().getDataList(bookshelf_item.BOOK_TAG).size() == 0){
            databooklist.add(new bookshelf_item(bookshelf_item.ADD_TYPE,"","",""));
        }
        {
            databooklist.addAll(ListDataSaveUtil.getInstance().getDataList(bookshelf_item.BOOK_TAG));
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        booklistRecycleView.setLayoutManager(layoutManager);
        bookListAdapter = new BookListAdapter(databooklist,this);
        booklistRecycleView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        booklistRecycleView.setAdapter(bookListAdapter);
        //添加Android自带的分割线
        booklistRecycleView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        bookListAdapter.setOnItemClickListener(new BookListAdapter.OnAddItemClickListener() {

            @Override
            public void DeleteBook(int position) {
                databooklist.remove(position);
                ListDataSaveUtil.getInstance().setDataList(bookshelf_item.BOOK_TAG,databooklist);
                bookListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        StringUtils.setBlockLength(ListDataSaveUtil.getInstance().GetReadMode());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_read_mode:
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(this, R.style.AlertDialog);
                mBuilder.setTitle(R.string.mode_set_title);
                View mView = View.inflate(this, R.layout.view_dialog_input, null);
                final EditText mInput = (EditText) mView.findViewById(R.id.input);
                mBuilder.setView(mView);
                mBuilder.setPositiveButton(R.string.mode_set_ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String val = mInput.getText().toString();
                        if (val != null && val.length() > 0 && Float.parseFloat(val) < 10)
                        {
                            ListDataSaveUtil.getInstance().SetReadMode(Integer.parseInt(val));
                            StringUtils.setBlockLength(Integer.parseInt(val));
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"请输入正确的参数！",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mBuilder.create().show();
                return true;
            case R.id.set_read_backgroud:
                Toast.makeText(MainActivity.this,"此功能尚未开放！",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            databooklist.add(new bookshelf_item(bookshelf_item.SHOW_TYEP,FileUtils.getFileNameNotType(filePath),filePath,""+System.currentTimeMillis()));
            ListDataSaveUtil.getInstance().setDataList(bookshelf_item.BOOK_TAG,databooklist);
            bookListAdapter.notifyDataSetChanged();
        }
    }
}