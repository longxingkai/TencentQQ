package com.example.tencentqq;

import com.example.drag.DragLayout;
import com.example.drag.DragLayout.OnDragStatusChangeListener;
import com.example.myListView.MyQQListView;
import com.example.myListView.MyQQListView.DelButtonClickListener;
import com.example.utilty.Cheeses;
import com.nineoldandroids.view.ViewHelper;

import android.support.v7.app.ActionBarActivity;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private ListView mLeftList;
	private MyQQListView mMainList;
	private ImageView img;
	private ArrayAdapter<String> mAdapter;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DragLayout 	mDragLayout = (DragLayout) findViewById(R.id.dl);
        img = (ImageView) findViewById(R.id.img);
        
        mDragLayout.setDragStatusListener(new OnDragStatusChangeListener() {
			public void onOpen() {	
				//Toast.makeText(getApplicationContext(), "OPEN", 1).show();
			}
			public void onDraging(float percent) {
				//图标透明度改变
				ViewHelper.setAlpha(img, 1.0f-percent);	
			}
			public void onClose() {
				//Toast.makeText(getApplicationContext(), "CLOSE", 1).show();
				ObjectAnimator animator = ObjectAnimator.ofFloat(img, "translation", 15.0f);	
				animator.setInterpolator(new CycleInterpolator(4));
				animator.setDuration(500);
				animator.start();
			}
		});
        
        mLeftList = (ListView) findViewById(R.id.lv_left);
        mLeftList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, Cheeses.QQS));
        
        mMainList = (MyQQListView) findViewById(R.id.lv_right);
        mMainList.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, Cheeses.NAMES)
        		{
        			@Override
        			public View getView(int position, View convertView,
        					ViewGroup parent) {
        				// TODO Auto-generated method stub
        				View view = super.getView(position, convertView, parent);
        				TextView mText = (TextView)view;
        				mText.setTextColor(Color.BLACK);
        				return view;
        			}
        		});
        mAdapter = (ArrayAdapter<String>) mMainList.getAdapter();
        mMainList.setDelButtonClickListener(new DelButtonClickListener(){

			@Override
			public void clickHappend(int position) {
				//Toast.makeText(MainActivity.this, position + " : " + mAdapter.getItem(position), 1).show();  
                mAdapter.remove(mAdapter.getItem(position));
				
			}
        	
        });
        
        mMainList.setOnItemClickListener(new OnItemClickListener()  
        {  

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// Toast.makeText(MainActivity.this, position + " : " + mAdapter.getItem(position),10).show();
				
			}  
        });  
        
    }

}
