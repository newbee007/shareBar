package com.android.sharebar;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

public class camera extends Activity {

	TabHost tabHost;
	ImageView img;
	int startLeft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 窗口不显示辩题。
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.camera);
		tabHost = (TabHost) this.findViewById(R.id.tabhost);
		LocalActivityManager mLocalActivityManager = new LocalActivityManager(
				this, false);
		mLocalActivityManager.dispatchCreate(savedInstanceState);
		tabHost.setup(mLocalActivityManager);
		// tabHost.setup();//实例化了tabWidget和tabContent

		// 第一个tab

		TabSpec tabSpec01 = tabHost.newTabSpec("tab1");
		// 选项卡其实就是一个tabspec，获取一个新的TabHost.TabSpec，并关联到当前tabhost
		tabSpec01.setIndicator("");// 往选项卡里添加东西
		tabSpec01.setContent(new Intent(this, file.class));

		// 第二个tab
		TabSpec tabSpec02 = tabHost.newTabSpec("tab2");
		tabSpec02.setIndicator("");
		tabSpec02.setContent(new Intent(this, take.class));

		// 第三个tab
		TabSpec tabSpec03 = tabHost.newTabSpec("tab3");
		tabSpec03.setIndicator("");
		tabSpec03.setContent(new Intent(this, viewfile.class));

		tabHost.addTab(tabSpec01);
		tabHost.addTab(tabSpec02);
		tabHost.addTab(tabSpec03);
		tabHost.setCurrentTab(2);
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		radioGroup.setOnCheckedChangeListener(checkedChangeListener);

	}

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.photosview:
				tabHost.setCurrentTabByTag("tab1");

				break;
			case R.id.camera:
				tabHost.setCurrentTabByTag("tab2");

				break;
			case R.id.fileview:
				tabHost.setCurrentTabByTag("tab3");

				break;

			default:
				break;
			}
		}
	};

}
