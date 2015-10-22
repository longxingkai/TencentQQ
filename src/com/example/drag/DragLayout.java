package com.example.drag;

import com.nineoldandroids.view.ViewHelper;

import android.R.color;
import android.content.Context;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.graphics.PorterDuff;

public class DragLayout extends FrameLayout {

	private ViewDragHelper mdragHelper;
	private ViewGroup mLeftContent;
	private ViewGroup mMainContent;
	private OnDragStatusChangeListener mListener;
	private Status mStatus = Status.Close;
	//����״̬
	public static enum Status{
		Close,Open,Draging
	}
	public interface OnDragStatusChangeListener{
		void onClose();
		void onOpen();
		void onDraging(float percent);
	}
	public void setDragStatusListener(OnDragStatusChangeListener mListener){
		this.mListener = mListener;
	}
	/**
	 * �໬���
	 * @param context
	 */
	public DragLayout(Context context) {
		this(context,null);
		
	}

	public DragLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//��ʼ��
		mdragHelper = ViewDragHelper.create(this,mCallBack);
	}
	ViewDragHelper.Callback mCallBack =  new ViewDragHelper.Callback() {
		//��д�¼�
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			//���ݷ��ؽ��������ǰView�Ƿ���Ա���ק
			//child��ǰ����ק��View
			//pointerId��㴥����ID
			return true;
		}
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			//���񱻴�����View
			super.onViewCaptured(capturedChild, activePointerId);
		}
		
		
		@Override
		public int getViewHorizontalDragRange(View child) {
			//��ȡ�����϶���Χ,������ק�������������ƣ������������϶����ٶ�
			
			return mRange;
		}
		//���ݽ���ֵ������Ҫ�ƶ�����ֵ������
		//��ʱ��û�з����������ƶ�
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			//child��ǰ�϶���view,dxΪ�仯��
			if(child == mMainContent){
				left = fixLeft(left);
			}
			
					return left;
				}
		
		//��viewλ�øı���Ҫ�������飨����״̬�����涯�����ػ���棩
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			int newLeft = left;
			if(changedView == mLeftContent){
				newLeft = fixLeft(mMainContent.getLeft() + dx);
				mLeftContent.layout(0, 0, 0 + mWidth, 0 + mHeight);
				mMainContent.layout(newLeft, 0, newLeft + mWidth, 0 + mHeight);
			}
			//����״ִ̬�ж���
			dispatchDragEvent(newLeft);
		
			//Ϊ�˼��ݵͰ汾�ģ�ÿ���޸�ֵ������ػ�
			invalidate();
		}
		
		//��View���ͷ�֮��������飨ִ�ж�����
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			//View releasedChild, ���ͷŵĺ��� 
			//float xvel, ˮƽ������ٶȣ�����Ϊ+
			//float yvel  ��ֱ������ٶ� ������Ϊ+
			//�жϹرպͿ���
			if(xvel == 0 && mMainContent.getLeft()>mRange/2){
				open();
			}else if(xvel >0){
				open();
			}else if(xvel == 0 && mMainContent.getLeft()<mRange/2){
				close();
			}
		};
		
		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			// TODO Auto-generated method stub
			return super.clampViewPositionVertical(child, top, dy);
		}
		


		
		@Override
		public void onViewDragStateChanged(int state) {
			
			super.onViewDragStateChanged(state);
			
		}
		
		
		
	};
	/**
	 * �رմ���
	 */
	public void close(){
		close(true);
	}
	public void close(boolean isSmooth) {
		int finalLeft = 0;
		if(isSmooth){
			//������Ч����
			if(mdragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
				//����ֵΪtrue��Ҫˢ��ҳ��
				//��������Ϊchild���ڵ�View
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else{
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
		
		
	}
	@Override
	public void computeScroll() {
		super.computeScroll();
		//
		if(mdragHelper.continueSettling(true)){
			//�������true,����Ҫ����ʵ��
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	/**
	 * �򿪴���
	 */
	public void open(){
		open(true);
	}
	public void open(boolean isSmooth) {
		int finalLeft = mRange;
		if(isSmooth){
			//������Ч����
			if(mdragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
				//����ֵΪtrue��Ҫˢ��ҳ��
				//��������Ϊchild���ڵ�View
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else{
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
		
	}
	/**
	 * ���ݷ�Χ�������ֵ
	 * @param left
	 * @return
	 */
	private int fixLeft(int left) {
		if(left <0){
			return 0;
		}else if(left>mRange){
			return mRange;
		}
		return left;
	}
	private void dispatchDragEvent(int newLeft) {
		float percent = (1.0f)*newLeft/mRange;
		//����״ִ̬�лص�
		if(mListener != null){
			mListener.onDraging(percent);
		}
		Status preStatus = mStatus;//֮ǰ��״̬
		mStatus = updateStatus(percent);
		if(mListener != null){
			if(mStatus != preStatus){
				if(mStatus == Status.Close){
					mListener.onClose();
				}else if(mStatus == Status.Open){
					mListener.onOpen();
				}
			}
		}
		//���Զ���
		//mLeftContent ���Ŷ�����ƽ�ƶ�����͸���ȱ仯 
		animViews(percent);
		
		
	}
	private Status updateStatus(float percent) {
		if(percent == 0){
			return Status.Close;
		}else if(percent == 1){
			return Status.Open;
		}
		return Status.Draging;
	}
	private void animViews(float percent) {
		ViewHelper.setScaleX(mLeftContent, 0.5f + 0.5f*percent );
		ViewHelper.setScaleY(mLeftContent, 0.5f + 0.5f*percent );
		
		ViewHelper.setAlpha(mLeftContent, 0.5f + 0.5f*percent);
		//ƽ�ƶ���
		ViewHelper.setTranslationX(mLeftContent, evaluate(percent,-mWidth/2.0f,0));
		
		//����嶯��
		//ViewHelper.setTranslationX(mMainContent, evaluate(percent, 1.0f, 0.8f));
		//ViewHelper.setTranslationY(mMainContent, evaluate(percent, 1.0f, 0.8f));
		ViewHelper.setScaleX(mMainContent, evaluate(percent, 1.0f, 0.8f) );
		ViewHelper.setScaleY(mMainContent, evaluate(percent, 1.0f, 0.8f) );
		
		getBackground().setColorFilter((Integer)evaluateColor(percent, Color.BLACK, Color.TRANSPARENT),PorterDuff.Mode.SRC_OVER);
	}
	private Float evaluate(float fraction,Number startValue, Number endValue){
		float startFloat = startValue.floatValue();
		return startFloat + fraction*(endValue.floatValue() - startFloat);
	}
	/**
	 * ��ɫ�仯
	 * @param fraction
	 * @param startValue
	 * @param endValue
	 * @return
	 */
	private Object evaluateColor(float fraction,Object startValue, Object endValue){
		int startInt = (Integer)startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;
		
		int endInt = (Integer)endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;
		
		return (int)((startA + (int)(fraction*(endA - startA)))<<24 |
				(int)(startR + (int)(fraction*(endR - startR)))<<16|
				(int)(startG + (int)(fraction*(endG - startG)))<<8|
				(int)(startB + (int)(fraction*(endB - startB))));
	}
	private int mHeight;
	private int mWidth;
	private int mRange;
	
	//���ݴ����¼�
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//���ݸ�mdragHelper
		return mdragHelper.shouldInterceptTouchEvent(ev);
		};
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			mdragHelper.processTouchEvent(event);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//���������¼�
		return true;
	}
	@Override
	protected void onFinishInflate() {
		
		super.onFinishInflate();
		//�õ�ҳ��
		if(getChildCount()<2){
			throw new IllegalStateException("must have two children");
		}
		if(!(getChildAt(0) instanceof ViewGroup) && getChildAt(1) instanceof ViewGroup){
			throw new IllegalAccessError("child������ViewGroup������");
		}
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}
	//�����Ļ�Ŀ��
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mHeight = getMeasuredHeight();
		mWidth = getMeasuredWidth();
		mRange = (int) (mWidth*0.6f);
	}
	
}

	
   