package com.xmu.lxq.aiad.SudokuUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmu.lxq.aiad.R;
import com.xmu.lxq.aiad.activity.SudokuActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * 九宫格适配器
 * @author Administrator
 *
 */
public class DragBaseAdapter extends BaseAdapter {

	private List<HashMap<String, String>> list=null;
	private LayoutInflater mInflater;
	private int mHidePosition = -1;
	private Context mContext;
	
	
	
	
	public DragBaseAdapter(Context mContext) {
		super();
		this.mContext = mContext;
	}
	
	public DragBaseAdapter(Context context, List<HashMap<String, String>> list){
		this(context);
		this.list = list;
		mInflater = LayoutInflater.from(mContext);
		
	}
	
	public void reset(List<HashMap<String, String>> list){
		this.list = list;
	}
	
	public List<HashMap<String, String>> get(){
		return this.list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public HashMap<String, String> getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 由于复用convertView导致某些item消失了，所以这里不复用item，
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.grid_item, parent, false);
		
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
		ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
		
		Map.Entry entry = loopItem(list,position);
		if(entry != null){
			Log.e(TAG,"entry:"+entry);
			//iv.setImageResource((Integer)entry.getValue());
			iv.setImageBitmap(getBitmap(entry.getValue()+""));
			tv.setText((String)entry.getKey());
		}
		
		//iv.setBackgroundResource(imgs[position]);
		//tv.setText(img_text[position]);
		
		if(position == mHidePosition){
			convertView.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
	public Bitmap getBitmap(String filePath){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeFile(filePath+"", options); //此时返回bm为空
		options.inJustDecodeBounds = false;
		//计算缩放比
		int rate = (int)(options.outHeight / (float)100);
		if (rate <= 0)
			rate = 1;
		options.inSampleSize = rate;
		//重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
		bitmap=BitmapFactory.decodeFile(filePath+"",options);
		//File nFile=new File(filePath);
		/*if(nFile.exists()){
			return BitmapFactory.decodeFile(filePath);
		}*/
		return bitmap;
	}

	
	public Map.Entry loopItem(List<HashMap<String, String>> list,int position){
		Map.Entry entry = null;
		HashMap<String, String> loomap = list.get(position);
		Iterator iter = loomap.entrySet().iterator();
		if(iter.hasNext()) {
		    entry = (Map.Entry) iter.next();
		}
		return entry;
	}
	

	public void reorderItems(int oldPosition, int newPosition) {
		HashMap<String, String> temp = list.get(oldPosition);
		if(oldPosition < newPosition){
			for(int i=oldPosition; i<newPosition; i++){
				Collections.swap(list, i, i+1);
			}
		}else if(oldPosition > newPosition){
			for(int i=oldPosition; i>newPosition; i--){
				Collections.swap(list, i, i-1);
			}
		}
		
		list.set(newPosition, temp);
	}

	public void setHideItem(int hidePosition) {
		this.mHidePosition = hidePosition; 
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		        super.notifyDataSetChanged();
		SudokuActivity.list=new ArrayList<>(this.get()) ;
		SudokuActivity.reInitial();
	}

}
