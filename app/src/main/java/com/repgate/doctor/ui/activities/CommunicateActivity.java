package com.repgate.doctor.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.repgate.doctor.R;
import com.repgate.doctor.common.AppPreferences;
import com.repgate.doctor.common.Constants;
import com.repgate.doctor.service.SalesGcmListenerService;
import com.squareup.picasso.Picasso;

import info.hoang8f.widget.FButton;

public class CommunicateActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "CommunicateActivity";
    public AppPreferences prefs;

    private Button btnBack;
    private FButton btnMessages, btnRequests;
    private ImageView imgLogo, imgAvatar;
    private TextView txtTitle, txtName, txtRoleName, txtPhone, txtAddress, txtCompany, txtMissMsg, txtMissReq;
    private LinearLayout companyLayout;
    private BoomMenuButton bmb;

    private String mUserId, mRoleId, mRoleName, mUserName, mLogoUrl, mPhone, mAddress, mCompany;
    private int mMissMsg, mMissReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);

        prefs = new AppPreferences(this);

        mUserId = getIntent().getStringExtra(Constants.PARAM_USER_ID);
        mRoleId = getIntent().getStringExtra(Constants.PARAM_ROLE_ID);
        mRoleName = getIntent().getStringExtra(Constants.PARAM_ROLE_NAME);
        mUserName = getIntent().getStringExtra(Constants.PARAM_USER_NAME);
        mLogoUrl = getIntent().getStringExtra(Constants.PARAM_USER_AVATAR);
        mPhone = getIntent().getStringExtra(Constants.PARAM_USER_PHONE);
        mAddress = getIntent().getStringExtra(Constants.PARAM_USER_ADDRESS);
        mCompany = getIntent().getStringExtra(Constants.PARAM_USER_COMPANY);
        mMissMsg = getIntent().getIntExtra(Constants.PARAM_USER_MISS_MSG, 0);
        mMissReq = getIntent().getIntExtra(Constants.PARAM_USER_MISS_REQ, 0);

        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);

        HamButton.Builder createMsgBuilder = new HamButton.Builder()
                .normalImageRes(R.mipmap.menu_icon_messege)
                .normalColor(Color.rgb(33, 150,243))
                .normalTextRes(R.string.create_message).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intentMessage = new Intent(CommunicateActivity.this, CreateMessageActivity.class);
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
                        Intent intentRequest = new Intent(CommunicateActivity.this, CreateRequestActivity.class);
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
                        Intent intent = new Intent(CommunicateActivity.this, CalendarActivity.class);
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
                        new AlertDialog.Builder(CommunicateActivity.this)
                                .setTitle(R.string.confirm_title)
                                .setMessage(R.string.are_you_logout)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        prefs.clearUserInformation();

                                        Intent intent = new Intent(CommunicateActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        CommunicateActivity.this.finish();
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                });
        bmb.addBuilder(logoutBuilder);

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
        txtTitle.setText("Communicate");

        imgAvatar = (ImageView) findViewById(R.id.imgPicture);
        if (mLogoUrl.isEmpty() == false) {

            Picasso.with(this)
                    .load(mLogoUrl)
                    .placeholder(R.mipmap.doctor)
                    .error(R.mipmap.doctor)
                    .fit()
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.mipmap.doctor);
        }

        txtName = (TextView) findViewById(R.id.txtName);
        txtName.setText(mUserName);
        txtRoleName = (TextView) findViewById(R.id.txtRoleName);
        txtRoleName.setText(", " + mRoleName);

        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtPhone.setText(mPhone);
        txtAddress = (TextView) findViewById(R.id.txtAddr);
        txtAddress.setText(mAddress);

        companyLayout = (LinearLayout) findViewById(R.id.contentCompany);

        txtCompany = (TextView) findViewById(R.id.txtCompany);
        txtCompany.setText(mCompany);

        btnMessages = (FButton) findViewById(R.id.btnMessages);
        btnMessages.setOnClickListener(this);

        btnRequests = (FButton) findViewById(R.id.btnRequests);
        if (mRoleId.equalsIgnoreCase(String.valueOf(Constants.USER_ROLE_FRONT_DESK))) {
            companyLayout.setVisibility(View.GONE);
            btnRequests.setVisibility(View.GONE);
        }
        else {
            btnRequests.setOnClickListener(this);
        }

//        txtMissMsg = (TextView) findViewById(R.id.missed_msgs);
//        if (mMissMsg > 0) {
//            txtMissMsg.setVisibility(View.VISIBLE);
//            txtMissMsg.setText(String.valueOf(mMissMsg));
//        } else {
//            txtMissMsg.setVisibility(View.INVISIBLE);
//        }
//        txtMissMsg.setVisibility(View.INVISIBLE);

//        txtMissReq = (TextView) findViewById(R.id.missed_reqs);
//        if (mMissReq > 0) {
//            txtMissReq.setVisibility(View.VISIBLE);
//            txtMissReq.setText(String.valueOf(mMissReq));
//        } else {
//            txtMissReq.setVisibility(View.INVISIBLE);
//        }
//        txtMissReq.setVisibility(View.INVISIBLE);
    }

    private void registerReceiver() {
        registerReceiver(mMessageReceiver, new IntentFilter(SalesGcmListenerService.ACTION_MESSAGE_NOTIFICATION));
    }

    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentMessageShares = new Intent(CommunicateActivity.this, MyMessagesActivity.class);
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
            Intent intentRequests = new Intent(CommunicateActivity.this, MyRequestActivity.class);
            intentRequests.putExtra(Constants.PARAM_USER_ID, prefs.getUserId());
            startActivity(intentRequests);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver();
        registerRequestReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mMessageReceiver);
        unregisterReceiver(mRequestReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMessages:
                Intent intentMessage = new Intent(this, CreateMessageActivity.class);
                intentMessage.putExtra(Constants.PARAM_MESSAGE_SENDER_ID, mUserId);
                intentMessage.putExtra(Constants.PARAM_MESSAGE_SENDER_NAME, mUserName);
                intentMessage.putExtra(Constants.PARAM_MESSAGE_TYPE, Constants.CREATE_MESSAGE);
                startActivity(intentMessage);
                break;
            case R.id.btnRequests:
                Intent intentRequest = new Intent(this, CreateRequestActivity.class);
                intentRequest.putExtra(Constants.PARAM_REQUEST_RECEIVER_ID, mUserId);
                intentRequest.putExtra(Constants.PARAM_REQUEST_RECEIVER_NAME, mUserName);
                startActivity(intentRequest);
                break;
            default:
                break;
        }
    }
}
