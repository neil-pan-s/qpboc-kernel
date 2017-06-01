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

    private ICCCommand    mICC = null;
    private EMVParam      mParam = null;
    private EMVBuf        mBuf = null;

    private QOption       mOption = null;
    private ProcessAble   mProcess = null;

    private ArrayList<ProcessAble> mQProcessTable = null;
    private ArrayList<ProcessAble> mQProcessOnlineTable = null;
    private ArrayList<ProcessAble> mQProcessOfflineTable = null;

    private ArrayList<ProcessAble> mECashQueryProcessTable = null;

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

        mBuf.setTransAmount(amount, 0);    //设置交易金额,返现金额
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
}
