package com.think360.picsloot.imagepicker;

import java.util.ArrayList;

import android.net.Uri;

public interface PhotoLoadListener {
  void onLoadComplete(ArrayList<Uri> photoUris);

  void onLoadError();
}
