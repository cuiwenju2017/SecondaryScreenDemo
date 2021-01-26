package com.example.secondaryscreendemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chaychan.library.BottomBarItem;
import com.chaychan.library.BottomBarLayout;
import com.example.secondaryscreendemo.adapter.MyAdapter;
import com.example.secondaryscreendemo.view.SecondaryScreenView;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SecondaryScreenView secondaryScreenView;
    private ImageView iv_head, iv_out;
    private LinearLayout ll;
    private RelativeLayout rl;
    private long mFirstPressedTime;
    private ViewPager vp_content;
    private BottomBarLayout bbl;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private RotateAnimation mRotateAnimation;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();

        ImmersionBar.with(this)
                .titleBar(R.id.ll)
                .keyboardEnable(true)
                .init();

        ImmersionBar.with(this)
                .titleBarMarginTop(R.id.rl)
                .keyboardEnable(true)
                .init();

        //头像点击
        iv_head.setOnClickListener(v -> secondaryScreenView.translateToSecondaryView());

        //退出负一屏点击
        iv_out.setOnClickListener(v -> secondaryScreenView.translateToMainView());
    }

    public void initView() {
        vp_content = findViewById(R.id.vp_content);
        bbl = findViewById(R.id.bbl);
        secondaryScreenView = findViewById(R.id.secondaryScreenView);
        iv_head = findViewById(R.id.iv_head);
        iv_out = findViewById(R.id.iv_out);
        ll = findViewById(R.id.ll);
        rl = findViewById(R.id.rl);
        vp_content = findViewById(R.id.vp_content);
        bbl = findViewById(R.id.bbl);
    }

    private void initData() {
        HomeFragment homeFragment = new HomeFragment();
        VideoFragment videoFragment = new VideoFragment();
        NewsFragment newsFragment = new NewsFragment();
        MyFragment myFragment = new MyFragment();
        mFragmentList.add(homeFragment);
        mFragmentList.add(videoFragment);
        mFragmentList.add(newsFragment);
        mFragmentList.add(myFragment);
    }

    public void initListener() {
        vp_content.setAdapter(new MyAdapter(getSupportFragmentManager(), mFragmentList));
        bbl.setViewPager(vp_content);

        bbl.setUnread(0, 20);//设置第一个页签的未读数为20
        bbl.setUnread(1, 21);//设置第二个页签的未读数
        bbl.showNotify(2);//设置第三个页签显示提示的小红点
        bbl.setMsg(3, "新年快乐");//设置第四个页签显示NEW提示文字

        vp_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    bbl.setUnread(0, 0);//设置第一个页全部已读
                    ll.setVisibility(View.VISIBLE);
                    ImmersionBar.with(MainActivity.this)
                            .titleBar(R.id.ll)
                            .statusBarDarkFont(false)//状态栏字体是深色，不写默认为亮色
                            .keyboardEnable(true)
                            .init();
                } else if (position == 1) {
                    bbl.setUnread(1, 0);//设置第二个页全部已读
                    ll.setVisibility(View.VISIBLE);
                    ImmersionBar.with(MainActivity.this)
                            .titleBar(R.id.ll)
                            .statusBarDarkFont(false)//状态栏字体是深色，不写默认为亮色
                            .keyboardEnable(true)
                            .init();
                } else if (position == 2) {
                    bbl.hideNotify(2);//设置第三页小红点隐藏
                    ll.setVisibility(View.GONE);
                    ImmersionBar.with(MainActivity.this)
                            .titleBar(R.id.ll)
                            .statusBarDarkFont(true)//状态栏字体是深色，不写默认为亮色
                            .keyboardEnable(true)
                            .init();
                } else if (position == 3) {
                    bbl.hideMsg(3);//设置第四页文字描述隐藏
                    ll.setVisibility(View.VISIBLE);
                    ImmersionBar.with(MainActivity.this)
                            .titleBar(R.id.ll)
                            .statusBarDarkFont(false)//状态栏字体是深色，不写默认为亮色
                            .keyboardEnable(true)
                            .init();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bbl.setOnItemSelectedListener((bottomBarItem, previousPosition, currentPosition) -> {
            Log.i("MainActivity", "position: " + currentPosition);
            if (currentPosition == 0) {
                //如果是第一个，即首页
                if (previousPosition == currentPosition) {
                    //如果是在原来位置上点击,更换首页图标并播放旋转动画
                    if (mRotateAnimation != null && !mRotateAnimation.hasEnded()) {
                        //如果当前动画正在执行
                        return;
                    }

                    bottomBarItem.setSelectedIcon(R.mipmap.tab_loading);//更换成加载图标

                    //播放旋转动画
                    if (mRotateAnimation == null) {
                        mRotateAnimation = new RotateAnimation(0, 360,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                0.5f);
                        mRotateAnimation.setDuration(800);
                        mRotateAnimation.setRepeatCount(-1);
                    }
                    ImageView bottomImageView = bottomBarItem.getImageView();
                    bottomImageView.setAnimation(mRotateAnimation);
                    bottomImageView.startAnimation(mRotateAnimation);//播放旋转动画

                    //模拟数据刷新完毕
                    mHandler.postDelayed(() -> {
                        boolean tabNotChanged = bbl.getCurrentItem() == currentPosition; //是否还停留在当前页签
                        bottomBarItem.setSelectedIcon(R.mipmap.tab_home_selected);//更换成首页原来选中图标
                        cancelTabLoading(bottomBarItem);
                    }, 3000);
                    return;
                }
            }

            //如果点击了其他条目
            BottomBarItem bottomItem = bbl.getBottomItem(0);
            bottomItem.setSelectedIcon(R.mipmap.tab_home_selected);//更换为原来的图标

            cancelTabLoading(bottomItem);//停止旋转动画
        });
    }

    /**
     * 停止首页页签的旋转动画
     */
    private void cancelTabLoading(BottomBarItem bottomItem) {
        Animation animation = bottomItem.getImageView().getAnimation();
        if (animation != null) {
            animation.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if (!secondaryScreenView.isStillMainView()) {
            secondaryScreenView.translateToMainView();
        } else {
            if (System.currentTimeMillis() - mFirstPressedTime < 2000) {
                super.onBackPressed();
                finish();
            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mFirstPressedTime = System.currentTimeMillis();
            }
        }
    }
}