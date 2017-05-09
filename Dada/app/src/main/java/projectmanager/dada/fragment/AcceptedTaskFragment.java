package projectmanager.dada.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import projectmanager.dada.R;
import projectmanager.dada.adapter.ViewMyAcceptTaskAdapter;
import projectmanager.dada.model.StatusType;
import projectmanager.dada.model.Task;
import projectmanager.dada.model.User;
import projectmanager.dada.pages.MyAcceptTaskDetailActivity;
import projectmanager.dada.util.ApiManager;
import projectmanager.dada.util.DataManager;

/**
 * Created by tao on 2016/12/17.
 */

public class AcceptedTaskFragment extends Fragment {
    private GetMyAcceptTaskSetTask getMyAcceptSetTask;
    private View myAcceptTotalLinearLayout;
    private View progressView;
    private ListView myAcceptListView;
    private ArrayList<Task> myAcceptTaskList;
    private ViewMyAcceptTaskAdapter myAcceptTaskAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_accept_task, container, false);
        myAcceptListView = (ListView) view.findViewById(R.id.my_accept_task_list_view);
        progressView = view.findViewById(R.id.get_my_accept_task_progress);
        myAcceptTotalLinearLayout = view.findViewById(R.id.my_accept_total_linear_layout);
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        myAcceptTaskList = new ArrayList<>();
        tryGetMyAcceptTasks();
        new UpdateUserProfile().execute();
    }

    /**
     * 执行从服务器获取数据的动作
     */
    private void tryGetMyAcceptTasks(){
        showProgress(true);
        getMyAcceptSetTask = new GetMyAcceptTaskSetTask();
        getMyAcceptSetTask.execute((Void) null);
    }


    /**
     * 在用户从服务器获取自己发布的任务的时候
     * @param show 设定进度框显示与否
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            myAcceptTotalLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            myAcceptTotalLinearLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    myAcceptTotalLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            myAcceptTotalLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * 从服务器获取数据的线程
     */
    public class GetMyAcceptTaskSetTask extends AsyncTask<Void, Void, Boolean> {
        private User nowLoginUser;
        GetMyAcceptTaskSetTask(){
            nowLoginUser = DataManager.getInstance().getCurrentUser();
        }
        @Override
        protected void onPreExecute(){
            showProgress(true);
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            ArrayList<Task> acceptList = ApiManager.getInstance().handleGetAcceptTasks(
                    nowLoginUser.getUserId(),
                    0,10);
            if(acceptList == null) {
                return false;
            }else if(acceptList.isEmpty()){
                myAcceptTaskList = new ArrayList<>();
                rearrangeTaskSort();
                return true;
            }else{
                myAcceptTaskList = acceptList;
                rearrangeTaskSort();
                return true;
            }
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            getMyAcceptSetTask = null;
            showProgress(false);
            if (success == true) {
                myAcceptTaskAdapter = new ViewMyAcceptTaskAdapter(getActivity(),
                        R.layout.my_accept_task_view,myAcceptTaskList);
                myAcceptListView.setAdapter(myAcceptTaskAdapter);
                myAcceptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        Task clickTask = myAcceptTaskList.get(position);
                        DataManager.getInstance().setSelectedMyAcceptTask(clickTask);
                        Intent nextPage = new Intent(getActivity(),MyAcceptTaskDetailActivity.class);
                        startActivity(nextPage);
                    }
                });
                myAcceptTaskAdapter.notifyDataSetChanged();
            }
        }
        @Override
        protected void onCancelled() {
            getMyAcceptSetTask = null;
            showProgress(false);
        }
    }

    public class UpdateUserProfile extends AsyncTask<Void, Void, Boolean>{
        private User newLoginUser;
        @Override
        protected Boolean doInBackground(Void... voids) {
            newLoginUser = ApiManager.getInstance().handleGetUserById
                    (DataManager.getInstance().getCurrentUser().getUserId());
            if(newLoginUser == null){
                return false;
            }else{
                return true;
            }
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            if (success == true) {
                DataManager.getInstance().setCurrentUser(newLoginUser);
            }
        }
        @Override
        protected void onCancelled() { }
    }

    private void rearrangeTaskSort(){
        ArrayList<Task> confirmTasks = new ArrayList<>();
        ArrayList<Task> ongoingTasks = new ArrayList<>();
        ArrayList<Task> openTasks = new ArrayList<>();
        ArrayList<Task> finishTasks = new ArrayList<>();
        ArrayList<Task> cancelTask = new ArrayList<>();
        for(Task tempTask : myAcceptTaskList){
            if(tempTask.getStatus() == StatusType.WAITCONFIRM.getCode()){
                confirmTasks.add(tempTask);
            }else if(tempTask.getStatus() == StatusType.GOINGON.getCode()){
                ongoingTasks.add(tempTask);
            }else if(tempTask.getStatus() == StatusType.OPEN.getCode()){
                openTasks.add(tempTask);
            }else if(tempTask.getStatus() == StatusType.FINISHED.getCode()){
                finishTasks.add(tempTask);
            }else{
                cancelTask.add(tempTask);
            }
        }
        ArrayList<Task> newList = new ArrayList<>();
        newList.addAll(confirmTasks);
        newList.addAll(ongoingTasks);
        newList.addAll(openTasks);
        newList.addAll(finishTasks);
        newList.addAll(cancelTask);
        myAcceptTaskList = newList;
    }
}
