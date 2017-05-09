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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import projectmanager.dada.model.LoginResult;
import projectmanager.dada.model.User;
import projectmanager.dada.pages.ProjectInstructionActivity;
import projectmanager.dada.util.ApiManager;
import projectmanager.dada.util.DataManager;

public class LoginActivity extends AppCompatActivity{

    private UserLoginTask loginTask;
    private EditText      phoneView;
    private EditText      passwordView;
    private View          progressView;
    private View          loginFormView;
    private Button        signInButton;
    private Button        turnRegisterButton;
    private Button        turnInstructionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login);
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
        phoneView = (EditText) findViewById(R.id.login_phone);
        passwordView = (EditText) findViewById(R.id.login_password);
        signInButton = (Button) findViewById(R.id.login_sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        turnRegisterButton = (Button) findViewById(R.id.login_turn_register_button);
        turnRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToRegisterPage();
            }
        });
        turnInstructionButton = (Button) findViewById(R.id.login_turn_instruction_button);
        turnInstructionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                turnToInstructionPage();
            }
        });
    }

    /**
     * 点击“跳转到注册”按钮之后，关闭登录页面并打开注册页面
     */
    private void turnToRegisterPage(){
        Intent nextPage = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(nextPage);
    }

    private void turnToInstructionPage(){
        Intent intent = new Intent(LoginActivity.this, ProjectInstructionActivity.class);
        startActivity(intent);
    }

    /**
     * 在点击“登录”按钮之后，尝试登录的方法。
     */
    private void attemptLogin() {
        if (loginTask != null) {
            return;
        }
        phoneView.setError(null);
        passwordView.setError(null);
        String phone = phoneView.getText().toString();
        String password = passwordView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
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
            loginTask = new UserLoginTask(phone, password);
            loginTask.execute((Void) null);
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
     * 在用户登录线程进行的时候，显示旋转的载入框，表示联网操作进行中
     * @param show 设定进度框显示与否
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 本类的主要作用是另起一个线程，实现用户的登录操作
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String phone;
        private final String password;
        private LoginResult lr;

        UserLoginTask(String ph, String pw) {
            phone = ph;
            password = pw;
            lr = null;
        }
        @Override
        protected void onPreExecute(){
            showProgress(true);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            lr = ApiManager.getInstance().handleLogin(phone,password);
            if(lr == null){
                return false;
            }else{
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            loginTask = null;
            showProgress(false);
            if (success == true) {
                if(lr.getStatus() == true){
                    Toast.makeText(getApplicationContext(),"登录成功 ID:" + lr.getAccount().getId(), Toast.LENGTH_LONG).show();
                    finish();
                    Intent nextPage = new Intent(LoginActivity.this,MainActivity.class);
                    //DataManager.getInstance().setCurrentUser(loginUser);
                    startActivity(nextPage);
                }else{
                    Toast.makeText(getApplicationContext(),"登录失败 Reason:" + lr.getMessage(), Toast.LENGTH_LONG).show();
                }

            } else {
                String rtErrorMsg = lr.getMessage();
                if(rtErrorMsg.contains("exist")){
                    Toast.makeText(getApplicationContext(),"该用户不存在",
                            Toast.LENGTH_LONG).show();
                    phoneView.setError("该用户不存在");
                    phoneView.requestFocus();
                }else if(rtErrorMsg.contains("password")){
                    Toast.makeText(getApplicationContext(),"用户名与密码不匹配",
                            Toast.LENGTH_LONG).show();
                    passwordView.setError("用户名与密码不匹配");
                    passwordView.requestFocus();
                }else{
                    Toast.makeText(getApplicationContext(),"登录失败 错误原因不明",
                            Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            loginTask = null;
            showProgress(false);
        }
    }
}

