package com.aaron.filepickerlibrary.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aaron.filepickerlibrary.R;
import com.aaron.filepickerlibrary.adapter.PathAdapter;
import com.aaron.filepickerlibrary.filter.LFileFilter;
import com.aaron.filepickerlibrary.model.ParamEntity;
import com.aaron.filepickerlibrary.utils.Constant;
import com.aaron.filepickerlibrary.utils.FileUtils;
import com.aaron.filepickerlibrary.utils.PermissionHelper;
import com.aaron.filepickerlibrary.utils.UltimateBar;
import com.aaron.filepickerlibrary.widget.EmptyRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilePickerActivity extends AppCompatActivity {
    public static final int  WRITE_EXTERNAL_STORAGE_CODE = 0x123;
    private final String TAG = "FilePicker";
    private EmptyRecyclerView mRecylerView;
    private View mEmptyView;
    private TextView mTvPath;
    private TextView mTvBack;
    private Button mBtnAddBook;
    private String mPath;
    private List<File> mListFiles;
    private ArrayList<String> mListNumbers = new ArrayList<String>();//存放选中条目的数据地址
    private PathAdapter mPathAdapter;
    private Toolbar mToolbar;
    private ParamEntity mParamEntity;
    private LFileFilter mFilter;
    private PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lfile_picker);
        mParamEntity = (ParamEntity) getIntent().getExtras().getSerializable("param");
        UltimateBar.newColorBuilder()
                .statusColor(Color.parseColor(mParamEntity.getBackgroundColor()))// 状态栏颜色
                .applyNav(true)                 // 是否应用到导航栏
                .navColor(Color.parseColor(mParamEntity.getBackgroundColor())) // 导航栏颜色
                .build(this)
                .apply();
        initView();
        permissionHelper =new PermissionHelper(this);
        permissionHelper
                .requestCodes(WRITE_EXTERNAL_STORAGE_CODE)
                .requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .requestRationaleDialog(true)
                .requestListener(new PermissionHelper.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        initListener();
                    }

                    @Override
                    public void onPermissionDenied(String[] deniedPermissions) {
                        Toast.makeText(FilePickerActivity.this,"功能受限，请开启读取存储权限！"
                                ,Toast.LENGTH_SHORT).show();
                    }
                })
                .require();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(permissionHelper != null){
            permissionHelper.onRequestPermissionsResult(requestCode,permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 更新Toolbar展示
     */
    private void initToolbar() {
        if (mParamEntity.getTitle() != null) {
            mToolbar.setTitle(mParamEntity.getTitle());
        }
        if (mParamEntity.getTitleColor() != null) {
            mToolbar.setTitleTextColor(Color.parseColor(mParamEntity.getTitleColor())); //设置标题颜色
        }
        if (mParamEntity.getBackgroundColor() != null) {
            mToolbar.setBackgroundColor(Color.parseColor(mParamEntity.getBackgroundColor()));
        }
//        if (!mParamEntity.isMutilyMode()) {
//            mBtnAddBook.setVisibility(View.GONE);
//        }
        switch (mParamEntity.getBackIcon()) {
            case Constant.BACKICON_STYLEONE:
                mToolbar.setNavigationIcon(R.mipmap.backincostyleone);
                break;
            case Constant.BACKICON_STYLETWO:
                mToolbar.setNavigationIcon(R.mipmap.backincostyletwo);
                break;
            case Constant.BACKICON_STYLETHREE:
                //默认风格
                break;
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 添加点击事件处理
     */
    private void initListener() {
        // 返回目录上一级
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempPath = new File(mPath).getParent();
                if (tempPath == null) {
                    return;
                }
                mPath = tempPath;
                mListFiles = getFileList(mPath);
                mPathAdapter.setmListData(mListFiles);
                mPathAdapter.notifyDataSetChanged();
                mRecylerView.scrollToPosition(0);
                setShowPath(mPath);
                //清除添加集合中数据
                mListNumbers.clear();
                if (mParamEntity.getAddText() != null) {
                    mBtnAddBook.setText(mParamEntity.getAddText());
                } else {
                    mBtnAddBook.setText(R.string.Selected);
                }
            }
        });
        mPathAdapter.setOnItemClickListener(new PathAdapter.OnItemClickListener() {
            @Override
            public void click(int position) {
                if (mParamEntity.isMutilyMode()) {
                    if (mListFiles.get(position).isDirectory()) {
                        //如果当前是目录，则进入继续查看目录
                        chekInDirectory(position);
                    } else {
                        if(mParamEntity.getChooseType() == Constant.CHOOSE_FILE){
                            //如果已经选择则取消，否则添加进来
                            if (mListNumbers.contains(mListFiles.get(position).getAbsolutePath())) {
                                mListNumbers.remove(mListFiles.get(position).getAbsolutePath());
                            } else {
                                mListNumbers.add(mListFiles.get(position).getAbsolutePath());
                            }
                            if (mParamEntity.getAddText() != null) {
                                mBtnAddBook.setText(mParamEntity.getAddText() + "( " + mListNumbers.size() + " )");
                            } else {
                                mBtnAddBook.setText(getString(R.string.Selected) + "( " + mListNumbers.size() + " )");
                            }
                        }
                    }
                } else {
                    //单选模式直接返回
                    if (mListFiles.get(position).isDirectory()) {
                        chekInDirectory(position);
                        return;
                    }
                    if(mParamEntity.getChooseType() == Constant.CHOOSE_FILE){
                        mListNumbers.add(mListFiles.get(position).getAbsolutePath());
                        chooseDone();
                    }
                }

            }
        });

        mBtnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mParamEntity.getChooseType() == Constant.CHOOSE_DIR){
                    mListNumbers.add(mPath);
                    //返回
                    chooseDone();
                }
                if (mListNumbers.size() < 1) {
                    String info = mParamEntity.getNotFoundFiles();
                    if (TextUtils.isEmpty(info)) {
                        Toast.makeText(FilePickerActivity.this, R.string.NotFoundBooks, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FilePickerActivity.this, info, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    //返回
                    chooseDone();
                }
            }
        });
    }


    /**
     * 点击进入目录
     *
     * @param position
     */
    private void chekInDirectory(int position) {
        mPath = mListFiles.get(position).getAbsolutePath();
        setShowPath(mPath);
        //更新数据源
        mListFiles = getFileList(mPath);
        mPathAdapter.setmListData(mListFiles);
        mPathAdapter.notifyDataSetChanged();
        mRecylerView.scrollToPosition(0);
    }

    /**
     * 完成提交
     */
    private void chooseDone() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("paths", mListNumbers);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * 根据地址获取当前地址下的所有目录和文件，并且排序
     *
     * @param path
     * @return List<File>
     */
    private List<File> getFileList(String path) {
        File file = new File(path);
        List<File> list = FileUtils.getFileListByDirPath(path, mFilter);

        return list;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mRecylerView = (EmptyRecyclerView) findViewById(R.id.recylerview);
        mTvPath = (TextView) findViewById(R.id.tv_path);
        mTvBack = (TextView) findViewById(R.id.tv_back);
        mBtnAddBook = (Button) findViewById(R.id.btn_addbook);
        mEmptyView = findViewById(R.id.empty_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mParamEntity.getAddText() != null) {
            mBtnAddBook.setText(mParamEntity.getAddText());
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initToolbar();
        if (!checkSDState()) {
            Toast.makeText(this, R.string.NotFoundPath, Toast.LENGTH_SHORT).show();
            return;
        }
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        mTvPath.setText(mPath);
        mFilter = new LFileFilter(mParamEntity.getFileTypes());
        mListFiles = getFileList(mPath);
        mPathAdapter = new PathAdapter(mListFiles, this, mFilter, mParamEntity.isMutilyMode(),mParamEntity.getChooseType());
        mRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mPathAdapter.setmIconStyle(mParamEntity.getIconStyle());
        mRecylerView.setAdapter(mPathAdapter);
        mRecylerView.setmEmptyView(mEmptyView);
    }

    /**
     * 检测SD卡是否可用
     */
    private boolean checkSDState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 显示顶部地址
     *
     * @param path
     */
    private void setShowPath(String path) {
        mTvPath.setText(path);
    }
}
