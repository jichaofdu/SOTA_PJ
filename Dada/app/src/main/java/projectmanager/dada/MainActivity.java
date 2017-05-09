package projectmanager.dada;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import projectmanager.dada.fragment.AcceptedTaskFragment;
import projectmanager.dada.fragment.NearbyFragment;
import projectmanager.dada.fragment.PublishedTaskFragment;
import projectmanager.dada.fragment.UserProfileFragment;
import projectmanager.dada.pages.PublishTaskStepOneActivity;

public class MainActivity extends AppCompatActivity implements OnClickListener{

//    private TextView publishTask;

    // 底部菜单4个Linearlayout
    private LinearLayout nearby;
    private LinearLayout accepted;
    private LinearLayout published;
    private LinearLayout profile;

    // 底部菜单4个ImageView
    private ImageView img_nearby;
    private ImageView img_accepted;
    private ImageView img_published;
    private ImageView img_profile;

    // 底部菜单4个菜单标题
    private TextView tv_nearby;
    private TextView tv_accepted;
    private TextView tv_published;
    private TextView tv_profile;

    private Fragment nearbyFragment;
    private Fragment acceptedFragment;
    private Fragment publishedFragment;
    private Fragment profileFragment;

    public FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;
    public final String tag0 = "0";
    public final String tag1 = "1";
    public final String tag2 = "2";
    public final String tag3 = "3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化控件
        initView();
        // 初始化底部按钮事件
        initEvent();
        // 初始化并设置当前Fragment
        onClick(nearby);
    }

    private void initEvent() {
        // 设置按钮监听
        nearby.setOnClickListener(this);
        accepted.setOnClickListener(this);
        published.setOnClickListener(this);
        profile.setOnClickListener(this);
    }

    private void initView() {
        // 底部菜单4个Linearlayout
        this.nearby = (LinearLayout) findViewById(R.id.nearby);
        this.accepted = (LinearLayout) findViewById(R.id.accepted);
        this.published = (LinearLayout) findViewById(R.id.published);
        this.profile = (LinearLayout) findViewById(R.id.profile);

        // 底部菜单4个ImageView
        this.img_nearby = (ImageView) findViewById(R.id.img_nearby);
        this.img_accepted = (ImageView) findViewById(R.id.img_accepted);
        this.img_published = (ImageView) findViewById(R.id.img_published);
        this.img_profile = (ImageView) findViewById(R.id.img_profile);

        // 底部菜单4个菜单标题
        this.tv_nearby = (TextView) findViewById(R.id.txt_nearby);
        this.tv_accepted = (TextView) findViewById(R.id.txt_accepted);
        this.tv_published = (TextView) findViewById(R.id.txt_published);
        this.tv_profile = (TextView) findViewById(R.id.txt_profile);
    }

    private void initFragment(int index) {
        // 由于是引用了V4包下的Fragment，所以这里的管理器要用getSupportFragmentManager获取
        FragmentManager fragmentManager = getSupportFragmentManager();
        // 开启事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 隐藏所有Fragment
        hideFragment(transaction);
        switch (index) {
            case 0:
                if (nearbyFragment == null) {
                    nearbyFragment = new NearbyFragment();
                    transaction.add(R.id.fl_content, nearbyFragment, tag0);
                } else {

                    transaction.show(nearbyFragment);
                    nearbyFragment.onResume();
                }
                break;
            case 1:
                if (acceptedFragment == null) {
                    acceptedFragment = new AcceptedTaskFragment();
                    transaction.add(R.id.fl_content, acceptedFragment, tag1);
                } else {
                    transaction.show(acceptedFragment);
                    acceptedFragment.onResume();
                }
                break;
            case 2:
                if (publishedFragment == null) {
                    publishedFragment = new PublishedTaskFragment();
                    transaction.add(R.id.fl_content, publishedFragment, tag2);
                } else {
                    transaction.show(publishedFragment);
                    publishedFragment.onResume();
                }
                break;
            case 3:
                if (profileFragment == null) {
                    profileFragment = new UserProfileFragment();
                    transaction.add(R.id.fl_content, profileFragment, tag3);
                } else {
                    transaction.show(profileFragment);
                    profileFragment.onResume();
                }
                break;
            default:
                break;
        }
        transaction.addToBackStack(null);
        // 提交事务
        transaction.commit();
    }

    //隐藏Fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (nearbyFragment != null) {
            transaction.hide(nearbyFragment);
        }
        if (acceptedFragment != null) {
            transaction.hide(acceptedFragment);
        }
        if (publishedFragment != null) {
            transaction.hide(publishedFragment);
        }
        if (profileFragment != null) {
            transaction.hide(profileFragment);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentByTag(tag3);
        f.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        // 在每次点击后将所有的底部按钮(ImageView,TextView)颜色改为灰色，然后根据点击着色
        restartBotton();
        // ImageView和TetxView置为绿色，页面随之跳转
        switch (v.getId()) {
            case R.id.nearby:
                img_nearby.setImageResource(R.drawable.nearby_selected);
                tv_nearby.setTextColor(getResources().getColor(R.color.dada_blue));
                initFragment(0);
                break;
            case R.id.accepted:
//                img_accepted.setImageResource(R.drawable.accepted_task_selected);
//                tv_accepted.setTextColor(getResources().getColor(R.color.dada_blue));
//                initFragment(1);
                break;
            case R.id.published:
//                img_published.setImageResource(R.drawable.published_task_selected);
//                tv_published.setTextColor(getResources().getColor(R.color.dada_blue));
//                initFragment(2);
                break;
            case R.id.profile:
//                img_profile.setImageResource(R.drawable.profile_selected);
//                tv_profile.setTextColor(getResources().getColor(R.color.dada_blue));
//                initFragment(3);
                break;

            default:
                break;
        }
    }

    private void restartBotton() {
        // ImageView置为灰色
        img_nearby.setImageResource(R.drawable.nearby_normal);
        img_accepted.setImageResource(R.drawable.accepted_task_normal);
        img_published.setImageResource(R.drawable.published_task_normal);
        img_profile.setImageResource(R.drawable.profile_normal);
        // TextView置为白色
        tv_nearby.setTextColor(getResources().getColor(R.color.gray));
        tv_accepted.setTextColor(getResources().getColor(R.color.gray));
        tv_published.setTextColor(getResources().getColor(R.color.gray));
        tv_profile.setTextColor(getResources().getColor(R.color.gray));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.publish_new_task:
                Intent nextPage = new Intent(MainActivity.this, PublishTaskStepOneActivity.class);
                startActivity(nextPage);
                return true;
            case R.id.logout:
                finish();
                //DataManager.getInstance().setCurrentUser(null);
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
