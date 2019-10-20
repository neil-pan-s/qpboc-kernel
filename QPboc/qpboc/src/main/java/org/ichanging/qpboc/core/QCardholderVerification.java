package org.ichanging.qpboc.core;

import org.ichanging.qpboc.callback.ByteArrayCallback;
import org.ichanging.qpboc.callback.QCallback;
import org.ichanging.qpboc.callback.UICallback;
import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

/**
 * Created by ChangingP on 16/6/21.
 */
public class QCardholderVerification extends QCore implements ProcessAble {

    private static final String TAG = "QCardholderVerification";

    public QCardholderVerification(QOption option) {
        super(option);
    }

    public int process() {
        int iRet = QCORE_SUCESS;
        byte[] tag9F6C = null,tag9F66 = null;
        byte[] pin = new byte[40];
        byte[] tag_9f34 = { 0x00 , 0x00, 0x00 };

        //default no cvm execute
        mBuf.setTagValue("9F34", tag_9f34);

        tag9F6C = mBuf.getTagValue("9F6C");
        if(tag9F6C == null)
        {
            // show execute cvm sign
            tag_9f34[0] |= 0x1E;
            mBuf.setTagValue("9F34", tag_9f34);

            tag9F66 = mBuf.getTagValue("9F66");
            if(( tag9F66[0] & 0x02 ) == 0x02  && ( tag9F66[1] & 0x40 ) == 0x40 )
            {
                LogUtil.w(TAG, "CardholderVerification Needed Signature");
                mOption._isSignatureRequest = true;
            }
            mCallback.onOnline();
            return QCORE_SUCESS;
        }

        if(( tag9F6C[0] & 0x40) == 0x40)
        {
            if(EMVParam.terminal_cap(EMVParam.TC_Signature_Paper, mParam._cap))
            {
                // show execute cvm sign
                tag_9f34[0] |= 0x1E;
                mBuf.setTagValue("9F34", tag_9f34);

                LogUtil.w(TAG, "CardholderVerification need Signature");
                //return QCORE_CVM_SIGNE;
                mOption._isSignatureRequest = true;
            }
        }

        if( ( tag9F6C[0] & 0x80 ) == 0x80)
        {
            if(mParam.IsSupportOnline() && EMVParam.terminal_cap(EMVParam.TC_Enciphered_PIN_Online, mParam._cap))
            {
                // show execute cvm online pin
                tag_9f34[0] |= 0x02;
                mBuf.setTagValue("9F34", tag_9f34);

                mAdapter._GetOnlinePin(0,new ByteArrayCallback() {
                    @Override
                    public void onSuccess(byte[] value) {

                        LogUtil.i(TAG, "onlinePin[" + HexUtil.ByteArrayToHexString(value) + "]");

                        // Record online password
                        mOption._onlinePIN = value;
                        mOption.getQCallback().onOnline();
                    }

                    @Override
                    public void onCancel() {

                        mOption.getQCallback().onTermination(QCORE_TERMINATION);
                    }

                    @Override
                    public void onTimeout() {

                        mOption.getQCallback().onTermination(QCORE_TERMINATION);
                    }
                });

                return QCORE_SUCESS;
            }
        }

        mOption.getQCallback().onOnline();
        return QCORE_SUCESS;
    }

    @Override
    public void onProcess()
    {
        if (!mParam.IsSupportOnline()) {
            mOption.getCoreInterface()._ShowMessage(mAdapter._I18NString("STR_CANNTGOONLINE"), 500);
            //QCORE_DECLINE;

            mOption.getQCallback().onDecline();
            return;
        }

        LogUtil.i(TAG, "------------------ QCardholderVerification onProcess Start -------------------");
        process();
        LogUtil.i(TAG, "------------------ QCardholderVerification onProcess End -------------------");

    }

}