package projectmanager.dada.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.List;
import projectmanager.dada.R;
import projectmanager.dada.model.StatusType;
import projectmanager.dada.model.Task;

/**
 * jichao at 2016/12/03
 * 用来将任务集显示在手机上的adapter
 */
public class ViewMyPublishTaskAdapter extends ArrayAdapter<Task> {

    int resourceId;

    /**
     * Adapter的构造方法
     * @param context            当前活动的上下文
     * @param textViewResourceId ListView的子项布局id
     * @param tasks              要适配的数据
     */
    public ViewMyPublishTaskAdapter(Context context, int textViewResourceId, List<Task> tasks){
        super(context,textViewResourceId,tasks);
        resourceId = textViewResourceId;
    }

    /**
     * LIstView中每一个子项被滚动到屏幕的时候调用
     * position：滚到屏幕中的子项位置，可以通过这个位置拿到子项实例
     * convertView：之前加载好的布局进行缓存
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//子项的view
        TextView titleView = (TextView) view.findViewById(R.id.my_publish_task_title);
        TextView descriptionView = (TextView)view.findViewById(R.id.my_publish_task_content);
        TextView deadlineView = (TextView)view.findViewById(R.id.my_publish_task_deadline);
        TextView statusView = (TextView)view.findViewById(R.id.my_publish_task_status);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String deadlineString;
        if(task.getDeadline() != null){
            deadlineString = sdf.format(task.getDeadline());
        }else{
            deadlineString = "无限期";
        }
        titleView.setText(task.getTitle());
        descriptionView.setText(task.getDescription());
        deadlineView.setText(deadlineString);
        statusView.setText(StatusType.getTypeByStatusId(task.getStatus()));
        if(task.getStatus() == StatusType.WAITCONFIRM.getCode()){
            statusView.setTextColor(Color.RED);
        }else if(task.getStatus() == StatusType.GOINGON.getCode()){
            statusView.setTextColor(Color.rgb(250,128,10));
        }else if(task.getStatus() == StatusType.OPEN.getCode()){
            statusView.setTextColor(Color.rgb(0x00,0x9f,0xd3));
        }
        return view;
    }
}
