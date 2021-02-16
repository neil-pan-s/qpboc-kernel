package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ChangingP on 16/6/20.
 */
public class QSelectAid extends QCore implements ProcessAble {
    private static final String TAG = "QSelectAid";
    private ArrayList<EMVCandidate> mCandidateList = null;
    private EMVCandidate mCurCandidate = null;
    private ArrayList<EMVAid> mAidList = null;
    private EMVAid mAid = null;

    public QSelectAid(QOption option) {
        super(option);
    }

    private int loadAidParam(byte transType, EMVCandidate candidate)
    {
        mAidList = mParam.getAids();

        LogUtil.i(TAG,"loadAidParam - Candidate AID: " + HexUtil.ByteArrayToHexString(candidate._aid));

        for (EMVAid aid  : mAidList)
        {
            LogUtil.i(TAG,"loadAidParam - Search AID: " + HexUtil.ByteArrayToHexString(aid._aid));

            if(Arrays.equals(candidate._aid,aid._aid))
            {
                LogUtil.i(TAG,"loadAidParam - AID [" + aid + "]");
                mAid = aid;
                break;
            }

            // Allow partial matching
            if( candidate._aid.length > aid._aid.length && aid._app_sel_indicator == 0)
            {
                byte[] tmp = new byte[aid._aid.length];
                System.arraycopy(candidate._aid,0,tmp,0,aid._aid.length);

                if(Arrays.equals(tmp,aid._aid))
                {
                    LogUtil.i(TAG,"loadAidParam - AID - part match [" + aid + "]");
                    mAid = aid;
                    break;
                }
            }
        }

        if(mAid == null)
        {
            LogUtil.w(TAG,"loadAidParam - No Matched AID");

            return -1;
        }

        // terminal type
        mBuf.setTagValue(0x9F35, mParam._type);
        // 终端能力
        mBuf.setTagValue(0x9F33, mParam._cap);
        // 终端附件能力
        mBuf.setTagValue(0x9F40, mParam._add_cap);
        // 应用标识(AID)--终端
        mBuf.setTagValue(0x9F06, mAid._aid);
        // 应用版本
        mBuf.setTagValue(0x9F09, mAid._app_ver);
        // 销售点输入方式
        mBuf.setTagValue(0x9F39, mParam._pos_entry);
        // 终端最低限额
        mBuf.setTagValue(0x9F1B, mAid._floorlimit);
        // 受单行标识
        mBuf.setTagValue(0x9F01, mParam._acq_id);
        // 商户分类码
        mBuf.setTagValue(0x9F15, mParam._mer_category_code);
        // 商户标识
        mBuf.setTagValue(0x9F16, mParam._merchant_id);
        //商户名称
        mBuf.setTagValue(0x9F4E, mParam._merchant_name);
        // 交易货币代码
        mBuf.setTagValue(0x5F2A, mParam._trans_curr_code);
        // 交易货币指数
        mBuf.setTagValue(0x5F36, mParam._trans_curr_exp);
        // 交易参考货币代码
        mBuf.setTagValue(0x9F3C, mParam._trans_ref_curr_code);
        // 交易参考货币指数
        mBuf.setTagValue(0x9F3D, mParam._trans_ref_curr_exp);
        // 终端国家代码
        mBuf.setTagValue(0x9F1A, mParam._term_country_code);
        // IFD序列号
        mBuf.setTagValue(0x9F1E, mParam._ifd_serial_num);
        // 终端标识
        mBuf.setTagValue(0x9F1C, mParam._terminal_id);

        // 电子现金终端交易限额
        mBuf.setTagValue(0x9F7B, mAid._ec_trans_limit);

        return 0;
    }

    private int SelectAid(EMVCandidate candidate)
    {
        int iRet;
        ICCResponse rsp = new ICCResponse();
        EMVTlv tlv,subtlv = null;
        TLVTag tlvTag;
        byte[] btag = null;

        mBuf.setTagValue("4F",candidate._aid);
        mBuf.setTagValue("50",candidate._lable);
        mBuf.setTagValue("87",candidate._priority);
        mBuf.setTagValue("9F12",candidate._preferred_name);
        mBuf.setTagValue("9F06",candidate._aid);

        String aid = HexUtil.ByteArrayToHexString(candidate._aid);
        LogUtil.i(TAG,"QSelectAid - AID [" + aid + "]");

        iRet = mICC.SelectAid(candidate._aid,true,rsp);
        if(iRet != 0x9000)
        {
            if(     iRet == 0x6300 || iRet == 0x63C1 || iRet == 0x6983 || iRet == 0x6984 ||
                    iRet == 0x6985 || iRet == 0x6A83 || iRet == 0x6A88 || iRet == 0x6283 ||
                    iRet == 0x6400 || iRet == 0x6500 || iRet == 0x9001 || iRet == 0x6A82 ||
                    iRet == 0x63C2)
            {
                return QCORE_SEL6283;
            }

            return QCORE_SELFAILED;
        }

        tlv = new EMVTlv(rsp.data);
        if (!tlv.validTlv())
        {
            return QCORE_DECODE;
        }

        if(!mBuf.tlv2buf(tlv))
        {
            return QCORE_DECODE;
        }

        if ((tlvTag = tlv.childTag("6F")) == null || tlvTag.value == null)
        {
            return QCORE_SEL6283; /* Cannot find FCI template, return to uing AID list */
        }

        //check tag 6f
        tlv.setTlv(tlvTag.value);

        if ((tlvTag = tlv.childTag("84")) == null || tlvTag.value == null)
        {
            return QCORE_SEL6283; /* Cannot find DF Name, return to uing AID list */
        }
        if ((tlvTag = tlv.childTag("A5")) == null || tlvTag.value == null)
        {
            return QCORE_SEL6283; /* Cannot find FCI Proprietary Template , return to uing AID list */
        }

        return QCORE_SUCESS;
    }


    public int process() {

        int iRet;

        LogUtil.i(TAG, "------------------ QSelectAid Start -------------------");

        iRet = SelectAid( mCurCandidate );
        LogUtil.i(TAG,"QSelectAid - SelectAid ret[" + iRet + "]");
        if(iRet < 0)
        {
            if(QCORE_SEL6283 != iRet)
            {
                mICC.powerOff();
                return QCORE_TERMINATION;
            }
        }

        // mark this candidate selected
        mCurCandidate._isSelected = true;

        iRet = loadAidParam(mOption._transType, mCurCandidate);
        LogUtil.i(TAG,"QSelectAid - LoadAidParam ret[" + iRet + "]");
        if(iRet < 0)
        {
            mICC.powerOff();
            return QCORE_TERMINATION;
        }

        return QCORE_SUCESS;
    }


    @Override
    public void onProcess()
    {

        mCandidateList = mParam.getCandidates();

        // Select an unselected AID and apply application initialization
        for(int i = 0; i < mCandidateList.size(); i++)
        {
            mCurCandidate = mCandidateList.get(i);

            if(!mCurCandidate._isSelected)
            {
                break;
            }
        }

        // All AIDs have been selected
        if(mCurCandidate._isSelected)
        {
            LogUtil.w(TAG, "All Candidates Selected");

            mICC.powerOff();
            mOption.getQCallback().onTermination(QCORE_TERMINATION);
            return;
        }

        int iRet = process();
        LogUtil.i(TAG, "------------------ QSelectAid onProcess [ " + iRet + " ] -------------------");
        if(iRet == QCORE_SUCESS)
        {
            mOption.getProcessSwitch().onNextProcess(QCORE_SUCESS);
        }else{
            mOption.getQCallback().onTermination(iRet);
        }
    }

}
