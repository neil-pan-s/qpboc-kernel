package org.ichanging.qpboc.core;

import org.ichanging.qpboc.callback.QCallback;

/**
 * Created by ChangingP on 16/6/8.
 */
public class QCore {

    private static final String TAG = "QCore";

    public static final int QCORE_SUCESS            = 0;                       //操作成功
    public static final int QCORE_ACCEPT            = 0;                       //交易批准
    public static final int QCORE_DECLINE           = 1;                       //交易拒绝

    public static final int QCORE_ARQC              = 2;                       //终端联机处理
    public static final int QCORE_TC                = 3;                       //终端脱机处理
    public static final int QCORE_AAC               = 4;                       //终端脱机拒绝
    public static final int QCORE_QPBOC             = 5;                       //选择QPBOC 进行交易
    public static final int QCORE_PBOC              = 6;                       //选择PBOC界面进行交易

    public static final int QCORE_ONLINEPROC        = 7;                        //联机处理
    public static final int QCORE_OFFLINEDECLINE    = 8;                        //脱机拒绝
    public static final int QCORE_OFFLINEPROC       = 9;                        //脱机交易
    public static final int QCORE_CVM_ONLINEPIN     = 10;                       //联机密文PIN
    public static final int QCORE_CVM_SIGNE         = 11;                       //签名

    public static final int QCORE_GOSEARCHCARD      = 12;                       //去寻卡
    public static final int QCORE_TRYOTHEAR         = 13;                       //提示尝试其它界面


    public static final int QCORE_BASE              = (-3000);
    public static final int QCORE_TERMINATION       = (QCORE_BASE - 1);         //交易终止

    public static final int QCORE_SEARCHCARDTIMEOUT = (QCORE_BASE - 2);         //寻卡超时
    public static final int QCORE_ICPOWERUPFAILED   = (QCORE_BASE - 3);         //上电失败

    public static final int QCORE_SELPPSEFAILED     = (QCORE_BASE - 4);         //选择PPSE失败
    public static final int QCORE_DECODE            = (QCORE_BASE - 5);         //解析tlv错
    public static final int QCORE_NO6F              = (QCORE_BASE - 6);         //没有6F
    public static final int QCORE_NO84              = (QCORE_BASE - 7);         //没有84
    public static final int QCORE_NOA5              = (QCORE_BASE - 8);         //没有A5
    public static final int QCORE_NOBF0C            = (QCORE_BASE - 9);         //没有BF0C
    public static final int QCORE_NO61              = (QCORE_BASE - 10);        //没有61
    public static final int QCORE_NO4F              = (QCORE_BASE - 11);        //没有4F
    public static final int QCORE_NO50              = (QCORE_BASE - 12);        //没有50
    public static final int QCORE_NO87              = (QCORE_BASE - 13);        //没有87
    public static final int QCORE_NOAID             = (QCORE_BASE - 14);        //AID列表为空

    public static final int QCORE_FAILED            = (QCORE_BASE - 15);        //失败

    public static final int QCORE_SEL6283           = (QCORE_BASE - 16);        //选择AID返回6283
    public static final int QCORE_SELFAILED         = (QCORE_BASE - 17);        //选择AID失败

    public static final int QCORE_NO9F38            = (QCORE_BASE - 18);        //没有PDL 9F38
    public static final int QCORE_DOLFMTNO9F66      = (QCORE_BASE - 19);        //PDOL 里没有9F66
    public static final int QCORE_DOLPACK           = (QCORE_BASE - 20);        //打包PDOL错
    public static final int QCORE_INITAPP6984       = (QCORE_BASE - 21);        //应用初始化返回6984
    public static final int QCORE_INITAPP6985       = (QCORE_BASE - 22);        //应用初始化返回6985

    public static final int QCORE_80VALUELEN        = (QCORE_BASE - 23);        //80 长度错
    public static final int QCORE_77NOAIP           = (QCORE_BASE - 24);        //77中没有82 AIP
    public static final int QCORE_AIPLEN            = (QCORE_BASE - 25);        //AIP长度错
    public static final int QCORE_77NOAFL           = (QCORE_BASE - 26);        //77 中没有94AFL
    public static final int QCORE_AFLLEN            = (QCORE_BASE - 27);        //AFL长度错
    public static final int QCORE_UNEXPECTTAG       = (QCORE_BASE - 28);        //应用初始化返回没有80或77
    public static final int QCORE_NO9F10            = (QCORE_BASE - 29);        //IC卡返回数据中没有9F10

    public static final int QCORE_NO94              = (QCORE_BASE - 50);        //没有94
    public static final int QCORE_READREC_FZERO     = (QCORE_BASE - 51);	    //文件中要读的第 1 个记录的记录号为0
    public static final int QCORE_READREC_RECRANGEERR = (QCORE_BASE - 52);	    //记录条数有误
    public static final int QCORE_READREC_SFIERR    = (QCORE_BASE - 53);	    //sfi错
    public static final int QCORE_READCMDERR        = (QCORE_BASE - 54);        //读应用数据指令返回错
    public static final int QCORE_SAVEAPPDATA       = (QCORE_BASE - 55);        //保存应用数据错
    public static final int QCORE_LOSTEFFICACY      = (QCORE_BASE - 56);        //应用失效
    public static final int QCORE_NOTTOUSE          = (QCORE_BASE - 57);        //应用未启用
    public static final int QCORE_ERRSDADATA        = (QCORE_BASE - 58);        //错误的静态数据认证数据

    public static final String PSE = "1PAY.SYS.DDF01";
    public static final String PPSE = "2PAY.SYS.DDF01";

    protected ICCCommand    mICC;
    protected EMVParam      mParam;
    protected EMVBuf        mBuf;

    protected QOption       mOption;

    protected CoreInterface mAdapter;
    protected QCallback     mCallback;
    protected ProcessSwitch mSwitch;

    public QCore(QOption option) {
        mICC    = ICCCommand.getInstance();
        mParam  = EMVParam.getInstance();
        mBuf    = EMVBuf.getInstance();

        mOption = option;

        if(option != null)
        {
            mAdapter = option.getCoreInterface();
            mCallback = option.getQCallback();
            mSwitch = option.getProcessSwitch();
        }
    }
}
