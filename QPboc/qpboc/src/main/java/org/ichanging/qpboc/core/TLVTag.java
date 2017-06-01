package org.ichanging.qpboc.core;

/**
 * Created by ChangingP on 16/6/17.
 */
public class TLVTag
{
    public String tag;
    public byte[] value;

    public TLVTag(String tag,byte[] value)
    {
        this.tag = tag;
        this.value = value;
    }

}
