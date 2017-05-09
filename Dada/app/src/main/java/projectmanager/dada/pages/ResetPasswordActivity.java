package projectmanager.dada.pages;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import projectmanager.dada.R;
import projectmanager.dada.util.ApiManager;
import projectmanager.dada.util.DataManager;

public class ResetPasswordActivity extends AppCompatActivity{

    private ResetPasswordTask resetPasswordTask = null;
    private EditText          oldPasswordView;
    private EditText          newPasswordView;
    private EditText          newPasswordAgainView;
    private View              progressView;
    private View              resetPasswordFormView;
    private Button            confirmButton;
    private String            resetResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_reset_password);
        progressView = findViewById(R.id.reset_password_progress);
        resetPasswordFormView = findViewById(R.id.reset_password_form);
        oldPasswordView = (EditText)findViewById(R.id.reset_password_old_password);
        newPasswordView = (EditText)findViewById(R.id.reset_password_new_password);
        newPasswordAgainView = (EditText)findViewById(R.id.reset_password_new_password_again);
        confirmButton = (Button)findViewById(R.id.reset_password_confirm_button);
        confirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptResetPassword();
            }
        });
    }

    /**
     * 在点击“登录”按钮之后，尝试登录的方法。
     */
    private void attemptResetPassword() {
        if (resetPasswordTask != null) {
            return;
        }
        oldPasswordView.setError(null);
        newPasswordView.setError(null);
        newPasswordAgainView.setError(null);
        String oldPassword = oldPasswordView.getText().toString();
        String newPassword = newPasswordView.getText().toString();
        String newPasswordAgain = newPasswordAgainView.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (!TextUtils.isEmpty(oldPassword) && !isPasswordValid(oldPassword)) {
            oldPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = oldPasswordView;
            cancel = true;
        }
        if (!TextUtils.isEmpty(newPassword) && !isPasswordValid(newPassword)) {
            newPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = newPasswordView;
            cancel = true;
        }
        if (newPassword.equals(newPasswordAgain) == false){
            newPasswordAgainView.setError(getString(R.string.error_password_again_not_match));
            focusView = newPasswordAgainView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            resetPasswordTask = new ResetPasswordTask(oldPassword,newPassword);
            resetPasswordTask.execute((Void) null);
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
     * 在用户重置线程进行的时候，显示旋转的载入框，表示联网操作进行中
     * @param show 设定进度框显示与否
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            resetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            resetPasswordFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            resetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
     * 本类的主要作用是另起一个线程，实现用户的登录操作
     */
    public class ResetPasswordTask extends AsyncTask<Void, Void, Boolean> {

        private String oldPassword;
        private String newPassword;
        private int    userId;

        ResetPasswordTask(String op, String np) {
            oldPassword = op;
            newPassword = np;
            userId = DataManager.getInstance().getCurrentUser().getUserId();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            resetResult = ApiManager.getInstance().handleChangePassword(userId,oldPassword,newPassword);
            if(!resetResult.equals("succeed")){
                return false;
            }else{
                return true;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);
            if (success == true) {
                Toast toast = Toast.makeText(getApplicationContext(), "密码修改成功", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                finish();
            } else {
                confirmButton.setError(resetResult);
                confirmButton.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}

