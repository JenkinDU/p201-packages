package com.android.watch.recorder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteListActivity extends Activity{
	public ListView listdelete;
	ImageView deleImage;
	Button commit;
	ViewHolder holder;
	Intent intent;
	boolean check;
	boolean bb;
	CheckBox checkBox1;
	ArrayList<Integer> positionList=new ArrayList<Integer>();
	Button cancel;
	private List<Item> list; 
	DeleteBaseAdater deleteBaseAdater=new DeleteBaseAdater();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete);
		DelteClick delteClick=new DelteClick();
		list = new ArrayList<Item>();  
		listdelete=(ListView) this.findViewById(R.id.listdelete);
		commit=(Button) this.findViewById(R.id.deleteCommit);
		cancel=(Button) this.findViewById(R.id.cancelDelete);
		deleImage=(ImageView) this.findViewById(R.id.deleImage);
		deleImage.setOnClickListener(delteClick);
		commit.setOnClickListener(delteClick);
		cancel.setOnClickListener(delteClick);
		check=false;
		
		listdelete.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				positionList.add(arg2);
				Item item = list.get(arg2);   
				item.status = !item.status;// ȡ��
				holder.cb.setChecked(item.status);
				deleteBaseAdater.notifyDataSetChanged();
				Toast.makeText(DeleteListActivity.this, ""+arg2, 3000).show();
				
			}
		});
		init();
		listdelete.setAdapter(deleteBaseAdater);
		
		commit.setOnClickListener(delteClick);
	}
	
	private void init() {
		for (String s : MainActivity.recordFiles) {  
			   list.add(new Item(s, false));  
		}  
	}

	class DelteClick implements View.OnClickListener{
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.deleImage:
				//�޲���
				break;
            case R.id.cancelDelete:
            	intent=new Intent();
        		DeleteListActivity.this.setResult(RESULT_OK, intent);
        		DeleteListActivity.this.finish();
				break;
            case R.id.deleteCommit:
            	//MainActivity.recordFiles.remove(index);
            	if(positionList.size()==0){
            		Toast.makeText(DeleteListActivity.this, "��ѡ���ļ�", 3000).show();
            	}else{
            		for(int i=0;i<positionList.size();i++){
                		File file=new File(MainActivity.recordFiles.get((Integer) positionList.get(i)));
                		file.delete();
                		File file2=new File(MainActivity.myRecAudioDir.getAbsolutePath()+file.getAbsolutePath());
                		Toast.makeText(DeleteListActivity.this, file2+"", 3000).show();
                		file2.delete();
                		MainActivity.recordFiles.remove((int)(positionList.get(i)));
                		list.remove(positionList.get(i));
                	}
            		intent=new Intent();
            		deleteBaseAdater.notifyDataSetChanged();
            		int m=MainActivity.recordFiles.size();
            		DeleteListActivity.this.setResult(RESULT_OK, intent);
            		DeleteListActivity.this.finish();
            	}
				break;
			default:
				break;
			}
			
		}
	}
	class DeleteBaseAdater extends BaseAdapter{
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			
			if(view==null||(holder=(ViewHolder)view.getTag())==null){
				view=LayoutInflater.from(getApplicationContext()).inflate(R.layout.delete_item, null);
				holder=new ViewHolder();
				holder.tv=(TextView) view.findViewById(R.id.deletename);
				holder.cb = (CheckBox) view.findViewById(R.id.checkBox1);
				view.setTag(holder);  
			}
			Item item=(Item) getItem(arg0);
			holder.tv.setText(item.name); 
			holder.cb.setChecked(item.status);
			DeleteBaseAdater.this.notifyDataSetChanged();
			return view;
		}
		
	}
	class Item {  
		        public String name;  
		        public boolean status = true;  
		        public Item(String name, boolean b) {  
		           this.name = name;  
		           this.status = b;  
		       }  
	}
	
		private void deleteFiles(){
			for(int i=0;i<MainActivity.lists.size();i++){
				File file=new File((String) MainActivity.lists.get(i));
				if(file.exists()){
					file.delete();
				}
			}
//			//������ͣ�󣬼���¼������һ����Ƶ�ļ�
//			if(isAddLastRecord){
//				MainActivity.myRecAudioFile.delete();
//			}
		}
}
