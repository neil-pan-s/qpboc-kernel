package org.ichanging.qpboc.core;

/**
 * Created by ChangingP on 16/6/16.
 */
public class EMVCandidate
{
    public byte[]   _aid;
    public byte[]   _lable;               /* 50(ICC), ans, 1-16 bytes*/
    public byte[]   _preferred_name;      /* 9F12(ICC), ans, 1-16 bytes */
    public byte     _priority;            /* 87(ICC), b, 1 bytes */
    public boolean  _isSelected;          /* indicate whether the candidate is selected */
}