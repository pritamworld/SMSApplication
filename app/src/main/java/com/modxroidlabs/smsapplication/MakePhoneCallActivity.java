package com.modxroidlabs.smsapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MakePhoneCallActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_PHONE_CALL = 10;
    private Button btnMakeCall;
    String phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_phone_call);

        phoneNo = "5556";
        btnMakeCall = (Button) findViewById(R.id.btnCall);

        btnMakeCall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MakePhoneCallActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MakePhoneCallActivity.this,
                            Manifest.permission.CALL_PHONE)) {
                    } else {
                        ActivityCompat.requestPermissions(MakePhoneCallActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_PHONE_CALL);
                    }
                } else {
                    makePhoneCall();
                }

            }
        });
    }

    private void makePhoneCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNo));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_PHONE_CALL:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    makePhoneCall();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),
                            "Failed to place call, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
