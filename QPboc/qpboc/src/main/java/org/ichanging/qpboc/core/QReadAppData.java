package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ChangingP on 16/6/21.
 */
public class QReadAppData extends QCore implements ProcessAble{

    private static final String TAG = "QReadAppData";
    private byte[] mAuthData = null;

    public QReadAppData(QOption option) {
        super(option);
    }

    public byte[] getAuthData()
    {
        return mAuthData;
    }

    /**
     *
     * @param date must be 3 byte eg.160621
     * @return
     */
    private boolean isBeforeCurrDate(byte[] date)
    {
        byte[] pdate = new byte[4];

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        byte[] curr = HexUtil.HexStringToByteArray(df.format(new Date()));

        LogUtil.i(TAG,"isBeforeCurrDate - CurrDate - " + HexUtil.ByteArrayToHexString(curr) + " - TestDate - " + HexUtil.ByteArrayToHexString(date));

        pdate[0] = (byte)((date[0] >= 0x50) ? 0x19 : 0x20);
        pdate[1] = date[0];
        pdate[2] = date[1];
        pdate[3] = date[2];

        for (int i = 0 ; i < 4 ; i++ )
        {
            if(pdate[i] < curr[i]) return true;
        }

        return false;
    }

    public int process()
    {
        int iRet = QCORE_SUCESS;
        byte[] tag94 = null;
        byte[] uAuthData = new byte[1024];
        int iSfi = 0, iFrec = 0, iLrec = 0, iNrec = 0;
        int i = 0,iAuthLen = 0,iErrSDAdata = 0;

        TLVTag tag = null;

        ICCResponse rsp = new ICCResponse();
        EMVTlv tlv = new EMVTlv(null);

        tag94 = mBuf.getTagValue("94");
        if(tag94 == null)
        {
            return QCORE_NO94;
        }

        while(i < tag94.length)
        {
            iSfi = ((tag94[i++]>>3)&0x1F);      //Byte 1: Bit 8–4 = SFI short file identifier
            iFrec = tag94[i++];                 //字节2：文件中要读的第 1 个记录的记录号（不能为0）
            iLrec = tag94[i++];                 //字节3：文件中要读的最后一个记录的记录号 （等于或大于字节2）
            iNrec = tag94[i++];                 //字节4：从字节 2中的记录号开始，存放认证用静态数据记录的个数（值从 0到字节3-字节2+1的值）

            if (iSfi < 1 || iSfi >= 0x1F)
            {
                return QCORE_READREC_SFIERR;
            }
            if (iFrec == 0)
            {
                return QCORE_READREC_FZERO;
            }
            if ((iLrec < iFrec) || (iNrec > iLrec - iFrec + 1))
            {
                return QCORE_READREC_RECRANGEERR;
            }

            while (iFrec <= iLrec)
            {
                iRet = mICC.ReadRecord(iSfi, iFrec, rsp);
                if(iRet != 0x9000)
                {
                    return QCORE_READCMDERR;
                }

                tlv.setTlv(rsp.data);
                if (!tlv.validTlv())
                {
                    return QCORE_DECODE;
                }

                tlv.hasNext();
                tag = tlv.next();
                if (tag.tag.equals("70"))
                {
                    if(iNrec > 0)
                    {
                        if(iSfi < 11)
                        {
                            System.arraycopy(tag.value,0,uAuthData,iAuthLen,tag.value.length);
                            iAuthLen += tag.value.length;
                            iNrec--;
                        }
                        else
                        {
                            System.arraycopy(rsp.data,0,uAuthData,iAuthLen,rsp.data.length);
                            iAuthLen += rsp.data.length;
                            iNrec--;
                        }
                    }

                    tlv.setTlv(tag.value);

                    while ( tlv.hasNext() )
                    {
                        tag = tlv.next();

                        //Save label
                        if( (tag.tag.equals("5A") || tag.tag.equals("57") || tag.tag.equals("5F24")) && mBuf.getTagValue(tag.tag) != null)
                        {
                            return QCORE_SAVEAPPDATA;
                        }

                        if(!tag.tag.equals("5F2A") && !tag.tag.equals("9F02") && !tag.tag.equals("9F37"))
                        {
                            if(mBuf.findTag(tag.tag) == null)
                            {
                                return QCORE_SAVEAPPDATA;
                            }

                            mBuf.setTagValue(tag.tag,tag.value);
                        }

                        if(tag.tag.equals("5F24"))
                        {
                            //失效日期
                            if(tag.value[0] >= 0x50 || isBeforeCurrDate(tag.value))
                            {
                                return QCORE_LOSTEFFICACY;
                            }
                        }

                        if(tag.tag.equals("5F25"))
                        {
                            //生效日期
                            if(!isBeforeCurrDate(tag.value))
                            {
                                return QCORE_NOTTOUSE;
                            }
                        }
                    }
                }
                else
                {
                    if (tag.tag.equals("74"))
                    {
                        return QCORE_TERMINATION;
                    }

                    if(iNrec > 0)
                    {
                        System.arraycopy(rsp.data,0,uAuthData,iAuthLen,rsp.data.length);
                        iAuthLen += rsp.data.length;
                        iNrec--;
                        iErrSDAdata = 1;
                    }
                }
                //读取下一个记录
                iFrec ++;
            }
        }


        if(iAuthLen > 0)
        {
            mAuthData = new byte[iAuthLen];
            System.arraycopy(uAuthData, 0,mAuthData,0,iAuthLen);
        }

        if(iErrSDAdata != 0)
        {
            return QCORE_ERRSDADATA;
        }
        return QCORE_SUCESS;

    }

    @Override
    public void onProcess()
    {
        int iRet = process();
        LogUtil.i(TAG, "------------------ QReadAppData onProcess [ " + iRet + " ] -------------------");
        //mICC.powerOff();
        if(iRet < 0)
        {
            if(iRet == QCORE_LOSTEFFICACY)
            {
                mAdapter._ShowMessage(mAdapter._I18NString("STR_APP_LOSE_EFFICACY"), 500);
                mOption.getQCallback().onDecline();
                return;
            }
            else if(iRet == QCORE_ERRSDADATA)
            {
                mOption.getQCallback().onDecline();
                return;
            }
            else
            {
                mOption.getQCallback().onTermination(QCORE_TERMINATION);
                return;
            }
        }

        mAdapter._ShowMessage(mAdapter._I18NString("STR_REMOVECARD"), 500);

        if(mBuf.getTagValue("82") == null)
        {
            mOption.getQCallback().onTermination(QCORE_TERMINATION);
            return;
        }

        mOption.getProcessSwitch().onNextProcess(QCORE_SUCESS);
    }

}
