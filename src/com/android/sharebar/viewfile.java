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
	private List<String> items = null;// 存放名称
	private List<String> paths = null;// 存放路径

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 窗口不显示辩题。
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
		this.path.setText("当前的路径是" + filepath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(filepath);
		File[] files = f.listFiles();// 列出所有文件
		if (!filepath.equals(rootpath)) {
			items.add("返回主菜单");
			paths.add(rootpath);
			items.add("返回上一菜单");
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
			alertDialog.setTitle("提示");
			alertDialog.setIcon(drawable.ic_dialog_info);
			alertDialog.setMessage(file.getName() + "确定选择这个文件吗?");
			// 设置左面确定
			alertDialog.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// 执行删除，或者什么。。。操作
							File delFile = new File(file.getAbsolutePath());
							if (delFile.exists()) {
								// Log.i("PATH",delFile.getAbsolutePath());
								/*
								 * delFile.delete(); //刷新界面
								 * getFileDir(file.getParent());
								 */

								// 把选择的文件路径传递给上传文件路径picPath
								select.uploadpath = file.getPath();
								// select.picpath.setText("文件路径:" +
								// file.getPath());
								Toast.makeText(viewfile.this,
										"已选择文件" + file.getName(),
										Toast.LENGTH_LONG).show();
								Intent intent = new Intent(viewfile.this,
										camera.class);
								startActivity(intent);
								finish();
							}
						}
					});
			// 设置右边取消
			alertDialog.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// 执行操作
							fileview(file.getParent());
						}
					});
			alertDialog.show();
		}
	}
}
