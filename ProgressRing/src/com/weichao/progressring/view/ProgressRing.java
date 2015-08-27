package com.weichao.progressring.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.weichao.progressring.R;

public class ProgressRing extends View {
	private static final String TAG = "ProgressRing";

	private static final int NOT_INITIALIZED = -1;
	private static final long WAIT_FOR_INVALIDATE_TIME = 100l;

	private float mDensity = 1f;

	public static final int DEFAULT_BACKGROUND_SIZE = 240;
	public static final int DEFAULT_BACKGROUND_SRC = R.drawable.sharing;
	public static final boolean DEFAULT_BACKGROUND_VISIBILITY = true;
	public static final int DEFAULT_RING_COLOR = Color.LTGRAY;
	public static final int DEFAULT_RING_SIZE = 14;
	public static final boolean DEFAULT_RING_VISIBILITY = true;
	public static final int DEFAULT_TEXT_COLOR = Color.LTGRAY;
	public static final int DEFAULT_TEXT_SIZE = 54;
	public static final boolean DEFAULT_TEXT_VISIBILITY = true;

	private Context mContext = null;

	private float mViewWidth = NOT_INITIALIZED;
	private float mViewHeight = NOT_INITIALIZED;
	private float mViewScaleValue = 1f;
	private boolean mViewIsVisible = false;

	private boolean mBackgroundVisibility = DEFAULT_BACKGROUND_VISIBILITY;
	private boolean mBackgroundVisible = false;
	private RectF mBackgroundRectF = null;
	private Paint mBackgroundPaint = null;
	private float mBackgroundSize = DEFAULT_BACKGROUND_SIZE;
	private int mBackgroundResourceId = DEFAULT_BACKGROUND_SRC;
	private Bitmap mBackgroundBitmap = null;

	private boolean mRingVisibility = DEFAULT_RING_VISIBILITY;
	private boolean mRingVisible = false;
	private RectF mRingRectF = null;
	private float mRingRectFOffset = 0f;
	private Paint mRingPaint = null;
	private float mRingSize = DEFAULT_RING_SIZE;
	private int mRingColor = DEFAULT_RING_COLOR;
	private float mRingStartAngle = -90f;
	private float mRingSweepAngle = 360f;

	private boolean mTextVisibility = DEFAULT_TEXT_VISIBILITY;
	private boolean mTextVisible = false;
	private Paint mTextPaint = null;
	private float mTextSize = DEFAULT_TEXT_SIZE;
	private int mTextColor = DEFAULT_TEXT_COLOR;
	private FontMetrics mTextFontMetrics = null;
	private String mText = "0%";
	private int mTextLength = NOT_INITIALIZED;
	private float mTextLeft = NOT_INITIALIZED;
	private float mTextTop = NOT_INITIALIZED;

	private int mTotalValue = 0;
	private int mConsumedValue = 0;
	private float mProgress = 0f;

	public ProgressRing(Context context) {
		this(context, null);
	}

	public ProgressRing(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ProgressRing(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		mContext = context;
		initDensity();
		initView();
		loadViewFromXML(attrs, defStyleAttr);
	}

	private void initDensity() {
		mDensity = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
		Log.i(TAG, "mDensity:" + mDensity);
	}

	private void initView() {
		initBackground();
		initRing();
		initText();
	}

	private void initBackground() {
		mBackgroundSize *= mDensity;
		// mViewScaleValue = BitmapFactory.decodeResource(getResources(),
		// DEFAULT_BACKGROUND_SRC).getWidth() / DEFAULT_BACKGROUND_SIZE;
		mViewScaleValue = mBackgroundSize / BitmapFactory.decodeResource(getResources(), DEFAULT_BACKGROUND_SRC).getWidth();
		mBackgroundBitmap = getScaledBitmap(mBackgroundResourceId, mBackgroundSize);
		mBackgroundRectF = new RectF();
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		Log.i(TAG, "(float) BitmapFactory.decodeResource(getResources(), DEFAULT_BACKGROUND_SRC).getWidth():" + (float) BitmapFactory.decodeResource(getResources(), DEFAULT_BACKGROUND_SRC).getWidth());
		Log.i(TAG, "mBackgroundSize:" + mBackgroundSize);
		Log.i(TAG, "initBackgroundmViewScaleValue:" + mViewScaleValue);
	}

	private void initRing() {
		mRingRectF = new RectF();
		mRingPaint = new Paint();
		mRingPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mRingPaint.setColor(DEFAULT_RING_COLOR);
		mRingPaint.setStyle(Style.STROKE);
		mRingPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RING_SIZE, getResources().getDisplayMetrics()));
	}

	private void initText() {
		mTextPaint = new Paint();
		mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setColor(DEFAULT_TEXT_COLOR);
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, getResources().getDisplayMetrics()));
		mTextFontMetrics = mTextPaint.getFontMetrics();
	}

	private void loadViewFromXML(AttributeSet attrs, int defStyleAttr) {
		TypedArray attributes = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressRing, defStyleAttr, 0);
		int count = attributes.getIndexCount();
		for (int i = 0; i < count; i++) {
			int index = attributes.getIndex(i);
			switch (index) {
			case R.styleable.ProgressRing_backgroundSrc:
				setBackgroundSrc(attributes.getResourceId(index, DEFAULT_BACKGROUND_SRC));
				break;
			case R.styleable.ProgressRing_backgroundVisibility:
				setBackgroundVisibility(attributes.getBoolean(index, true));
				break;
			case R.styleable.ProgressRing_lengthOfSide:
				setLengthOfSide(attributes.getDimension(index, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RING_SIZE, getResources().getDisplayMetrics())));
				break;
			case R.styleable.ProgressRing_ringColor:
				setRingColor(attributes.getColor(index, DEFAULT_RING_COLOR));
				break;
			case R.styleable.ProgressRing_ringSize:
				setRingSize(attributes.getDimension(index, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_RING_SIZE, getResources().getDisplayMetrics())));
				break;
			case R.styleable.ProgressRing_ringVisibility:
				setRingVisibility(attributes.getBoolean(index, true));
				break;
			case R.styleable.ProgressRing_textColor:
				setTextColor(attributes.getColor(index, DEFAULT_TEXT_COLOR));
				break;
			case R.styleable.ProgressRing_textSize:
				setTextSize(attributes.getDimension(index, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, getResources().getDisplayMetrics())));
				break;
			case R.styleable.ProgressRing_textVisibility:
				setTextVisibility(attributes.getBoolean(index, true));
				break;
			}
		}
		attributes.recycle();
	}

	private void setBackgroundSrc(int resourceId) {
		if (resourceId != NOT_INITIALIZED) {
			mBackgroundResourceId = resourceId;
			if (mBackgroundSize != NOT_INITIALIZED) {
				mBackgroundBitmap = getScaledBitmap(mBackgroundResourceId, mBackgroundSize);
			}
		}
	}

	private void setBackgroundVisibility(boolean visibility) {
		mBackgroundVisibility = visibility;
	}

	private void setLengthOfSide(float length) {
		mBackgroundSize = length;
		mViewScaleValue *= length / mDensity / DEFAULT_BACKGROUND_SIZE;
		mBackgroundBitmap = getScaledBitmap(mBackgroundResourceId, mBackgroundSize);
		updateRingRectAndPaint();
		updateTextPaintAndFontMetric();
		Log.i(TAG, "mBackgroundSize:" + mBackgroundSize);
		Log.i(TAG, "setLengthOfSidemViewScaleValue:" + mViewScaleValue);
	}

	private void setRingColor(int color) {
		mRingColor = color;
		mRingPaint.setColor(color);
	}

	private void setRingSize(float size) {
		mRingSize = size / mDensity;
		Log.i(TAG, "mRingSize:" + mRingSize);
		updateRingRectAndPaint();
	}

	private void setRingVisibility(boolean visibility) {
		mRingVisibility = visibility;
	}

	private void setTextColor(int color) {
		mTextColor = color;
		mTextPaint.setColor(color);
	}

	private void setTextSize(float size) {
		mTextSize = size / mDensity;
		Log.i(TAG, "mTextSize:" + mTextSize);
		updateTextPaintAndFontMetric();
	}

	private void setTextVisibility(boolean visibility) {
		mTextVisibility = visibility;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		float paddingLeft = getPaddingLeft();
		float paddingTop = getPaddingTop();
		mViewWidth = paddingLeft + mBackgroundSize + getPaddingRight();
		mViewHeight = paddingTop + mBackgroundSize + getPaddingBottom();
		updateBackgroundRect(paddingLeft, paddingTop);
		updateRingRectAndPaint();
		updateTextPaintAndFontMetric();
		setMeasuredDimension((int) Math.ceil(mViewWidth), (int) Math.ceil(mViewHeight));
		Log.i(TAG, "paddingLeft:" + paddingLeft);
	}

	private Bitmap getScaledBitmap(int resourceId, float size) {
		if (size > 0) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);
			if (bitmap != null) {
				int oldSize = bitmap.getWidth();
				Matrix matrix = new Matrix();
				float scaleValue = size / oldSize;
				matrix.postScale(scaleValue, scaleValue);
				try {
					return Bitmap.createBitmap(bitmap, 0, 0, oldSize, oldSize, matrix, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private void updateBackgroundRect(float left, float top) {
		mBackgroundRectF.left = left;
		mBackgroundRectF.top = top;
		mBackgroundRectF.right = left + mBackgroundSize;
		mBackgroundRectF.bottom = top + mBackgroundSize;
		mRingRectFOffset = left - 20;
	}

	private void updateRingRectAndPaint() {
		float newRingSize = mRingSize * mViewScaleValue;
		mRingRectF.left = mBackgroundRectF.left + newRingSize + mRingRectFOffset;
		mRingRectF.top = mBackgroundRectF.top + newRingSize + mRingRectFOffset;
		mRingRectF.right = mBackgroundRectF.right - newRingSize - mRingRectFOffset;
		mRingRectF.bottom = mBackgroundRectF.bottom - newRingSize - mRingRectFOffset;
		mRingPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newRingSize, getResources().getDisplayMetrics()));
		Log.i(TAG, "mRingSize:" + mRingSize);
		Log.i(TAG, "updateRingRectAndPaintmViewScaleValue:" + mViewScaleValue);
		Log.i(TAG, "newRingSize:" + newRingSize);
	}

	private void updateTextPaintAndFontMetric() {
		mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize * mViewScaleValue, getResources().getDisplayMetrics()));
		mTextFontMetrics = mTextPaint.getFontMetrics();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBackgroundVisible) {
			canvas.drawBitmap(mBackgroundBitmap, mBackgroundRectF.left, mBackgroundRectF.top, mBackgroundPaint);
		}
		if (mRingVisible) {
			if (mProgress >= 0f && mProgress <= 100f) {
				mRingStartAngle = mProgress * 3.6f - 90f;
				mRingSweepAngle = 360f - mProgress * 3.6f;
				canvas.drawArc(mRingRectF, mRingStartAngle, mRingSweepAngle, false, mRingPaint);
			}
		}
		if (mTextVisible) {
			if (mProgress >= 0f && mProgress <= 100f) {
				mText = (int) Math.ceil(mProgress) + "%";
				int textLength = mText.length();
				if (textLength != mTextLength) {
					mTextLeft = mBackgroundRectF.centerX();
					mTextTop = (mBackgroundRectF.top + mBackgroundRectF.bottom - (mTextFontMetrics.top + mTextFontMetrics.bottom)) / 2;
					mTextLength = textLength;
				}
				canvas.drawText(mText, mTextLeft, mTextTop, mTextPaint);
			} else {
				// reset();
			}
		}
	}

	@Override
	public void setVisibility(int visibility) {
		switch (visibility) {
		case View.VISIBLE:
			if (mTextVisibility) {
				mTextVisible = true;
			}
			if (mRingVisibility) {
				mRingVisible = true;
			}
			if (mBackgroundVisibility) {
				mBackgroundVisible = true;
			}
			mViewIsVisible = true;
			break;
		default:
			mTextVisible = false;
			mRingVisible = false;
			mBackgroundVisible = false;
			mViewIsVisible = false;
			break;
		}
		invalidate();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		SystemClock.sleep(WAIT_FOR_INVALIDATE_TIME);
	}

	public void increaseTotalValue(int value) {
		mTotalValue += value;
		if (mTotalValue != 0) {
			mProgress = (float) Math.floor((float) mConsumedValue * 100 / mTotalValue);
			if (!mViewIsVisible) {
				setVisibility(View.VISIBLE);
			} else {
				invalidate();
			}
		}
	}

	public void increaseConsumedValue(int value) {
		mConsumedValue += value;
		if (mTotalValue != 0) {
			float progress = (float) Math.ceil((float) mConsumedValue * 100 / mTotalValue);
			if (progress < mProgress + 1) {
				return;
			}
			if (progress > 100f) {
				progress = 100f;
			}
			mProgress = progress;
			if (!mViewIsVisible) {
				setVisibility(View.VISIBLE);
			} else {
				invalidate();
			}
		}
	}

	private void reset() {
		mConsumedValue = 0;
		mTotalValue = 0;
		mProgress = 0f;
		if (mViewIsVisible) {
			setVisibility(View.GONE);
		}
	}
}
