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

public class MyPublishTaskDetailActivity extends AppCompatActivity {

    private View myPublishTaskDetailView;
    private View progressView;
    private Task selectedTask;
    private ConfirmMyPublishTask confirmTask;
    private CancelMyPublishTask  cancelTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedTask = DataManager.getInstance().getSelectedMyPublishTask();
        setContentView(R.layout.activity_my_publish_task_detail);

        myPublishTaskDetailView = findViewById(R.id.activity_my_publish_task_detail);
        progressView = findViewById(R.id.get_my_publish_task_progress);

        TextView titleView = (TextView)myPublishTaskDetailView.findViewById(R.id.my_publish_detail_title);
        titleView.setText(selectedTask.getTitle());
        TextView publishTimeView = (TextView)myPublishTaskDetailView.findViewById(R.id.my_publish_detail_publish_time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        publishTimeView.setText(sdf.format(selectedTask.getPublishedTime()));
        TextView deadlineView = (TextView)myPublishTaskDetailView.findViewById(R.id.my_publish_detail_deadline);
        String deadlineString;
        if(selectedTask.getDeadline() != null){
            deadlineString = sdf.format(selectedTask.getDeadline());
        }else{
            deadlineString = "无限期";
        }
        deadlineView.setText(deadlineString);
        TextView descriptionView = (TextView)myPublishTaskDetailView.findViewById(R.id.my_publish_detail_description);
        descriptionView.setText(selectedTask.getDescription());
        TextView locationView = (TextView)myPublishTaskDetailView.findViewById(R.id.my_publish_detail_location);
        locationView.setText(selectedTask.getLocation().getDescription());
        TextView statusView = (TextView)myPublishTaskDetailView.findViewById(R.id.my_publish_detail_status);
        statusView.setText(StatusType.getTypeByStatusId(selectedTask.getStatus()));
        TextView accepterView = (TextView)myPublishTaskDetailView.findViewById(R.id.my_publish_detail_acceptor);
        if(selectedTask.getAccepter() == null){
            accepterView.setText("尚无人接受任务");
        }else{
            accepterView.setText(selectedTask.getAccepter().getUsername());
        }
        TextView creditView = (TextView)myPublishTaskDetailView.findViewById(R.id.my_publish_detail_spend_credit);
        creditView.setText("" + selectedTask.getCredit());
        TagListView mTagListView = (TagListView) findViewById(R.id.my_publish_detail_tag_view);
        List<Tag> mTags = new ArrayList<Tag>();
        for (int i = 0; i < selectedTask.getTags().length; i++) {
            Tag tag = new Tag();
            tag.setId(i);
            tag.setChecked(false);
            tag.setTitle(selectedTask.getTags()[i]);
            mTags.add(tag);
        }
        mTagListView.setTags(mTags);

        Button cancelButton = (Button)findViewById(R.id.my_publish_detail_cancel_task);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCancelButton();
            }
        });
        if(selectedTask.getStatus() != StatusType.CLOSED.getCode()){
            cancelButton.setVisibility(View.VISIBLE);
        }

        Button confirmButton = (Button)findViewById(R.id.my_publish_detail_confirm_task);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickConfirmButton();
            }
        });
        if(selectedTask.getStatus() == StatusType.WAITCONFIRM.getCode()){
            confirmButton.setVisibility(View.VISIBLE);
        }

    }

    private void clickCancelButton(){
        if(selectedTask.getStatus() == StatusType.OPEN.getCode()){
            new AlertDialog.Builder(this).setTitle("确认要取消这个任务吗？（无信用惩罚）")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelTask = new CancelMyPublishTask();
                            cancelTask.execute();
                        }
                    })
                    .setNegativeButton("不取消了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }
                    }).show();
        }else{
            new AlertDialog.Builder(this).setTitle("确认要取消这个任务吗？（将受到6点信用惩罚）")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelTask = new CancelMyPublishTask();
                            cancelTask.execute();
                        }
                    })
                    .setNegativeButton("不取消了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }
                    }).show();
        }

    }

    private void clickConfirmButton(){
        new AlertDialog.Builder(this).setTitle("确定此任务已完成吗？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        confirmTask = new ConfirmMyPublishTask();
                        confirmTask.execute();
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
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
     * 连接服务器取消任务的线程
     */
    public class CancelMyPublishTask extends AsyncTask<Void, Void, Boolean> {
        private String cancelResult;
        @Override
        protected Boolean doInBackground(Void... voids) {
            cancelResult = "";
            cancelResult = ApiManager.getInstance().handleCancelTask(
                    DataManager.getInstance().getSelectedMyPublishTask().getTaskId(),
                    DataManager.getInstance().getCurrentUser().getUserId());
            if(cancelResult != null && cancelResult.equals("succeed")){
                return true;
            }else{
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if(success == true){
                Toast.makeText(MyPublishTaskDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                if(cancelResult.contains("Credit")){
                    Toast.makeText(MyPublishTaskDetailActivity.this, "信用值不足，无法取消", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MyPublishTaskDetailActivity.this, "取消失败：原因不明", Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        protected void onCancelled() {
            Toast.makeText(MyPublishTaskDetailActivity.this, "你取消了本次操作", Toast.LENGTH_SHORT).show();
        }
    }

    public class ConfirmMyPublishTask extends AsyncTask<Void, Void, Boolean> {
        private String confirmResult;
        @Override
        protected Boolean doInBackground(Void... voids) {
            confirmResult = "";
            confirmResult = ApiManager.getInstance().handleConfirmTask(
                    DataManager.getInstance().getSelectedMyPublishTask().getTaskId(),
                    DataManager.getInstance().getCurrentUser().getUserId());
            if(confirmResult != null && confirmResult.equals("succeed")){
                return true;
            }else{
                return false;
            }
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if(success == true){
                Toast.makeText(MyPublishTaskDetailActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(MyPublishTaskDetailActivity.this, "确认失败：原因不明", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        protected void onCancelled() {
            Toast.makeText(MyPublishTaskDetailActivity.this, "你取消了本次操作", Toast.LENGTH_SHORT).show();
        }

    }

}
