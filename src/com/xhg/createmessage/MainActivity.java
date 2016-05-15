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
		requestWindowFeature(Window.FEATURE_NO_TITLE); // ȥ����Ļ��Title��
		setContentView(R.layout.activity_main);

		new AlertDialog.Builder(MainActivity.this).setTitle("����").setMessage("����������ڰ�׿4.4�����°汾\n\n֣��������\n��������������֣�����Ƿ�ʹ�ã���������ʹ����ɲ���������뿪�����޹أ�\n\nby: XHG")
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {// ���ȡ����ť
					public void onClick(DialogInterface dialog, int which) {// ȡ����ť����Ӧ�¼�
						MainActivity.this.finish(); 
					}
				}).setPositiveButton("ͬ��", new DialogInterface.OnClickListener() {// ���ͬ�ⰴť
					public void onClick(DialogInterface dialog, int which) {// ��Ӧ�¼�
						main(); //ִ������������
					}

				}).show();// �ڰ�����Ӧ�¼�����ʾ�˶Ի���

		
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
					startActivityForResult(Contact.getIntent(), ContactResult); //��ȡ�ֻ������ϵ���б�
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
				builder.setTitle("��ʾ");
				builder.setMessage("�ѳɹ����ն��ţ�������Ϣ��鿴��");
				builder.setPositiveButton("OK", null);
				builder.show();
			} else if (RadioSend.isChecked()) {
				getContentResolver().insert(Uri.parse("content://sms/sent"), values);
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("��ʾ");
				builder.setMessage("�ѳɹ����Ͷ��ţ�������Ϣ��鿴��");
				builder.setPositiveButton("OK", null);
				builder.show();
			}

			TextMessage.setText("");
		} else {

			AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
			builder.setTitle("��ʾ");
			builder.setMessage("�����������ݲ���Ϊ�գ�");
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

	/** ʵ�����������κ��˼��˳���Ϸ���� **/
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
