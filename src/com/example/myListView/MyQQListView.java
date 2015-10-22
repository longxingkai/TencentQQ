package com.example.myListView;


import com.example.tencentqq.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MyQQListView extends ListView {
	//用户滑动最小距离
	private int touchSlop;
	//是否响应滑动
	private boolean isSlidng;
	//手指按下时的X坐标
	private int xDown;
	//手指按下时y的坐标
	private int yDown;
	//手指移动时X的坐标
	private int xMove;
	//手指移动时y的坐标
	private int yMove;
	//布局填充
	private LayoutInflater mInflater;
	
	private Button mDeleteBt;
	
	private PopupWindow mPopupWindow;
	private int mPopupWindowHeight;
	private int mPopupWindowWidth;
	
	//为删除按钮提供一个回掉接口
	private DelButtonClickListener mListener;
	
	//当前手指触摸的View
	private View mCurrentView;
	//当前手指触摸的位置
	
	private int mCurrentViewPos;
	
	
	
	
	public MyQQListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//初始化
		mInflater = LayoutInflater.from(context);
		//触发事件的最小距离
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		
		View view = mInflater.inflate(R.layout.delete_button, null);
		
		mDeleteBt = (Button) findViewById(R.id.id_item_btn);
		
		mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		//先调下mesure才能拿下宽和高
		mPopupWindow.getContentView().measure(0, 0);
		mPopupWindowWidth = mPopupWindow.getWidth();
		mPopupWindowHeight = mPopupWindow.getHeight();
		
	}

	
	//触摸事件处理
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//得到想x,y的值
		int action = ev.getAction();
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			xDown = x;
			yDown = y;
			//如果当前页pop显示则直接隐藏
			if(mPopupWindow.isShowing()){
				dismissPop();
				return false;
			}
			
			//获取当前按下item的位置
			mCurrentViewPos = pointToPosition(xMove, yDown);
			//获取当前按下的item
			View view = getChildAt(mCurrentViewPos - getFirstVisiblePosition());
			mCurrentView = view;
			break;

		case MotionEvent.ACTION_MOVE:
			xMove = x;
			yMove = y;
			
			int dx = xMove - xDown;
			int dy = yMove - yDown;
			
			//判断是否从右向左移动
			if(xMove < xDown && Math.abs(dx)> touchSlop && Math.abs(dy) < touchSlop ){
				isSlidng = true;
			}
			
			break;
		}
		
		return super.dispatchTouchEvent(ev);
	}
	
	//
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		
		//是否为滑动事件
		if(isSlidng){
			switch (action) {
			case MotionEvent.ACTION_MOVE:  
				  
                int[] location = new int[2];  
                // 获得当前item的位置x与y  
                mCurrentView.getLocationOnScreen(location);  
                // 设置popupWindow的动画  
                mPopupWindow.setAnimationStyle(R.style.popwindow_delete_btn_anim_style);
                mPopupWindow.update();  
                mPopupWindow.showAtLocation(mCurrentView, Gravity.LEFT | Gravity.TOP,  
                        location[0] + mCurrentView.getWidth(), location[1] + mCurrentView.getHeight() / 2  
                                - mPopupWindowHeight / 2);  
                // 设置删除按钮的回调  
                mDeleteBt.setOnClickListener(new OnClickListener()  
                {  
                    @Override  
                    public void onClick(View v)  
                    {  
                        if (mListener != null)  
                        {  
                            mListener.clickHappend(mCurrentViewPos);  
                            mPopupWindow.dismiss();  
                        }  
                    }  
                });   
				break;

			case MotionEvent.ACTION_UP:
				isSlidng = false;
			}
			return true;
		}
		
		return super.onTouchEvent(ev);
	}
	
	//隐藏popwindow
	private void dismissPop() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}
	//回调方法
	
	public void setDelButtonClickListener(DelButtonClickListener listener){
		mListener = listener;
	}
	
	
	//接口
	public interface DelButtonClickListener{
		public void clickHappend(int position);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
