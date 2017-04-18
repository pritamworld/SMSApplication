package com.modxroidlabs.smsapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendSMSActivity extends AppCompatActivity implements View.OnClickListener
{
    Button btnSendSms, btnDefaultSMSApp;
    EditText mTextPhoneNo;
    EditText mTextSMS;
    String phoneNo, sms;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        btnSendSms = (Button) findViewById(R.id.buttonSend);
        btnDefaultSMSApp = (Button)findViewById(R.id.btnSmsDefault);
        mTextPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
        mTextSMS = (EditText) findViewById(R.id.editTextSMS);

        btnSendSms.setOnClickListener(this);
        btnDefaultSMSApp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.buttonSend:
                phoneNo = mTextPhoneNo.getText().toString();
                sms = mTextSMS.getText().toString();

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.SEND_SMS))
                    {
                    }
                    else
                    {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                    }
                } else
                {
                    sendSMS(phoneNo, sms);
                }
                break;

            case R.id.btnSmsDefault:
                sendSMSDefaultApplication();
                break;
        }
    }

    private void sendSMS(String phoneNo, String sms)
    {
        try
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent Successfully...",
                    Toast.LENGTH_LONG).show();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),
                    "SMS Sent Failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void sendSMSDefaultApplication()
    {
        try
        {
            Intent sendIntent;
            Uri uri = Uri.parse("smsto:" + mTextPhoneNo.getText().toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
            {
                sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
            }
            else
            {
                sendIntent = new Intent(Intent.ACTION_VIEW);
            }
            sendIntent.setType("text/plain");
            //sendIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            //sendIntent.setType("image/jpeg");
            sendIntent.putExtra("sms_body", mTextSMS.getText().toString());
            startActivity(sendIntent);
            //startActivity(Intent.createChooser(sendIntent, "Select application to send"));

            /*
            //Send Multiple pieces of content
            //https://developer.android.com/training/sharing/send.html
            //https://developer.android.com/guide/components/intents-filters.html
            ArrayList<Uri> imageUris = new ArrayList<Uri>();
            imageUris.add(imageUri1); // Add your image URIs here
            imageUris.add(imageUri2);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "Share images to.."));
            */
        } catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
    }
}


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS(phoneNo,sms);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public static class IncomingSms extends BroadcastReceiver
    {

        // Get the object of SmsManager
        final SmsManager sms = SmsManager.getDefault();

        public void onReceive(Context context, Intent intent) {

            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (Object aPdusObj : pdusObj)
                    {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);

                        String senderNum = currentMessage.getDisplayOriginatingAddress();
                        String message = currentMessage.getDisplayMessageBody();

                        Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);


                        // Show Alert
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context,
                                "senderNum: " + senderNum + ", message: " + message, duration);
                        toast.show();

                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" +e);

            }
        }
    }
}
