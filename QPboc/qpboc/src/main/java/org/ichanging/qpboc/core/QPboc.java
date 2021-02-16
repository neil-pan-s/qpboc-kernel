package org.ichanging.qpboc.core;

import org.ichanging.qpboc.callback.ByteArrayCallback;
import org.ichanging.qpboc.callback.QCallback;
import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

import java.util.ArrayList;

/**
 * Created by ChangingP on 16/6/16.
 */
public class QPboc extends ProcessSwitch{

    private static final String TAG = "QPboc";

    private ICCCommand    mICC;
    private EMVParam      mParam;
    private EMVBuf        mBuf;

    private QOption       mOption;
    private ProcessAble   mProcess = null;

    private ArrayList<ProcessAble> mQProcessTable;
    private ArrayList<ProcessAble> mQProcessOnlineTable;
    private ArrayList<ProcessAble> mQProcessOfflineTable;

    private ArrayList<ProcessAble> mECashQueryProcessTable;

    public QPboc(ICCInterface icc, CoreInterface adapter)
    {
        // set delegate for icc
        mICC    = ICCCommand.getInstance();
        mICC.setICC(icc);

        mParam  = EMVParam.getInstance();
        mParam.setDataPath(adapter._getDataPath());

        mBuf    = EMVBuf.getInstance();

        mOption = new QOption();
        mOption.setCoreInterface(adapter);
        mOption.setProcessSwitch(this);

        // QPBOC Process
        mQProcessTable = new ArrayList<>();
        mQProcessTable.add(new QPreProcess(mOption));
        mQProcessTable.add(new QSelectPPSE(mOption));
        mQProcessTable.add(new QSelectAid(mOption));
        mQProcessTable.add(new QPreProcess(mOption));
        mQProcessTable.add(new QAppInit(mOption));
        mQProcessTable.add(new QTerminalActionAnalysis(mOption));

        // QPBOC Online Process
        mQProcessOnlineTable = new ArrayList<>();
        mQProcessOnlineTable.add(new QCardholderVerification(mOption));

        // QPBOC Offline Process
        mQProcessOfflineTable = new ArrayList<>();
        mQProcessOfflineTable.add(new QReadAppData(mOption));
        mQProcessOfflineTable.add(new QfDDAProcess(mOption));

        // ECashQuery Process
        mECashQueryProcessTable = new ArrayList<>();
        mECashQueryProcessTable.add(new QPreProcess(mOption));
        mECashQueryProcessTable.add(new QSelectPPSE(mOption));
        mECashQueryProcessTable.add(new QSelectAid(mOption));
        mECashQueryProcessTable.add(new QAppInit(mOption));
        mECashQueryProcessTable.add(new QECashQuery(mOption));

    }

    private byte[] generateRandomNumber()
    {
        byte[] rbytes = new byte[4];

        for (int i = 0; i < 4 ; i++)
        {
            rbytes[i] = (byte) ( mOption.getCoreInterface()._GenerateRandomInt() & 0xFF);
        }

        return rbytes;
    }

    public void onTransactionProcess(byte transType,int amount,QCallback callback)
    {
        mOption.clear();
        mParam.clear();
        mBuf.clear();

        setProcessTable(mQProcessTable);
        //setProcessTable(mECashQueryProcessTable);

        mOption.setQCallback(callback);
        mOption._amount = amount;
        mOption._transType = transType;

        mBuf.setTransAmount(amount, 0);    //Set the transaction amount, the cash back amount
        mBuf.setTagValue("9C", transType);
        mBuf.setTagValue("95",mParam._tvr);
        mBuf.setTagValue("9B",mParam._tsi);

        mBuf.setDateTime();
        mBuf.setUnpredictableNumber(generateRandomNumber());

        onNextProcess(0);
    }

    @Override
    public void onNextProcess(int curProcessResult)
    {
        LogUtil.i(TAG,"onNextProcess - ProcessResult[ " + curProcessResult +" ] - Current Process - [ " + getCurrentProcess() + " ]");


        if(getCurrentProcess() instanceof QAppInit)
        {
            if(curProcessResult == QCore.QCORE_INITAPP6985)
            {
                // turn to QSelectAid process
                moveToProcess(2);
            }
        }

        if(getCurrentProcess() instanceof QTerminalActionAnalysis)
        {
            if(curProcessResult == QCore.QCORE_ONLINEPROC)
            {
                // turn to QCardholderVerification process
                setProcessTable(mQProcessOnlineTable);
            }
            else if(curProcessResult == QCore.QCORE_OFFLINEPROC)
            {
                // turn to QReadAppData process
                setProcessTable(mQProcessOfflineTable);
            }
        }

        mProcess = nextProcess();
        mProcess.onProcess();
    }

//TODO This is from EMVApi to work like in the Jar
    private void loadParam()
    {
        mBuf.setTagValue("9F39",mParam._pos_entry);             // 9F39(Terminal), n2, 1 bytes ，Point of sale (POS) input method
        mBuf.setTagValue("9F01",mParam._acq_id);                // 9F01(Terminal), n6-11, 6 bytes ，Acquirer line identifier
        mBuf.setTagValue("9F15",mParam._mer_category_code);     // 9F15(Terminal), n4, 2 bytes ，Merchant classification code
        mBuf.setTagValue("9F16",mParam._merchant_id);           // 9F16(Terminal), ans15, 15 bytes ，Merchant identification
        mBuf.setTagValue("9F4E",mParam._merchant_name);         // 9F4E(Terminal), ans20, 20bytes,  商户名称
        mBuf.setTagValue("5F2A",mParam._trans_curr_code);       // 5F2A(Terminal), n3, 2 bytes ，交易货币代码
        mBuf.setTagValue("5F36",mParam._trans_curr_exp);        // 5F36(Terminal), n1, 1 bytes ，交易货币指数
        mBuf.setTagValue("9F3C",mParam._trans_ref_curr_code);   // 9F3C(Terminal), n3, 2 bytes ，交易参考货币代码
        mBuf.setTagValue("9F3D",mParam._trans_ref_curr_exp);    // 9F3D(Terminal), n1, 1 bytes ，交易参考货币指数
        mBuf.setTagValue("9F1A",mParam._term_country_code);     // 9F1A(Terminal), n3, 2 bytes ，终端国家代码
        mBuf.setTagValue("9F1E",mParam._ifd_serial_num);        // 9F1E(Terminal), an8, 8 bytes ，接口设备（IFD）序列号
        mBuf.setTagValue("9F1C",mParam._terminal_id);           // 9F1C(Terminal), an8, 8 bytes ，终端标识
        mBuf.setTagValue("9F38",mParam._default_tdol);          // Default Transaction Certificate Data Object List (TDOL)

        mBuf.setTagValue("9F35",mParam._type);     // 9F35(Terminal), n2, 1 ,终端类型
        mBuf.setTagValue("9F33",mParam._cap);      // 9F33(Terminal), b,  3 ，终端能力
        mBuf.setTagValue("9F40",mParam._add_cap);  // 9F40(Terminal), b,  5 ，终端附加能力
    }

    public void setParam(String pid,String tid,String sname,String sn)
    {
        mParam.initParam(pid,tid,sname,sn);
        loadParam();

        mParam.loadAids();
        mParam.loadCapks();
    }

    public void updateAid(String field62,boolean isInit)
    {
        if(isInit) mParam.clearAids();

        EMVAid aid = EMVAid.genetate4Filed62(HexUtil.HexStringToByteArray(field62));

        aid.setDataPath(this.mOption.getCoreInterface()._getDataPath());
        aid.save();
        mParam.loadAids();
    }
    public byte[] getField55()
    {

        /*
         9F26 08 3D4148E5C3C63C83
         9F27 01 80
         9F10 13 070F0103A00000010A010000001867032654FE
         9F37 04 4490034E
         9F36 02 042E
         95   05 0000000000
         9A   03 160325
         9C   01 00
         9F02 06 000000000001
         5F2A 02 0156
         82   02 7C00
         9F1A 02 0156
         9F03 06 000000000000
         9F33 03 E0E9C8
         9F34 03 020000
         9F35 01 22
         9F1E 08 3833323049434330
         84   08 A000000333010101
         9F09 02 0020
         9F41 04 00000000
         */

        int[] tagTable =
                {
                        0x9F26, 0x9F27, 0x9F10, 0x9F37, 0x9F36, 0x95, 0x9A, 0x9C,
                        0x9F02, 0x5F2A, 0x82, 0x9F1A, 0x9F03, 0x9F33, 0x9F34, 0x9F35, 0x9F1E, 0x84,
                        0x9F09, 0x9F41
                };

        EMVTag tag;
        byte[] tmp, field = new byte[1024];
        int len,fieldLen = 0;

        int iTraceNumber = this.mOption.getCoreInterface()._inc_tsc();
        mBuf.setTagValue("9F41",HexUtil.UnsignedIntToByte4(iTraceNumber));

        for (int iTag : tagTable )
        {
            tag = mBuf.findTag(iTag);
            if( tag == null || tag.getTagValue() == null)
            {
                LogUtil.w(TAG,"getField55 - [ " + String.format("%04x",iTag) + " ] Tag is Missing");
                return null;
            }

            // field 55 tag len all are 1 byte
            len = tag.getTagID().length + 1 +tag.getTagValue().length;
            tmp = new byte[len];

            len = 0;

            System.arraycopy(tag.getTagID(),0,tmp,len,tag.getTagID().length);
            len += tag.getTagID().length;

            tmp[len] = (byte)tag.getTagValue().length;
            len++;

            System.arraycopy(tag.getTagValue(),0,tmp,len,tag.getTagValue().length);
            len += tag.getTagValue().length;

            System.arraycopy(tmp,0,field,fieldLen,tmp.length);
            fieldLen += len;
        }

        byte[] field55 = new byte[fieldLen];
        System.arraycopy(field,0,field55,0,fieldLen);

        LogUtil.i(TAG,"Field55 - [ " + HexUtil.ByteArrayToHexString(field55) + " ]");

        return field55;
    }

}
