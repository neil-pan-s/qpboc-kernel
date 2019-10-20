package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by ChangingP on 16/6/17.
 */
public class EMVDol {

    private static final String TAG = "EMVDol";

    private EMVBuf mEmvBuf = null;
    private byte[] mDol = null;

    private HashMap<String,Integer> mDolMap = new HashMap<>();

    public EMVDol(EMVBuf emvBuf)
    {
        mEmvBuf = emvBuf;
    }

    public void setDol(byte[] dol)
    {
        mDol = dol;
        mDolMap.clear();
    }

    public boolean findTag(String tag)
    {
        return mDolMap.containsKey(tag);
    }

    /**
     * Call this after validDol()
     *
     */
    public void praseDol()
    {
        String  tag = "";
        int     iLen = 0 , iOffset = 0;

        for (iOffset = 0;iOffset < mDol.length;)
        {
            tag = (EMVTag.isOneByteTagName(mDol[iOffset])) ? String.format("%02X", mDol[iOffset]) : String.format("%02X%02X", mDol[iOffset], mDol[++iOffset]);
            //offset to next byte
            iOffset++;

            // emv/pboc only use one or two byte len
            if(!EMVTag.isOneByteTagLen(mDol[iOffset]))
            {
                //offset to next byte
                iOffset++;
            }

            iLen = mDol[iOffset] & 0xFF;

            LogUtil.i(TAG,"Find DOL: [" + tag + "] - [" + iLen + "], Offset: " + iOffset);
            mDolMap.put(tag,iLen);

            if(iOffset == mDol.length - 1)
            {
                LogUtil.i(TAG,"It's End, Offset: " + iOffset);
                return;
            }

            //offset to next byte
            iOffset++;
        }
    }


    public boolean validDol()
    {
        if(validDol(mDol))
        {
            praseDol();
            return true;
        }

        return false;
    }

    public static boolean validDol(byte[] dol)
    {
        String  tag = "";
        int     iLen = 0 , iOffset = 0;

        for (iOffset = 0;iOffset < dol.length;)
        {
            //one byte name + one byte len or two byte name, so must have one byte after
            if(iOffset + 1 > dol.length - 1)
            {
                LogUtil.w(TAG,"Unexpected end of value field, DOL: " + HexUtil.ByteArrayToHexString(dol) + ", Offset: " + iOffset);
                return false;
            }

            tag = (EMVTag.isOneByteTagName(dol[iOffset])) ? String.format("%02X", dol[iOffset]) : String.format("%02X%02X", dol[iOffset], dol[++iOffset]);

            //offset to next byte
            iOffset++;
            if(iOffset > dol.length - 1)
            {
                LogUtil.w(TAG,"Unexpected end of value field, DOL: " + HexUtil.ByteArrayToHexString(dol) + ", Offset: " + iOffset);
                return false;
            }

            // emv/pboc only use one or two byte len
            if(!EMVTag.isOneByteTagLen(dol[iOffset]))
            {
                //offset to next byte
                iOffset++;
                if(iOffset > dol.length - 1)
                {
                    LogUtil.w(TAG,"Unexpected end of value field, DOL: " + HexUtil.ByteArrayToHexString(dol) + ", Offset: " + iOffset);
                    return false;
                }
            }

            iLen = dol[iOffset] & 0xFF;

            LogUtil.i(TAG,"Find DOL: [" + tag + "] - [" + iLen + "], Offset: " + iOffset);

            if(iOffset == dol.length - 1)
            {
                LogUtil.i(TAG,"It's End, Offset: " + iOffset);
                return true;
            }

            //offset to next byte
            iOffset++;
            if(iOffset > dol.length - 1)
            {
                LogUtil.w(TAG,"Unexpected end of value field, DOL: " + HexUtil.ByteArrayToHexString(dol) + ", Offset: " + iOffset);
                return false;
            }


        }

        return iOffset == dol.length;
    }


    public byte[] sealDol()
    {
        return sealDol(mDol);
    }

    public byte[] sealDol(byte[] dol)
    {
        String tag = "";
        int iLen = 0,iValueLen = 0;
        byte[] dolValue = null,tmp = null;
        EMVTag eTag = null;

        if(!validDol())
        {
            LogUtil.w(TAG,"validDol fail");
            return null;
        }

        for (int iOffset = 0;iOffset < dol.length;) {
            tag = (EMVTag.isOneByteTagName(dol[iOffset])) ? String.format("%02X", dol[iOffset]) : String.format("%02X%02X", dol[iOffset], dol[++iOffset]);
            iOffset++;
            iLen = (EMVTag.isOneByteTagLen(dol[iOffset])) ? dol[iOffset] & 0xFF : dol[++iOffset] & 0xFF;
            iOffset++;

            iValueLen += iLen;
        }

        dolValue = new byte[iValueLen];

        iValueLen = 0;
        for (int iOffset = 0;iOffset < dol.length;) {
            tag = (EMVTag.isOneByteTagName(dol[iOffset])) ? String.format("%02X", dol[iOffset]) : String.format("%02X%02X", dol[iOffset], dol[++iOffset]);
            iOffset++;
            iLen = (EMVTag.isOneByteTagLen(dol[iOffset])) ? dol[iOffset] & 0xFF : dol[++iOffset] & 0xFF;
            iOffset++;

            tmp = new byte[iLen];

            eTag = mEmvBuf.findTag(tag);
            if(eTag == null || eTag.getTagValue() == null)
            {
                LogUtil.w(TAG,"Unexpected Tag: " + tag);
                return null;
            }

            if(iLen > eTag.getTagValue().length) {

                if (EMVTag.isFormatNTag(tag))
                {
                    //right align , left padded 0x00
                    System.arraycopy(eTag.getTagValue(), 0, tmp, iLen - eTag.getTagValue().length,  eTag.getTagValue().length);

                }else if(EMVTag.isFormatCNTag(tag)){

                    // left align, right padded 0xFF
                    Arrays.fill(tmp,(byte)0xFF);
                    System.arraycopy(eTag.getTagValue(),0,tmp,0,eTag.getTagValue().length);

                }else{

                    //left align ,right padded 0x00
                    System.arraycopy(eTag.getTagValue(),0,tmp,0,eTag.getTagValue().length);
                }

            }
            else if(iLen < eTag.getTagValue().length)
            {
                if (EMVTag.isFormatNTag(tag))
                {
                    // use right iLen part
                    System.arraycopy(eTag.getTagValue(),eTag.getTagValue().length - iLen,tmp,0,iLen);

                }else{
                    System.arraycopy(eTag.getTagValue(),0,tmp,0,iLen);
                }
            }else{
                System.arraycopy(eTag.getTagValue(),0,tmp,0,iLen);
            }

            System.arraycopy(tmp,0,dolValue,iValueLen,iLen);

            iValueLen += iLen;
        }

        return dolValue;
    }
}
