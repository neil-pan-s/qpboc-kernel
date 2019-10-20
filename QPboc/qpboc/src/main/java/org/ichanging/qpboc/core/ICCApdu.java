package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

/**
 * Created by ChangingP on 16/6/20.
 */
public class ICCApdu
{
    public byte _cla;
    public byte _ins;
    public byte _p1;
    public byte _p2;
    public byte _lc;
    public byte _le;

    public byte[] _data = null;


    /**
     * mask only use low 6 bit(bit1 ~ bit7)
     *
     * bit1 - _cla always exist
     * bit2 - _ins always exist
     * bit3 - _p1  always exist
     * bit4 - _p2  always exist
     * bit5 - _lc
     * bit6 - _data
     * bit7 - _le
     *
     * max in binary: 0x01111111
     */

    public byte _mask = 0x7F;

    public ICCApdu( byte cla , byte ins , byte par1 , byte par2 , byte lc , byte le)
    {
        _cla = cla;
        _ins = ins;
        _p1 = par1;
        _p2 = par2;
        _lc = lc;
        _le = le;
    }

    public ICCApdu( byte[] apdu)
    {
        _cla = apdu[0];
        _ins = apdu[1];
        _p1 = apdu[2];
        _p2 = apdu[3];
        _lc = apdu[4];
        _le = apdu[5];
    }

    public void setData(byte[] data)
    {
        _data = data;
        _lc = (byte)data.length;
    }

    public byte[] build()
    {
        int len = 0;
        byte[] cmd;

        if(_mask == 0x4F)
        {
            //no _lc and _data
            cmd = new byte[ 5 ];
        }else if(_mask == 0x3F){
            //no _le
            cmd = new byte[ _lc + 5 ];
        }else{
            cmd = new byte[ _lc + 6 ];
        }

        cmd[0] = _cla;
        cmd[1] = _ins;
        cmd[2] = _p1;
        cmd[3] = _p2;
        len += 4;

        //exist lc and data field
        if((_mask & 0x10) == 0x10 && (_mask & 0x20) == 0x20 )
        {
            cmd[4] = _lc;
            len ++;

            System.arraycopy( _data , 0 , cmd , len , _lc);
            len += _lc;

        }

        if((_mask & 0x40) == 0x40 )
        {
            cmd[len] = _le;
            len ++;
        }

        return cmd;
    }
}
