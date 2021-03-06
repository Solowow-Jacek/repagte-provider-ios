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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;
import com.repgate.doctor.R;
import com.repgate.doctor.common.AppPreferences;
import com.repgate.doctor.common.Constants;
import com.repgate.doctor.data.AllMessageResponseData;
import com.repgate.doctor.data.MessageResponseData;
import com.repgate.doctor.http.HttpInterface;
import com.repgate.doctor.service.SalesGcmListenerService;
import com.repgate.doctor.view.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyMessagesActivity extends Activity {
    private final static String TAG = "MyMessagesActivity";
    AppPreferences prefs;

    private Button btnHome, btnBack;
    private ImageView imgLogo;
    private TextView txtTitle;
    private PullRefreshLayout msgSwipe;
    private RadioButton rdInbox, rdSent;
    private LinearLayout titleLayer;
    private RadioGroup radioSelReceiver;
    private BoomMenuButton bmb;

    private String mUserId;

    private ListView lstMessages;
    private ListAdapter mAdapter;
    public ArrayList<MessageResponseData.DataModel> mMessageArray;

    private static int mMessageType = 1;
    private static final int MESSAGE_INBOX = 1;
    private static final int MESSAGE_SENT = 2;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        mUserId = getIntent().getStringExtra(Constants.PARAM_USER_ID);

        prefs = new AppPreferences(this);

        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.HAM_4);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.HAM_4);

        HamButton.Builder createMsgBuilder = new HamButton.Builder()
                .normalImageRes(R.mipmap.menu_icon_messege)
                .normalColor(Color.rgb(33, 150,243))
                .normalTextRes(R.string.create_message).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        Intent intentMessage = new Intent(MyMessagesActivity.this, CreateMessageActivity.class);
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
                        Intent intentRequest = new Intent(MyMessagesActivity.this, CreateRequestActivity.class);
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
                        Intent intent = new Intent(MyMessagesActivity.this, CalendarActivity.class);
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
                        new AlertDialog.Builder(MyMessagesActivity.this)
                                .setTitle(R.string.confirm_title)
                                .setMessage(R.string.are_you_logout)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        prefs.clearUserInformation();

                                        Intent intent = new Intent(MyMessagesActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        MyMessagesActivity.this.finish();
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                });
        bmb.addBuilder(logoutBuilder);

        titleLayer = (LinearLayout) findViewById(R.id.titleLayer);
        titleLayer.setVisibility(View.GONE);
        radioSelReceiver = (RadioGroup) findViewById(R.id.radioSelReceiver);
        radioSelReceiver.setVisibility(View.VISIBLE);

        btnHome = (Button) findViewById(R.id.action_home);
        btnHome.setVisibility(View.GONE);
        imgLogo = (ImageView) findViewById(R.id.imgTitle);
        imgLogo.setVisibility(View.GONE);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtTitle.setText("My Messages");
        btnBack = (Button) findViewById(R.id.action_back);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMessageArray = new ArrayList<MessageResponseData.DataModel>();

        lstMessages = (ListView) findViewById(R.id.lstMessage);
        mAdapter = new ListAdapter(this);
        lstMessages.setAdapter(mAdapter);

        msgSwipe = (PullRefreshLayout) findViewById(R.id.message_swipe);
        msgSwipe.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAllMessageInformation(mMessageType);
            }
        });

        rdInbox = (RadioButton) findViewById(R.id.inbox);
        rdInbox.setChecked(true);
        rdInbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    mMessageType = MESSAGE_INBOX;
                    msgSwipe.setRefreshing(true);
                    loadAllMessageInformation(mMessageType);
                }
            }
        });
        rdSent = (RadioButton) findViewById(R.id.sent);
        rdSent.setChecked(false);
        rdSent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    mMessageType = MESSAGE_SENT;
                    msgSwipe.setRefreshing(true);
                    loadAllMessageInformation(mMessageType);
                }
            }
        });

        mMessageType = MESSAGE_INBOX;
    }

    private void registerReceiver() {
        registerReceiver(mMessageReceiver, new IntentFilter(SalesGcmListenerService.ACTION_MESSAGE_NOTIFICATION));
    }

    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            msgSwipe.setRefreshing(true);
            mMessageType = MESSAGE_INBOX;
            loadAllMessageInformation(MESSAGE_INBOX);
        }
    };

    private void registerRequestReceiver() {
        registerReceiver(mRequestReceiver, new IntentFilter(SalesGcmListenerService.ACTION_REQUEST_NOTIFICATION));
    }

    BroadcastReceiver mRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intentRequests = new Intent(MyMessagesActivity.this, MyRequestActivity.class);
            intentRequests.putExtra(Constants.PARAM_USER_ID, prefs.getUserId());
            startActivity(intentRequests);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(mMessageReceiver);
        unregisterReceiver(mRequestReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();

        registerReceiver();
        registerRequestReceiver();

        msgSwipe.setRefreshing(true);

        if (mMessageType == MESSAGE_INBOX) {
            rdInbox.setChecked(true);
        } else {
            rdSent.setChecked(true);
        }
        loadAllMessageInformation(mMessageType);
    }

    private void loadAllMessageInformation(final int msgType) {

        int user_id = Integer.valueOf(this.prefs.getUserId());

        Retrofit retrofit = Constants.getRetrofitInstanc();

        HttpInterface.GetAllMessageInterface httpInterface = retrofit.create(HttpInterface.GetAllMessageInterface.class);
        Call<AllMessageResponseData> call = httpInterface.getAllMessages(user_id);

        call.enqueue(new Callback<AllMessageResponseData>() {
            @Override
            public void onResponse(Call<AllMessageResponseData> call, Response<AllMessageResponseData> response) {

            }

            @Override
            public void onFailure(Call<AllMessageResponseData> call, Throwable t) {

            }
        });
        call.enqueue(new Callback<AllMessageResponseData>() {
            @Override
            public void onResponse(Call<AllMessageResponseData> call, Response<AllMessageResponseData> response) {
                msgSwipe.setRefreshing(false);
                AllMessageResponseData responseData = response.body();

                if (responseData == null) {
                    new AlertDialog.Builder(MyMessagesActivity.this)
                            .setTitle(R.string.error_title)
                            .setMessage(R.string.service_error_msg)
                            .setNegativeButton(R.string.ok, null)
                            .show();
                } else {
                    Log.d(TAG, "success = " + responseData.success);
                    if (responseData.success == true) {

                        mMessageArray.clear();

                        if (responseData.data.size() > 0) {
                            for (int i = 0; i < responseData.data.size(); i++) {
                                switch (msgType) {
                                    case MESSAGE_INBOX:
                                        if (responseData.data.get(i).receiverId != null &&
                                                responseData.data.get(i).receiverId.equalsIgnoreCase(prefs.getUserId()))
                                            mMessageArray.add(responseData.data.get(i));
                                        break;
                                    case MESSAGE_SENT:
                                        if (responseData.data.get(i).senderId != null &&
                                                responseData.data.get(i).senderId.equalsIgnoreCase(prefs.getUserId()))
                                            mMessageArray.add(responseData.data.get(i));
                                        break;
                                }
                            }
//                            mMessageArray = responseData.data;
                        } else {
                            new AlertDialog.Builder(MyMessagesActivity.this)
                                    .setTitle("")
                                    .setMessage("There is no message.")
                                    .setNegativeButton(R.string.ok, null)
                                    .show();
                        }

                        mAdapter.notifyDataSetChanged();

                    } else {

                        String message = responseData.error.err_msg;
                        checkErrorMessage(message);

                    }
                }
            }

            @Override
            public void onFailure(Call<AllMessageResponseData> call, Throwable t) {
                msgSwipe.setRefreshing(false);
                new AlertDialog.Builder(MyMessagesActivity.this)
                        .setTitle(R.string.error_title)
                        .setMessage(R.string.network_error_msg)
                        .setNegativeButton(R.string.ok, null)
                        .show();
            }
        });
    }

    public class ListAdapter extends BaseAdapter {
        LayoutInflater inflater;

        public ListAdapter(Context c) {
            inflater = LayoutInflater.from(c);
        }

        public int getCount() {
            return mMessageArray.size();
        }

        public Object getItem(int position) {
            return mMessageArray.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_message, null);
                holder = new ViewHolder();
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final MessageResponseData.DataModel message = (MessageResponseData.DataModel) getItem(position);

            holder.txtMessageSender = (TextView) convertView.findViewById(R.id.txtMessageSender);
            holder.txtMessageSender.setText(message.senderName);
            holder.txtMessageTitle = (TextView) convertView.findViewById(R.id.txtMessageTitle);
            holder.txtMessageTitle.setText(message.title);
            holder.txtMessageBody = (TextView) convertView.findViewById(R.id.txtMessageBody);
            holder.txtMessageBody.setText(message.content);
            holder.txtMessageDate = (TextView) convertView.findViewById(R.id.txtMessageDate);
            holder.txtMessageDate.setText(message.createdAt);

            holder.senderImg = (RoundedImageView) convertView.findViewById(R.id.imgPicture);
            if (message.senderImageUrl.isEmpty() == false) {
                Picasso.with(MyMessagesActivity.this)
                        .load(message.senderImageUrl)
                        .placeholder(R.mipmap.default_avatar)
                        .error(R.mipmap.default_avatar)
                        .fit()
                        .into(holder.senderImg);
            } else {
                holder.senderImg.setImageResource(R.mipmap.default_avatar);
            }

            holder.txtMissMsg = (TextView) convertView.findViewById(R.id.missed_messages);
            if (message.isNew == true && !message.senderId.equalsIgnoreCase(prefs.getUserId())) {
                holder.txtMissMsg.setVisibility(View.VISIBLE);
            } else {
                holder.txtMissMsg.setVisibility(View.INVISIBLE);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MyMessagesActivity.this, ShowMessageActivity.class);
                    intent.putExtra(Constants.PARAM_USER_ID, mUserId);
                    intent.putExtra(Constants.PARAM_MESSAGE_ID, message.ID);
                    intent.putExtra(Constants.PARAM_MESSAGE_TITLE, message.title);
                    intent.putExtra(Constants.PARAM_MESSAGE_SENDER_ID, message.senderId);
                    intent.putExtra(Constants.PARAM_MESSAGE_SENDER_IMAGE, message.senderImageUrl);
                    intent.putExtra(Constants.PARAM_MESSAGE_SENDER_NAME, message.senderName);
                    intent.putExtra(Constants.PARAM_MESSAGE_RECEIVER_ID, message.receiverId);
                    intent.putExtra(Constants.PARAM_MESSAGE_RECEIVER_NAME, message.receiverName);
                    intent.putExtra(Constants.PARAM_MESSAGE_DATE, message.createdAt);
                    intent.putExtra(Constants.PARAM_MESSAGE_PDF_ATTACH, message.attachs);
                    intent.putExtra(Constants.PARAM_MESSAGE_VIDEO_ATTACH, message.videoLink);
                    intent.putExtra(Constants.PARAM_MESSAGE_BODY, message.content);
                    intent.putExtra(Constants.PARAM_MESSAGE_IS_NEW, message.isNew);

                    startActivity(intent);
                }
            });

            return convertView;
        }
    }

    class ViewHolder {
        TextView txtMessageSender;
        TextView txtMessageTitle;
        TextView txtMessageBody;
        TextView txtMessageDate;
        TextView txtMissMsg;
        RoundedImageView senderImg;
    }

    public void checkErrorMessage(String error) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(error)
                .setNegativeButton(R.string.ok, null)
                .show();
        return;
    }
}
