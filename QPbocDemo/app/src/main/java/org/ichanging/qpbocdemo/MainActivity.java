package org.ichanging.qpbocdemo;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import org.ichanging.qpboc.callback.QCallback;
import org.ichanging.qpboc.core.QPboc;
import org.ichanging.qpboc.platform.CoreAdapter;
import org.ichanging.qpboc.platform.IsoDepAdapter;
import org.ichanging.qpboc.util.HexUtil;

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
        //载入一个AID参数
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
     * launchMode为singleTask的时候 通过Intent启到一个Activity
     * 如果系统已经存在一个实例 系统就会将请求发送到这个实例上
     * 但这个时候 系统就不会再调用通常情况下我们处理请求数据的onCreate方法 而是调用onNewIntent方法
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

        //银行卡返回的ACTION 为 ACTION_TAG_DISCOVERED
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
        {

            Tag tagFromIntent = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

            //do something with Ndef Record
            System.out.println(tagFromIntent);

            mICC.onTagDiscovered(tagFromIntent);

            //调用内核
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


