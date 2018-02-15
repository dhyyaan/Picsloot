package com.think360.picsloot.imagepicker;

import java.util.ArrayList;

public interface PickerAction {
  void proceedResultAndFinish(ArrayList<String> selected, boolean original, int resultCode);
}