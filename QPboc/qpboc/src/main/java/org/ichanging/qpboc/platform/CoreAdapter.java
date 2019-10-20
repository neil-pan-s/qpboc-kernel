package org.ichanging.qpboc.platform;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import org.ichanging.qpboc.callback.BooleanCallback;
import org.ichanging.qpboc.callback.ByteArrayCallback;
import org.ichanging.qpboc.callback.IntegerCallback;
import org.ichanging.qpboc.core.CoreInterface;
import org.ichanging.qpboc.core.EMVCandidate;
import org.ichanging.qpboc.core.TransType;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ChangingP on 16/6/15.
 */
public class CoreAdapter implements CoreInterface {

    private Context mContext;
    private Toast   mToast;
    private Random  mRandom;
    private int     mTraceNumber = 0;

    public CoreAdapter(Context context)
    {
        mToast  = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        mRandom = new Random();
        mContext = context;
    }

    @Override
    public String _getDataPath() {
        return mContext.getFilesDir().getAbsolutePath();
    }

    @Override
    public String _I18NString(String key)
    {
        int resId = mContext.getResources().getIdentifier(key,"string",mContext.getPackageName());
        return mContext.getString(resId);
    }

    @Override
    public void _ShowMessage(String msg, int showTime) {

        //mToast.setDuration(showTime);
        mToast.setText(msg);
        mToast.show();
    }

    @Override
    public void _ShowDialog(String title, String msg,BooleanCallback callback) {

        final BooleanCallback _callback = callback;

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false);

        builder.setPositiveButton("Yes", (dialogInterface, i) -> _callback.onSuccess(true));

        builder.setNegativeButton("No", (dialogInterface, i) -> _callback.onSuccess(false));

        builder.setOnCancelListener(dialogInterface -> _callback.onCancel());

        builder.create().show();
    }


    @Override
    public int _GenerateRandomInt() {
        return mRandom.nextInt();
    }

    @Override
    public int _inc_tsc() {

        return (mTraceNumber <= 999999) ? mTraceNumber++ : (mTraceNumber = 1);
    }

    @Override
    public void _GetTransAmount(TransType transType , IntegerCallback callback) {

        // for test cash = 1
        callback.onSuccess(1);

    }

    @Override
    public void _GetCashbackAmount(TransType transType , IntegerCallback callback) {

        // for test cashback = 0
        callback.onSuccess(0);

    }


    @Override
    public void _GetOnlinePin(int tryTimes, ByteArrayCallback callback) {

        byte[] pin = {0x11,0x22,0x33,0x44,0x55,0x66,0x77,(byte)0x88};

        callback.onSuccess(pin);
    }

    @Override
    public void _GetOfflinePin(int tryTimes, ByteArrayCallback callback) {

        byte[] pin = {0x11,0x22,0x33,0x44,0x55,0x66,0x77,(byte)0x88};

        callback.onSuccess(pin);
    }


    @Override
    public void _iss_ref(byte[] pan,BooleanCallback callback) {
        callback.onSuccess(true);
    }

    @Override
    public void _acctype_sel(IntegerCallback callback) {

        //for test Select Account Type Default
        callback.onSuccess(0x00);
    }

    @Override
    public void _cert_confirm(int type, byte[] pcon,BooleanCallback callback) {

        callback.onSuccess(true);
    }

    @Override
    public void _candidate_sel(ArrayList<EMVCandidate> pcan,IntegerCallback callback) {

        // for test Choose Default One
        callback.onSuccess(0);
    }
}
