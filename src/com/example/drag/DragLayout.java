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
	//三种状态
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
	 * 侧滑面板
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
		//初始化
		mdragHelper = ViewDragHelper.create(this,mCallBack);
	}
	ViewDragHelper.Callback mCallBack =  new ViewDragHelper.Callback() {
		//重写事件
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			//根据返回结果决定当前View是否可以被拖拽
			//child当前被拖拽的View
			//pointerId多点触摸的ID
			return true;
		}
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			//捕获被触摸的View
			super.onViewCaptured(capturedChild, activePointerId);
		}
		
		
		@Override
		public int getViewHorizontalDragRange(View child) {
			//获取横向拖动范围,不对拖拽进行真正的限制，仅仅决定了拖动的速度
			
			return mRange;
		}
		//根据建议值修正将要移动到的值（横向）
		//此时还没有发生真正的移动
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			//child当前拖动的view,dx为变化量
			if(child == mMainContent){
				left = fixLeft(left);
			}
			
					return left;
				}
		
		//当view位置改变是要做的事情（更新状态，伴随动画，重绘界面）
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
			//更新状态执行动画
			dispatchDragEvent(newLeft);
		
			//为了兼容低版本的，每次修改值后进行重绘
			invalidate();
		}
		
		//当View被释放之后处理的事情（执行动画）
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			//View releasedChild, 被释放的孩子 
			//float xvel, 水平方向的速度，向右为+
			//float yvel  垂直方向的速度 ，向下为+
			//判断关闭和开启
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
	 * 关闭窗口
	 */
	public void close(){
		close(true);
	}
	public void close(boolean isSmooth) {
		int finalLeft = 0;
		if(isSmooth){
			//带动画效果的
			if(mdragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
				//返回值为true需要刷新页面
				//参数传递为child所在的View
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
			//如果返回true,还需要继续实行
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	/**
	 * 打开窗口
	 */
	public void open(){
		open(true);
	}
	public void open(boolean isSmooth) {
		int finalLeft = mRange;
		if(isSmooth){
			//带动画效果的
			if(mdragHelper.smoothSlideViewTo(mMainContent, finalLeft, 0)){
				//返回值为true需要刷新页面
				//参数传递为child所在的View
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else{
			mMainContent.layout(finalLeft, 0, finalLeft + mWidth, 0 + mHeight);
		}
		
	}
	/**
	 * 根据范围修正左边值
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
		//更新状态执行回调
		if(mListener != null){
			mListener.onDraging(percent);
		}
		Status preStatus = mStatus;//之前的状态
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
		//属性动画
		//mLeftContent 缩放动画，平移动画，透明度变化 
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
		//平移动画
		ViewHelper.setTranslationX(mLeftContent, evaluate(percent,-mWidth/2.0f,0));
		
		//主面板动画
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
	 * 颜色变化
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
	
	//传递触摸事件
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//传递给mdragHelper
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
		//持续接收事件
		return true;
	}
	@Override
	protected void onFinishInflate() {
		
		super.onFinishInflate();
		//得到页面
		if(getChildCount()<2){
			throw new IllegalStateException("must have two children");
		}
		if(!(getChildAt(0) instanceof ViewGroup) && getChildAt(1) instanceof ViewGroup){
			throw new IllegalAccessError("child必须是ViewGroup的子类");
		}
		mLeftContent = (ViewGroup) getChildAt(0);
		mMainContent = (ViewGroup) getChildAt(1);
	}
	//获得屏幕的宽高
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mHeight = getMeasuredHeight();
		mWidth = getMeasuredWidth();
		mRange = (int) (mWidth*0.6f);
	}
	
}

	
   