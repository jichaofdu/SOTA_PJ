package projectmanager.dada;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import projectmanager.dada.model.RegisterResult;
import projectmanager.dada.model.User;
import projectmanager.dada.util.ApiManager;
import projectmanager.dada.util.DataManager;

public class RegisterActivity extends AppCompatActivity{

    private UserRegisterTask registerTask = null;
    private EditText         phoneView;
    private EditText         usernameView;
    private EditText         passwordView;
    private EditText         passwordAgainView;
    private View             progressView;
    private View             registerFormView;
    private Button           registerButton;
    private Button           turnLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_register);
        registerFormView = findViewById(R.id.register_form);
        progressView = findViewById(R.id.register_progress);
        phoneView = (EditText) findViewById(R.id.register_phone);
        usernameView = (EditText) findViewById(R.id.register_username);
        passwordView = (EditText) findViewById(R.id.register_password);
        passwordAgainView = (EditText) findViewById(R.id.register_passwordAgain);
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
        turnLoginButton = (Button) findViewById(R.id.register_turn_login_button);
        turnLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToLoginPage();
            }
        });
    }

    /**
     * 点击“跳转到登录”按钮之后，关闭注册页面并打开登录页面
     */
    private void turnToLoginPage(){
        finish();
        Intent nextPage = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(nextPage);
    }

    /**
     * 在点击“注册”按钮之后，尝试注册的方法。
     */
    private void attemptRegister() {
        if (registerTask != null) {
            return;
        }
        phoneView.setError(null);
        passwordView.setError(null);
        String phone = phoneView.getText().toString();
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        String passwordAgain = passwordAgainView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }
        if(password.equals(passwordAgain) == false){
            passwordView.setError(getString(R.string.error_password_again_not_match));
            focusView = passwordAgainView;
            cancel = true;
        }
        if (TextUtils.isEmpty(phone)) {
            phoneView.setError(getString(R.string.error_field_required));
            focusView = phoneView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            registerTask = new UserRegisterTask(phone, username ,password);
            registerTask.execute((Void) null);
        }
    }

    /**
     * 检测输入框中的密码是否是一个有效格式的密码
     * @param password 用户传入的密码
     * @return         返回密码是否符合用户的要求
     */
    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }


    /**
     * 在用户注册线程进行的时候，显示旋转的载入框，表示联网操作进行中
     * @param show 设定进度框显示与否
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            registerFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
     * 本类的主要作用是另起一个线程，实现用户的注册操作
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String phone;
        private final String username;
        private final String password;
        private RegisterResult registerResult;

        UserRegisterTask(String ph, String un, String pw) {
            phone = ph;
            username = un;
            password = pw;
            registerResult = null;
        }
        @Override
        protected void onPreExecute(){
            showProgress(true);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            registerResult = ApiManager.getInstance().handleRegister(phone,password);
            if(registerResult == null){
                return false;
            }else{
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            registerTask = null;
            showProgress(false);
            if (success == true) {
                if(registerResult.isStatus() == true){
                    Toast.makeText(getApplicationContext(),"注册成功 ID:" + registerResult.getAccount().getId(), Toast.LENGTH_LONG).show();
                    finish();
                    Intent nextPage = new Intent(RegisterActivity.this,MainActivity.class);
                    //DataManager.getInstance().setCurrentUser(registerUser);
                    startActivity(nextPage);
                }else{
                    Toast.makeText(getApplicationContext(),"注册失败 Reason:" + registerResult.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                String rtErrorMsg = DataManager.getInstance().getRegisterErrorMessage();
                if(rtErrorMsg.contains("exist")){
                    Toast.makeText(getApplicationContext(), "账号已经被注册，请更换账号。",
                            Toast.LENGTH_LONG).show();
                    phoneView.setError("账号已经被注册，请更换账号。");
                    phoneView.requestFocus();
                }else{
                    Toast.makeText(getApplicationContext(), "注册失败，原因不明",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            registerTask = null;
            showProgress(false);
        }
    }
}

