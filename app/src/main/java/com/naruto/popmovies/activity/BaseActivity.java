package com.naruto.popmovies.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.naruto.popmovies.R;

/**
 * @author jelly.
 * @Date 2019-02-01.
 * @Time 22:10.
 */
public class BaseActivity extends AppCompatActivity {

    protected AlertDialog mDialog;
    protected SPUtils mSPUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSPUtils = SPUtils.getInstance(AppUtils.getAppName());
    }

    public void showDialog() {
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(this).create();
        }
        mDialog.show();
        mDialog.setCancelable(false);
        Window window = mDialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.item_alert_dialog);
        }
    }

    public void dismissDialog() {
        mDialog.dismiss();
    }
}
