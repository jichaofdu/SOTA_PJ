package projectmanager.dada.pages;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import projectmanager.dada.R;

/**
 * Created by JScarlet on 2016/12/19.
 */
public class ProjectInstructionActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private ImageView[] imageViews;
    private int[] imgIdArray;
    private String[] description;
    private TextView textDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_instruction);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        imgIdArray = new int[]{R.drawable.item1, R.drawable.item2, R.drawable.item3, R.drawable.item4};
        textDescription = (TextView) findViewById(R.id.viewPagerDescription);
        description = new String[]{
                "哒哒找人是一个基于信誉值的任务互助平台。您可以利用本应用在指定地点发布任务，轻松解决诸如“代领快递”、“借用物品”、“问卷填写”的生活问题~",
                "投之以桃，报之以李~\n发布任务会消耗2点基础信誉值+自定义悬赏信誉值，接受任务会获得3点基础信誉值+任务悬赏信誉值奖励。",
                "取消任务须谨慎~\n任务发布者需要在接受者完成任务后进行确认。若任务发布者在发布的任务未被接受时取消任务，不会受到信誉值惩罚，但若该任务已被人接受，则发布者会被扣除6点信誉值作为惩罚~",
                "接受任务量力而行~\n若要中途放弃自己已接受的任务，将会被扣除5点信誉值。若自己已接受的任务超过deadline还未完成，则会被扣除6点信誉值~"
        };

     /*   imageViews = new ImageView[imgIdArray.length];
        for(int i = 0; i < imageViews.length; i++){
            ImageView imageView = new ImageView(this);
            imageViews[i] = imageView;
            imageView.setBackgroundResource(imgIdArray[i]);
        }*/

        if (imgIdArray.length == 1) {
            imageViews = new ImageView[2];
            for (int i = 0; i < (imageViews.length); i++) {
                ImageView imageView = new ImageView(this);
                imageViews[i] = imageView;
                imageView.setBackgroundResource(imgIdArray[0]);
            }

            viewPager.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View arg0, MotionEvent arg1) {
                    return true;
                }
            });
        } else if (imgIdArray.length == 2 || imgIdArray.length == 3) {
            imageViews = new ImageView[imgIdArray.length * 2];
            for (int i = 0; i < (imageViews.length); i++) {
                ImageView imageView = new ImageView(this);
                imageViews[i] = imageView;
                imageView.setBackgroundResource(imgIdArray[(i > (imgIdArray.length - 1)) ? i - imgIdArray.length : i]);
            }
        } else {
            imageViews = new ImageView[imgIdArray.length];
            for (int i = 0; i < imageViews.length; i++) {
                ImageView imageView = new ImageView(this);
                imageViews[i] = imageView;
                imageView.setBackgroundResource(imgIdArray[i]);
            }
        }

        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(imageViews.length * 100);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        textDescription.setText(description[position % imageViews.length]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(imageViews[position % imageViews.length]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(imageViews[position % imageViews.length], 0);
            return imageViews[position % imageViews.length];
        }
    }

}
