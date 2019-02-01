package com.naruto.popmovies.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.naruto.popmovies.R;

/**
 * fragment基类
 *
 * @author jelly.
 * @Date 2019-01-28.
 * @Time 10:31.
 */
public class BaseFragment extends Fragment {

    protected AlertDialog mDialog;
    protected SPUtils mSPUtils;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSPUtils = SPUtils.getInstance(AppUtils.getAppName());
    }

    public void showDialog() {
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(getContext()).create();
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
