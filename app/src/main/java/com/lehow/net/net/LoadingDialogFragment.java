package com.lehow.net.net;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.lehow.net.R;

/**
 * desc:
 * author: luoh17
 * time: 2018/7/14 17:29
 */
public class LoadingDialogFragment extends DialogFragment {

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
    setCancelable(false);
    return inflater.inflate(R.layout.progressbar_loading, container, false);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    if (getDialog() == null) {  // Returns mDialog
      // Tells DialogFragment to not use the fragment as a dialog, and so won't try to use mDialog
      setShowsDialog(false);
    }
    super.onActivityCreated(savedInstanceState);  // Will now complete and not crash
  }
}
