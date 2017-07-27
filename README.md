# SimpleQRCodeScanView

[![](https://jitpack.io/v/jaychang0917/SimpleQRCodeScanView.svg)](https://jitpack.io/#jaychang0917/SimpleQRCodeScanView)

An QR code scan view using camera1 and ZBar

## Installation
In your project level build.gradle :

```java
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

In your app level build.gradle :

```java
dependencies {
    compile 'com.github.jaychang0917:SimpleQRCodeScanView:{latest_version}'
}
```
[![](https://jitpack.io/v/jaychang0917/SimpleQRCodeScanView.svg)](https://jitpack.io/#jaychang0917/SimpleQRCodeScanView)


## Basic Usage
```xml
<com.jaychang.widget.sqrcsv.SimpleQRCodeScanView
        android:id="@+id/scanView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

```java
SimpleQRCodeScanView scanView = ((SimpleQRCodeScanView) findViewById(R.id.scanView));
scanView.init(this, new SimpleQRCodeScanView.Callback() {
  @Override
  public void onTextDecoded(String text) {
    Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
  }
});
```

## Creadit
- [cameraview](https://github.com/google/cameraview) by Google
- [ZBar Android SDK](http://sourceforge.net/projects/zbar/files/AndroidSDK/)

## License
```
Copyright 2017 Jay Chang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
