package com.android.sharebar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher.ViewFactory;

public class file extends Activity implements ViewFactory {
	private static final int MENU_DELETE = Menu.FIRST;
	private static final int MENU_RENAME = Menu.FIRST + 1;
	private static final int MENU_share = Menu.FIRST + 2;
	private File[] files;
	private String[] names;
	private String[] paths;
	private Gallery pick_photo;
	private BaseAdapter adapter = null;
	private String path;
	private EditText etRename;
	private File file;
	private ImageSwitcher show_photo;
	private String Uri = "http://58.194.170.244:1180/upload/receive_file.php";

	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ���ڲ���ʾ���⡣
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.file);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		path = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/shareBar/img/";
		pick_photo = (Gallery) super.findViewById(R.id.pick_photo);
		show_photo = (ImageSwitcher) super.findViewById(R.id.show_photo);
		show_photo.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_out));
		show_photo.setInAnimation(AnimationUtils.loadAnimation(this,
				android.R.anim.fade_in));
		show_photo.setFactory(this);
		adapter = new ImageAdapter(this, getSD());// ʵ����һ������������
		pick_photo.setAdapter(adapter);// Ϊ������ͼ�������������
		showFileItems();
		pick_photo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bitmap bm = BitmapFactory.decodeFile(paths[arg2]);
				BitmapDrawable bd = new BitmapDrawable(bm);
				show_photo.setImageDrawable(bd);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		// ע�������Ĳ˵����û���ʱ�䰴�Ų��������Ĳ˵����������ģ�
		registerForContextMenu(pick_photo);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = null;

		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			return;
		}
		menu.setHeaderTitle(names[info.position]);
		menu.add(0, MENU_DELETE, 1, "ɾ��");
		menu.add(0, MENU_RENAME, 2, "������");
		menu.add(0, MENU_share, 3, "�ϴ�");
	}

	// * �����Ĳ˵�����

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		final File file = new File(paths[info.position]);
		switch (item.getItemId()) {
		case MENU_DELETE:
			file.delete();
			showFileItems();
			return true;
		case MENU_RENAME:
			fileRename(file);
			showFileItems();
			return true;
		case MENU_share:
			shareFile(file);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void shareFile(File file) {
		String fileName = file.getName();
		String filePath = file.getPath();
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		URL uri;
		try {
			uri = new URL(Uri);
			try {
				HttpURLConnection con = (HttpURLConnection) uri
						.openConnection();
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				// con.setConnectTimeout(6*1000);
				con.setRequestMethod("POST");
				con.setRequestProperty("Connection", "Keep-Alive");
				con.setRequestProperty("Charset", "UTF-8");
				con.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				DataOutputStream ds = new DataOutputStream(
						con.getOutputStream());
				ds.writeBytes(twoHyphens + boundary + end);
				/**
				 * �����ص�ע�⣺ name�����ֵΪ����������Ҫkey ֻ�����key �ſ��Եõ���Ӧ���ļ�
				 * filename���ļ������֣�������׺���� ����:abc.png
				 */
				ds.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";"
						+ " filename=\"" + fileName + "\"" + end);
				ds.writeBytes(end);

				FileInputStream fStream = new FileInputStream(filePath);
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = fStream.read(buffer)) != -1) {
					ds.write(buffer, 0, length);
				}
				fStream.close();
				ds.writeBytes(end);
				ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
				ds.flush();

				int cah = 0;
				cah = con.getResponseCode();
				if (cah != 200) {
					Toast.makeText(this, "����ʧ��", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(this, "����ɹ�", Toast.LENGTH_LONG).show();
				}
				InputStream is = con.getInputStream();
				int ch;
				StringBuffer b = new StringBuffer();
				while ((ch = is.read()) != -1) {
					b.append((char) ch);
				}
				Toast.makeText(this, b.toString(), Toast.LENGTH_LONG).show();
				ds.close();

			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
				Log.i("upload", e.toString());
			}

		} catch (MalformedURLException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			Log.i("upload", e.toString());
		}

	}

	// * ��ȡ�ļ�

	private void showFileItems() {
		File fil = new File(path);
		if (!fil.exists()) {
			fil.mkdirs();
		}
		fil = new File(path);
		files = fil.listFiles();
		int count = files.length;
		if (count != 0) {
			names = new String[count];
			paths = new String[count];
			for (int i = 0; i < count; i++) {
				File f1 = files[i];
				names[i] = f1.getName();
				paths[i] = f1.getPath();
			}
			Bitmap bm = BitmapFactory.decodeFile(paths[0]);
			BitmapDrawable bd = new BitmapDrawable(bm);
			show_photo.setImageDrawable(bd);
		}
		adapter.notifyDataSetChanged();
	}

	private void fileRename(File file) {
		this.file = file;
		View view = getLayoutInflater().inflate(R.layout.rename, null);
		etRename = (EditText) view.findViewById(R.id.rename);
		new AlertDialog.Builder(this).setView(view)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String newName = etRename.getText().toString().trim();
						File newFile = new File(path, newName + ".jpg");
						if (newFile.exists()) {
							showMsg(newName + "�Ѿ����ڣ�����������");
						} else
							file.this.file.renameTo(newFile);
						showFileItems();
					}
				}).setNegativeButton("ȡ��", null).show();
	}

	// * ��Ϣ��ʾ

	private Toast toast;

	public void showMsg(String arg) {
		if (toast == null) {
			toast = Toast.makeText(this, arg, Toast.LENGTH_SHORT);
		} else {
			toast.cancel();
			toast.setText(arg);
		}
		toast.show();
	}

	// * File adapter����
	private List<String> getSD() {
		List<String> it = new ArrayList<String>();
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(path);
		File[] files1 = f.listFiles();
		for (int i = 0; i < files1.length; i++) {
			File file = files1[i];
			String fName = file.getName();
			String end = fName.substring(fName.lastIndexOf(".") + 1,
					fName.length()).toLowerCase();
			if (end.equals("jpg"))
				it.add(file.getPath());
		}
		return it;
	}

	public class ImageAdapter extends BaseAdapter {

		/* �������� */
		int mGalleryItemBackground;
		private Context mContext;
		private List<String> lis;

		/* ImageAdapter�Ĺ���� */
		public ImageAdapter(Context c, List<String> li) {
			mContext = c;
			lis = li;
			/*
			 * ʹ��res/values/attrs.xml�е�<declare-styleable>���� ��Gallery����.
			 */
			TypedArray mTypeArray = obtainStyledAttributes(R.styleable.Gallery);
			/* ȡ��Gallery���Ե�Index id */
			mGalleryItemBackground = mTypeArray.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
			/* �ö����styleable�����ܹ�����ʹ�� */
			mTypeArray.recycle();
		}

		/* ��д�ķ���getCount,����ͼƬ��Ŀ */
		public int getCount() {
			return lis.size();
		}

		/* ��д�ķ���getItem,����position */
		public Object getItem(int position) {
			return position;
		}

		/* ��д�ķ���getItemId,����position */
		public long getItemId(int position) {
			return position;
		}

		/* ��д����getView,������View���� */
		public View getView(int position, View convertView, ViewGroup parent) {
			/* ����ImageView���� */
			ImageView i = new ImageView(mContext);
			/* �趨ͼƬ��imageView���� */
			Bitmap bm = BitmapFactory.decodeFile(lis.get(position).toString());
			i.setImageBitmap(bm);
			/* �����趨ͼƬ�Ŀ�� */
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			/* �����趨Layout�Ŀ�� */
			i.setLayoutParams(new Gallery.LayoutParams(136, 88));
			/* �趨Gallery����ͼ */
			i.setBackgroundResource(mGalleryItemBackground);
			/* ����imageView���� */

			return i;
		}
	}

	@Override
	public View makeView() {
		// TODO �Զ����ɵķ������
		ImageView imageView = new ImageView(this);
		// imageView.setBackgroundResource(R.drawable.background);
		imageView.setScaleType(ImageView.ScaleType.CENTER);
		imageView.setLayoutParams(new ImageSwitcher.LayoutParams(// ����ӦͼƬ��С
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// imageView.setBackgroundResource(R.drawable.background);
		return imageView;
	}

}
