package com.android.watch.recorder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryListActivity extends Activity{
    public static ListView listView;
    MyBaseAdater myBaseAdater=new MyBaseAdater();
    MainActivity me= new MainActivity();
    private List<Item> list; 
    ImageView imageView;
    TextView name;
    TextView starttime;
    TextView endtime;
    TextView timesitem;
    SeekBar seekBar1;
    private File myPlayFile;
    MediaPlayer mediaPlayer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.historylist);
		listView=(ListView) this.findViewById(R.id.listView1);
	    listView.setAdapter(myBaseAdater);
	    list = new ArrayList<Item>();  
	    starttime=(TextView) this.findViewById(R.id.startime);
	    endtime=(TextView) this.findViewById(R.id.endtime);
	    seekBar1=(SeekBar) this.findViewById(R.id.seekBar1);
	    imageView=(ImageView) this.findViewById(R.id.imageView2);
	    myPlayFile=null;
	    imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent deleteinIntent=new Intent(HistoryListActivity.this,DeleteListActivity.class);
				startActivityForResult(deleteinIntent, 1);
			}
		});
	    myBaseAdater.notifyDataSetChanged();
	    listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//ÿ�ο�ʼʱ�������Ϣ������������
				init();
				TextView textView=(TextView) arg1.findViewById(R.id.name);
				myPlayFile = new File(MainActivity.myRecAudioDir.getAbsolutePath()
						+ File.separator
						+ textView.getText().toString());
			//	Toast.makeText(HistoryListActivity.this, MainActivity.myRecAudioDir.getAbsolutePath(), 3000).show();
				try {
					play(myPlayFile);
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	    
	  //  seekBar1.setOnSeekBarChangeListener(onSeekBarChangeListener);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
			myBaseAdater.notifyDataSetChanged();
		
	}
    
	OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener() {
		// ������
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

			if (mediaPlayer == null) { return; }
			// ���������̶�
			mediaPlayer.seekTo(seekBar.getProgress());
			startTime();
		}

		// ��ʼ��
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			stopTime();
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		}
	};
    
	OnCompletionListener onCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			// mediaPlayer���Ž����¼�[mediaPlayer�����Լ�������]
			init();
		}
	};
	// ���������ʼ��
		private void init() {
			// ���г�ʼ��
			starttime.setText("00:00");
			endtime.setText("00:00");
			seekBar1.setMax(0);
			seekBar1.setProgress(0);
			if (mediaPlayer != null) {
				mediaPlayer.release();
				mediaPlayer = null;
			}
			stopTime();
		}
	protected void play(File f) throws IllegalArgumentException, SecurityException, IOException {
		try {
			// ��ʼ����
			 if (mediaPlayer != null) {
                  mediaPlayer.stop();
               }
			if (mediaPlayer == null) {
				// ��ʼ��������׼���ͼ�������Դ
				//mediaPlayer=MediaPlayer.create(this, f.getAbsolutePath());
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setDataSource(f.getAbsolutePath());
				// ���Ž�������¼�
				mediaPlayer.setOnCompletionListener(onCompletionListener);
//				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
//			     	"mm:ss");
				
				// mediaPlayer.getDuration()��ȡ��ǰ��������ʱ�䡾���롿
//				Date date = new Date(mediaPlayer.getDuration());
//				starttime.setText(simpleDateFormat.format(date)+"s");
//				Toast.makeText(HistoryListActivity.this, simpleDateFormat.format(date)+"", 3000).show();
//				// �����̶ܿ�
//				seekBar1.setMax(mediaPlayer.getDuration());
			}
			mediaPlayer.prepare();
			mediaPlayer.start();
			startTime();
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	// ������
		Timer timer;
		// ִ���߳�����
		class MyTimerTask extends TimerTask {
			@Override
			public void run() {
				// ���߳�
				handler.obtainMessage().sendToTarget();
			}
		}

		private void startTime() {
			if (timer == null) {
				timer = new Timer();
				// 100000�����ִ��һ��
				// timer.schedule(new MyTimerTask(), 100000);
				// ÿ��1����ִ��һ������
				timer.schedule(new MyTimerTask(), 1000, 1000);
			}

		}
		Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				// ÿ��1����������Ҫ�����Ϣ
		        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
				// mediaPlayer.getCurrentPosition()��ȡ��ǰ�����ĵ�ǰʱ�䡾���롿
			//	Date date = new Date(mediaPlayer.getCurrentPosition());
			//	starttime.setText(simpleDateFormat.format(date));
				seekBar1.setProgress(mediaPlayer.getCurrentPosition());
			}
		};
		private void stopTime() {
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
		}
	class MyBaseAdater extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return me.recordFiles.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return me.recordFiles.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			view=LayoutInflater.from(getApplicationContext()).inflate(R.layout.historylist_item, null);
			name=(TextView) view.findViewById(R.id.name);
			name.setText(MainActivity.recordFiles.get(arg0));
//			name.setText((ArrayList)MainActivity.map.get("recordFiles").);
			timesitem=(TextView) view.findViewById(R.id.timesitem);
			//����ʱ��
//			Item item=(Item) getItem(arg0);
//			timesitem.setText(item.times);
			return view;
		}
		
	}
	
}
