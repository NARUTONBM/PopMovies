package com.naruto.popmovies.fragment;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.view.Window;

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

    protected void showAlertDialog() {
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

    protected void dismissDialog() {
        mDialog.dismiss();
    }
}
