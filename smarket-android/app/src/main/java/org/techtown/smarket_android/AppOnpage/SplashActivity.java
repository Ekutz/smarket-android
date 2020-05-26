package org.techtown.smarket_android.AppOnpage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.techtown.smarket_android.MainNavigation.MainNavigationActivity;
import org.techtown.smarket_android.R;

import java.lang.ref.WeakReference;

public class SplashActivity extends AppCompatActivity {

    private SplashHandler splashHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apponpage_splash);

        setSplashHandler();

        startMainNavigationActivity();
    }

    private void setSplashHandler() {
        splashHandler = new SplashHandler(this);
    }

    private void startMainNavigationActivity() {
        splashHandler.sendEmptyMessageDelayed(0, 3000);
    }

    private static class SplashHandler extends Handler { // 엄연히 class 이므로 보통의 컨벤션에서는 대문자로 시작합니다.
        WeakReference<SplashActivity> activity;

        public SplashHandler(SplashActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            activity.get().startActivity(new Intent(activity.get(), MainNavigationActivity.class)); //로딩이 끝난 후, ChoiceFunction 이동
            activity.get().finish(); // 로딩페이지 Activity stack에서 제거
            super.handleMessage(msg);
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }
}
