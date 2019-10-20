package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

import java.util.ArrayList;

/**
 * Created by ChangingP on 16/6/16.
 */
public class QSelectPPSE extends QCore implements ProcessAble {

    private static final String TAG = "QSelectPPSE";
    private ArrayList<EMVCandidate> mCandidateList = null;

    public QSelectPPSE(QOption option) {
        super(option);

        mCandidateList = mParam.getCandidates();
    }

    public int process() {

        int iRet = QCORE_SUCESS;
        EMVTlv tlv = null , tlv_61 = null;
        TLVTag tag = null , tag_61 = null;

        LogUtil.i(TAG, "------------------ QSelectPPSE Start -------------------");

        EMVCandidate candidate = new EMVCandidate();
        mCandidateList.clear();

        ICCResponse rsp = new ICCResponse();

        iRet = mICC.SelectAid(PPSE, true ,rsp);
        if (0x9000 != iRet) {
            return QCORE_SELPPSEFAILED;
        }

        tlv = new EMVTlv(rsp.data);
        if (!tlv.validTlv())
        {
            return QCORE_DECODE;
        }

        if ((tag = tlv.childTag("6F")) == null || tag.value == null)
        {
            return QCORE_NO6F; /* Cannot find FCI template, return to uing AID list */
        }

        //check tag 6f
        tlv.setTlv(tag.value);

        if ((tag = tlv.childTag("84")) == null || tag.value == null)
        {
            return QCORE_NO84; /* Cannot find DF Name, return to uing AID list */
        }
        if ((tag = tlv.childTag("A5")) == null || tag.value == null)
        {
            return QCORE_NOA5; /* Cannot find FCI Proprietary Template , return to uing AID list */
        }

        if(tlv.childTagOffset("84") > tlv.childTagOffset("A5"))
        {
            /* tag '84' should be placed before tag 'A5', return to uing AID list */
            return QCORE_SELPPSEFAILED;
        }

        //check tag A5
        tlv.setTlv(tag.value);
        if (!tlv.validTlv())
        {
            return QCORE_DECODE;
        }

        if ((tag = tlv.childTag("BF0C")) == null || tag.value == null)
        {
            return QCORE_NOBF0C; /* Cannot find FCI Proprietary Template , return to uing AID list */
        }

        mBuf.setTagValue("BF0C",tag.value);

        //check tag BF0C
        tlv.setTlv(tag.value);
        if (!tlv.validTlv())
        {
            return QCORE_DECODE;
        }

        //Get the first TAG61 location first
        if ((tag = tlv.childTag("61")) == null || tag.value == null)
        {
            return QCORE_NO61;
        }

        tlv_61 = new EMVTlv(null);

        while(true)
        {
            //Determine if the TAG of the current location is TAG61. If not, it means there is no TAG61.
            if (tlv.next() == null || !tlv.next().tag.equals("61"))
            {
                break;
            }

            tlv_61.setTlv(tag.value);
            if (!tlv.validTlv())
            {
                return QCORE_DECODE;
            }

            //Look for TAG4F under TAG61. If not found, the current TAG61+ sub-tab of the current TAG61
            //The number of +1, the next TAG61 position, the subsequent TAG50, TAG87 lookup processing
            if ((tag = tlv_61.childTag("4F")) == null || tag.value == null)
            {
                iRet = QCORE_NO4F;
                //Point to the next TAG61
                tlv.hasNext();
                continue;
            }

            if(!mParam.IsSupportAid(tag.value))
            {
                //指向下一个TAG61
                tlv.hasNext();
                continue;
            }

            candidate._aid = tag.value;

            if ((tag = tlv_61.childTag("50")) == null || tag.value == null)
            {
                iRet = QCORE_NO50;
                //Point to the next TAG61
                tlv.hasNext();
                continue;
            }

            candidate._lable = tag.value;

            if ((tag = tlv_61.childTag("87")) == null || tag.value == null)
            {
                iRet = QCORE_NO87;
                //Point to the next TAG61
                tlv.hasNext();
                continue;
            }

            candidate._priority = tag.value[0];
            candidate._isSelected = false;

            //Point to the next TAG61
            tlv.hasNext();

            mCandidateList.add(candidate);

        }

        LogUtil.i(TAG,"The number of candidate AIDs is [" + mCandidateList.size() + "]");
        if(mCandidateList.size() == 0)
        {
            return QCORE_NOAID;
        }

        //Start bubble sorting
        for (int i = 0; i < mCandidateList.size() - 1; i++)
        {
            for (int j = i + 1 ; j < mCandidateList.size() ; j++)
            {
                if ((mCandidateList.get(i)._priority & 0x0F) > (mCandidateList.get(j)._priority & 0x0F))
                {
                    // Interchange j >> i
                    candidate = mCandidateList.get(i);
                    mCandidateList.set(i,mCandidateList.get(j));
                    mCandidateList.set(j,candidate);
                }
            }
        }

        return QCORE_SUCESS;
    }

    @Override
    public void onProcess()
    {
        int iRet = process();
        LogUtil.i(TAG, "------------------ QSelectPPSE onProcess [ " + iRet + " ] -------------------");
        if(iRet == QCORE_SUCESS)
        {
            mOption.getProcessSwitch().onNextProcess(QCORE_SUCESS);
        }else{
            mOption.getQCallback().onTermination(iRet);
        }
    }
}
