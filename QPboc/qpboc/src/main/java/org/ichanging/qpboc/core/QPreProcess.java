package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

/**
 * Created by ChangingP on 16/6/16.
 */
public class QPreProcess extends QCore implements ProcessAble{

    private static final String TAG = "QPreProcess";
    private int mAmount = 0;

    public QPreProcess(QOption option) {
        super(option);
    }

    public int process()
    {
        boolean bNeedOnlineKey; //Do you need to apply ciphertext online?
        byte[] u9F66 = new byte[4];     //Assume that the terminal does not support MSD or non-connected PBOC.

        LogUtil.i(TAG, "------------------ QPreProcess Start -------------------");

        mAmount = mOption._amount;

        mBuf.setTransAmount(mAmount, 0);    //Set transaction amount

        LogUtil.i(TAG, "max_qpboc_trans_limit = " + EMVParam.max_qpboc_trans_limit);
        LogUtil.i(TAG, "max_qpboc_offline_limit = " + EMVParam.max_qpboc_offline_limit);
        LogUtil.i(TAG, "max_qpboc_cvm_limit = " + EMVParam.max_qpboc_cvm_limit);

        /*
        if (mAmount >= EMVParam.max_qpboc_trans_limit)
        {
            //授权金额>=终端非接触交易限额
            if(mParam.capa_options(EMVParam.CAPA_Support_Contact_Pboc))
            {
                mAdapter._ShowMessage(mAdapter._I18NString("STR_TRYOTHEAR"), mAdapter.MSG_SHOW_TIME_SHORT);
                return QCORE_TRYOTHEAR;
            }
            return QCORE_TERMINATION;
        }

        if (!(mAmount > EMVParam.max_qpboc_offline_limit))
        {
            if (!mParam.capa_options(mParam.CAPA_Support_QPBOC_Status_Check))
            {
                //The terminal does not support status check.
                if (mAmount == 0)
                {
                    if (!mParam.IsSupportOnline())	//终端不支持联机
                    {
                        return QCORE_TERMINATION;
                    }
                    else
                    {
                        bNeedOnlineKey = true;
                    }
                }
            }
            else
            {
                if (mAmount == 100)	//授权金额为一个货币单位: 1元
                {
                    bNeedOnlineKey = true;
                }
            }
        }
        else
        {
            bNeedOnlineKey = true;
        }

        if (mAmount == 0)  //授权金额为一个货币单位: 0.00元
        {
            bNeedOnlineKey = true;
        }
        */

        bNeedOnlineKey = true;

        if(mParam.capa_options(mParam.CAPA_Support_QPBOC))
        {
            u9F66[0] |= 0x20;
        }
        if(mParam.capa_options(mParam.CAPA_Support_Contact_Pboc))
        {
            u9F66[0] |= 0x10;
        }

        if(!mParam.IsSupportOnline())
        {
            u9F66[0] |= 0x08;
        }

        if(mParam.terminal_cap(mParam.TC_Enciphered_PIN_Online, mParam._cap))
        {
            u9F66[0] |= 0x04;
        }
        if(mParam.terminal_cap(mParam.TC_Signature_Paper, mParam._cap))
        {
            u9F66[0] |= 0x02;
        }

        if (bNeedOnlineKey == true)
        {
            u9F66[1] |= 0x80;
        }

        //检查cvm
        if (mAmount >= EMVParam.max_qpboc_cvm_limit)
        {
            u9F66[1] |= 0x40;
        }

        //支持01 FDDA
        if((mParam._cap[2] & 0x01) == 0x01)
        {
            u9F66[3] |= 0x80;
        }

        LogUtil.i(TAG, " 9F66 = [ " + HexUtil.ByteArrayToHexString(u9F66) + " ]");

        mBuf.setTagValue(0x9F66,u9F66);

        return QCORE_SUCESS;
    }


    @Override
    public void onProcess()
    {
        int iRet = process();
        LogUtil.i(TAG, "------------------ QPreProcess onProcess [ " + iRet + " ] -------------------");
        if(iRet == QCORE_SUCESS)
        {
            mOption.getProcessSwitch().onNextProcess(QCORE_SUCESS);
        }else{
            mOption.getQCallback().onTermination(iRet);
        }
    }
}
