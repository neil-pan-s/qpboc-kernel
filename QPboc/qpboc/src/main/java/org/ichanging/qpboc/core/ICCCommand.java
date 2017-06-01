package org.ichanging.qpboc.core;

import org.ichanging.qpboc.util.HexUtil;
import org.ichanging.qpboc.platform.LogUtil;

/**
 * Created by ChangingP on 2016/4/15.
 */
public class ICCCommand {

    private static final String TAG = "ICCCommand";

    // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LC | LE]
    public static final byte[] APDU_SELECT_AID          = { 0x00 , (byte)0xA4 , 0x04 , 0x00 , 0x00 , 0x00 };
    public static final byte[] APDU_READ_RECORD         = { 0x00 , (byte)0xB2 , 0x00 , 0x00 , 0x00 , 0x00 };
    public static final byte[] APDU_GET_PROCESS_OPTIONS = { (byte)0x80 , (byte)0xA8 , 0x00 , 0x00 , 0x00 , 0x00 };
    public static final byte[] APDU_GET_DATA            = { (byte)0x80 , (byte)0xCA , 0x00 , 0x00 , 0x00, 0x00 };

    private static final int APDU_SW_OK = 0x9000;

    private ICCInterface mIcc = null;
    private static ICCCommand mICCCommand = null;

    public static ICCCommand getInstance()
    {
        if(mICCCommand == null)
        {
            mICCCommand = new ICCCommand();
        }

        return mICCCommand;
    }

    /**
     *  set ICCInterface delegate
     */
    public void setICC(ICCInterface icc)
    {
        mIcc = icc;
    }

    public int powerOn()
    {
        return mIcc._PowerOn();
    }

    public int powerOff()
    {
        return mIcc._PowerOff();
    }

    /**
     * Build APDU for SELECT AID command. See ISO 7816-4.
     *
     * @param aid Application ID (AID) to select
     * @param selfirst Select First or Next
     * @param rsp APDU rsponse of Card
     *
     */
    public int SelectAid(String aid, boolean selfirst, ICCResponse rsp) {

        ICCApdu apdu = new ICCApdu(APDU_SELECT_AID);

        apdu.setData(aid.getBytes());

        //Select First or Next
        if(!selfirst) apdu._p2 = 0x02;

        LogUtil.i(TAG, "----------------------- EMV/PBOC select aid");
        return mIcc._ApduComm(apdu,rsp);
    }

    public int SelectAid(byte[] aid, boolean selfirst, ICCResponse rsp) {

        ICCApdu apdu = new ICCApdu(APDU_SELECT_AID);

        apdu.setData(aid);

        //Select First or Next
        if(!selfirst) apdu._p2 = 0x02;

        LogUtil.i(TAG, "----------------------- EMV/PBOC select aid");
        return mIcc._ApduComm(apdu,rsp);
    }


    /**
     * Build APDU for READ_RECORD command. See ISO 7816-4.
     *
     * @param sfi
     * @param index
     * @param rsp APDU for SELECT AID/PSE/PPSE command
     *
     */
    public int ReadRecord(int sfi,int index,ICCResponse rsp) {

        ICCApdu apdu = new ICCApdu(APDU_READ_RECORD);
        apdu._p1 = (byte)index;
        apdu._p2 = (byte)(((sfi & 0x1F) << 3) | 0x04);

        apdu._mask = 0x4F;

        LogUtil.i(TAG, "----------------------- EMV/PBOC read record");
        return mIcc._ApduComm(apdu,rsp);
    }

    /**
     * Build APDU for GPO command. See ISO 7816-4.
     *
     * @param pdol APDU command Part for GPO
     * @param rsp APDU rsponse of Card
     *
     */
    public int GetProcessOptions(byte[] pdol ,ICCResponse rsp) {

        ICCApdu apdu = new ICCApdu(APDU_GET_PROCESS_OPTIONS);

        apdu.setData(pdol);

        LogUtil.i(TAG, "----------------------- EMV/PBOC get process options");
        return mIcc._ApduComm(apdu,rsp);
    }

    /**
     * Build APDU for GET_DATA command.  See ISO 7816-4.
     *
     * @param tag Emv/PBOC Tag eg. "9F79"
     * @param rsp APDU rsponse of Card
     * @return sw eg. 0x9000
     */
    public int GetData(String tag,ICCResponse rsp) {

        byte[] btag = HexUtil.HexStringToByteArray(tag);

        ICCApdu apdu = new ICCApdu(APDU_GET_DATA);
        apdu._p1 = btag[0];
        apdu._p2 = btag[1];

        apdu._mask = 0x4F;

        LogUtil.i(TAG, "----------------------- EMV/PBOC get data");
        return mIcc._ApduComm(apdu,rsp);
    }

}
