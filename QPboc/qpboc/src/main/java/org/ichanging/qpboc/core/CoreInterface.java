package org.ichanging.qpboc.core;

import android.view.View;
import android.widget.Toast;

import org.ichanging.qpboc.callback.BooleanCallback;
import org.ichanging.qpboc.callback.ByteArrayCallback;
import org.ichanging.qpboc.callback.IntegerCallback;
import org.ichanging.qpboc.callback.UICallback;

import java.util.ArrayList;

/**
 * Created by ChangingP on 16/6/15.
 */
public interface CoreInterface {

    int MSG_SHOW_TIME_SHORT = Toast.LENGTH_SHORT;
    int MSG_SHOW_TIME_LONG  = Toast.LENGTH_LONG;


    /**
     * Kernel Callback Get File Read/Write Path
     *
     * @return
     */
    String _getDataPath();

    /**
     * Kernel Callback Get I18N String
     *
     * @param key
     */
    String _I18NString(String key);

    /**
     * Kernel Callback Show Message
     *
     * @param msg
     * @param showTime
     */
    void _ShowMessage(String msg, int showTime);


    /**
     * Kernel Callback Show Dialog
     *
     * @param title
     * @param msg
     * @param callback  return user confirm choose yse or no
     *
     * @return
     */
    void _ShowDialog(String title, String msg, BooleanCallback callback);


    /**
     * Kernel Callback Generate Random int
     * Support to use Hardware Random or Software Random
     *
     * @return
     */
    int _GenerateRandomInt();

    /**
     * Kernel Callback Get Trade Count Number／Trace Number
     *
     * @return
     */
    int _inc_tsc();

    /**
     *
     * @param transType
     * @param callback return user int amount
     *
     * @return
     */
    void _GetTransAmount(TransType transType , IntegerCallback callback);

    /**
     *
     * @param transType
     * @param callback return user int cashback amount
     *
     * @return
     */
    void _GetCashbackAmount(TransType transType , IntegerCallback callback);

    /**
     * Kernel Callback Get Online PIN
     *
     * @param tryTimes
     * @param callback  return user pin byte array
     *
     * @return
     */
    void _GetOnlinePin(int tryTimes, ByteArrayCallback callback);

    /**
     * Kernel Callback Get Offline PIN
     *
     * @param tryTimes
     * @param callback  return user pin byte array
     *
     * @return
     */
    void _GetOfflinePin(int tryTimes, ByteArrayCallback callback);

    /**
     * Kernel Callback Show Issuer Initiated Voice Referrals
     * Issuer may send Voice Referrals in Script
     *
     * @param pan
     * @param callback  return user confirm choose yse or no
     *
     * @return
     */
    void _iss_ref(byte[] pan, BooleanCallback callback);


    /**
     * Kernel Callback Select Account Type
     *
     * @param callback return the index id of account type
     * 0x00 Default - Unspecified 0x10 Savings Account 0x20 Checking Account / Debit Account 0x30 Credit Account
     *
     * @return
     */
    void _acctype_sel(IntegerCallback callback);


    /**
     * Kernel Callback CardHolder to Confirm ID Info
     *
     * @param type 0x00: ID card 0x01: Military officer card 0x02: Passport 0x03: Entry permit 0x04: Temporary ID card 0x05: Other
     * @param pcon
     * @param callback  return user confirm choose yse or no
     *
     * @return
     */
    void _cert_confirm(int type, byte[] pcon, BooleanCallback callback);


    /**
     * Kernel Callback CardHolder to Select EMVCandidate
     *
     * @param pcan
     * @param callback return the index id of EMVCandidate
     *
     * @return
     */
    void _candidate_sel(ArrayList<EMVCandidate> pcan, IntegerCallback callback);


}
