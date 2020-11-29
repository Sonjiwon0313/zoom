package com.zoomw.zoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.zoomw.zoom.features.camera.CameraManager;
import com.zoomw.zoom.features.camera.CameraPreview;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 100001; //이 번호 가지고 식별


    private CameraPreview cameraPreview; //변수 설정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) { //카메라를 쓸 수 있는지 확인
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA); //처리해주세요 ->카메라가 필요하다고 전달
                return;
            }
        }

        //카메라 쓸 수 있는지 없는지 검사
        CameraManager manager = CameraManager.getCameraManager();
        if (!manager.checkCameraUsable(this)) {
            new AlertDialog.Builder(this)
                    .setMessage("카메라가 사용 불가합니다.") //텍스트 띄우고 밑에서 종료버튼 누르면 종료됨
                    .setNeutralButton("종료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0); //종료 시키세요
                        }
                    })
                    .show();
        }
        //내가 현재 쓸 수 있는 카메라 불러옴
        Camera camera = manager.getCamera();
        cameraPreview = new CameraPreview(this, camera); //this가 메인엑티비티랑 카메라 넘겨줌
        FrameLayout preview = findViewById(R.id.camera_preview); //아까 만들어 놓은 framelayout가져옴
        preview.addView(cameraPreview);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //권한 승인이 된 경우 다시 그리기
                    recreate();
                } else {
                    //권한 승인이 안 된 경우 종료
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public void changeCamera(View view) {
        CameraManager manager = CameraManager.getCameraManager();
        Camera camera = manager.getNextCamera(); //다음 카메라를 주세요 미리보기에 사용되는 카메라를 주세요
        cameraPreview.changeCamera(camera);
    }

}
