package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

/**
 * Created by ChangingP on 16/6/22.
 */
public class QECashQuery extends QCore implements ProcessAble{

    private static final String TAG = "QECashQuery";

    public QECashQuery(QOption option) {
        super(option);
    }

    /**
     * Get ECash Balance
     *
     * @return
     *      <  0 Error
     *      >= 0 ECash Balance
     *
     */
    private int getECashBalance()
    {

        ICCResponse rsp = new ICCResponse();

        int iRet = mICC.GetData("9F79", rsp);
        if (iRet != 0x9000) {
            return iRet;
        }

        EMVTlv tlv = new EMVTlv(rsp.data);
        if (!tlv.validTlv()) {
            return QCORE_DECODE;
        }

        if (tlv.childTag("9F79") != null)
        {
            mBuf.setTagValue("9F79",tlv.childTag("9F79").value);

            return Integer.parseInt(HexUtil.ByteArrayToHexString(tlv.childTag("9F79").value));
        }

        return QCORE_FAILED;
    }


    @Override
    public void onProcess() {

        int iRet = getECashBalance();
        LogUtil.i(TAG, "------------------ QECashQuery onProcess [ " + iRet + " ] -------------------");

        mOption.getQCallback().onTermination(iRet);
    }
}
