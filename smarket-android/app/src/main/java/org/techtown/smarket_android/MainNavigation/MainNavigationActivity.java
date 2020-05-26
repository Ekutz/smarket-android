package org.techtown.smarket_android.MainNavigation;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.techtown.smarket_android.Alaram.alarm_fragment;
import org.techtown.smarket_android.Home.home_fragment;
import org.techtown.smarket_android.R;
import org.techtown.smarket_android.User.user_login_fragment;
import org.techtown.smarket_android.User.user_login_success;
import org.techtown.smarket_android.searchItemList.search_fragment;


public class MainNavigationActivity extends AppCompatActivity {

    private Context mContext = this;
    private BottomNavigationView bottomNavigationView;
    private home_fragment home_fragment1;
    //user_login_success user_fragment2; // 로그인 완료 창
    private user_login_fragment user_fragment2; // 로그인 창
    private alarm_fragment alarm_fragment3;
    private search_fragment search_fragment4;

    private long firstBack = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setWidgets();

        setBottomNavigationView();

        if (home_fragment1 == null) {
            home_fragment1 = new home_fragment();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, home_fragment1).commitAllowingStateLoss();
    }

    private void setWidgets() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView); //Bottom Navigation View 아이디 할당
    }

    private void setBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                switch (menuItem.getItemId()) { //menu_bottom.xml에서 지정해줬던 아이디 값을 받아와서 각 아이디값마다 다른 이벤트를 발생시킵니다.
                    case R.id.tab1:
                        if (home_fragment1 == null) { // Fragment 여러개의 객체를 만들면서 자원을 낭비할 필요가 없습니다
                            home_fragment1 = new home_fragment();// 스마켓 홈 창
                        }
                        ft.replace(R.id.main_layout, home_fragment1).addToBackStack(null).commitAllowingStateLoss();
                        break;
                    case R.id.tab2:
                        if (user_fragment2 == null) {
                            //user_fragment2 = new user_login_success(); // 로그인 완료 창
                            user_fragment2 = new user_login_fragment(); // 로그인 창
                        }
                        ft.replace(R.id.main_layout, user_fragment2).commitAllowingStateLoss(); //로그인 완료 창
                        //getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, user_fragment2).commitAllowingStateLoss(); // 로그인 창
                        break;
                    case R.id.tab3:
                        if (alarm_fragment3 == null) {
                            alarm_fragment3 = new alarm_fragment(); // 최저가 알림창
                        }
                        ft.replace(R.id.main_layout, alarm_fragment3).addToBackStack(null).commitAllowingStateLoss();
                        break;
                    case R.id.tab4:
                        if (search_fragment4 == null) {
                            search_fragment4 = new search_fragment();
                        }
                        ft.replace(R.id.main_layout, search_fragment4).addToBackStack(null).commitAllowingStateLoss();
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstBack < 2000L) {
            super.onBackPressed();
        } else {
            firstBack = System.currentTimeMillis();
            Toast.makeText(mContext, "뒤로 가기 두번 2초 안에 하면 앱 종료", Toast.LENGTH_SHORT).show();
        }
    }
}


