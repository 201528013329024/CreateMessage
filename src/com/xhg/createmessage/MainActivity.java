/**
 * @author Haoguang Xu
 * Copyright (c) 2016, UCAS
 * All rights reserved. 
 * 
 * MainActivity class {@link MainActivity}  
 */

package com.xhg.createmessage;

import com.example.createmessage.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	public Button SendMessage;
	public EditText TextMessage;
	public EditText PhoneNumber;

	public RadioButton RadioSend;
	public RadioButton RadioReceive;
	public ImageView SearchContact;
	public ArrayAdapter<String> adapter;
	final int ContactResult = 888;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 去除屏幕的Title栏
		setContentView(R.layout.activity_main);

		new AlertDialog.Builder(MainActivity.this).setTitle("警告").setMessage("软件仅适用于安卓4.4及以下版本\n\n郑重声明：\n此软件仅用于娱乐，请勿非法使用！若因不正当使用造成不良后果，与开发者无关！\n\nby: XHG")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {// 添加取消按钮
					public void onClick(DialogInterface dialog, int which) {// 取消按钮的响应事件
						MainActivity.this.finish(); 
					}
				}).setPositiveButton("同意", new DialogInterface.OnClickListener() {// 添加同意按钮
					public void onClick(DialogInterface dialog, int which) {// 响应事件
						main(); //执行主函数功能
					}

				}).show();// 在按键响应事件中显示此对话框

		
	}

	protected void main() {
		SendMessage = (Button) findViewById(R.id.sendMessage);
		TextMessage = (EditText) findViewById(R.id.message);
		PhoneNumber = (EditText) findViewById(R.id.phoneNumber);
		RadioSend = (RadioButton) findViewById(R.id.radioSend);
		RadioReceive = (RadioButton) findViewById(R.id.radioReceive);
		SearchContact = (ImageView) findViewById(R.id.btnSearch);

		RadioSend.setChecked(true);

		SearchContact.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
				if (sdkVersion >= Build.VERSION_CODES.ECLAIR) {
					getContacts Contact = new getContacts();
					startActivityForResult(Contact.getIntent(), ContactResult); //获取手机里的联系人列表
				}
			}
		});

		SendMessage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SaveMSG();
			}
		});
	}

	public void SaveMSG() {
		String phoneNumber = PhoneNumber.getText().toString();
		String message = TextMessage.getText().toString();
		if (phoneNumber.length() > 0 && message.length() > 0) {
			ContentValues values = new ContentValues();
			values.put("address", PhoneNumber.getText().toString());
			values.put("body", TextMessage.getText().toString());
			if (RadioReceive.isChecked()) {
				getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("提示");
				builder.setMessage("已成功接收短信，请在信息里查看！");
				builder.setPositiveButton("OK", null);
				builder.show();
			} else if (RadioSend.isChecked()) {
				getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("提示");
				builder.setMessage("已成功发送短信，请在信息里查看！");
				builder.setPositiveButton("OK", null);
				builder.show();
			}

			TextMessage.setText("");
		} else {

			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("提示");
			builder.setMessage("号码或短信内容不能为空！");
			builder.setPositiveButton("OK", null);
			builder.show();

		}

	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (ContactResult):
			int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
			if (sdkVersion >= Build.VERSION_CODES.ECLAIR) {
				getContacts Contact = new getContacts();
				PhoneNumber.setText(Contact.GetName(this, resultCode, data));
			}
			break;
		}
	}

	/** 实现连续按两次后退键退出游戏程序 **/
	private long lastClickTime = 0;

	@Override
	public void onBackPressed() {

		if (lastClickTime <= 0) {
			Toast.makeText(this, "Press again to exit!", Toast.LENGTH_SHORT).show();
			lastClickTime = System.currentTimeMillis();
		} else {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - lastClickTime) < 1000) {
				finish();
			} else {
				lastClickTime = currentTime;
			}
		}
	}
}
