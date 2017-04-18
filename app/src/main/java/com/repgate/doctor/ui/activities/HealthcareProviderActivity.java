package com.repgate.doctor.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.repgate.doctor.R;
import com.repgate.doctor.common.AppPreferences;
import com.repgate.doctor.common.Constants;
import com.repgate.doctor.data.MyProvidersResponseData;
import com.repgate.doctor.data.ProviderProfileResponseData;
import com.repgate.doctor.http.HttpInterface;
import com.repgate.doctor.service.SalesGcmListenerService;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HealthcareProviderActivity extends Activity implements View.OnClickListener{

    private final static String TAG = "HealthcareProviderActivity";
    public AppPreferences prefs;

    private FButton btnOfficer, btnSaleRep, btnCompany;
    private String mUserId;
    private Button btnHome, btnBack;
    private ImageView imgLogo;
    private TextView txtTitle;
    private ProviderProfileResponseData.DataModel mOffice;
    private BoomMenuButton bmb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_healthcare_provider);

        prefs = new AppPreferences(this);
        mUserId = getIntent().getStringExtra(Constants.PARAM_USER_ID);

        mOffice = new ProviderProfileResponseData.DataModel();

        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);

        HamButton.Builder createMsgBuilder = new HamButton.Builder()
                .normalImageRes(R.mipmap.menu_icon_messege)
                .normalColor(Color.rgb(33, 150,243))
                .normalTextRes(R.string.create_message).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intentMessage = new Intent(HealthcareProviderActivity.this, CreateMessageActivity.class);
                        intentMessage.putExtra(Constants.PARAM_USER_ID, prefs.getUserId());
                        startActivity(intentMessage);
                    }
                });
        bmb.addBuilder(createMsgBuilder);

        HamButton.Builder createReqBuilder = new HamButton.Builder()
                .normalImageRes(R.mipmap.menu_icon_request)
                .normalColor(Color.rgb(33, 150,243))
                .normalTextRes(R.string.create_request).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intentRequest = new Intent(HealthcareProviderActivity.this, CreateRequestActivity.class);
                        intentRequest.putExtra(Constants.PARAM_USER_ID, prefs.getUserId());
                        startActivity(intentRequest);
                    }
                });
        bmb.addBuilder(createReqBuilder);

        HamButton.Builder scheduleBuilder = new HamButton.Builder()
                .normalImageRes(R.mipmap.menu_icon_schedule)
                .normalColor(Color.rgb(33, 150,243))
                .normalTextRes(R.string.schedule).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intent = new Intent(HealthcareProviderActivity.this, CalendarActivity.class);
                        startActivity(intent);
                    }
                });
        bmb.addBuilder(scheduleBuilder);

        HamButton.Builder logoutBuilder = new HamButton.Builder()
                .normalImageRes(R.mipmap.menu_icon_logout)
                .normalColor(Color.GRAY)
                .normalTextRes(R.string.logout).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        new AlertDialog.Builder(HealthcareProviderActivity.this)
                                .setTitle(R.string.confirm_title)
                                .setMessage(R.string.are_you_logout)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        prefs.clearUserInformation();

                                        Intent intent = new Intent(HealthcareProviderActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        HealthcareProviderActivity.this.finish();
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                });
        bmb.addBuilder(logoutBuilder);

        btnOfficer = (FButton) findViewById(R.id.btnOfficer);
        btnOfficer.setOnClickListener(this);
        btnSaleRep = (FButton) findViewById(R.id.btnSaleRep);
        btnSaleRep.setOnClickListener(this);

        btnCompany = (FButton) findViewById(R.id.btnSaleRep);
        btnSaleRep.setOnClickListener(this);

        btnBack = (Button) findViewById(R.id.action_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgLogo = (ImageView) findViewById(R.id.imgTitle);
        imgLogo.setVisibility(View.GONE);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("Users");
    }

    private void registerReceiver() {
        registerReceiver(mMessageReceiver, new IntentFilter(SalesGcmListenerService.ACTION_MESSAGE_NOTIFICATION));
    }

    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentMessageShares = new Intent(HealthcareProviderActivity.this, MyMessagesActivity.class);
            intentMessageShares.putExtra(Constants.PARAM_USER_ID, prefs.getUserId());
            startActivity(intentMessageShares);
        }
    };

    private void registerRequestReceiver() {
        registerReceiver(mRequestReceiver, new IntentFilter(SalesGcmListenerService.ACTION_REQUEST_NOTIFICATION));
    }

    BroadcastReceiver mRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentRequests = new Intent(HealthcareProviderActivity.this, MyRequestActivity.class);
            intentRequests.putExtra(Constants.PARAM_USER_ID, prefs.getUserId());
            startActivity(intentRequests);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver();
        registerRequestReceiver();

        loadMyOfficeInformation();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mMessageReceiver);
        unregisterReceiver(mRequestReceiver);
    }

    private void loadMyOfficeInformation() {
        Retrofit retrofit = Constants.getRetrofitInstanc();

        HttpInterface.GetMyProviderInterface httpInterface = retrofit.create(HttpInterface.GetMyProviderInterface.class);
        Call<MyProvidersResponseData> call = httpInterface.getMyProviders(Integer.parseInt(mUserId), String.valueOf(Constants.USER_ROLE_FRONT_DESK));

        final ProgressDialog progress = ProgressDialog.show(this, null, "Please wait...", true);
        call.enqueue(new Callback<MyProvidersResponseData>() {
            @Override
            public void onResponse(Call<MyProvidersResponseData> call, Response<MyProvidersResponseData> response) {
                progress.dismiss();
                MyProvidersResponseData responseData = response.body();

                if (responseData == null) {
                    new AlertDialog.Builder(HealthcareProviderActivity.this)
                            .setTitle(R.string.error_title)
                            .setMessage(R.string.service_error_msg)
                            .setNegativeButton(R.string.ok, null)
                            .show();
                } else {
                    if (responseData.success == true) {

                        mOffice = responseData.data.get(0);

                    } else {

                        String message = responseData.error.err_msg;
                        checkErrorMessage(message);

                    }
                }
            }

            @Override
            public void onFailure(Call<MyProvidersResponseData> call, Throwable t) {
                progress.dismiss();

                new AlertDialog.Builder(HealthcareProviderActivity.this)
                        .setTitle(R.string.error_title)
                        .setMessage(R.string.network_error_msg)
                        .setNegativeButton(R.string.ok, null)
                        .show();
            }
        });

    }

    public void checkErrorMessage(String error) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(error)
                .setNegativeButton(R.string.ok, null)
                .show();
        return;
    }

    @Override
    public void onClick(View view) {
        int roleId = 0;

        switch (view.getId()) {
            case R.id.btnOfficer:
                Intent intent = new Intent(HealthcareProviderActivity.this, CommunicateActivity.class);
                intent.putExtra(Constants.PARAM_USER_ID, mOffice.ID);
                intent.putExtra(Constants.PARAM_ROLE_ID, mOffice.role);
                intent.putExtra(Constants.PARAM_ROLE_NAME, Constants.roleArray[Constants.USER_ROLE_FRONT_DESK]);
                intent.putExtra(Constants.PARAM_USER_NAME, mOffice.displayName);
                intent.putExtra(Constants.PARAM_USER_AVATAR, mOffice.logoUrl);
                intent.putExtra(Constants.PARAM_USER_PHONE, mOffice.phone);
                intent.putExtra(Constants.PARAM_USER_ADDRESS, mOffice.officeAddr);
                intent.putExtra(Constants.PARAM_USER_COMPANY, mOffice.company);
                intent.putExtra(Constants.PARAM_USER_MISS_MSG, mOffice.messageNew);
                intent.putExtra(Constants.PARAM_USER_MISS_REQ, mOffice.requestNew);
                startActivity(intent);
                break;
            case R.id.btnSaleRep:
                roleId = Constants.USER_ROLE_SALES_REP;
                break;
            case R.id.btnCompany:
                break;
            default:
                break;
        }

        if (roleId > 0) {
            Intent intent = new Intent(this, ProvidersActivity.class);
            int userId = Integer.valueOf(mUserId);
            intent.putExtra(Constants.PARAM_USER_ID, userId);
            intent.putExtra(Constants.PARAM_ROLE_ID, roleId);
            startActivity(intent);
        }

    }
}
