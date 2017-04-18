package com.repgate.doctor.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.repgate.doctor.R;
import com.repgate.doctor.common.AppPreferences;
import com.repgate.doctor.common.Constants;
import com.repgate.doctor.data.LoginResponseData;
import com.repgate.doctor.http.HttpInterface;
import com.repgate.doctor.util.Validation;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by developer on 3/17/16.
 */
public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    private EditText edtEmail, edtPasswd;
    private Button btnForgot;
    private FButton btnLogin;
    private AppCompatCheckBox chkRemember;

    private boolean isRememberMe;

    AppPreferences prefs;

    private String mDeviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mDeviceToken = getIntent().getStringExtra(Constants.INTENT_PARAM_TOKEN);

        prefs = new AppPreferences(this);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPasswd = (EditText) findViewById(R.id.edtPasswd);
        edtPasswd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    onLogin();
                    handled = true;
                }

                return handled;
            }
        });

        btnLogin = (FButton) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });

        btnForgot = (Button) findViewById(R.id.btnForgot);
        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgotPassword();
            }
        });

        isRememberMe = false;

        chkRemember = (AppCompatCheckBox) findViewById(R.id.chkRemember);
        chkRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRememberMe = isChecked;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Auto login
        isRememberMe = prefs.getAutoLogin();
        if (isRememberMe == true)
        {
            edtEmail.setText(prefs.getUserEmail());
            edtPasswd.setText(prefs.getUserPassword());
            chkRemember.setChecked(true);
            onLogin();
        }
    }

    private void onLogin() {

        if (false == Validation.EmptyValidation(edtEmail, getResources().getString(R.string.input_email_msg)))
            return;

//        if (false == Validation.EmailValid(edtEmail, getResources().getString(R.string.invalid_email_msg)))
//            return;

        if (false == Validation.EmptyValidation(edtPasswd, getResources().getString(R.string.input_password_msg)))
            return;

        if (false == Validation.LengthMoreValidation(edtPasswd, Constants.PASSWROD_MIN_LENGTH, getResources().getString(R.string.min_length_password)))
            return;

        String txtEmail = edtEmail.getText().toString();
        final String txtPasswd = edtPasswd.getText().toString();

        Retrofit retrofit = Constants.getRetrofitInstanc();

        HttpInterface.LoginInterface loginInterface = retrofit.create(HttpInterface.LoginInterface.class);
        Call<LoginResponseData> loginCall = loginInterface.login(txtEmail, txtPasswd, mDeviceToken, Constants.USER_DEVICE_TYPE_ANDROID, Constants.USER_ROLE_UNDEFINED, Constants.APP_TYPE_HEALTHCARE);

        final ProgressDialog progress = ProgressDialog.show(LoginActivity.this, null, "Logging in...", true);
        loginCall.enqueue(new Callback<LoginResponseData>() {
            @Override
            public void onResponse(Call<LoginResponseData> call, Response<LoginResponseData> response) {
                progress.dismiss();
                LoginResponseData responseData = response.body();

                if (responseData == null) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle(R.string.error_title)
                            .setMessage(R.string.service_error_msg)
                            .setNegativeButton(R.string.ok, null)
                            .show();
                } else {
                    Log.d(TAG, "success = " + responseData.success);
                    if (responseData.success == true) {

                        responseData.data.password = txtPasswd;
                        prefs.saveUserInformation(responseData.data);
                        prefs.setAutoLogin(isRememberMe);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        //intent.putExtra(Constants.INTENT_PARAM_TOKEN, mDeviceToken);
                        startActivity(intent);
                        finish();
                    } else {

                        String message = responseData.error.err_msg;

                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle(R.string.error_title)
                                .setMessage(message)
                                .setNegativeButton(R.string.ok, null)
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponseData> call, Throwable t) {
                progress.dismiss();

                Log.d(TAG, "onFailure");

                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(R.string.error_title)
                        .setMessage(R.string.network_error_msg)
                        .setNegativeButton(R.string.ok, null)
                        .show();
            }
        });
    }

    private void onForgotPassword() {


    }

}
