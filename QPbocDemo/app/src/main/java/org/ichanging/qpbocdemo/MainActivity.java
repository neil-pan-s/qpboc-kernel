package org.ichanging.qpbocdemo;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.ichanging.qpboc.callback.QCallback;
import org.ichanging.qpboc.core.QPboc;
import org.ichanging.qpboc.platform.CoreAdapter;
import org.ichanging.qpboc.platform.IsoDepAdapter;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter mAdapter = null;
    private PendingIntent mPendingIntent = null;
    private IsoDepAdapter mICC = null;
    private QPboc mQPboc = null;
    private CoreAdapter mCoreAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter=NfcAdapter.getDefaultAdapter(this);
        if(mAdapter==null)
        {
            Toast.makeText(this, "No NFC found on this device", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        mICC = new IsoDepAdapter();
        mCoreAdapter = new CoreAdapter(this);

        mQPboc = new QPboc(mICC,mCoreAdapter);

        //for test
        mQPboc.setParam("123456789012345","12345678","BCTC","FFFFFFFF");

        String field62 = "9F0608A000000333010101DF0101009F08020020DF1105D84000A800DF1205D84004F800DF130500100000009F1B0400000001DF150400000000DF160199DF170199DF14039F3704DF1801019F7B06000000100000DF1906000000100000DF2006000000100000DF2106000000100000";
        //Load an AID parameter
        mQPboc.updateAid(field62,true);

    }


    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null)
        {
            mAdapter.disableForegroundDispatch(this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null) {
            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    /*
     *
     * When launchMode is singleTask, an activity is initiated through the Intent.
     * If the system already has an instance, the system will send the request to this instance.
     * But at this time, the system will not call the onCreate method that normally handles the request data, but instead calls the onNewIntent method.
     *
     */
    @Override
    public void onNewIntent(Intent intent) {

        //must store the new intent unless getIntent() will return the old one
        setIntent(intent);

        onReadCard(intent);

    }


    void onReadCard(Intent intent) {
        // Parse the intent
        String action = intent.getAction();
        final Context mContext = this;

        //The ACTION returned by the row card is ACTION_TAG_DISCOVERED
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
        {

            Tag tagFromIntent = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

            //do something with Ndef Record
            System.out.println(tagFromIntent);

            mICC.onTagDiscovered(tagFromIntent);

            //Calling the kernel
            mQPboc.onTransactionProcess((byte)0x0,1,new QCallback() {

                @Override
                public void onAccept() {
                    Toast.makeText(mContext, "QPBOC Accept",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onDecline() {
                    Toast.makeText(mContext, "QPBOC Decline",Toast.LENGTH_LONG).show();

                }

                @Override
                public void onOnline() {
                    byte[] filed55 = mQPboc.getField55();

                    Toast.makeText(mContext, "QPBOC Online",Toast.LENGTH_LONG).show();

                }

                @Override
                public void onTermination(int errCode)
                {
                    Toast.makeText(mContext, "QPBOC Termination - " + errCode ,Toast.LENGTH_LONG).show();

                }
            });

        } else {
            // Unknown tag type
            Toast.makeText(this, "TNF_UNKNOWN",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mICC != null)
        {
            mICC._PowerOff();
        }
    }
}


