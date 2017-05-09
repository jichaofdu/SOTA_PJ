package projectmanager.dada.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import projectmanager.dada.R;
import projectmanager.dada.model.SexType;
import projectmanager.dada.model.User;
import projectmanager.dada.pages.ResetPasswordActivity;
import projectmanager.dada.pages.UserBioModifyActivity;
import projectmanager.dada.pages.UsernameModifyActivity;
import projectmanager.dada.util.ApiManager;
import projectmanager.dada.util.DataManager;
import projectmanager.dada.util.MPoPuWindow;

/**
 * Created by tao on 2016/12/17.
 */

public class UserProfileFragment extends Fragment {

    private User currentUser;
    private ImageView avatar;
    private TextView username;
    private TextView sex;
    private TextView phone;
    private TextView bio;
    private TextView resetPassword;
    private TextView creditView;
    private Type type;
    private MPoPuWindow puWindow;
    private File file;
    private Uri ImgUri;
    private String picPath = null;
    private Uri temp;

    public enum Type {
        PHONE, CAMERA
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_information, container, false);
        currentUser = DataManager.getInstance().getCurrentUser();
        avatar = (ImageView) view.findViewById(R.id.avatar);
        username = (TextView) view.findViewById(R.id.username);
        sex = (TextView) view.findViewById(R.id.sex);
        phone = (TextView) view.findViewById(R.id.phone);
        bio = (TextView) view.findViewById(R.id.bio);
        resetPassword = (TextView) view.findViewById(R.id.reset_password);
        creditView = (TextView)view.findViewById(R.id.my_credit);
        if(currentUser != null){
            if(currentUser.getUsername() != null && !currentUser.getUsername().equals("")){
                username.setText(currentUser.getUsername());
            }else {
                username.setText("未填写");
            }

            if(currentUser.getAvatar() != null && !currentUser.getAvatar().equals("")){
                UserAvatar userAvatar = new UserAvatar(currentUser.getAvatar());
                userAvatar.execute((Void) null);
            }

            if(currentUser.getSex() <= 3) {
                sex.setText(SexType.getTypeBySexId(currentUser.getSex()));
            }else {
                sex.setText(SexType.getTypeBySexId(0));
            }

            if(currentUser.getPhone() != null && !currentUser.getPhone().equals("")){
                phone.setText(currentUser.getPhone());
            }else {
                phone.setText("未绑定手机");
            }

            if(currentUser.getBio() != null && !currentUser.getBio().equals("")){
                bio.setText(currentUser.getBio());
            }else {
                bio.setText("未填写");
            }
            creditView.setText("" + currentUser.getCredit());
        }

        View avatarView = view.findViewById(R.id.avatarLayout);
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                puWindow = new MPoPuWindow(getActivity(), getActivity());
                puWindow.showPopupWindow(view.findViewById(R.id.avatarLayout));
                puWindow.setOnGetTypeClckListener(new MPoPuWindow.onGetTypeClckListener() {

                    @Override
                    public void getType(Type type) {
                        UserProfileFragment.this.type = type;
                    }

                    @Override
                    public void getImgUri(Uri ImgUri, File file) {
                        UserProfileFragment.this.ImgUri = ImgUri;
                        UserProfileFragment.this.file = file;
                    }
                });

            }
        });

        View usernameView = view.findViewById(R.id.usernameLayout);
        usernameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UsernameModifyActivity.class);
                startActivity(intent);
            }
        });

        View sexView = view.findViewById(R.id.sexLayout);
        sexView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("请选择性别");
                final String[] sexStrs = new String[]{SexType.MALE.getName(),
                        SexType.FEMALE.getName(), SexType.OTHER.getName() };
                int tempSex = currentUser.getSex();
                if(tempSex == 0){
                    tempSex = 3;
                }
                builder.setSingleChoiceItems(sexStrs, tempSex - 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currentUser.setSex(i + 1);
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UserInfoTask userInfoTask = new UserInfoTask(currentUser.getUsername(), currentUser.getSex(), currentUser.getBio());
                        userInfoTask.execute((Void) null);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });


        View bioView = view.findViewById(R.id.bioLayout);
        bioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), UserBioModifyActivity.class);
                startActivity(intent);
            }
        });

        View passwordView = view.findViewById(R.id.passwordLayout);
        passwordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser = DataManager.getInstance().getCurrentUser();
        if(currentUser != null){
            if(currentUser.getUsername() != null && !currentUser.getUsername().equals("")){
                username.setText(currentUser.getUsername());
            }else {
                username.setText("未填写");
            }

            if(currentUser.getSex() <= 3) {
                sex.setText(SexType.getTypeBySexId(currentUser.getSex()));
            }else {
                sex.setText(SexType.getTypeBySexId(0));
            }

            if(currentUser.getPhone() != null && !currentUser.getPhone().equals("")){
                phone.setText(currentUser.getPhone());
            }else {
                phone.setText("未绑定手机");
            }

            if(currentUser.getBio() != null && !currentUser.getBio().equals("")){
                bio.setText(currentUser.getBio());
            }else {
                bio.setText("未填写");
            }
        }else{
            if(currentUser.getUsername() != null && !currentUser.getUsername().equals("")){
                username.setText(currentUser.getUsername());
            }else {
                username.setText("未填写");
            }

            if(currentUser.getAvatar() != null && !currentUser.getAvatar().equals("")){
                UserAvatar userAvatar = new UserAvatar(currentUser.getAvatar());
                userAvatar.execute((Void) null);
            }
            if(currentUser.getSex() <= 3) {
                sex.setText(SexType.getTypeBySexId(currentUser.getSex()));
            }else {
                sex.setText(SexType.getTypeBySexId(0));
            }
            if(currentUser.getPhone() != null && !currentUser.getPhone().equals("")){
                phone.setText(currentUser.getPhone());
            }else {
                phone.setText("未绑定手机");
            }
            if(currentUser.getBio() != null && !currentUser.getBio().equals("")){
                bio.setText(currentUser.getBio());
            }else {
                bio.setText("未填写");
            }
            creditView.setText("" + currentUser.getCredit());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (ImgUri != null) {
                puWindow.onPhoto(ImgUri, 300, 300);
            }
        } else if (requestCode == 2) {
            if (data != null) {
                Uri uri = data.getData();
                temp = uri;
                puWindow.onPhoto(uri, 300, 300);
            }
        } else if (requestCode == 3) {
            if (type == Type.PHONE) {
                if (data != null) {
                    try{
                        String[] pojo = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getActivity().managedQuery(temp, pojo, null, null, null);
                        if(cursor != null){
                            ContentResolver cr = getActivity().getContentResolver();
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            String path = cursor.getString(column_index);
                            if(path.endsWith("jpg") || path.endsWith("png")){
                                picPath = path;
                                UploadFileTask uploadFileTask = new UploadFileTask(getActivity());
                                uploadFileTask.execute(picPath);
                            }else {
                                alert();
                            }
                        }else {
                            alert();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } else if (type == Type.CAMERA) {
                avatar.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                UploadFileTask uploadFileTask = new UploadFileTask(getActivity());
                uploadFileTask.execute(file.getPath());
            }
        }
    }

    private void alert() {
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("提示")
                .setMessage("您选择的不是有效的图片")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                picPath = null;
                            }
                        })
                .create();
        dialog.show();
    }

    public class UserInfoTask extends AsyncTask<Void, Void, Boolean> {
        private final String username;
        private final int sexId;
        private final String bio;
        private User currentUser;

        UserInfoTask(String username, int sex, String bio) {
            this.username = username;
            this.sexId = sex;
            this.bio = bio;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            currentUser = ApiManager.getInstance().handleUpdateProfile(DataManager.getInstance().getCurrentUser().getUserId(), username, sexId, DataManager.getInstance().getCurrentUser().getAvatar(), bio);
            if(currentUser == null){
                return false;
            }else{
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                DataManager.getInstance().setCurrentUser(currentUser);
                sex.setText(SexType.getTypeBySexId(currentUser.getSex()));
            }else {
                Toast.makeText(getActivity(), "操作失败：原因不明", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onCancelled() {
        }
    }

    public class UploadFileTask extends AsyncTask<String, Void, String>{
        private ProgressDialog progressDialog;
        private Activity context = null;

        public UploadFileTask(Activity context){
            this.context = context;
            progressDialog = ProgressDialog.show(context, "正在加载...", "系统正在处理您的请求");
        }

        @Override
        protected String doInBackground(String... strings) {
            File file = new File(strings[0]);
            return ApiManager.getInstance().handleUploadAvatars(file);
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if(s.equalsIgnoreCase("SUCCESS")){
                Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                currentUser = DataManager.getInstance().getCurrentUser();
                UserAvatar userAvatar = new UserAvatar(currentUser.getAvatar());
                userAvatar.execute((Void) null);
            }else {
                Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class UserAvatar extends AsyncTask<Void, Void, Boolean> {
        private String image;
        private Bitmap bitmap;

        UserAvatar(String image) {
            this.image = image;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            bitmap = ApiManager.getInstance().getAvatarBitmap(image);
            if(bitmap == null){
                return false;
            }else{
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                avatar.setImageBitmap(bitmap);
            }else {
                Toast.makeText(getActivity(), "操作失败：原因不明", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
}
