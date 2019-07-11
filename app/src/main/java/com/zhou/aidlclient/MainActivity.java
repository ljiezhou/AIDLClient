package com.zhou.aidlclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zhou.aidlservice.aidl.IMyService;
import com.zhou.aidlservice.aidl.Student;

import java.util.List;


public class MainActivity extends Activity {

    private static final String ACTION_BIND_SERVICE = "com.zhou.aidlservice.aidl.MyService";
    private IMyService mIMyService;
    private static final String TAG = "MainActivity";

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIMyService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //通过服务端onBind方法返回的binder对象得到IMyService的实例，得到实例就可以调用它的方法了
            mIMyService = IMyService.Stub.asInterface(service);
            try {
//                Student student = mIMyService.getStudent().get(4);
//                showDialog(student.toString());
//                Toast.makeText(MainActivity.this,student.toString(),Toast.LENGTH_SHORT).show();
                List<Student> studentList = mIMyService.getStudent();
                for (Student student : studentList) {
                    Log.d(TAG, "onServiceConnected: " + student.toString());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.button1) {
//                    Intent intentService = new Intent(ACTION_BIND_SERVICE);
//                    intentService.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (mIMyService != null) {
                        List<Student> studentList = null;
                        try {
                            studentList = mIMyService.getStudent();
                            for (Student student : studentList) {
                                Log.d(TAG, "onServiceConnected: " + student.toString());
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        return;
                    }
                    Intent intent = new Intent();
                    intent.setPackage("com.zhou.aidlservice");
                    intent.setAction("com.zhou.aidlservice.MyService");
                    MainActivity.this.bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
                }

            }


        });
    }

    public void showDialog(String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("scott")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (mIMyService != null) {
            unbindService(mServiceConnection);
        }
        super.onDestroy();
    }
}