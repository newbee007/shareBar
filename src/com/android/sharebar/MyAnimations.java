package com.android.sharebar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

public class MyAnimations {

	private static int xOffset = 0;
	private static int yOffset = 0;

	public static void initOffset(Context context) {
		xOffset = (int) (10.667 * context.getResources().getDisplayMetrics().density);
		yOffset = (int) (8.667 * context.getResources().getDisplayMetrics().density);
	}

	public static void moveFrontBg(View v, int startX, int toX, int startY,
			int toY) {
		TranslateAnimation anim = new TranslateAnimation(startX, toX, startY,
				toY);
		anim.setDuration(200);
		anim.setFillAfter(true);
		v.startAnimation(anim);
	}

	public static Animation getRotateAnimation(float fromDegrees,
			float toDegrees, int durationMillis) {
		RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate.setDuration(durationMillis);
		rotate.setFillAfter(true);
		return rotate;
	}

	public static void startAnimationsIn(ViewGroup viewgroup, int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			final Button inoutimagebutton = (Button) viewgroup.getChildAt(i);
			inoutimagebutton.setVisibility(0);
			final MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton
					.getLayoutParams();
			Animation animation = new TranslateAnimation(-mlp.leftMargin
					+ xOffset, 0F, -mlp.topMargin + yOffset, 0F);
			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			animation.setStartOffset((i * 100)
					/ (-1 + viewgroup.getChildCount()));
			animation.setInterpolator(new OvershootInterpolator(2F));
			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
				}
			});
			inoutimagebutton.startAnimation(animation);
		}
	}

	public static void startAnimationsOut(ViewGroup viewgroup,
			int durationMillis) {
		for (int i = 0; i < viewgroup.getChildCount(); i++) {
			final Button inoutimagebutton = (Button) viewgroup.getChildAt(i);

			final MarginLayoutParams mlp = (MarginLayoutParams) inoutimagebutton
					.getLayoutParams();
			Animation animation = new TranslateAnimation(0F, -mlp.leftMargin
					+ xOffset, 0F, -mlp.topMargin + yOffset);

			animation.setFillAfter(true);
			animation.setDuration(durationMillis);
			animation.setStartOffset(((viewgroup.getChildCount() - i) * 100)
					/ (-1 + viewgroup.getChildCount()));// 椤哄簭鍊掍竴涓嬫瘮杈冭垝鏈�
			animation.setInterpolator(new AnticipateInterpolator(2F));
			animation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
				}
			});
			inoutimagebutton.startAnimation(animation);
		}

	}

}