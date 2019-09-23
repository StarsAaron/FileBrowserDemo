package com.aaron.filepickerlibrary;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aaron.filepickerlibrary.model.ParamEntity;
import com.aaron.filepickerlibrary.ui.FilePickerActivity;
import com.aaron.filepickerlibrary.utils.Constant;


/**
 * 文件路径选择类
 *
 * 两种类型:
 * CHOOSE_DIR 文件夹
 * CHOOSE_FILE 文件
 *
 * 图标风格:
 * ICON_STYLE_YELLOW
 * ICON_STYLE_BLUE
 * ICON_STYLE_GREEN
 *
 * 返回图标风格:
 * BACKICON_STYLEONE
 * BACKICON_STYLETWO
 * BACKICON_STYLETHREE
 *
 * new FilePicker()
 *         .withActivity(ShowActivity.this)
 *         .withRequestCode(REQUEST_DIR_PATH)
 *         .withTitle("请选择导出文件夹")
 *         .withIconStyle(Constant.ICON_STYLE_BLUE)
 *         .withChooseType(Constant.CHOOSE_DIR)
 *         .withMutilyMode(false)
 *         .start();
 */
public class FilePicker {
    private AppCompatActivity mActivity;
    private Fragment mFragment;
    private Fragment mSupportFragment;
    private String mTitle;
    private String mTitleColor;
    private String mBackgroundColor;
    private int mBackStyle;
    private int mRequestCode;
    private boolean mMutilyMode = true;
    private String mAddText;
    private int mIconStyle;
    private String[] mFileTypes;
    private String mNotFoundFiles;
    private int chooseType = Constant.CHOOSE_FILE;//默认选择类型为文件

    /**
     * 绑定Activity
     *
     * @param activity
     * @return
     */
    public FilePicker withActivity(AppCompatActivity activity) {
        this.mActivity = activity;
        return this;
    }

    /**
     * 绑定Fragment
     *
     * @param fragment
     * @return
     */
    public FilePicker withFragment(Fragment fragment) {
        this.mFragment = fragment;
        return this;
    }

    /**
     * 绑定v4包Fragment
     *
     * @param supportFragment
     * @return
     */
    public FilePicker withSupportFragment(Fragment supportFragment) {
        this.mSupportFragment = supportFragment;
        return this;
    }

    /**
     * 选择类型：文件/文件夹
     * Constant.CHOOSE_DIR Constant.CHOOSE_FILE
     *
     * @param chooseType
     * @return
     */
    public FilePicker withChooseType(int chooseType) {
        this.chooseType = chooseType;
        return this;
    }

    /**
     * 设置主标题
     *
     * @param title
     * @return
     */
    public FilePicker withTitle(String title) {
        this.mTitle = title;
        return this;
    }

    /**
     * 设置辩题颜色
     *
     * @param color
     * @return
     */
    public FilePicker withTitleColor(String color) {
        this.mTitleColor = color;
        return this;
    }

    /**
     * 设置背景色
     *
     * @param color
     * @return
     */
    public FilePicker withBackgroundColor(String color) {
        this.mBackgroundColor = color;
        return this;
    }

    /**
     * 请求码
     *
     * @param requestCode
     * @return
     */
    public FilePicker withRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    /**
     * 设置返回图标
     *
     * @param backStyle
     * @return
     */
    public FilePicker withBackIcon(int backStyle) {
        this.mBackStyle = 0;//默认样式
        this.mBackStyle = backStyle;
        return this;
    }

    /**
     * 设置选择模式，默认为true,多选；false为单选
     *
     * @param isMutily
     * @return
     */
    public FilePicker withMutilyMode(boolean isMutily) {
        this.mMutilyMode = isMutily;
        return this;
    }

    /**
     * 设置多选时按钮文字
     *
     * @param text
     * @return
     */
    public FilePicker withAddText(String text) {
        this.mAddText = text;
        return this;
    }

    /**
     * 设置文件夹图标风格
     *
     * @param style
     * @return
     */
    public FilePicker withIconStyle(int style) {
        this.mIconStyle = style;
        return this;
    }

    public FilePicker withFileFilter(String[] arrs) {
        this.mFileTypes = arrs;
        return this;
    }

    /**
     * 没有选中文件时的提示信息
     *
     * @param notFoundFiles
     * @return
     */
    public FilePicker withNotFoundBooks(String notFoundFiles) {
        this.mNotFoundFiles = notFoundFiles;
        return this;
    }

    public void start() {
        if (mActivity == null && mFragment == null && mSupportFragment == null) {
            throw new RuntimeException("You must pass Activity or Fragment by withActivity or withFragment or withSupportFragment method");
        }
        Intent intent = initIntent();
        Bundle bundle = getBundle();
        intent.putExtras(bundle);

        if (mActivity != null) {
            mActivity.startActivityForResult(intent, mRequestCode);
        } else if (mFragment != null) {
            mFragment.startActivityForResult(intent, mRequestCode);
        } else {
            mSupportFragment.startActivityForResult(intent, mRequestCode);
        }
    }


    private Intent initIntent() {
        Intent intent;
        if (mActivity != null) {
            intent = new Intent(mActivity, FilePickerActivity.class);
        } else if (mFragment != null) {
            intent = new Intent(mFragment.getActivity(), FilePickerActivity.class);
        } else {
            intent = new Intent(mSupportFragment.getActivity(), FilePickerActivity.class);
        }
        return intent;
    }

    @NonNull
    private Bundle getBundle() {
        ParamEntity paramEntity = new ParamEntity();
        paramEntity.setTitle(mTitle);
        paramEntity.setTitleColor(mTitleColor);
        paramEntity.setBackgroundColor(mBackgroundColor);
        paramEntity.setBackIcon(mBackStyle);
        paramEntity.setMutilyMode(mMutilyMode);
        paramEntity.setAddText(mAddText);
        paramEntity.setIconStyle(mIconStyle);
        paramEntity.setFileTypes(mFileTypes);
        paramEntity.setNotFoundFiles(mNotFoundFiles);
        paramEntity.setChooseType(chooseType);
        Bundle bundle = new Bundle();
        bundle.putSerializable("param", paramEntity);
        return bundle;
    }
}
