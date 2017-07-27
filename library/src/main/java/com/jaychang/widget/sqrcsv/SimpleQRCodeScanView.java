package com.jaychang.widget.sqrcsv;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.jaychang.widget.sqrcsv.camera.CameraView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleQRCodeScanView extends CameraView implements LifecycleObserver {

  public interface Callback {
    void onTextDecoded(String text);
  }

  private static final int REQUEST_CAMERA_PERMISSION = 1;
  private WeakReference<Activity> activityRef;
  private ImageScanner scanner;
  private Image barcode;
  private Callback callback;
  private AtomicBoolean isDetected;
  private Handler mainThreadHandler;
  private ExecutorService backgroundExecutor;
  private ScanTextTask scanTextTask;
  private CameraView.Callback cameraCallback = new CameraView.Callback() {
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
      if (!isDetected.get()) {
        scanTextTask.setData(data);
        backgroundExecutor.execute(scanTextTask);
      }
    }
  };

  public SimpleQRCodeScanView(Context context) {
    this(context, null);
  }

  public SimpleQRCodeScanView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SimpleQRCodeScanView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    setAutoFocus(true);
    setBackgroundColor(Color.BLACK);
    setAdjustViewBounds(true);
  }

  public void init(@NonNull LifecycleActivity activity, @NonNull final Callback callback) {
    this.activityRef = new WeakReference<Activity>(activity);
    this.scanner = new ImageScanner();
    this.isDetected = new AtomicBoolean(false);
    this.backgroundExecutor = Executors.newSingleThreadExecutor();
    this.mainThreadHandler = new Handler(Looper.getMainLooper());
    this.scanTextTask = new ScanTextTask();
    this.callback = callback;

    scanner.setConfig(0, Config.X_DENSITY, 3);
    scanner.setConfig(0, Config.Y_DENSITY, 3);
    scanner.setConfig(Constant.QRCODE, Config.ENABLE, 1);

    addCallback(cameraCallback);

    activity.getLifecycle().addObserver(this);
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  void onResume() {
    Activity activity = activityRef.get();
    if (activity == null) {
      return;
    }

    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
      == PackageManager.PERMISSION_GRANTED) {
      start();
    } else {
      ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
        REQUEST_CAMERA_PERMISSION);
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  void onPause() {
    stop();
  }

  private String scanText(byte[] data) {
    if (barcode == null) {
      barcode = new Image(getPreviewSize().getWidth(), getPreviewSize().getHeight(), "Y800");
    }

    barcode.setData(data);

    int result = scanner.scanImage(barcode);

    if (result == 0) {
      return "";
    }

    SymbolSet syms = scanner.getResults();
    for (Symbol sym : syms) {
      String symData = sym.getData();
      if (!TextUtils.isEmpty(symData)) {
        return symData;
      }
    }

    return "";
  }

  private class ScanTextTask implements Runnable {

    byte[] data;

    void setData(byte[] data) {
      this.data = data;
    }

    @Override
    public void run() {
      if (isDetected.get()) {
        return;
      }

      final String text = scanText(data);
      if (!TextUtils.isEmpty(text)) {
        isDetected.set(true);
        mainThreadHandler.post(new Runnable() {
          @Override
          public void run() {
            callback.onTextDecoded(text);
          }
        });
      }
    }
  }

}
