package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by ChangingP on 16/6/12.
 */
public class EMVTlv implements Iterator<TLVTag> {

    private static final String TAG = "EMVTlv";
    private byte[] mTlv = null;
    private int mOffset = 0;
    private TLVTag mTag = null;

    public EMVTlv(byte[] tlv)
    {
        mTlv = tlv;
        mOffset = 0;
        mTag = null;
    }


    public void setTlv(byte[] tlv)
    {
        mTlv = tlv;
        mOffset = 0;
        mTag = null;
    }

    /**
     * EMVTlv to valid TLV Data Format
     *
     */
    public boolean validTlv()
    {
        return validTlv(mTlv);
    }

    /**
     * EMVTlv to valid TLV Data Format
     *
     * @param tlv
     * @return
     *
     */
    public static boolean validTlv(byte[] tlv)
    {
        String  tag = null;
        int     iLen = 0 , iOffset = 0;
        byte[]  value = null;
        boolean isConstructed = false;

        for (iOffset = 0;iOffset < tlv.length;)
        {
            //LogUtil.i(TAG,"Offset: " + iOffset + " tlv.length " + tlv.length);

            isConstructed = EMVTag.isConstructed(tlv[iOffset]);

            //one byte name + one byte len or two byte name, so must have one byte after
            if(iOffset + 1 > tlv.length - 1)
            {
                LogUtil.w(TAG,"Unexpected end of value field, TLV: " + HexUtil.ByteArrayToHexString(tlv) + ", Offset: " + iOffset);
                return false;
            }

            tag = (EMVTag.isOneByteTagName(tlv[iOffset])) ? String.format("%02X",tlv[iOffset]) : String.format("%02X%02X",tlv[iOffset],tlv[++iOffset]);
            //LogUtil.i(TAG,"Find Tag Name: " + tag + ", Offset: " + iOffset);

            //offset to next byte
            iOffset++;
            if(iOffset > tlv.length - 1)
            {
                LogUtil.w(TAG,"Unexpected end of value field, TLV: " + HexUtil.ByteArrayToHexString(tlv) + ", Offset: " + iOffset);
                return false;
            }


            // emv/pboc only use one or two byte len
            if(!EMVTag.isOneByteTagLen(tlv[iOffset]))
            {
                //offset to next byte
                iOffset++;
                if(iOffset > tlv.length - 1)
                {
                    LogUtil.w(TAG,"Unexpected end of value field, TLV: " + HexUtil.ByteArrayToHexString(tlv) + ", Offset: " + iOffset);
                    return false;
                }
            }

            //-_-!!!
            iLen = tlv[iOffset] & 0xFF;
            //LogUtil.i(TAG,"Find Len: " + iLen + ", Offset: " + iOffset);

            if(iLen == 0)
            {

                LogUtil.i(TAG,"Find TLV:[" + tag + "] - [" + iLen + "] - Offset: " + iOffset);

                //eg. 90 00
                if(iOffset == tlv.length - 1)
                {
                    return true;
                }

                //offset to next byte
                iOffset++;
                if(iOffset > tlv.length - 1)
                {
                    LogUtil.w(TAG,"Unexpected end of value field, TLV: " + HexUtil.ByteArrayToHexString(tlv) + ", Offset: " + iOffset);
                    return false;
                }

                continue;
            }


            if(iLen > 0)
            {
                if (iOffset + iLen > tlv.length - 1)
                {
                    // emv/pboc case may have this situation
                    // bank ic card won't happen

                    LogUtil.w(TAG,"Unexpected end of value field, TLV: " + HexUtil.ByteArrayToHexString(tlv) + ", Offset: " + iOffset);
                    return false;
                }

                //offset to next byte
                iOffset++;

                value = new byte[iLen];
                System.arraycopy(tlv, iOffset,value, 0,iLen);
                iOffset += iLen;

                //LogUtil.i(TAG,"Find Value: " + HexUtil.ByteArrayToHexString(value) + ", Offset: " + iOffset);

                //Construct tag need to prase child tlv
                if (isConstructed) {
                    //child tlv valid fail
                    if (!validTlv(value))
                    {
                        LogUtil.i(TAG,"validTlv : " + false + ", Offset: " + iOffset);
                        return false;
                    }
                }

                LogUtil.i(TAG,"Find TLV:[" + tag + "] - [" + iLen + "] - [" + HexUtil.ByteArrayToHexString(value) + "] - Offset: " + iOffset);
            }
        }

        return (iOffset == tlv.length ) ? true : false;
    }

    private boolean isOverFlow()
    {
        return  mOffset > mTlv.length - 1;
    }

    private boolean isEnd()
    {
        return  mOffset == mTlv.length - 1;
    }


    /**
     * Search in desc
     * Constructed Tag won't be search
     *
     * @return
     */
    @Override
    public boolean hasNext()
    {
        String  tag = null;
        int     iLen = 0;
        byte[]  value = null;
        boolean isConstructed = false;

        mTag = null;

        if(mOffset == mTlv.length)
        {
            LogUtil.i(TAG,"It's end - Offset: " + mOffset);
            return false;
        }

        //one byte name + one byte len or two byte name, so must have one byte after
        if(mOffset + 1 > mTlv.length - 1)
        {
            LogUtil.w(TAG,"Unexpected end of value field, TLV: " + HexUtil.ByteArrayToHexString(mTlv) + ", Offset: " + mOffset);
            return false;
        }

        isConstructed = EMVTag.isConstructed(mTlv[mOffset]);

        tag = (EMVTag.isOneByteTagName(mTlv[mOffset])) ? String.format("%02X",mTlv[mOffset]) : String.format("%02X%02X",mTlv[mOffset],mTlv[++mOffset]);

        //offset to next byte
        mOffset++;
        if(isOverFlow())
        {
            LogUtil.w(TAG,"Unexpected end of value field, TLV: " + HexUtil.ByteArrayToHexString(mTlv) + ", Offset: " + mOffset);
            return false;
        }

        if(!EMVTag.isOneByteTagLen(mTlv[mOffset]))
        {
            //offset to next byte
            mOffset++;
            if(isOverFlow())
            {
                LogUtil.w(TAG,"Unexpected end of value field, TLV: " + HexUtil.ByteArrayToHexString(mTlv) + ", Offset: " + mOffset);
                return false;
            }
        }

        iLen = mTlv[mOffset] & 0xFF;


        if(iLen == 0)
        {
            LogUtil.i(TAG,"Find TLV:[" + tag + "] - [" + iLen + "] - Offset: " + mOffset);

            //offset to next byte
            mOffset++;
        }


        if(iLen > 0)
        {
            if (mOffset + iLen > mTlv.length - 1)
            {
                // emv/pboc case may have this situation
                // bank ic card won't happen

                LogUtil.w(TAG,"Unexpected end of value field, TLV: " + HexUtil.ByteArrayToHexString(mTlv) + ", Offset: " + mOffset);
                return false;
            }

            //offset to next byte
            mOffset++;

            value = new byte[iLen];
            System.arraycopy(mTlv, mOffset,value, 0,iLen);
            mOffset += iLen;

            LogUtil.i(TAG,"Find TLV:[" + tag + "] - [" + iLen + "] - [" + HexUtil.ByteArrayToHexString(value) + "] - Offset: " + mOffset);
        }

        mTag = new TLVTag(tag,value);

        return true;
    }

    @Override
    public TLVTag next() {
        return mTag;
    }

    @Override
    public void remove() {
        //unsupport this action
    }

    public boolean isExistTag(String tag)
    {
        mOffset = 0;

        while (hasNext())
        {
            if(next().tag.equals(tag)) return true;
        }

        return false;
    }

    public TLVTag childTag(String tag)
    {
        mOffset = 0;

        while (hasNext())
        {
            if( next().tag.equals(tag)) return mTag;
        }

        return null;
    }

    /**
     * EMVTlv to parse TLV Data Format
     *
     * @param tag
     * @return
     *          = -1 fail
     *          > 0  succ,return child offset
     */
    public int childTagOffset(String tag)
    {
        int index = 0;

        mOffset = 0;

        while (hasNext())
        {
            index++;

            if(next().tag.equals(tag)) return index;
        }

        return -1;
    }


    /**
     * EMVTlv to parse TLV Data Format
     *
     * @param tag
     * @return
     *
     */
    public byte[] childTagValue(String tag)
    {
        TLVTag tlvTag = null;

        mOffset = 0;

        while (hasNext())
        {
            tlvTag = next();

            if(tlvTag.tag.equals(tag)) return tlvTag.value;
        }

        return null;
    }

}
