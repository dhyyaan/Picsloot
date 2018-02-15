package com.think360.picsloot.imagepicker.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.think360.picsloot.R;
import com.think360.picsloot.activities.PicsLootActivity;
import com.think360.picsloot.api.AppController;
import com.think360.picsloot.imagepicker.PhotoLoadListener;
import com.think360.picsloot.imagepicker.SImagePicker;
import com.think360.picsloot.imagepicker.model.Photo;
import com.think360.picsloot.imagepicker.util.SystemUtil;
import com.think360.picsloot.imagepicker.util.UriUtil;
import com.think360.picsloot.imagepicker.widget.SquareRelativeLayout;
import com.think360.picsloot.util.ConnectivityReceiver;

/**
 * Created by Martin on 2017/1/17.
 */
public class PhotoAdapter extends BaseRecycleCursorAdapter<RecyclerView.ViewHolder> {

  private final LayoutInflater layoutInflater;
  private final int photoSize;
  private ArrayList<String> selectedPhoto;
  private OnPhotoActionListener actionListener;

  private int maxCount = 1;
  private int mode;


  public PhotoAdapter(Context context, Cursor c, @SImagePicker.PickMode int mode, int rowCount) {
    super(context, c);

    this.layoutInflater = LayoutInflater.from(context);
    this.photoSize = SystemUtil.displaySize.x / rowCount;
    this.selectedPhoto = new ArrayList<>();
    this.mode = mode;
  }

  public void setActionListener(OnPhotoActionListener actionListener) {
    this.actionListener = actionListener;
  }

  @Override
  public void onBindViewHolder(final RecyclerView.ViewHolder originHolder, Cursor cursor) {

    final PhotoViewHolder holder = (PhotoViewHolder) originHolder;

    final Photo photo = Photo.fromCursor(cursor);

    final int position = cursor.getPosition();
    SImagePicker.getPickerConfig().getImageLoader().bindImage(holder.photoCell.photo,
        new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME)
            .path(photo.getFilePath()).build(), photoSize, photoSize);
    holder.photoCell.photo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        actionListener.onPreview(position, photo, originHolder.itemView);
      }
    });
    if (mode == SImagePicker.MODE_IMAGE) {
      holder.photoCell.checkBox.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          PhotoAdapter.this.onCheckStateChange(holder.photoCell, photo);
        }
      });
    } else if (mode == SImagePicker.MODE_AVATAR) {
      holder.photoCell.checkBox.setVisibility(View.INVISIBLE);
    }

    if (selectedPhoto.contains(photo.getFilePath())) {
      holder.photoCell.checkBox
          .setText(String.valueOf(selectedPhoto.indexOf(photo.getFilePath()) + 1));
      holder.photoCell.checkBox.setChecked(true, false);
      holder.photoCell.photo.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
    } else {
      holder.photoCell.checkBox.setChecked(false, false);
      holder.photoCell.photo.clearColorFilter();
    }

    holder.photoCell.setTag(photo.getFilePath());
  }

  @Override
  public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = layoutInflater.inflate(R.layout.picker_photo_item, parent, false);
    SquareRelativeLayout photoCell = (SquareRelativeLayout) itemView.findViewById(R.id.photo_cell);
    photoCell.setPhotoView(SImagePicker.getPickerConfig().getImageLoader()
        .createImageView(parent.getContext()));
    return new PhotoViewHolder(itemView);
  }


  static class PhotoViewHolder extends RecyclerView.ViewHolder {

    SquareRelativeLayout photoCell;

    public PhotoViewHolder(View itemView) {
      super(itemView);
      photoCell = (SquareRelativeLayout) itemView.findViewById(R.id.photo_cell);
    }
  }

  private void onCheckStateChange(SquareRelativeLayout photoCell, Photo photo) {
    if (isCountOver() && !selectedPhoto.contains(photo.getFilePath())) {
      showMaxDialog(mContext, maxCount);
      return;
    }
    if (selectedPhoto.contains(photo.getFilePath())) {
      selectedPhoto.remove(photo.getFilePath());
      photoCell.checkBox.setChecked(false, true);
      photoCell.photo.clearColorFilter();
      if (actionListener != null) {
        actionListener.onDeselect(photo.getFilePath());
      }
    } else {
      selectedPhoto.add(photo.getFilePath());
      photoCell.checkBox.setText(String.valueOf(selectedPhoto.size()));
      photoCell.checkBox.setChecked(true, true);
      photoCell.photo.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
      if (actionListener != null) {
        actionListener.onSelect(photo.getFilePath());
      }
    }
  }


  public boolean isCountOver() {

    return  selectedPhoto.size() >= maxCount;
  }
//selectedPhoto.size()== minCount &&
  public interface OnPhotoActionListener {
    void onSelect(String filePath);

    void onDeselect(String filePath);

    void onPreview(int position, Photo photo, View view);
  }

  public ArrayList<String> getSelectedPhoto() {
    return selectedPhoto;
  }

  public void setSelectedPhoto(ArrayList<String> selectedPhoto) {
    this.selectedPhoto = selectedPhoto;
    notifyDataSetChanged();
  }

  public void getAllPhoto(final PhotoLoadListener photoLoadListener) {
    new AsyncTask<Void, Void, Void>() {

      @Override
      protected Void doInBackground(Void... params) {
        try {
          final ArrayList<Uri> result = new ArrayList<>();
          mCursor.moveToPosition(-1);
          while (mCursor.moveToNext()) {
            result.add(new Uri.Builder().scheme(UriUtil.LOCAL_FILE_SCHEME).path(
                Photo.fromCursor(mCursor).getFilePath()).build());
          }
          SystemUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
              if (photoLoadListener != null) {
                photoLoadListener.onLoadComplete(result);
              }
            }
          });
        } catch (Exception e) {
          SystemUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
              if (photoLoadListener != null) {
                photoLoadListener.onLoadError();
              }
            }
          });
        }
        return null;
      }
    }.execute();
  }


  public void setMaxCount(int maxCount) {
    this.maxCount = maxCount;
  }


  private  void showMaxDialog(final Context context, int max) {
    if(max == 5){


  new AlertDialog.Builder(context)
          .setMessage("If you want to select 6th image then share this app")
          .setNegativeButton("No",
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                    }
                  })
          .setPositiveButton("Yes",
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      if(ConnectivityReceiver.isConnected()){

                        dialog.dismiss();

                        PicsLootActivity.Companion.getPicsLootActivity().share();

                        setMaxCount(6);
                        AppController.getSharedPref().edit().putString("eligible_images","6").apply();
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        //sharingIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "App Link");
                        sharingIntent.setType("text/plain");
                        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));

                      }else{

                        Toast toast = Toast.makeText(AppController.getAppContext(),"No internet connection!", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                      }

                    }
                  }).show();



    }else{
      new AlertDialog.Builder(context)
              .setMessage(context.getResources().getString(R.string.error_maximun_nine_photos, max))
              .setPositiveButton(R.string.general_ok,
                      new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                        }
                      }).show();
    }
    }

}
