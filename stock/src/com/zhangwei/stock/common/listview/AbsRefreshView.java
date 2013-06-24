/**
 * 
 */
package com.zhangwei.stock.common.listview;

import java.util.ArrayList;
import java.util.Date;

import com.zhangwei.stock.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-4-23
 */
public abstract class AbsRefreshView<T> extends ScrollView {
	private static final String TAG = "PullToRefreshView";
	private static final boolean ISDEBUG = false;
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private int headContentHeight;
	private int footContentHeight;

	private LinearLayout innerLayout;
	private LinearLayout headView;
	private LinearLayout footView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView fArrowImageView;
	private ProgressBar fProgressBar;
	private TextView fTipsTextview;
	private TextView fLastUpdatedTextView;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;
	private boolean headorfooter = false;
	private int state;
	private boolean isBack;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private boolean canReturn;
	private boolean isRecored;
	private int startY;
	private Boolean isLastIndex = false;

	protected LayoutInflater inflater;
	protected Context context;

	public AbsRefreshView(Context context) {
		super(context);
		init(context);
	}

	public AbsRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	protected DisplayMetrics dm = new DisplayMetrics();
	
	public int getScreenWidth(){
		return dm.widthPixels;
	}
	
	public int getScreenHight(){
		return dm.heightPixels;
	}

	private void init(Context context) {
		this.context = context;
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		inflater = LayoutInflater.from(context);
		innerLayout = new LinearLayout(context);
		innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		innerLayout.setOrientation(LinearLayout.VERTICAL);

		headView = (LinearLayout) inflater.inflate(R.layout.mylistview_head,
				null);
		footView = (LinearLayout) inflater.inflate(R.layout.mylistview_footer,
				null);
		addHeader(headView);
		addFooter(footView);
		addView(innerLayout);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
		canReturn = false;
	}

	protected void addHeader(LinearLayout headView) {
		showHeader=true;
		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);
		measureView(headView);

		headContentHeight = headView.getMeasuredHeight();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();
		innerLayout.addView(headView);
	}

	protected void addFooter(LinearLayout footView) {
		showFooter=true;
		fArrowImageView = (ImageView) footView
				.findViewById(R.id.footer_arrowImageView);
		fProgressBar = (ProgressBar) footView
				.findViewById(R.id.footer_progressBar);
		fTipsTextview = (TextView) footView
				.findViewById(R.id.footer_tipsTextView);
		fLastUpdatedTextView = (TextView) footView
				.findViewById(R.id.footer_lastUpdatedTextView);
		measureView(footView);
		footContentHeight = footView.getMeasuredHeight();
		footView.setPadding(0, 0, 0, -1 * footContentHeight);
		footView.invalidate();
		innerLayout.addView(footView);
	}

	private boolean showFooter;
	public void removeFooter() {
		showFooter=false;
		innerLayout.removeView(footView);
		innerLayout.invalidate();
	}
	
	private boolean showHeader;
	public void removeHeader(){
		showHeader=false;
		innerLayout.removeView(headView);
		innerLayout.invalidate();
	}
	
	public void setRefreshable(boolean refreshable){
		isRefreshable=refreshable;
	}
	
	private boolean isProcessRefresh;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// 什么都不做
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						if (headorfooter) {
							changeFooterViewByState();
						} else
							changeHeaderViewByState();
						if (ISDEBUG)
							Log.i(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						// 向下拉
						if (headorfooter) {
							changeFooterViewByState();
							onMore();
						} else {
							changeHeaderViewByState();
							onRefresh();
						}
						if (ISDEBUG)
							Log.i(TAG, "由松开刷新状态，到done状态");
					}
				}
				isRecored = false;
				isBack = false;
				isProcessRefresh=false;
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (tempY > startY) {// pull down
					headorfooter = false;
					if(getScrollY() == 0)
						isProcessRefresh=true;
					if (isProcessRefresh&&showHeader&&state != REFRESHING && isRecored && state != LOADING) {
						// 可以松手去刷新了
						if (state == RELEASE_To_REFRESH) {
							canReturn = true;

							if (((tempY - startY) / RATIO < headContentHeight)
									&& (tempY - startY) > 0) {
								state = PULL_To_REFRESH;
								changeHeaderViewByState();
								if (ISDEBUG)
									Log.i(TAG, "由松开刷新状态转变到下拉刷新状态");
							}
							// 一下子推到顶了
							else if (tempY - startY <= 0) {
								state = DONE;
								changeHeaderViewByState();
								if (ISDEBUG)
									Log.i(TAG, "由松开刷新状态转变到done状态");
							} else {
								// 不用进行特别的操作，只用更新paddingTop的值就行了
							}
						}
						// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
						if (state == PULL_To_REFRESH) {
							canReturn = true;

							// 下拉到可以进入RELEASE_TO_REFRESH的状态
							if ((tempY - startY) / RATIO >= headContentHeight) {
								state = RELEASE_To_REFRESH;
								isBack = true;
								changeHeaderViewByState();
								if (ISDEBUG)
									Log.i(TAG, "由done或者下拉刷新状态转变到松开刷新");
							}
							// 上推到顶了
							else if (tempY - startY <= 0) {
								state = DONE;
								changeHeaderViewByState();
								if (ISDEBUG)
									Log.i(TAG, "由DOne或者下拉刷新状态转变到done状态");
							}
						}

						// done状态下
						if (state == DONE) {
							if (tempY - startY > 0) {
								state = PULL_To_REFRESH;
								changeHeaderViewByState();
							}
						}

						// 更新headView的size
						if (state == PULL_To_REFRESH) {
							headView.setPadding(0, -1 * headContentHeight
									+ (tempY - startY) / RATIO, 0, 0);

						}

						// 更新headView的paddingTop
						if (state == RELEASE_To_REFRESH) {
							headView.setPadding(0, (tempY - startY) / RATIO
									- headContentHeight, 0, 0);
						}
						if (canReturn) {
							canReturn = false;
							return true;
						}
					}
				} else {
					headorfooter = true;
					isLastIndex = innerLayout.getMeasuredHeight() - 20 <= getScrollY()
							+ getHeight();
					if (showFooter&&state != REFRESHING && isRecored && state != LOADING) {

						// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

						// 可以松手去刷新了
						if (state == RELEASE_To_REFRESH) {
							// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
							if (((startY - tempY) / RATIO < footContentHeight)
									&& (startY - tempY) > 0) {
								state = PULL_To_REFRESH;
								changeFooterViewByState();

								if (ISDEBUG)
									Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
							}
							// 一下子推到顶了
							else if (startY - tempY <= 0) {
								state = DONE;
								changeFooterViewByState();

								if (ISDEBUG)
									Log.v(TAG, "由松开刷新状态转变到done状态");
							}
							// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
							else {
								// 不用进行特别的操作，只用更新paddingTop的值就行了
							}
						}
						// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
						if (state == PULL_To_REFRESH && isLastIndex) {
							// setSelection(getCount() - 1);

							// 下拉到可以进入RELEASE_TO_REFRESH的状态
							if ((startY - tempY) / RATIO >= footContentHeight) {
								state = RELEASE_To_REFRESH;
								isBack = true;
								changeFooterViewByState();

								if (ISDEBUG)
									Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
							}
							// 上推到顶了
							else if (startY - tempY <= 0) {
								state = DONE;
								changeFooterViewByState();

								if (ISDEBUG)
									Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
							}
						}

						// done状态下
						if (state == DONE) {
							if (startY - tempY > 0) {
								state = PULL_To_REFRESH;
								changeFooterViewByState();
							}
						}

						if (isLastIndex) {
							// 更新footerView的size
							if (state == PULL_To_REFRESH) {
								footView.setPadding(0, 0, 0, -1
										* footContentHeight + (startY - tempY)
										/ RATIO);

							}

							// 更新footerView的paddingTop
							if (state == RELEASE_To_REFRESH) {
								footView.setPadding(0, 0, 0, (startY - tempY)
										/ RATIO - footContentHeight);
							}
						}
					}
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	float distance=0;
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (!isRecored) {
				isRecored = true;
				startY = (int) ev.getY();
			}
			distance=ev.getY()+ev.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if(Math.abs(distance-(ev.getY()+ev.getX()))<5)break;//防止子类中点击不灵敏
			return true;
		case MotionEvent.ACTION_UP:
//			Log.d(TAG, "startY="+startY);
//			Log.d(TAG, "ev.getY()="+ev.getY());
			Log.d(TAG, "ACTION_UP");
			isRecored = false;
			isBack = false;
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private void changeFooterViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			fArrowImageView.setVisibility(View.VISIBLE);
			fProgressBar.setVisibility(View.GONE);
			fTipsTextview.setVisibility(View.VISIBLE);
			fLastUpdatedTextView.setVisibility(View.VISIBLE);

			fArrowImageView.clearAnimation();
			fArrowImageView.startAnimation(reverseAnimation);

			fTipsTextview.setText("松开加载");

			if (ISDEBUG)
				Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			fProgressBar.setVisibility(View.GONE);
			fTipsTextview.setVisibility(View.VISIBLE);
			fLastUpdatedTextView.setVisibility(View.VISIBLE);
			fArrowImageView.clearAnimation();
			fArrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				fArrowImageView.clearAnimation();
				fArrowImageView.startAnimation(animation);

				fTipsTextview.setText("上拉加载");
			} else {
				fTipsTextview.setText("上拉加载");
			}
			if (ISDEBUG)
				Log.v(TAG, "当前状态，上拉加载");
			break;

		case REFRESHING:
			footView.setPadding(0, 0, 0, 0);
			fProgressBar.setVisibility(View.VISIBLE);
			fArrowImageView.clearAnimation();
			fArrowImageView.setVisibility(View.GONE);
			fTipsTextview.setText("正在加载...");
			fLastUpdatedTextView.setVisibility(View.VISIBLE);
			if (ISDEBUG)
				Log.v(TAG, "当前状态,正在加载...");
			break;
		case DONE:
			footView.setPadding(0, 0, 0, -1 * footContentHeight);

			fProgressBar.setVisibility(View.GONE);
			fArrowImageView.clearAnimation();
			fArrowImageView.setImageResource(R.drawable.goicon);
			fTipsTextview.setText("上拉加载");
			fLastUpdatedTextView.setVisibility(View.VISIBLE);

			if (ISDEBUG)
				Log.v(TAG, "当前状态，done");
			break;
		}
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText("松开刷新");

			if (ISDEBUG)
				Log.i(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText("下拉刷新");
			} else {
				tipsTextview.setText("下拉刷新");
			}
			if (ISDEBUG)
				Log.i(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:

			headView.setPadding(0, headContentHeight, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			if (ISDEBUG)
				Log.i(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.goicon);
			tipsTextview.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			if (ISDEBUG)
				Log.i(TAG, "当前状态，done");
			break;
		}
	}

	protected void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
//		removeFooter();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(refreshListener!=null)
			refreshListener.onAutoScroll(l, t, oldl, oldt);
	}

	public interface OnRefreshListener {
		public void onRefresh();

		public void onMore();

		public void onAutoScroll(int l, int t, int oldl, int oldt);
	}

	public void loadDateError() {
		state = DONE;
		changeHeaderViewByState();
		changeFooterViewByState();
	}

	protected void onRefreshComplete() {
		state = DONE;
		lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		changeHeaderViewByState();
		changeFooterViewByState();
		scrollTo(0, 0);
	}

	protected void onMoreComplete() {
		state = DONE;
		fLastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		changeHeaderViewByState();
		changeFooterViewByState();
		scrollTo(0, innerLayout.getMeasuredHeight());
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	private void onMore() {
		if (refreshListener != null) {
			refreshListener.onMore();
		}
	}

	protected void addChild(View child) {
		innerLayout.addView(child);
	}

	protected void addChild(View child, int position) {
		innerLayout.addView(child, position);
	}

	protected void clear() {
		innerLayout.removeAllViews();
		innerLayout.addView(headView);
		footView.invalidate();
		innerLayout.addView(footView);
		innerLayout.invalidate();
	}

	private boolean isReset;

	// 设置是刷新还是加载
	public void reset() {
		this.isReset = true;
	}

	public void loadMore(T items) {
		if (isReset) {
			isReset = false;
			initContent(items);
			onRefreshComplete();
		} else {
			addView(items);
			onMoreComplete();
		}
	}

	protected abstract void initContent(T items);

	protected abstract void addView(T items);

}
