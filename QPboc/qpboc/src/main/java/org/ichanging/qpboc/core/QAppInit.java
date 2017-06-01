package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;

/**
 * Created by ChangingP on 16/6/20.
 */
public class QAppInit extends QCore implements ProcessAble{

    private static final String TAG = "QAppInit";

    public QAppInit(QOption option) {
        super(option);
    }

    private int validGPOData()
    {
        //检查GPO返回的必备标签
        String[] tagTable = {"82", "9F36", "57", "9F26", "9F10"};

        for (int i = 0; i < tagTable.length; i++) {
            if (mBuf.findTag(tagTable[i]) == null) {
                if (mParam.capa_options(EMVParam.CAPA_Support_Contact_Pboc)) {
                    mAdapter._ShowMessage(mAdapter._I18NString("STR_TERMINATION"), 500);
                    return QCORE_TRYOTHEAR;
                }
                return QCORE_TERMINATION;
            }
        }

        return QCORE_SUCESS;
    }

    public int process() {

        int iRet = QCORE_SUCESS,iLen = 0;
        byte[] dolfmt = null,dolbuf = null,buf = null;

        int iSfi = 0, iFrec = 0, iLrec = 0, iNrec = 0;

        byte[] byte01 = { 0x01 };
        byte[] byte00 = { 0x00 };

        ICCResponse rsp = new ICCResponse();
        EMVDol dol = new EMVDol(mBuf);
        EMVTlv tlv = new EMVTlv(null);

        LogUtil.i(TAG, "------------------ QAppInit Start -------------------");

        if((dolfmt = mBuf.getTagValue("9F38")) == null)
        {
            if(mParam.capa_options(EMVParam.CAPA_Support_Contact_Pboc))
            {
                return QCORE_TRYOTHEAR;
            }
            return QCORE_NO9F38;
        }

        dol.setDol(dolfmt);
        if(!dol.validDol())
        {
            return QCORE_DOLPACK;
        }

        if(!dol.findTag("9F66"))
        {
            if(mParam.capa_options(EMVParam.CAPA_Support_Contact_Pboc))
            {
                return QCORE_TRYOTHEAR;
            }
            return QCORE_DOLFMTNO9F66;
        }

        if(dol.findTag("DF69"))
        {
            if(mParam.capa_options(EMVParam.CAPA_Support_SM))
            {
                mBuf.setTagValue(0xDF69, byte01);
            }
            else
            {
                mBuf.setTagValue(0xDF69, byte00);
            }
        }

        if((buf = dol.sealDol()) == null)
        {
            return QCORE_DOLPACK;
        }

        if(buf.length >= 128)
        {
            dolbuf = new byte[buf.length + 3];

            dolbuf[0] = (byte)0x83;
            dolbuf[1] = (byte)0x81;
            dolbuf[2] = (byte)buf.length;

            iLen = 3;
        }else{
            dolbuf = new byte[buf.length + 2];

            dolbuf[0] = (byte)0x83;
            dolbuf[1] = (byte)buf.length;

            iLen = 2;
        }

        System.arraycopy(buf,0,dolbuf,iLen,buf.length);

        iRet = mICC.GetProcessOptions(dolbuf,rsp);
        if(iRet != 0x9000)
        {
            if (iRet == 0x6985)
            {
                return QCORE_INITAPP6985;
            }
            else
            if (iRet == 0x6984)
            {
                if(mParam.capa_options(EMVParam.CAPA_Support_Contact_Pboc))
                {
                    return QCORE_TRYOTHEAR;
                }
                return QCORE_INITAPP6984;
            }
            return QCORE_TERMINATION;
        }

        tlv.setTlv(rsp.data);
        /* decode the response to fetch AIP and AFL */
        if (!tlv.validTlv())
        {
            return QCORE_DECODE;
        }

        LogUtil.i(TAG, "QAppInit- GPO Decode Succ");

        tlv.hasNext();
        TLVTag tlvTag = tlv.next();

        if (tlvTag.tag.equals("80"))
        {
            if (tlvTag.value.length % 4 != 2 || tlvTag.value.length == 2)
            {
            /* the length should be 2 bytes AIP + 4N bytes AFL,
                        An AFL with no entries is not allowed (see Book3 7.5)*/
                return QCORE_80VALUELEN;
            }

            buf = new byte[2];
            buf[0] = tlvTag.value[0];
            buf[1] = tlvTag.value[1];

            mBuf.setTagValue("82",buf);

            buf = new byte[tlvTag.value.length - 2];
            System.arraycopy(tlvTag.value,2,buf,0,tlvTag.value.length - 2);
            mBuf.setTagValue("94",buf);
        }
        if (tlvTag.tag.equals("77"))
        {

            tlv.setTlv(tlvTag.value);

            if ((tlvTag = tlv.childTag("82")) == null || tlvTag.value == null)
            {
                //return QCORE_77NOAIP;
                return QCORE_TRYOTHEAR;
            }

            if (tlvTag.value.length != 2)
            {
                return QCORE_AIPLEN;
            }

            if ((tlvTag = tlv.childTag("94")) != null && tlvTag.value != null)
            {
                if (tlvTag.value.length % 4 != 0 || tlvTag.value.length == 0)
                {
                    return QCORE_AFLLEN;
                }

                for(int i = 0; i <  tlvTag.value.length;)
                {
                    iSfi = ((tlvTag.value[i++]>>3)&0x1F);       //字节1：位8–4=SFI短文件标识符
                    iFrec = tlvTag.value[i++];                  //字节2：文件中要读的第 1 个记录的记录号（不能为0）
                    iLrec = tlvTag.value[i++];                  //字节3：文件中要读的最后一个记录的记录号 （等于或大于字节2）
                    iNrec = tlvTag.value[i++];                  //字节4：从字节 2中的记录号开始，存放认证用静态数据记录的个数（值从 0到字节3-字节2+1的值）

                    if (iSfi < 1 || iSfi >= 0x1F || iFrec == 0 || (iLrec < iFrec) || (iNrec > iLrec - iFrec + 1))
                    {
                        return QCORE_TERMINATION;
                    }
                }
            }
            tlv.setTlv(rsp.data);

            //CLQA01300 不能替换9F02,  但交易得成功
            mBuf.findTag("9F02").setUnique(true);
            if(!mBuf.tlv2buf(tlv))
            {
                return QCORE_DECODE;
            }
        }
        else
        {
        /* if not 80 or 77 encoded */
            return QCORE_UNEXPECTTAG;
        }

        byte[] utag82 = mBuf.getTagValue("82");
        if(utag82 == null)
        {
            return QCORE_TRYOTHEAR;
        }

        LogUtil.i(TAG, "QAppInit - tag82:" + String.format("[%02X%02X]",utag82[0], utag82[1]));

        if((iRet = validGPOData()) != QCORE_SUCESS)
        {
            return iRet;
        }

        /*//终端不会支持MSD,  故不作MSD的判断
        if((utag82[1] & 0x80) == 0x80)
        {
            return QCORE_CHOOSEMSD;
        }*/

        if(mParam.capa_options(EMVParam.CAPA_Support_QPBOC) && !mParam.capa_options(EMVParam.CAPA_Support_Contact_Pboc))
        {
            return QCORE_QPBOC;
        }

        if(mParam.capa_options(EMVParam.CAPA_Support_QPBOC) && mParam.capa_options(EMVParam.CAPA_Support_Contact_Pboc))
        {
            if((utag82[1] & 0x80) == 0x00)
            {
                if (mBuf.findTag("9F26") == null)
                {
                    return QCORE_TRYOTHEAR;
                }
                else
                {
                    return QCORE_QPBOC;
                }
            }
            else
            {
                return QCORE_QPBOC;
            }
        }

        return QCORE_SUCESS;

    }

    @Override
    public void onProcess()
    {
        int iRet = process();
        LogUtil.i(TAG, "------------------ AppInit onProcess [ " + iRet + " ] -------------------");
        if(iRet < 0)
        {
            if(QCORE_INITAPP6985 == iRet)
            {
                // 重新选择AID
                mOption.getProcessSwitch().onNextProcess(QCORE_INITAPP6985);
                return;
            }
            if(QCORE_SEL6283 != iRet)
            {
                mICC.powerOff();
                mOption.getQCallback().onTermination(iRet);
                return;
            }
        }else{

            //根据卡的返回值判断使用的界面
            if(iRet == QCORE_PBOC)
            {
                mICC.powerOff();
                //如果终端支持PBOC流程
                if(mParam.capa_options(EMVParam.CAPA_Support_Contact_Pboc))
                {
                    mAdapter._ShowMessage(mAdapter._I18NString("STR_TERMINATION"), 500);
                    mOption.getQCallback().onTermination(QCORE_PBOC);
                    return;
                }
                else
                {

                    mOption.getQCallback().onTermination(QCORE_TERMINATION);
                    return;
                }
            }
            else if(iRet == QCORE_TRYOTHEAR)
            {
                mAdapter._ShowMessage(mAdapter._I18NString("STR_TERMINATION"), 500);
                mOption.getQCallback().onTermination(QCORE_TRYOTHEAR);
                return;
            }
            else if(iRet != QCORE_QPBOC && iRet != QCORE_SUCESS)
            {
                mICC.powerOff();
                mOption.getQCallback().onTermination(QCORE_TERMINATION);
                return;
            }

            // QCORE_SUCESS
            mOption.getProcessSwitch().onNextProcess(QCORE_SUCESS);
        }
    }


}
