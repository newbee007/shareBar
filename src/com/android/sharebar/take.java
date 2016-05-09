package com.android.sharebar;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class take extends Activity {
	private Camera cam = null;
	private boolean previewRunning;
	public SurfaceHolder holder;
	private TextView time;
	private TextView place;
	private String text;
	public LocationClient mLocationClient = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	private boolean areButtonsShowing;
	private boolean locationed = false;
	private boolean timeed = false;
	private RelativeLayout composerButtonsWrapper;
	private ImageView composerButtonsShowHideButtonIcon;
	private RelativeLayout composerButtonsShowHideButton;

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.take);

		Button but_photo = (Button) super
				.findViewById(R.id.composer_button_photo);
		but_photo.setOnClickListener(new OnClickListenerlmp());
		Button but_auto = (Button) super
				.findViewById(R.id.composer_button_auto);
		but_auto.setOnClickListener(new OnClickListenerlmp());
		Button but_time = (Button) super
				.findViewById(R.id.composer_button_time);
		but_time.setOnClickListener(new OnClickListenerlmp());
		Button but_place = (Button) super
				.findViewById(R.id.composer_button_place);
		but_place.setOnClickListener(new OnClickListenerlmp());

		MyAnimations.initOffset(take.this);
		composerButtonsWrapper = (RelativeLayout) findViewById(R.id.composer_buttons_wrapper);
		composerButtonsShowHideButton = (RelativeLayout) findViewById(R.id.composer_buttons_show_hide_button);
		composerButtonsShowHideButtonIcon = (ImageView) findViewById(R.id.composer_buttons_show_hide_button_icon);
		composerButtonsShowHideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!areButtonsShowing) {
					MyAnimations.startAnimationsIn(composerButtonsWrapper, 300);
					composerButtonsShowHideButtonIcon
							.startAnimation(MyAnimations.getRotateAnimation(0,
									-270, 300));
				} else {
					MyAnimations
							.startAnimationsOut(composerButtonsWrapper, 300);
					composerButtonsShowHideButtonIcon
							.startAnimation(MyAnimations.getRotateAnimation(
									-270, 0, 300));
				}
				areButtonsShowing = !areButtonsShowing;
			}
		});

		composerButtonsShowHideButton.startAnimation(MyAnimations
				.getRotateAnimation(0, 360, 200));

		time = (TextView) super.findViewById(R.id.time);

		place = (TextView) super.findViewById(R.id.place);

		SurfaceView take_view = (SurfaceView) super
				.findViewById(R.id.take_view);
		holder = take_view.getHolder();

		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(new MySurfaceViewCallback());

	}

	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// ���صĶ�λ���������ַ��Ϣ
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setScanSpan(5000);// ���÷���λ����ļ��ʱ��Ϊ5000ms
		option.disableCache(true);// ��ֹ���û��涨λ
		// option.setPriority(LocationClientOption.NetWorkFirst); // ���ö�λ���ȼ���
		// option.setPoiNumber(5); // ��෵��POI����
		// //option.setPoiDistance(1000); // poi��ѯ����
		// option.setPoiExtraInfo(true); // �Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
		mLocationClient.setLocOption(option);

	}

	public class MyLocationListenner implements BDLocationListener {
		@Override
		// ����λ����Ϣ
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				/**
				 * ��ʽ����ʾ��ַ��Ϣ
				 */
				// sb.append("\naddr : ");
				sb.append(location.getProvince());
				sb.append(location.getCity());
				sb.append(location.getDistrict());
			}

			text = sb.toString();

			Log.i("��λ���", sb.toString());
		}

		// ����POI��Ϣ�������Ҳ���ҪPOI��������û��������
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
		}
	}

	public void onDestroy() {
		mLocationClient.stop();// ֹͣ��λ
		// mTv = null;
		super.onDestroy();
	}

	private final class MySurfaceViewCallback implements Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			// TODO �Զ����ɵķ������

		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			// TODO �Զ����ɵķ������
			try {

				cam = Camera.open();
				Camera.Parameters param = cam.getParameters();
				param.setPreviewFrameRate(5);
				param.setPictureFormat(PixelFormat.JPEG);
				param.set("jpeg", 100);
				cam.setParameters(param);
				cam.setDisplayOrientation(90);
				cam.setPreviewDisplay(holder);
				cam.startPreview();
				previewRunning = true;
			} catch (Exception e) {

			}

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO �Զ����ɵķ������
			if (cam != null) {
				if (previewRunning) {
					cam.stopPreview();
					previewRunning = false;
				}
				cam.release();
			}
		}
	}

	private PictureCallback jpgcall = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				Bitmap bmp = BitmapFactory
						.decodeByteArray(data, 0, data.length);
				int width = bmp.getWidth();
				int hight = bmp.getHeight();
				Bitmap icon = Bitmap.createBitmap(width, hight,
						Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(icon);
				Paint photoPaint = new Paint();
				photoPaint.setDither(true);
				photoPaint.setFilterBitmap(true);
				canvas.drawBitmap(bmp, 0, 0, photoPaint);
				Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
						| Paint.DEV_KERN_TEXT_FLAG);// ���û���
				textPaint.setTextSize(20.0f);// �����С
				textPaint.setColor(Color.RED);// ���õ���ɫ
				String timer = time.getText().toString();
				String location = place.getText().toString();
				canvas.drawText(timer, 410, 450, textPaint);
				canvas.drawText(location, 410, 470, textPaint);
				canvas.save(Canvas.ALL_SAVE_FLAG);
				canvas.restore();
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/shareBar/img/";
				String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
						.format(new Date()) + ".jpg";
				File out = new File(path);
				if (!out.exists()) {
					out.mkdirs();
				}
				out = new File(path, fileName);
				FileOutputStream bos = new FileOutputStream(out);
				icon.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
				Toast.makeText(take.this, "���ճɹ�����Ƭ�ѱ���", Toast.LENGTH_LONG)
						.show();
				take.this.cam.stopPreview();
				take.this.cam.startPreview();
			} catch (Exception e) {

			}
		}
	};

	ShutterCallback sc = new ShutterCallback() {

		@Override
		public void onShutter() {
			// TODO �Զ����ɵķ������

		}
	};

	private class OnClickListenerlmp implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO �Զ����ɵķ������
			switch (v.getId()) {
			case R.id.composer_button_photo:
				if (cam != null) {
					cam.takePicture(sc, null, jpgcall);
				}
				break;
			case R.id.composer_button_auto:
				if (cam != null) {
					cam.autoFocus(new AutoFocusCallbacklmpl());
				}
				break;
			case R.id.composer_button_time:
				if (!timeed) {
					new TimeThread().start();
				} else {
					time.setText("");
				}
				timeed = !timeed;
				break;
			case R.id.composer_button_place:
				if (!locationed) {
					mLocationClient = new LocationClient(
							getApplicationContext());
					mLocationClient.registerLocationListener(myListener);
					setLocationOption();
					mLocationClient.start();
					mLocationClient.requestLocation();
					place.setText(text);
				} else {
					place.setText("");
				}
				locationed = !locationed;
				break;
			}
		}
	}

	private class AutoFocusCallbacklmpl implements AutoFocusCallback {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {

		}
	}

	public class TimeThread extends Thread {
		@Override
		public void run() {
			while (timeed) {
				try {
					Thread.sleep(1000);
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (timeed) {
				time.setText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
						.format(new Date()));
			}

		}
	};

}
