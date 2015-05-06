package com.android.watch.recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity{
	int startCount=1;
    int second=0;
	int minute=0;
	
	/**�ļ�����**/
	private boolean sdcardExit;
	public static File myRecAudioFile;
	//**�Ƿ���ͣ��־λ**/
	private boolean isPause;
	/**����ͣ״̬��**/
	private boolean inThePause;
	/**¼������·��**/
	public static File myRecAudioDir;
	private  final String SUFFIX=".amr";
	/**�Ƿ�ֹͣ¼��**/
	private boolean isStopRecord;
	/**��¼��Ҫ�ϳɵļ���amr�����ļ�**/
	public static ArrayList<String> lists;
	private ArrayList<String> listTimes;
	public static Map map;
	/**�����Ƶ�ļ��б�**/
	public static ArrayList<String> recordFiles;
	public static ArrayList<Item> recordFile;
	private ArrayAdapter<String> adapter;
	private MediaRecorder mMediaRecorder;
	MediaPlayer mediaPlayer;
	private String length1 = null;
    ImageView imageView;
    ImageView menu;
    TextView times;
    Button cancel;
    Button save;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recorder_main);
		MyClick myClick=new MyClick();
		mediaPlayer = new MediaPlayer();
		lists=new ArrayList<String>();
		map=new HashMap();
		listTimes=new ArrayList<String>();
		imageView=(ImageView) this.findViewById(R.id.recorder);
		imageView.setOnClickListener(myClick);
		times=(TextView) this.findViewById(R.id.times);
		cancel=(Button) this.findViewById(R.id.cancel);
		save=(Button) this.findViewById(R.id.save);
		menu=(ImageView) this.findViewById(R.id.menu);
		menu.setOnClickListener(myClick);
		cancel.setOnClickListener(myClick);
		save.setOnClickListener(myClick);
		isPause=false;
		inThePause=false;
		// �ж�sd Card�Ƿ����
		sdcardExit = Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
				// ȡ��sd card·����Ϊ¼���ļ���λ��
				if (sdcardExit){
					String pathStr = Environment.getExternalStorageDirectory().getAbsolutePath()+"/YYT";
					//String pathStr = "/storage/sdcard1/MIUI/"+"/YY";
					myRecAudioDir= new File(pathStr);
					if(!myRecAudioDir.exists()){
						myRecAudioDir.mkdirs();
						Log.v("¼��", "����¼���ļ���" + myRecAudioDir.exists());
					}
//					Environment.getExternalStorageDirectory().getPath() + "/" + PREFIX + "/";
				}
				// ȡ��sd card Ŀ¼���.arm�ļ�
				getRecordFiles();
//				map.put("recordFiles", recordFiles);
//				map.put("listTimes", listTimes);
//				adapter = new ArrayAdapter<String>(this,
//						android.R.layout.simple_list_item_1, recordFiles);
	}

	class MyClick implements View.OnClickListener{
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.recorder:
				if(isPause){
					//��ǰ����¼�����ļ�����ȫ��
					imageView.setImageResource(R.drawable.startrecorder);
					lists.add(myRecAudioFile.getPath());
					recorderStop();
					//start();
					//				buttonpause.setText("����¼��");
					//��ʱֹͣ
//					timer.cancel();
					isPause=false; 
				}
				//����¼���������ͣ,����¼��״̬Ϊ��ͣ
				else{
					imageView.setImageResource(R.drawable.endre);
					start();
					isPause=true;
				}
//				}
				startCount++;
				break;
			case R.id.save:
				//timer.cancel();
				// TODO Auto-generated method stub
				//����д��ͣ����� �ļ�������list���� �����ϳ�����
				if(!isPause){
					//����ͣ״̬���½�����,����list�Ϳ�����
					getInputCollection(lists, false);
					isPause=true;
					inThePause=false;
				//	adapter.add(myRecAudioFile.getName());
				}
				else{
					lists.add(myRecAudioFile.getPath());
					recorderStop();
					getInputCollection(lists, true);
				}
				Toast.makeText(MainActivity.this, "����ɹ�", 3000).show();
				minute=0;
				second=0;
				times.setText(00+":"+00);
				isStopRecord = true;
				imageView.setImageResource(R.drawable.startrecorder);
				save.setEnabled(false);
				cancel.setEnabled(false);
				isPause=false;
				break;
			case R.id.cancel:
				recorderStop();
				deleteListRecord(isPause);
				minute=0;
				second=0;
				times.setText(00+":"+00);
				imageView.setImageResource(R.drawable.startrecorder);
				save.setEnabled(false);
				cancel.setEnabled(false);
				isPause=false;
				break;
			case R.id.menu:
				Intent intent=new Intent(MainActivity.this, HistoryListActivity.class);
				intent.putExtra("recordFiles", recordFiles);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	}
	/**��ʱ��**/
	Timer timer;
	public void start() {
		 TimerTask timerTask=new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				second++;
				if(second>=60){
					second=0;
					minute++;
				}
				handler.sendEmptyMessage(0);
			}
		};
		 timer=new Timer();
		 timer.schedule(timerTask, 0,1000);
		try {
			if (!sdcardExit) {
				Toast.makeText(MainActivity.this, "�����SD card",
						Toast.LENGTH_LONG).show();
				return;
			}
			String mMinute1=getTime();
			Toast.makeText(MainActivity.this, "��ǰʱ����:"+mMinute1,Toast.LENGTH_LONG).show();
			// ������Ƶ�ļ�
//			myRecAudioFile = File.createTempFile(mMinute1, ".amr",
//					myRecAudioDir);
			myRecAudioFile=new File(myRecAudioDir,mMinute1+SUFFIX);
			mMediaRecorder = new MediaRecorder();
			// ����¼��Ϊ��˷�
			mMediaRecorder
					.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			mMediaRecorder
					.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			//¼���ļ���������
			mMediaRecorder.setOutputFile(myRecAudioFile
					.getAbsolutePath());
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			cancel.setEnabled(true);
			save.setEnabled(true);
			isStopRecord = false;
		} catch (IOException e) {
			e.printStackTrace();

		}
	}
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String minutes = null,seconds=null;
			if(minute<10){
				minutes="0"+minute;
			}
			if(second<10){
				seconds="0"+second;
			}
			times.setText(minutes+":"+seconds);
		}
	};
	private String getTime(){
		SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyyMMddHHmmss");      
		Date  curDate=new  Date(System.currentTimeMillis());//��ȡ��ǰʱ��      
		String   time   =   formatter.format(curDate);  
		System.out.println("��ǰʱ��");
		return time;
		}
	protected void recorderStop() {
		if (mMediaRecorder != null) {
			// ֹͣ¼��
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
		}
		timer.cancel();
	}
	/**
	 *  @param isAddLastRecord �Ƿ���Ҫ���list֮�������¼����һ��ϲ�
	 *  @return ���ϲ��������ַ�����
	 */
	public  void getInputCollection(List list,boolean isAddLastRecord){
		String	mMinute1=getTime();
		Toast.makeText(MainActivity.this, "��ǰʱ����:"+mMinute1,Toast.LENGTH_LONG).show();
		// ������Ƶ�ļ�,�ϲ����ļ�������
		File file1=new File(myRecAudioDir,mMinute1+SUFFIX);
		FileOutputStream fileOutputStream = null;
		if(!file1.exists()){
			try {
				file1.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			fileOutputStream=new FileOutputStream(file1);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//list����Ϊ��ͣ¼�� �������� ����¼���ļ������֣��м伸���ļ��ļ�ȥǰ���6���ֽ�ͷ�ļ�
		
		for(int i=0;i<list.size();i++){
			File file=new File((String) list.get(i));
			try {
				FileInputStream fileInputStream=new FileInputStream(file);
				byte  []myByte=new byte[fileInputStream.available()];
				//�ļ�����
				int length = myByte.length;
				//ͷ�ļ�
				if(i==0){
						while(fileInputStream.read(myByte)!=-1){
								fileOutputStream.write(myByte, 0,length);
							}
						}
				//֮����ļ���ȥ��ͷ�ļ��Ϳ�����
				else{
					while(fileInputStream.read(myByte)!=-1){
						
						fileOutputStream.write(myByte, 6, length-6);
					}
				}
				fileOutputStream.flush();
				fileInputStream.close();
				System.out.println("�ϳ��ļ����ȣ�"+file1.length());
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
		//������ر���
		try {
			fileOutputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			//�ϳ�һ���ļ���ɾ��֮ǰ��ͣ¼�������������ϳ��ļ�
			deleteListRecord(isAddLastRecord);
			//
//			adapter.add(file1.getName());
//			listTimes.add(second+"s");
	}
	private void deleteListRecord(boolean isAddLastRecord){
		for(int i=0;i<lists.size();i++){
			File file=new File((String) lists.get(i));
			if(file.exists()){
				file.delete();
			}
		}
		//������ͣ�󣬼���¼������һ����Ƶ�ļ�
		if(isAddLastRecord){
			myRecAudioFile.delete();
		}
	}
	/**
	 * ��ȡĿ¼�µ�������Ƶ�ļ�
	 */
	private void getRecordFiles() {
		// TODO Auto-generated method stub
		recordFiles = new ArrayList<String>();
		recordFile = new ArrayList<Item>();
		if (sdcardExit) {
			File files[] = myRecAudioDir.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().indexOf(".") >= 0) { // ֻȡ.amr �ļ�
						String fileS = files[i].getName().substring(
								files[i].getName().indexOf("."));
						if (fileS.toLowerCase().equals(".mp3")
								|| fileS.toLowerCase().equals(".amr")
								|| fileS.toLowerCase().equals(".mp4"))
							recordFiles.add(files[i].getName());
							try {
								mediaPlayer.setDataSource(files[i].getAbsolutePath());
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SecurityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						recordFile.add(new Item(files[i].getName(), mediaPlayer.getDuration()+""));
					}
				}
			}
		}

	}
}
