package com.android.sharebar;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class select extends Activity implements OnClickListener {
	private Button select;
	private Button upload;
	static TextView picpath;
	public static String uploadpath = "/mnt";
	private String actionUrl = "http://192.168.1.103:1108/upload/receive_file.php";
	private String newName = "/mnt";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ���ڲ���ʾ���⡣
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.viewfile);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		select = (Button) super.findViewById(R.id.select);
		picpath = (TextView) super.findViewById(R.id.select_path);
		picpath.setText("��ǰ��·����" + uploadpath);
		newName = uploadpath.substring(uploadpath.lastIndexOf("/") + 1,
				uploadpath.length());
		upload = (Button) super.findViewById(R.id.upload);
		select.setOnClickListener(this);
		upload.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO �Զ����ɵķ������
		switch (v.getId()) {
		case R.id.select:
			Intent intent = new Intent(select.this, viewfile.class);
			startActivity(intent);
			break;
		case R.id.upload:
			upload();
			break;
		default:
			break;

		}
	}

	private void upload() {
		// TODO �Զ����ɵķ������

		String end = "\r\n";

		String twoHyphens = "--";
		// String BOUNDARY = "---------7d4a6d158c9";
		String boundary = "******";

		try

		{
			// Toast.makeText(this, uploadFile+srcPath, 1).show();
			URL url = new URL(actionUrl);

			HttpURLConnection httpURLConnection = (HttpURLConnection) url

			.openConnection();

			// ����ÿ�δ��������С��������Ч��ֹ�ֻ���Ϊ�ڴ治�����

			// �˷���������Ԥ�Ȳ�֪�����ݳ���ʱ����û�н����ڲ������ HTTP �������ĵ�����

			// httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K

			// �������������

			httpURLConnection.setDoInput(true);

			httpURLConnection.setDoOutput(true);

			httpURLConnection.setUseCaches(false);

			// ʹ��POST����

			httpURLConnection.setRequestMethod("POST");

			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");

			httpURLConnection.setRequestProperty("Charset", "UTF-8");

			httpURLConnection.setRequestProperty("Content-Type",

			"multipart/form-data;boundary=" + boundary);

			// httpURLConnection.connect();

			DataOutputStream dos = new DataOutputStream(

			httpURLConnection.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + end);

			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""

					+ newName + "\"" + end);

			dos.writeBytes(end);

			FileInputStream fis = new FileInputStream(uploadpath);

			byte[] buffer = new byte[1024]; // 8k

			int count = 0;

			// ��ȡ�ļ�

			while ((count = fis.read(buffer)) != -1)

			{

				dos.write(buffer, 0, count);

			}

			fis.close();

			dos.writeBytes(end);

			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);

			dos.flush();

			InputStream is = httpURLConnection.getInputStream();

			// InputStreamReader isr = new InputStreamReader(is, "utf-8");
			int cah = 0;
			cah = httpURLConnection.getResponseCode();
			if (cah != 200) {
				Toast.makeText(this, "����ʧ��", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "����chenggong", Toast.LENGTH_LONG).show();
			}
			int ch;
			StringBuffer br = new StringBuffer();

			// String result = br.readLine();
			while ((ch = is.read()) != -1) {
				br.append((char) ch);
			}

			Toast.makeText(this, br.toString(), Toast.LENGTH_LONG).show();

			dos.close();

			is.close();

		} catch (Exception e)

		{

			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

		}
	}
}
