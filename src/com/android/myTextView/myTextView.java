package com.android.myTextView;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class myTextView extends TextView {

	public myTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO 自动生成的构造函数存根
	}
	protected void onDraw(Canvas canvas){
		canvas.rotate(90,getMeasuredWidth()/2,getMeasuredHeight()/2);
		super.onDraw(canvas);
	}
}
