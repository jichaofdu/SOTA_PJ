package projectmanager.dada.pages;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import projectmanager.dada.R;
import projectmanager.dada.model.StatusType;
import projectmanager.dada.model.Tag;
import projectmanager.dada.model.TagListView;
import projectmanager.dada.model.Task;
import projectmanager.dada.util.ApiManager;
import projectmanager.dada.util.DataManager;

public class MyAcceptTaskDetailActivity extends AppCompatActivity {

    private View myAcceptTaskDetailView;
    private Task selectedTask;
    private DoneMyFinishTask doneTask;
    private QuitMyAcceptTask quitTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedTask = DataManager.getInstance().getSelectedMyAcceptTask();
        setContentView(R.layout.activity_my_accept_task_detail);
        myAcceptTaskDetailView = findViewById(R.id.activity_my_accept_task_detail);

        TextView titleView = (TextView)myAcceptTaskDetailView.findViewById(R.id.my_accept_detail_title);
        titleView.setText(selectedTask.getTitle());
        TextView publisher = (TextView)myAcceptTaskDetailView.findViewById(R.id.my_accept_detail_publisher);
        publisher.setText(selectedTask.getPublisher().getUsername());
        TextView publishTimeView = (TextView)myAcceptTaskDetailView.findViewById(R.id.my_accept_detail_publish_time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        publishTimeView.setText(sdf.format(selectedTask.getPublishedTime()));
        TextView deadlineView = (TextView)myAcceptTaskDetailView.findViewById(R.id.my_accept_detail_deadline);
        String deadlineString;
        if(selectedTask.getDeadline() != null){
            deadlineString = sdf.format(selectedTask.getDeadline());
        }else{
            deadlineString = "无限期";
        }
        deadlineView.setText(deadlineString);
        TextView descriptionView = (TextView)myAcceptTaskDetailView.findViewById(R.id.my_accept_detail_description);
        descriptionView.setText(selectedTask.getDescription());
        TextView locationView = (TextView)myAcceptTaskDetailView.findViewById(R.id.my_accept_detail_location);
        locationView.setText(selectedTask.getLocation().getDescription());
        TextView statusView = (TextView)myAcceptTaskDetailView.findViewById(R.id.my_accept_detail_status);
        statusView.setText(StatusType.getTypeByStatusId(selectedTask.getStatus()));
        TextView creditView = (TextView)myAcceptTaskDetailView.findViewById(R.id.my_accept_detail_spend_credit);
        creditView.setText("" + selectedTask.getCredit());
        TagListView mTagListView = (TagListView) findViewById(R.id.my_accept_detail_tag_view);
        List<Tag> mTags = new ArrayList<>();
        for (int i = 0; i < selectedTask.getTags().length; i++) {
            Tag tag = new Tag();
            tag.setId(i);
            tag.setChecked(false);
            tag.setTitle(selectedTask.getTags()[i]);
            mTags.add(tag);
        }
        mTagListView.setTags(mTags);

        Button finishButton = (Button)findViewById(R.id.my_accept_detail_finish_task);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               clickFinishButton();
            }
        });
        if(selectedTask.getStatus() == StatusType.GOINGON.getCode()){
            finishButton.setVisibility(View.VISIBLE);
        }

        Button quitButton = (Button)findViewById(R.id.my_accept_detail_quit_task);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickQuitButton();
            }
        });
        if(selectedTask.getStatus() == StatusType.GOINGON.getCode()
                || selectedTask.getStatus() == StatusType.WAITCONFIRM.getCode()){
            quitButton.setVisibility(View.VISIBLE);
        }
    }

    private void clickFinishButton(){
        new AlertDialog.Builder(this).setTitle("确认你已完成此任务吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doneTask = new DoneMyFinishTask();
                        doneTask.execute();
                    }
                })
                .setNegativeButton("没有完成", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                }).show();
    }

    private void clickQuitButton(){
        new AlertDialog.Builder(this).setTitle("确认要放弃这个任务吗？（将受到5点信用惩罚）")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quitTask = new QuitMyAcceptTask();
                        quitTask.execute();
                    }
                })
                .setNegativeButton("不放弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                }).show();
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

    /**
     * 连接服务器完成任务
     */
    public class DoneMyFinishTask extends AsyncTask<Void, Void, Boolean> {
        private String doneResult;
        @Override
        protected Boolean doInBackground(Void... voids) {
            doneResult = "";
            doneResult = ApiManager.getInstance().handleDoneTask(
                    selectedTask.getTaskId(),
                    DataManager.getInstance().getCurrentUser().getUserId());
            if(doneResult != null && doneResult.equals("succeed")){
                return true;
            }else{
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if(success == true){
                Toast.makeText(MyAcceptTaskDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(MyAcceptTaskDetailActivity.this, "操作失败：原因不明", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled() {
            Toast.makeText(MyAcceptTaskDetailActivity.this, "你取消了本次操作", Toast.LENGTH_SHORT).show();
        }
    }

    public class QuitMyAcceptTask extends AsyncTask<Void, Void, Boolean> {
        private String quitResult;
        @Override
        protected Boolean doInBackground(Void... voids) {
            quitResult = "";
            quitResult = ApiManager.getInstance().handleQuitTask(
                    selectedTask.getTaskId(),
                    DataManager.getInstance().getCurrentUser().getUserId());
            if(quitResult != null && quitResult.equals("succeed")){
                return true;
            }else{
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if(success == true){
                Toast.makeText(MyAcceptTaskDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                if(quitResult.contains("Credit")){
                    Toast.makeText(MyAcceptTaskDetailActivity.this, "信用值不足，无法取消", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MyAcceptTaskDetailActivity.this, "操作失败：原因不明", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onCancelled() {
            Toast.makeText(MyAcceptTaskDetailActivity.this, "你取消了本次操作", Toast.LENGTH_SHORT).show();
        }
    }
}
