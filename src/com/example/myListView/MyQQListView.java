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
	//�û�������С����
	private int touchSlop;
	//�Ƿ���Ӧ����
	private boolean isSlidng;
	//��ָ����ʱ��X����
	private int xDown;
	//��ָ����ʱy������
	private int yDown;
	//��ָ�ƶ�ʱX������
	private int xMove;
	//��ָ�ƶ�ʱy������
	private int yMove;
	//�������
	private LayoutInflater mInflater;
	
	private Button mDeleteBt;
	
	private PopupWindow mPopupWindow;
	private int mPopupWindowHeight;
	private int mPopupWindowWidth;
	
	//Ϊɾ����ť�ṩһ���ص��ӿ�
	private DelButtonClickListener mListener;
	
	//��ǰ��ָ������View
	private View mCurrentView;
	//��ǰ��ָ������λ��
	
	private int mCurrentViewPos;
	
	
	
	
	public MyQQListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//��ʼ��
		mInflater = LayoutInflater.from(context);
		//�����¼�����С����
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		
		View view = mInflater.inflate(R.layout.delete_button, null);
		
		mDeleteBt = (Button) findViewById(R.id.id_item_btn);
		
		mPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		//�ȵ���mesure�������¿�͸�
		mPopupWindow.getContentView().measure(0, 0);
		mPopupWindowWidth = mPopupWindow.getWidth();
		mPopupWindowHeight = mPopupWindow.getHeight();
		
	}

	
	//�����¼�����
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//�õ���x,y��ֵ
		int action = ev.getAction();
		int x = (int) ev.getX();
		int y = (int) ev.getY();
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			xDown = x;
			yDown = y;
			//�����ǰҳpop��ʾ��ֱ������
			if(mPopupWindow.isShowing()){
				dismissPop();
				return false;
			}
			
			//��ȡ��ǰ����item��λ��
			mCurrentViewPos = pointToPosition(xMove, yDown);
			//��ȡ��ǰ���µ�item
			View view = getChildAt(mCurrentViewPos - getFirstVisiblePosition());
			mCurrentView = view;
			break;

		case MotionEvent.ACTION_MOVE:
			xMove = x;
			yMove = y;
			
			int dx = xMove - xDown;
			int dy = yMove - yDown;
			
			//�ж��Ƿ���������ƶ�
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
		
		//�Ƿ�Ϊ�����¼�
		if(isSlidng){
			switch (action) {
			case MotionEvent.ACTION_MOVE:  
				  
                int[] location = new int[2];  
                // ��õ�ǰitem��λ��x��y  
                mCurrentView.getLocationOnScreen(location);  
                // ����popupWindow�Ķ���  
                mPopupWindow.setAnimationStyle(R.style.popwindow_delete_btn_anim_style);
                mPopupWindow.update();  
                mPopupWindow.showAtLocation(mCurrentView, Gravity.LEFT | Gravity.TOP,  
                        location[0] + mCurrentView.getWidth(), location[1] + mCurrentView.getHeight() / 2  
                                - mPopupWindowHeight / 2);  
                // ����ɾ����ť�Ļص�  
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
	
	//����popwindow
	private void dismissPop() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}
	//�ص�����
	
	public void setDelButtonClickListener(DelButtonClickListener listener){
		mListener = listener;
	}
	
	
	//�ӿ�
	public interface DelButtonClickListener{
		public void clickHappend(int position);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
