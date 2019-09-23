package com.aaron.filebrowserdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aaron.filepickerlibrary.FilePicker;
import com.aaron.filepickerlibrary.utils.Constant;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_FILE_PATH = 1000;      // 请求文件夹路径
    private static final int REQUEST_DIR_PATH = 2000;      // 请求文件夹路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_pickdir).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showChooseOutputPathDialog();
                    }
                }
        );
        findViewById(R.id.btn_pickfile).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showChooseInputPathDialog();
                    }
                }
        );
    }

    private void showChooseOutputPathDialog() {
        new FilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(REQUEST_DIR_PATH)
                .withBackgroundColor("#008577")
                .withTitle("请选择导出文件夹")
                .withIconStyle(Constant.ICON_STYLE_BLUE)
                .withChooseType(Constant.CHOOSE_DIR)
                .withMutilyMode(false)
                .start();
    }

    private void showChooseInputPathDialog() {
        new FilePicker()
                .withActivity(MainActivity.this)
                .withRequestCode(REQUEST_FILE_PATH)
                .withTitle("请选择导入的文件")
                .withIconStyle(Constant.ICON_STYLE_BLUE)
                .withChooseType(Constant.CHOOSE_FILE)
                .withMutilyMode(false)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            List<String> list = data.getStringArrayListExtra("paths");
            final String path = list.get(0);
            if (requestCode == REQUEST_DIR_PATH) {
                Toast.makeText(this,"文件夹路径："+path,Toast.LENGTH_SHORT).show();
            }
            if (requestCode == REQUEST_FILE_PATH) {
                Toast.makeText(this,"文件路径："+path,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
