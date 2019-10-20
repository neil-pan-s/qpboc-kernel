package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

import java.util.ArrayList;

/**
 * Created by ChangingP on 16/6/21.
 */
public class QTerminalActionAnalysis  extends QCore implements ProcessAble{

    private static final String TAG = "QTerminalActionAnalysis";
    private ArrayList<EMVCapk> mCapkList = null;

    public QTerminalActionAnalysis(QOption option) {
        super(option);

        mCapkList = mParam.getCapks();
    }

    public int process() {

        byte[] tag9F10 = null, tag57 = null;
        byte[] pan = null, sn = null;

        LogUtil.i(TAG, "------------------ QTerminalActionAnalysis Start -------------------");

        if((tag9F10 = mBuf.getTagValue("9F10")) == null)
        {
            //return QCORE_NO9F10;
            return QCORE_TRYOTHEAR;
        }

        if((tag9F10[4] & 0x30) == 0x20)
        {
            //ARQC
            mBuf.setTagValue("9F27", (byte) 0x80);

            return QCORE_ONLINEPROC;
        }

        if((tag9F10[4] & 0x30) == 0x00)
        {
            //AAC
            mBuf.setTagValue("9F27", (byte) 0x00);

            return QCORE_OFFLINEDECLINE;
        }

        if((tag9F10[4] & 0x30) == 0x10)
        {
            //TC
            mBuf.setTagValue("9F27", (byte) 0x40);

            pan = mBuf.getTagValue("5A");
            if(pan == null)
            {
                tag57 = mBuf.getTagValue("57");
                for(int i = 0; i < tag57.length; i++)
                {
                    //search for D in track
                    if((tag57[i] & 0xD0) == 0xD0)
                    {
                        pan = new byte[i+1];
                        System.arraycopy(tag57,0,pan,0,i+1);
                        break;

                    }else if((tag57[i] & 0x0D) == 0x0D){

                        pan = new byte[i+1];
                        System.arraycopy(tag57,0,pan,0,i+1);

                        //padding F in right
                        pan[i] |= 0x0F;

                        break;
                    }
                }
                LogUtil.i(TAG,"QTerminalActionAnalysis - change 57 to 5a[" + HexUtil.ByteArrayToHexString(pan) + "]");
            }

            sn = mBuf.getTagValue("5F34");
            if (mParam.isInCardBlack(pan, sn[0]))
            {
                //In the card blacklist, offline rejection
                return QCORE_OFFLINEDECLINE;
            }
            else
            {
                return QCORE_OFFLINEPROC;
            }
        }

        return QCORE_OFFLINEPROC;
    }

    @Override
    public void onProcess()
    {
        int iRet = process();
        LogUtil.i(TAG, "------------------ QTerminalActionAnalysis onProcess [ " + iRet + " ] -------------------");
        if(iRet == QCORE_ONLINEPROC)
        {
            mAdapter._ShowMessage(mAdapter._I18NString("STR_REMOVECARD"), 1000);
            mICC.powerOff();

            // turn to QCardholderVerification process
            mOption.getProcessSwitch().onNextProcess(QCORE_ONLINEPROC);
        }
        else if(iRet == QCORE_OFFLINEPROC)
        {
            // turn to QReadAppData process
            mOption.getProcessSwitch().onNextProcess(QCORE_OFFLINEPROC);
        }
        else if(iRet == QCORE_OFFLINEDECLINE)
        {
            mAdapter._ShowMessage(mAdapter._I18NString("STR_REMOVECARD"), 1000);
            LogUtil.i(TAG, "------------------ QOfflineDecline -------------------");
            mICC.powerOff();
            //QCORE_DECLINE
            mOption.getQCallback().onDecline();
        }
        else if(iRet == QCORE_TRYOTHEAR)
        {
            mAdapter._ShowMessage(mAdapter._I18NString("STR_TERMINATION"), 500);
            mOption.getQCallback().onTermination(QCORE_TRYOTHEAR);
        }
        else
        {
            mOption.getQCallback().onTermination(QCORE_TERMINATION);
        }

    }

}