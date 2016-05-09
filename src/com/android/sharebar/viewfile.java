package com.android.sharebar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.R.drawable;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class viewfile extends ListActivity {
	private String rootpath;
	private TextView path;
	private List<String> items = null;// �������
	private List<String> paths = null;// ���·��

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ���ڲ���ʾ���⡣
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.showfile);
		path = (TextView) super.findViewById(R.id.tv);
		rootpath = "/mnt";
		path.setText(rootpath);
		fileview(rootpath);
	}

	private void fileview(String filepath) {
		//
		this.path.setText("��ǰ��·����" + filepath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(filepath);
		File[] files = f.listFiles();// �г������ļ�
		if (!filepath.equals(rootpath)) {
			items.add("�������˵�");
			paths.add(rootpath);
			items.add("������һ�˵�");
			paths.add(f.getParent());
		}
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				items.add(files[i].getName());
				paths.add(files[i].getPath());
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		this.setListAdapter(adapter);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		String path = paths.get(position);
		final File file = new File(path);
		if (file.isDirectory()) {
			this.fileview(path);
		} else {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle("��ʾ");
			alertDialog.setIcon(drawable.ic_dialog_info);
			alertDialog.setMessage(file.getName() + "ȷ��ѡ������ļ���?");
			// ��������ȷ��
			alertDialog.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// ִ��ɾ��������ʲô����������
							File delFile = new File(file.getAbsolutePath());
							if (delFile.exists()) {
								// Log.i("PATH",delFile.getAbsolutePath());
								/*
								 * delFile.delete(); //ˢ�½���
								 * getFileDir(file.getParent());
								 */

								// ��ѡ����ļ�·�����ݸ��ϴ��ļ�·��picPath
								select.uploadpath = file.getPath();
								// select.picpath.setText("�ļ�·��:" +
								// file.getPath());
								Toast.makeText(viewfile.this,
										"��ѡ���ļ�" + file.getName(),
										Toast.LENGTH_LONG).show();
								Intent intent = new Intent(viewfile.this,
										camera.class);
								startActivity(intent);
								finish();
							}
						}
					});
			// �����ұ�ȡ��
			alertDialog.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// ִ�в���
							fileview(file.getParent());
						}
					});
			alertDialog.show();
		}
	}
}
