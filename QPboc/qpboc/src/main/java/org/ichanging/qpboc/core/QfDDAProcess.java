package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;

/**
 * Created by ChangingP on 16/6/21.
 */
public class QfDDAProcess extends QCore implements ProcessAble {

    private static final String TAG = "QfDDAProcess";

    public QfDDAProcess(QOption option) {
        super(option);
    }

    private int process()
    {
        //TODO offline data auth

        return QCORE_SUCESS;
    }

    @Override
    public void onProcess()
    {
        byte[] tag9F6C;

        int iRet = process();
        LogUtil.i(TAG, "------------------ QfDDAProcess onProcess [ " + iRet + " ] -------------------");
        if(iRet < 0)
        {
            if((tag9F6C = mBuf.getTagValue("9F6C")) == null)
            {
                mCallback.onDecline();
                return;
            }

            if((tag9F6C[0] & 0x20 ) == 0x20 && mParam.IsSupportOnline())
            {
                if(mBuf.getTagValue("5A") != null)
                {
                    // turn to QCardholderVerification process
                    mOption.getProcessSwitch().onNextProcess(QCORE_ONLINEPROC);
                    return;
                }
                else
                {
                    mOption.getQCallback().onDecline();
                    return;
                }
            }
            else if((tag9F6C[0] & 0x10) == 0x10 && mParam.capa_options(EMVParam.CAPA_Support_Contact_Pboc))
            {
                mAdapter._ShowMessage(mAdapter._I18NString("STR_TERMINATION"), 500);
                mOption.getQCallback().onTermination(QCORE_PBOC);
                return;
            }
            else
            {
                mOption.getQCallback().onDecline();
                return;
            }
        }
        else
        {
            mOption.getQCallback().onAccept();
            return;
        }
    }
}