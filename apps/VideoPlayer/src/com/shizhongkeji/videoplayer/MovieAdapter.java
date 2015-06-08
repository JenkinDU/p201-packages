package com.shizhongkeji.videoplayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.LinkedList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shizhongkeji.videoplayer.VideoChooseActivity.MovieInfo;

public class MovieAdapter extends BaseAdapter {
	private Context mContext;
	private LinkedList<MovieInfo> mLinkedList;
	public static HashMap<Integer, Boolean> mMap = null;
	private Uri videoListUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	public MovieAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		mLinkedList = new LinkedList<MovieAdapter.MovieInfo>();
		initData();
		mMap = initMap(mLinkedList.size());
	}

	@Override
	public int getCount() {

		return mLinkedList.size();
	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.list, null);
		}
		MovieInfo movieInfo = mLinkedList.get(position);
		TextView text = (TextView) convertView.findViewById(R.id.text);
		RelativeLayout image = (RelativeLayout) convertView.findViewById(R.id.thumb);
		ImageButton play = (ImageButton) convertView.findViewById(R.id.play);
		CheckBox box = (CheckBox) convertView.findViewById(R.id.box);
		Bitmap bm = getVideoThumbnail(movieInfo.path);
		if(bm != null){
			image.setBackground(new BitmapDrawable(comp(bm)));
		}
		text.setText(movieInfo.displayName);
		if (VideoApplication.isdelete) {
			play.setOnClickListener(null);
			box.setVisibility(View.VISIBLE);
			box.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mMap.get(position)) {
						mMap.put(position, false);
					} else {
						mMap.put(position, true);
					}
				}
			});
			box.setChecked(mMap.get(position));
		} else {
			play.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mContext, VideoPlayerActivity.class);
					MovieInfo movieInfo = mLinkedList.get(position);
					String displayName = movieInfo.displayName;
					String path = movieInfo.path;
					intent.putExtra("displayName", displayName);
					intent.putExtra("path", path);
					mContext.startActivity(intent);
				}
			});
			box.setVisibility(View.GONE);
		}

		return convertView;
	}

	private HashMap<Integer, Boolean> initMap(int count) {
		HashMap<Integer, Boolean> hashMap = new HashMap<Integer, Boolean>();
		for (int i = 0; i < count; i++) {
			hashMap.put(i, false);
		}
		return hashMap;
	}

	public Bitmap getVideoThumbnail(String filePath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		try {
			retriever.setDataSource(filePath);
			bitmap = retriever.getFrameAtTime();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	private Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}
	private Bitmap comp(Bitmap image) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();		
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出	
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;//这里设置高度为800f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}
	public void initData(){
		mLinkedList.clear();
		getVideoFile(mLinkedList, Environment.getExternalStorageDirectory());
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {

			Cursor cursor = mContext.getContentResolver().query(videoListUri,
					new String[] { "_display_name", "_data" }, null, null, null);
			int n = cursor.getCount();
			cursor.moveToFirst();
			LinkedList<MovieInfo> playList2 = new LinkedList<MovieInfo>();
			for (int i = 0; i != n; ++i) {
				MovieInfo mInfo = new MovieInfo();
				mInfo.displayName = cursor.getString(cursor.getColumnIndex("_display_name"));
				mInfo.path = cursor.getString(cursor.getColumnIndex("_data"));
				playList2.add(mInfo);
				cursor.moveToNext();
			}

			if (playList2.size() > mLinkedList.size()) {
				mLinkedList = playList2;
			}
		}
	}
	private void getVideoFile(final LinkedList<MovieInfo> list, File file) {

		file.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				String name = file.getName();
				int i = name.indexOf('.');
				if (i != -1) {
					name = name.substring(i);
					if (name.equalsIgnoreCase(".mp4") || name.equalsIgnoreCase(".3gp")
							|| name.equalsIgnoreCase(".mkv")) {
						MovieInfo mi = new MovieInfo();
						mi.displayName = file.getName();
						mi.path = file.getAbsolutePath();
						list.add(mi);
						return true;
					}
				} else if (file.isDirectory()) {
					getVideoFile(list, file);
				}
				return false;
			}
		});
	}
	public class MovieInfo {
		String displayName;
		String path;
	}
}
