package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ChangingP on 16/6/8.
 */
public class EMVParam {

    private static final String TAG = "EMVParam";

    /**
     *  QCore Param
     */

    // 终端支持的应用AID中支持的最大的非接触读写器脱机最低限额
    public static int max_qpboc_offline_limit = 0;

    // 终端支持的应用AID中支持的最大的非接触读写器交易限额
    public static int max_qpboc_trans_limit = 0;

    // 终端支持的应用AID中支持的读写器持卡人验证方法（CVM）所需限额
    public static int max_qpboc_cvm_limit = 0;


    /**
     * Terminal Param
     */

    public static byte   _pos_entry;            	// 9F39(Terminal), n2, 1 bytes ，销售点（POS）输入方式
    public static byte[] _acq_id;                   // 9F01(Terminal), n6-11, 6 bytes ，收单行标识
    public static byte[] _mer_category_code;	  	// 9F15(Terminal), n4, 2 bytes ，商户分类码
    public static byte[] _merchant_id;		  		// 9F16(Terminal), ans15, 15 bytes ，商户标识
    public static byte[] _merchant_name;            // 9F4E(Terminal), ans20, 20bytes,  商户名称
    public static byte[] _trans_curr_code;	  		// 5F2A(Terminal), n3, 2 bytes ，交易货币代码
    public static byte   _trans_curr_exp;		  	// 5F36(Terminal), n1, 1 bytes ，交易货币指数
    public static byte[] _trans_ref_curr_code;  	// 9F3C(Terminal), n3, 2 bytes ，交易参考货币代码
    public static byte   _trans_ref_curr_exp;	  	// 9F3D(Terminal), n1, 1 bytes ，交易参考货币指数
    public static byte[] _term_country_code;	  	// 9F1A(Terminal), n3, 2 bytes ，终端国家代码
    public static byte[] _ifd_serial_num; 	  		// 9F1E(Terminal), an8, 8 bytes ，接口设备（IFD）序列号
    public static byte[] _terminal_id;		  		// 9F1C(Terminal), an8, 8 bytes ，终端标识
    public static byte[] _default_tdol;             // 缺省交易证书数据对象列表（TDOL）
    public static byte[] _terminal_capa;            // 应用定义的终端能力

    // Terminal Capability
    public static byte[] _ics;
    public static byte   _type;                     // 9F35(Terminal), n2, 1 ,终端类型
    public static byte[] _cap;                      // 9F33(Terminal), b,  3 ，终端能力
    public static byte[] _add_cap;                  // 9F40(Terminal), b,  5 ，终端附加能力


    /**
     * Buffer Param
     *
     */

    public static byte[] _tvr;
    public static byte[] _tsi;
    public static byte[] _pboclog;      //交易日志入口
    public static byte[] _loadlog;      //圈存日志入口



    /**
     * Account Type
     *
     * See PBOC Book 6 Annex A.7
     *
     *  0x00 默认-未指定 0x10 储蓄账户 0x20 支票账户/借记账户 0x30 信用账户
     *
     */
    public static final int ACCOUNT_TYPE_DEFAULT    = 0x00;
    public static final int ACCOUNT_TYPE_DEPOSIT    = 0x10;
    public static final int ACCOUNT_TYPE_DEBIT      = 0x20;
    public static final int ACCOUNT_TYPE_CREDIT     = 0x30;



    /**
     * Cardholder ID Type
     *
     * See PBOC Book 5
     *
     *  0x00:身份证 0x01:军官证 0x02:护照 0x03:入境证 0x04:临时身份证 0x05:其它
     *
     */
    public static final int CVM_CERT_TYPE_ID            = 0x00;
    public static final int CVM_CERT_TYPE_OFFICER       = 0x01;
    public static final int CVM_CERT_TYPE_PASSPORT      = 0x02;
    public static final int CVM_CERT_TYPE_ID_TEMPORARY  = 0x03;
    public static final int CVM_CERT_TYPE_ARRIVAL_CARD  = 0x04;
    public static final int CVM_CERT_TYPE_OTHER         = 0x05;

    /**
     *  EMV Kernel Config Param
     */

    public static final String _conf_name       = "kernel.app";     //内核文件
    public static final String _capk_name       = "capk.app";       //公钥文件
    public static final String _capk_ecc_name   = "capk_sm.app";    //国密算法公钥文件
    public static final String _cert_blk        = "certblk.app";    //公钥回收文件
    public static final String _emv_log         = "emv.log";        //PBOC内核交易记录，主要用于频度检查等

    public static final int max_ddol_len = 252;
    public static final int max_tdol_len = 252;

    // RSA
    public static final int max_rsa_modulus_len  = 248;
    public static final int max_rsa_key_len      = 256;

    public static final int TRANS_Support_Goods_Services      = (0x0080);
    public static final int TRANS_Support_Cash                = (0x0040);
    public static final int TRANS_Support_Adjustment          = (0x0020);
    public static final int TRANS_Support_Return              = (0x0010);
    public static final int TRANS_Support_Deposit             = (0x0008);
    public static final int TRANS_Support_Inquiry             = (0x0004);
    public static final int TRANS_Support_Transfer            = (0x0002);
    public static final int TRANS_Support_Load                = (0x0001);

    public static int trans_opt_get(int nr , int[] _support) {
        return ((_support)[((int) (nr)) >> 8] & (((int) (nr)) & 0x00FF));
    }



    //支持接触式PBOC
    public static final int CAPA_Support_Contact_Pboc         = (0x0080);
    //支持非接触式PBOC
    public static final int CAPA_Support_Contactless_Pboc     = (0x0040);
    //支持电子现金
    public static final int CAPA_Support_Electronic_Cash      = (0x0020);
    //支持QPBOC
    public static final int CAPA_Support_QPBOC                = (0x0010);

    //支持QPBOC的状态监测
    public static final int CAPA_Support_QPBOC_Status_Check   = (0x0180);
    //支持强制联机
    public static final int CAPA_Support_Force_Online         = (0x0140);
    //支持账户选择
    public static final int CAPA_Support_Account_Select       = (0x0120);
    //支持强制联机密码
    public static final int CAPA_Support_Force_OnlinePin      = (0x0110);
    //支持PIN BY PASS
    public static final int CAPA_Support_PinByPass            = (0x0108);
    //支持国密算法
    public static final int CAPA_Support_SM                   = (0x0104);
    //支持英语语言
    public static final int CAPA_Support_English              = (0x0102);

    public static int capa_opt_get(int nr, byte[] _capa) {
        return ((_capa)[((int) (nr)) >> 8] & (((int) (nr)) & 0x00FF));
    }

    public static int capa_opt_set(int nr, byte[] _capa) {
        return ((_capa)[((int) (nr)) >> 8] |= (((int) (nr)) & 0x00FF));
    }

    public static int capa_opt_unset(int nr,byte[] _capa) {
        return ((_capa)[((int) (nr)) >> 8] &= ~(((int) (nr)) & 0x00FF));
    }

    /*
    * AS   : Application Selection
    * Macro:
        AS_Support_PSE                 : Support PSE selection method
        AS_Support_CardHolder_Confirm  : Support Cardholder confirmation
        AS_Support_Prefferd_Order      : Have a preferred order of displaying applications
        AS_Support_Partial_AID         : Does the terminal perform partial AID selection
        AS_Support_Multi_Language      : Does the terminal have multi language support
        AS_Support_Common_Charset      : Does the terminal support Common Character Set as
        defined in "Annex B table 20 Book 4"

    * EMV 4.1 ICS Version 3.9 Level2
    */
    public static final int  AS_Support_PSE                  = (0x0080);
    public static final int  AS_Support_CardHolder_Confirm   = (0x0040);
    public static final int  AS_Support_Preferred_Order      = (0x0020);
    public static final int  AS_Support_Partial_AID          = (0x0010);
    public static final int  AS_Support_Multi_Language       = (0x0008);
    public static final int  AS_Support_Common_Charset       = (0x0004);

    /*
    * DA   : Data Authentication
    * IPKC : Issuer Public Key Certificate
    * CAPK : Certification Authority Public Key
    * Macro:
        DA_Support_IPKC_Revoc_Check      : During DA, does the terminal check the revocation of IPKC
        DA_Support_Default_DDOL          : Does the terminal contain a default DDOL
        DA_Support_CAPKLoad_Fail_Action  : Is operation action required when loading CAPK fails
        DA_Support_CAPK_Checksum         : Is CAPK verified with CAPK checksum

    * EMV 4.1 ICS Version 3.9 Level2
    */
    public static final int  DA_Support_IPKC_Revoc_Check          = (0x0180);
    public static final int  DA_Support_Default_DDOL              = (0x0140);
    public static final int  DA_Support_CAPKLoad_Fail_Action      = (0x0120);
    public static final int  DA_Support_CAPK_Checksum             = (0x0110);

    /*
    * CV   : Cardholder Verification
    * CVM   : Cardholder Verification Methods
    * Macro:
        CV_Support_Bypass_PIN          : Terminal supports bypass PIN entry
        CV_Support_PIN_Try_Counter     : Terminal supports Get Data for PIN Try Counter
        CV_Support_Fail_CVM            : Terminal supports Fail CVM
        CV_Support_Amounts_before_CVM  : Are amounts known before CVM processing

    * EMV 4.1 ICS Version 3.9 Level2
    */
    public static final int  CV_Support_Bypass_PIN              = (0x0280);
    public static final int  CV_Support_PIN_Try_Counter         = (0x0240);
    public static final int  CV_Support_Fail_CVM                = (0x0220);
    public static final int  CV_Support_Amounts_before_CVM      = (0x0210);
    public static final int  CV_Support_Bypass_ALL_PIN          = (0x0208);

    /*
    * TRM  : Terminal Risk Management
    * Macro:
        TRM_Support_FloorLimit     : Floor Limit Checking,
        Mandatory for terminal with offline capability
        TRM_Support_RandomSelect   : Random Transaction Selections,
        Mandatory for offline terminal with online capability,
        except when cardholder controlled
        TRM_Support_VelocityCheck  : Velocity checking,
        Mandatory for for terminal with offline capability
        TRM_Support_TransLog       : Support transaction log
        TRM_Support_ExceptionFile  : Support exception file
        TRM_Support_AIPBased       : Performance of TRM based on AIP setting
        TRM_Use_EMV_LogPolicy      : EMV has a different log policy with PBOC2, marked here

    * EMV 4.1 ICS Version 3.9 Level2
    */
    public static final int  TRM_Support_FloorLimit        = (0x0380);
    public static final int  TRM_Support_RandomSelect      = (0x0340);
    public static final int  TRM_Support_VelocityCheck     = (0x0320);
    public static final int  TRM_Support_TransLog          = (0x0310);
    public static final int  TRM_Support_ExceptionFile     = (0x0308);
    public static final int  TRM_Support_AIPBased          = (0x0304);
    public static final int  TRM_Use_EMV_LogPolicy         = (0x0302);
    public static final int  TRM_Support_CardBin           = (0x0301);


    /*
    * TAA  : Terminal Action Analysis
    * (x)  : the var of struct emvconfig
    * TAC  : Terminal Action Codes
    * DAC  : Default Action Codes
    * Macro:
        TAA_Support_TAC                  : Does the terminal support Terminal Action Codes
        TAA_Support_DAC_before_1GenAC    : Does the terminal process DAC prior to first GenAC
        TAA_Support_DAC_after_1GenAC     : Does the terminal process DAC after first GenAC
        TAA_Support_Skip_DAC_OnlineFail  : Does the terminal skip DAC processing and automatically
        request an AAC when unable to go online
        TAA_Support_DAC_OnlineFail       : Does the terminal process DAC as normal
        when unable to go online
        TAA_Support_CDAFail_Detected     : Device capable of detecting CDA Failure before TAA
        TAA_Support_CDA_Always_in_ARQC   : CDA always requested in a first Gen AC, ARQC request
        TAA_Support_CDA_Never_in_ARQC    : CDA never requested in a first Gen AC, ARQC request
        TAA_Support_CDA_Alawys_in_2TC    : CDA always requested in a second Gen AC when successful
        host response is received, with TC request
        TAA_Support_CDA_Never_in_2TC     : CDA never requested in a second Gen AC when successful
        host response is received, with TC request


    * EMV 4.1 ICS Version 3.9 Level2
    */
    public static final int  TAA_Support_TAC                    = (0x0480);
    public static final int  TAA_Support_DAC_before_1GenAC      = (0x0440);
    public static final int  TAA_Support_DAC_after_1GenAC       = (0x0420);
    public static final int  TAA_Support_Skip_DAC_OnlineFail    = (0x0410);
    public static final int  TAA_Support_DAC_OnlineFail         = (0x0408);
    public static final int  TAA_Support_CDAFail_Detected       = (0x0404);
    public static final int  TAA_Support_CDA_Always_in_ARQC     = (0x0402);
    public static final int  TAA_Support_CDA_Alawys_in_2TC      = (0x0401);

    /*
    * CP  : Completion Process
    * (x)  : the var of struct emvconfig
    * Macro:
        CP_Support_Force_Online         : Transaction forced Online capability
        CP_Support_Force_Accept         : Transaction forced Acceptance capability
        CP_Support_Advices              : Does the terminal support advices
        CP_Support_Issuer_VoiceRef      : Does the terminal support Issuer Initiated Voice Referrals
        CP_Support_Batch_Data_Capture   : Does the terminal support Batch Data Capture
        CP_Support_Online_Data_capture  : Does the terminal support Online Data Capture
        CP_Support_Default_TDOL         : Does the terminal support a default TDOL

    * EMV 4.1 ICS Version 3.9 Level2
    */
    public static final int  CP_Support_Force_Online           = (0x0580);
    public static final int  CP_Support_Force_Accept           = (0x0540);
    public static final int  CP_Support_Advices                = (0x0520);
    public static final int  CP_Support_Issuer_VoiceRef        = (0x0510);
    public static final int  CP_Support_Batch_Data_Capture     = (0x0508);
    public static final int  CP_Support_Online_Data_capture    = (0x0504);
    public static final int  CP_Support_Default_TDOL           = (0x0502);

    /*
    * MISC : Miscellaneous
    * (x)  : the var of struct emvconfig
    * Macro:
        MISC_Support_Account_Select         : Does the terminal support account type selection
        MISC_Support_ISDL_Greater_than_128  : Is Issuer Script Device Limit greater than 128 bytes
        MISC_Support_Internal_Date_Mana     : Does the terminal support internal date management

    * EMV 4.1 ICS Version 3.9 Level2
    */
    public static final int  MISC_Support_Account_Select           = (0x0680);
    public static final int  MISC_Support_ISDL_Greater_than_128    = (0x0640);
    public static final int  MISC_Support_Internal_Date_Mana       = (0x0620);


    public static int  ics_opt_get(int nr, byte[] _ics) {
        return ((_ics)[((int) (nr)) >> 8] & (((int) (nr)) & 0x00FF));
    }

    public static int  ics_opt_set(int nr,byte[] _ics) {
        return ((_ics)[((int) (nr)) >> 8] |= (((int) (nr)) & 0x00FF));
    }

    public static int  ics_opt_unset(int nr,byte[] _ics){
        return ((_ics)[((int) (nr)) >> 8] &= ~(((int) (nr)) & 0x00FF));
    }


    /*
        RESV_Terminal_Present_DefaultTAC
        RESV_Terminal_Present_DenialTAC
        RESV_Terminal_Present_OnlineTAC     : to indicate whether the terminal provide these TACs
    */
    public static final int  RESV_Terminal_Present_DefaultTAC      = (0x0008);
    public static final int  RESV_Terminal_Present_DenialTAC       = (0x0004);
    public static final int  RESV_Terminal_Present_OnlineTAC       = (0x0002);

    public static int  _status_get(int nr,byte[] _status) {
        return ics_opt_get(nr, _status);
    }

    public static int  _status_set(int nr,byte[] _status) {
        return ics_opt_set(nr, _status);
    }

    public static int  _status_unset(int nr,byte[] _status)
    {
        return ics_opt_unset(nr, _status);
    }

    /*
    * TT : Terminal Type
    */
    public static boolean  TT_Unattended(byte x)
    {
        return (((x) & 0x0F) > 3);
    }

    public static boolean  TT_Attended(byte x)
    {
        return (((x) & 0x0F) < 4);
    }

    /*
    * TC : Terminal Capabilities
    */
    public static final int  TC_Manual_Key_Entry          = 0x0080;
    public static final int  TC_Magnetic_Stripe           = 0x0040;
    public static final int  TC_IC_With_Contacts          = 0x0020;

    public static final int  TC_Plaintext_PIN             = 0x0180;
    public static final int  TC_Enciphered_PIN_Online     = 0x0140;
    public static final int  TC_Signature_Paper           = 0x0120;
    public static final int  TC_Enciphered_PIN_Offline    = 0x0110;
    public static final int  TC_No_CVM_Required           = 0x0108;
    public static final int  TC_Cardholder_Cert           = 0x0101;

    public static final int  TC_SDA                       = 0x0280;
    public static final int  TC_DDA                       = 0x0240;
    public static final int  TC_Card_Capture              = 0x0220;
    public static final int  TC_CDA                       = 0x0208;

    public static boolean  terminal_cap(int nr,byte[] cap)
    {
        return ics_opt_get(nr, cap) != 0x00;
    }

    public static int  terminal_cap_set(int nr,byte[] cap)
    {
        return ics_opt_set(nr, cap);
    }

    public static int  terminal_cap_unset(int nr,byte[] cap)
    {
        return ics_opt_unset(nr, cap);
    }

    public static int  terminal_offline_pin(byte[] cap)
    {
        return  (cap[1] & 0x90);
    }

    /*
    * ATC : Additional Terminal Capabilities
    */
    public static final int  ATC_Cash                           = 0x0080;
    public static final int  ATC_Goods                          = 0x0040;
    public static final int  ATC_Services                       = 0x0020;
    public static final int  ATC_Cashback                       = 0x0010;
    public static final int  ATC_Inquiry                        = 0x0008;
    public static final int  ATC_Transfer                       = 0x0004;
    public static final int  ATC_Payment                        = 0x0002;
    public static final int  ATC_Administrative                 = 0x0001;
    public static final int  ATC_Cash_Deposit                   = 0x0180;

    public static final int  ATC_Numeric_Keys                   = 0x0280;
    public static final int  ATC_Alphabetic_Special_Keys        = 0x0240;
    public static final int  ATC_Command_Keys                   = 0x0220;
    public static final int  ATC_Function_Keys                  = 0x0210;

    public static final int  ATC_Print_Attendant                = 0x0380;
    public static final int  ATC_Print_Cardholder               = 0x0340;
    public static final int  ATC_Display_Attendant              = 0x0320;
    public static final int  ATC_Display_Cardholder             = 0x0310;

    public static final int  ATC_Code_Table_10                  = 0x0302;
    public static final int  ATC_Code_Table_9                   = 0x0301;
    public static final int  ATC_Code_Table_8                   = 0x0480;
    public static final int  ATC_Code_Table_7                   = 0x0440;
    public static final int  ATC_Code_Table_6                   = 0x0420;
    public static final int  ATC_Code_Table_5                   = 0x0410;
    public static final int  ATC_Code_Table_4                   = 0x0408;
    public static final int  ATC_Code_Table_3                   = 0x0404;
    public static final int  ATC_Code_Table_2                   = 0x0402;
    public static final int  ATC_Code_Table_1                   = 0x0401;

    public static int  add_terminal_cap(int nr,byte[] addcap)
    {
        return ics_opt_get(nr, addcap);
    }

    public static int  add_terminal_cap_set(int nr,byte[] addcap)
    {
        return ics_opt_set(nr, addcap);
    }

    public static int  add_terminal_cap_unset(int nr,byte[] addcap){
        return ics_opt_unset(nr, addcap);
    }


    /**
     * Tag
     */

    /*
     * AIP  : Application Interchange Profile
     * (x)  : A Pointer to the AIP <2 bytes>
     * Macro:
       AIP_SDA_Support : SDA supported
       AIP_DDA_Support : DDA supported
       AIP_CV_Support  : CardHolder Verification supported
       AIP_TRM_Support : Terminal Risk Management is to be performed
       AIP_IA_Support  : Issuer authentication supported
       AIP_CDA_Support : CDA supported

     * EMV 4.1 Book3.PartIV.Annex C.C1
     */
    public static int   AIP_SDA_Support(byte x)
    {
        return ((x) & 0x40);
    }
    public static int   AIP_DDA_Support(byte x)
    {
        return ((x) & 0x20);
    }
    public static int   AIP_CV_Support(byte x)
    {
        return ((x) & 0x10);
    }
    public static int   AIP_TRM_Support(byte x)
    {
        return ((x) & 0x08);
    }
    public static int   AIP_IA_Support(byte x)
    {
        return ((x) & 0x04);
    }
    public static int   AIP_CDA_Support(byte x)
    {
        return ((x) & 0x01);
    }


    /*
     * AUC  : Application Usage Control
     * (x)  : A Pointer to the AUC <2 bytes>
     * Macro:
        AUC_For_Domestic_Cash          : Valid for domestic cash transactions
        AUC_For_International_Cash     : Valid for international cash transactions
        AUC_For_Domestic_Goods         : Valid for domestic goods
        AUC_For_International_Goods    : Valid for international goods
        AUC_For_Domestic_Services      : Valid for domestic services
        AUC_For_International_Services : Valid for international services
        AUC_For_ATMs                   : Valid at ATMs
        AUC_For_Not_ATMs               : Valid at terminals other than ATMs
        AUC_For_Domestic_CashBack      : Domestic cashback allowed
        AUC_For_International_CashBack : International cashback allowed
     * EMV 4.1 Book3.PartIV.Annex C.C2
     */
    public static int   AUC_For_Domestic_Cash(byte x)
    {
        return ((x) & 0x80);
    }
    public static int   AUC_For_International_Cash(byte x)
    {
        return ((x) & 0x40);
    }
    public static int   AUC_For_Domestic_Goods(byte x)
    {
        return ((x) & 0x20);
    }
    public static int   AUC_For_International_Goods(byte x)
    {
        return ((x) & 0x10);
    }
    public static int   AUC_For_Domestic_Services(byte x)
    {
        return ((x) & 0x08);
    }
    public static int   AUC_For_International_Services(byte x)
    {
        return ((x) & 0x04);
    }
    public static int   AUC_For_ATMs(byte x)
    {
        return ((x) & 0x02);
    }
    public static int   AUC_For_Not_ATMs(byte x)
    {
        return ((x) & 0x01);
    }
    public static int   AUC_For_Domestic_CashBack(byte x)
    {
        return ((x + 1) & 0x80);
    }
    public static int   AUC_For_International_CashBack(byte x)
    {
        return ((x + 1) & 0x40);
    }

    public static int   AUC_For_Domestic_Goods_or_Services(byte x)
    {
        return ((x) & 0x28);
    }
    public static int   AUC_For_International_Goods_or_Services(byte x)
    {
        return ((x) & 0x14);
    }

    /**
     * Terminal Action Analyzing
     * Generate AC response - CID
     */
    public static boolean Resp_AAC(byte x)
    {
        return (((x) & 0xC0) == 0x00);
    }

    public static boolean Resp_TC(byte x)
    {
        return (((x) & 0xC0) == 0x40);
    }
    public static boolean Resp_ARQC(byte x)
    {
        return (((x) & 0xC0) == 0x80);
    }
    public static boolean Resp_AAR(byte x)
    {
        return (((x) & 0xC0) == 0xC0);
    }
    public static boolean Resp_Advice(byte x)
    {
        return (((x) & 0x08) == 0x08);
    }
    public static boolean Resp_Info(byte x)
    {
        return (((x) & 0x07) == 0x07);
    }


    /**
     * TVR  : Terminal Verification Results -- Byte 1 (Leftmost)
     * Macro:
     OFFLINE_DA_NOT_PERFORMED      : Offline data authentication was not performed
     SDA_FAILED                    : SDA failed
     ICC_DATA_MISSING              : ICC data missing
     CARD_ON_EXCEPTION_FILE        : Card appears on terminal exception file
     DDA_FAILED                    : DDA failed
     CDA_FAILED                    : CDA failed

     * EMV 4.1 Annex C  Coding of Data Elements Used in Transaction Processing C5
     */
    public static final int OFFLINE_DA_NOT_PERFORMED                    = 0x0080;
    public static final int SDA_FAILED                                  = 0x0040;
    public static final int ICC_DATA_MISSING                            = 0x0020;
    public static final int CARD_ON_EXCEPTION_FILE                      = 0x0010;
    public static final int DDA_FAILED                                  = 0x0008;
    public static final int CDA_FAILED                                  = 0x0004;
    public static final int SDA_PERFORMED                               = 0x0002;

    /**
     * TVR  : Terminal Verification Results -- Byte 2
     * Macro:
     APP_VER_NOT_MATCHED            : ICC and terminal have different application versions
     EXPIRED_APP                    : Expired application
     APP_NOT_EFFECTIVE              : Application not yet effective
     REQUESTED_SERVICE_NOT_ALLOWED  : Requested service not allowed for card product
     NEW_CARD                       : New card

     * EMV 4.1 Annex C  Coding of Data Elements Used in Transaction Processing C5
     */
    public static final int APP_VER_NOT_MATCHED                         = 0x0180;
    public static final int EXPIRED_APP                                 = 0x0140;
    public static final int APP_NOT_EFFECTIVE                           = 0x0120;
    public static final int REQUESTED_SERVICE_NOT_ALLOWED               = 0x0110;
    public static final int NEW_CARD                                    = 0x0108;

    /**
     * TVR  : Terminal Verification Results -- Byte 3
     * Macro:
     CV_NOT_SUCCESSFUL                   : Cardholder verification was not successful
     UNRECOGNISED_CVM                    : Unrecognised CVM
     PIN_TRY_LIMIT_EXCEEDED              : PIN Try Limit exceeded
     PIN_PAD_NOT_PRESENT_OR_NOT_WORKING  : PIN entry required and PIN pad not present or not working
     PIN_PAD_PRESENT_BUT_PIN_NOT_ENTERED : PIN entry required, PIN pad present, but PIN was not entered
     ONLINE_PIN_ENTERED                  : Online PIN entered

     * EMV 4.1 Annex C  Coding of Data Elements Used in Transaction Processing C5
     */
    public static final int CV_NOT_SUCCESSFUL                           = 0x0280;
    public static final int UNRECOGNISED_CVM                            = 0x0240;
    public static final int PIN_TRY_LIMIT_EXCEEDED                      = 0x0220;
    public static final int PIN_PAD_NOT_PRESENT_OR_NOT_WORKING          = 0x0210;
    public static final int PIN_PAD_PRESENT_BUT_PIN_NOT_ENTERED         = 0x0208;
    public static final int ONLINE_PIN_ENTERED                          = 0x0204;

    /**
     * TVR  : Terminal Verification Results -- Byte 4
     * Macro:
     TRANSACTION_EXCEEDS_FLOOR_LIMIT           : Transaction exceeds floor limit
     LOWER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED  : Lower consecutive offline limit exceeded
     UPPER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED  : Upper consecutive offline limit exceeded
     TRANSACTION_SELECTED_RANDOMLY_FOR_ONLINE  : Transaction selected randomly for online processing
     MERCHANT_FORCED_TRANSACTION_ONLINE        : Merchant forced transaction online

     * EMV 4.1 Annex C  Coding of Data Elements Used in Transaction Processing C5
     */
    public static final int TRANSACTION_EXCEEDS_FLOOR_LIMIT             = 0x0380;
    public static final int LOWER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED    = 0x0340;
    public static final int UPPER_CONSECUTIVE_OFFLINE_LIMIT_EXCEEDED    = 0x0320;
    public static final int TRANSACTION_SELECTED_RANDOMLY_FOR_ONLINE    = 0x0310;
    public static final int MERCHANT_FORCED_TRANSACTION_ONLINE          = 0x0308;

    /**
     * TVR  : Terminal Verification Results -- Byte 5 (Rightmost)
     * Macro:
     DEFAULT_TDOL_USED                      : Default TDOL used
     ISSUER_AUTHENTICATION_FAILED           : Issuer authentication failed
     SCRIPT_PROCESSING_FAILED_BEFORE_2GENAC : Script processing failed before final GENERATE AC
     SCRIPT_PROCESSING_FAILED_AFTER_2GENAC  : Script processing failed after final GENERATE AC

     * EMV 4.1 Annex C  Coding of Data Elements Used in Transaction Processing C5
     */
    public static final int DEFAULT_TDOL_USED                           = 0x0480;
    public static final int ISSUER_AUTHENTICATION_FAILED                = 0x0440;
    public static final int SCRIPT_PROCESSING_FAILED_BEFORE_2GENAC      = 0x0420;
    public static final int SCRIPT_PROCESSING_FAILED_AFTER_2GENAC       = 0x0410;

    public static int tvr_set(int nr , byte[] _tvr) {
        return (_tvr[((int) (nr)) >> 8] |= (((int) (nr)) & 0x00FF));
    }

    public static int tvr_get(int nr, byte[] _tvr) {
        return (_tvr[((int)(nr)) >> 8] & (((int)(nr)) & 0x00FF));
    }


    /**
     * TSI  : Transaction Status Information --  Byte 1 (Leftmost)
     * Macro:
     OFFLINE_DA_PERFORMED            : Offline data authentication was performed
     CV_PERFORMED                    : Cardholder verification was performed
     CARD_RISK_MANA_PERFORMED        : Card risk management was performed
     ISSUER_AUTH_PERFORMED           : Issuer authentication was performed
     TERM_RISK_MANA_PERFORMED        : Terminal risk management was performed
     SCRIPT_PROCESSING_PERFORMED     : Script processing was performed

     * EMV 4.1 Annex C  Coding of Data Elements Used in Transaction Processing C6
     */
    public static final int OFFLINE_DA_PERFORMED                       = 0x0080;
    public static final int CV_PERFORMED                               = 0x0040;
    public static final int CARD_RISK_MANA_PERFORMED                   = 0x0020;
    public static final int ISSUER_AUTH_PERFORMED                      = 0x0010;
    public static final int TERM_RISK_MANA_PERFORMED                   = 0x0008;
    public static final int SCRIPT_PROCESSING_PERFORMED                = 0x0004;

    public static int tsi_set(int nr , byte[] _tsi) {
        return _tsi[((int) (nr)) >> 8] |= (((int) (nr)) & 0x00FF);
    }

    public static int tsi_get(int nr , byte[] _tsi) {
        return (_tsi[((int) (nr)) >> 8] & (((int) (nr)) & 0x00FF));
    }

    public ArrayList<EMVAid> mAidList = new ArrayList<>();
    public ArrayList<EMVCapk> mCapkList = new ArrayList<>();
    public ArrayList<EMVCandidate> mCandidateList = new ArrayList<>();
    public String mDataPath = null;     //文件读写路径

    private static EMVParam mEmvParam = null;

    public static EMVParam getInstance()
    {
        if(mEmvParam == null)
        {
            mEmvParam = new EMVParam();
        }

        return  mEmvParam;
    }


    public boolean capa_options(int nr)
    {
        return capa_opt_get(nr, this._terminal_capa) != 0;
    }


    public void initParam(String pid,String tid,String sname,String sn)
    {

        byte[] countryCode = { 0x01 , 0x56 };
        byte[] tdol = { (byte)0x9f, 0x08 , 0x02 };
        byte[] capa = { (byte)0x90, 0x04 , 0x00, 0x00 }; // 支持QPboc , 支持强制联机
        byte[] ics = { (byte)0xf4 , (byte)0xf0 , (byte)0xf0 , (byte)0xf8 , (byte)0xaf , (byte)0xfe , (byte)0xa0 };
        byte[] cap = { (byte)0xe0 , (byte)0xe9 , (byte)0xc8 };
        byte[] add_cap = { (byte)0xff ,  (byte)0x80 , (byte)0xf0 , (byte)0xa0 , (byte)0x01 };

        this._pos_entry             = (byte) 0x80;            	                    // 9F39(Terminal), n2, 1 bytes ，销售点（POS）输入方式
        this._acq_id                = HexUtil.HexStringToByteArray("FFFFFFFFFFFF"); // 9F01(Terminal), n6-11, 6 bytes ，收单行标识
        this._mer_category_code     = HexUtil.HexStringToByteArray("FFFF");	  	    // 9F15(Terminal), n4, 2 bytes ，商户分类码
        this._merchant_id           = pid.getBytes();	                            // 9F16(Terminal), ans15, 15 bytes ，商户标识
        this._merchant_name         = sname.getBytes();                             // 9F4E(Terminal), ans20, 20bytes,  商户名称
        this._trans_curr_code       = countryCode;                                  // 5F2A(Terminal), n3, 2 bytes ，交易货币代码
        this._trans_curr_exp        = 0x02;		  	                                // 5F36(Terminal), n1, 1 bytes ，交易货币指数
        this._trans_ref_curr_code   = countryCode;  	                            // 9F3C(Terminal), n3, 2 bytes ，交易参考货币代码
        this._trans_ref_curr_exp    = 0x00;	  	                                    // 9F3D(Terminal), n1, 1 bytes ，交易参考货币指数
        this._term_country_code     = countryCode;	  	                            // 9F1A(Terminal), n3, 2 bytes ，终端国家代码
        this._ifd_serial_num        = sn.getBytes(); 	  		                    // 9F1E(Terminal), an8, 8 bytes ，接口设备（IFD）序列号
        this._terminal_id           = tid.getBytes();		  	                	// 9F1C(Terminal), an8, 8 bytes ，终端标识
        this._default_tdol          = tdol;                                         // 缺省交易证书数据对象列表（TDOL）
        this._terminal_capa         = capa;                                         // 应用定义的终端能力


        this._ics       = ics;
        this._type      = 0x22;                                     // 9F35(Terminal), n2, 1 ,终端类型
        this._cap       = cap;                                      // 9F33(Terminal), b,  3 ，终端能力
        this._add_cap   = add_cap;                                  // 9F40(Terminal), b,  5 ，终端附加能力

    }

    public ArrayList<EMVAid> getAids()
    {
        return mAidList;
    }

    public ArrayList<EMVCapk> getCapks()
    {
        return mCapkList;
    }

    public ArrayList<EMVCandidate> getCandidates()
    {
        return mCandidateList;
    }


    public boolean IsSupportOnline()
    {
        byte utype = (byte)(this._type & 0x0F);
        if(utype == 0x01 || utype == 0x02 || utype == 0x04 || utype == 0x05)
        {
            LogUtil.i(TAG, "Terminal Type " + String.format("%02X",this._type));
            return true;
        }
        return false;
    }


    public boolean IsSupportAid(byte[] aid)
    {
        return true;
    }

    public void clear()
    {
        //Buffer Param
        this._tvr = new byte[5];
        this._tsi = new byte[2];
        this._pboclog = new byte[2];      //交易日志入口
        this._loadlog = new byte[2];      //圈存日志入口
    }

    public void setDataPath(String path)
    {
        mDataPath = path;
    }

    public void clearAids()
    {
        mAidList.clear();
        max_qpboc_offline_limit = 0;
        max_qpboc_trans_limit = 0;
        max_qpboc_cvm_limit = 0;

        String path = mDataPath + "/" + EMVAid._aids_folder;

        File file = new File(path);
        File[] aidList = file.listFiles();

        if(aidList == null) return;

        LogUtil.i(TAG,"" + path + "files count = " + aidList.length);

        for (File f : aidList) {
            if (f.isFile()) {
                LogUtil.i(TAG,"Delete Aid - " + f.getAbsolutePath());
                f.delete();
            }
        }
    }

    public void loadAids()
    {
        int limit = 0,translimit = 0,cvmlimit = 0;
        EMVAid aid = null;

        mAidList.clear();
        max_qpboc_offline_limit = 0;
        max_qpboc_trans_limit = 0;
        max_qpboc_cvm_limit = 0;

        String path = mDataPath + "/" + EMVAid._aids_folder;

        File file = new File(path);
        File[] aidList = file.listFiles();

        if(aidList == null) return;

        LogUtil.i(TAG,"" + path + "files count = " + aidList.length);

        for (File f : aidList)
        {
            if (f.isFile())
            {
                aid = EMVAid.genetateFromFile(f.getAbsolutePath());
                if(aid != null)
                {
                    mAidList.add(aid);
                    LogUtil.i(TAG,"Load Aid - " + aid);

                    //获取最大的3个QPBOC交易限额:
                    //非接触读写器脱机最低限额
                    limit = Integer.parseInt(HexUtil.ByteArrayToHexString(aid._qpboc_offline_limit));
                    LogUtil.i(TAG,"_qpboc_offline_limit = " + limit);
                    if(max_qpboc_offline_limit < limit)
                    {
                        max_qpboc_offline_limit = limit;
                    }

                    //非接触读写器交易限额
                    limit = Integer.parseInt(HexUtil.ByteArrayToHexString(aid._qpboc_trans_limit));
                    LogUtil.i(TAG,"_qpboc_trans_limit = " + limit);
                    if(max_qpboc_trans_limit < limit)
                    {
                        max_qpboc_trans_limit = limit;
                    }

                    //读写器持卡人验证方法（CVM）所需限额
                    limit = Integer.parseInt(HexUtil.ByteArrayToHexString(aid._qpboc_cvm_limit));
                    LogUtil.i(TAG,"_qpboc_cvm_limit = " + limit);
                    if(max_qpboc_cvm_limit < limit)
                    {
                        max_qpboc_cvm_limit = limit;
                    }
                }
            }
        }

        LogUtil.i(TAG,"final max_qpboc_offline_limit = " + max_qpboc_offline_limit + " max_qpboc_trans_limit = " + max_qpboc_trans_limit + " max_qpboc_cvm_limit = " + max_qpboc_cvm_limit);
    }

    //TODO: build Card Black List
    /**
     * Find in Black Card List
     *
     * @param pan
     * @param sn
     * @return
     */
    public boolean isInCardBlack(byte[] pan, int sn)
    {
        return false;
    }

    //TODO: Load Capks
    public int loadCapks()
    {
        return 0;
    }

}
