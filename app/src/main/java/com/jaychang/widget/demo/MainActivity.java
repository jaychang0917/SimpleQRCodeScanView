package com.jaychang.widget.demo;

import android.arch.lifecycle.LifecycleActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jaychang.widget.sqrcsv.SimpleQRCodeScanView;

public class MainActivity extends LifecycleActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    SimpleQRCodeScanView scanView = ((SimpleQRCodeScanView) findViewById(R.id.scanView));
    scanView.init(this, new SimpleQRCodeScanView.Callback() {
      @Override
      public void onTextDecoded(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
      }
    });
  }


}
