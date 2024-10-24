package com.example.magicpenclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {
    private static final String TAG = "ClientActivity";
    private static final String SERVER_IP = "192.168.50.224"; // 替换为手机 A 的 IP 地址
    private static final int PORT = 12345;
    private Socket socket;
    Button connectButton;
    Button refreshButton;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        initView();

    }

    private void initView() {
        imageView = findViewById(R.id.imageView);
        connectButton = findViewById(R.id.connectButton);
        refreshButton = findViewById(R.id.refresh_btn);
        connectButton.setOnClickListener(view -> connectToServer());
        refreshButton.setOnClickListener(view -> refreshUI());

    }

    private void connectToServer() {
        Log.i(TAG,"connectToServer called.");
        new Thread(() -> {
            try {
                socket = new Socket(SERVER_IP, PORT);
                // 连接成功，可以在这里处理后续逻辑
                runOnUiThread(() ->
                        Toast.makeText(ClientActivity.this, "客户端连接成功", Toast.LENGTH_SHORT).show()
                );

                // 接收图片
                InputStream inputStream = null;
                try {
                    inputStream = socket.getInputStream();
                    Log.i(TAG,"inputStream : "+inputStream);
                    // 读取输入流并转换为 Bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap != null){
                        runOnUiThread(() ->
                                showImage(bitmap)
                        );
                    }
                    // 关闭输入流
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

//                // 保存图片到文件
//                FileOutputStream fileOutputStream = openFileOutput("received_image.jpg", MODE_PRIVATE);
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    fileOutputStream.write(buffer, 0, bytesRead);
//                }
//
//                fileOutputStream.close();
//                inputStream.close();
//                socket.close();
//                Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir() + "/received_image.jpg");



            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void showImage(Bitmap bitmap) {
        Log.i(TAG,"showImage called ");
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap); // 显示接收到的图片
        }
    }

    private void refreshUI() {
        Log.i(TAG,"refreshUI called ");
        new Thread(() -> {
            // 接收图片
            InputStream inputStream = null;
            try {
                inputStream = socket.getInputStream();
                Log.i(TAG,"inputStream : "+inputStream);
                // 读取输入流并转换为 Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null){
                    runOnUiThread(() ->
                            showImage(bitmap)
                    );
                }
                // 关闭输入流
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭连接
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
