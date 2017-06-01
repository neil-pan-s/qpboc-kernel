package org.ichanging.qpboc.platform;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import org.ichanging.qpboc.core.ICCApdu;
import org.ichanging.qpboc.core.ICCInterface;
import org.ichanging.qpboc.core.ICCResponse;
import org.ichanging.qpboc.util.HexUtil;

import java.io.IOException;

/**
 * Created by ChangingP on 16/6/8.
 */
public class IsoDepAdapter implements ICCInterface {

    private static final String TAG = "IsoDepAdapter";

    private static final int TIMEOUT = 3600;

    private static IsoDep mIsoDep = null;


    public IsoDepAdapter()
    {


    }

    /**
     * Callback when a new tag is discovered by the system.
     *
     * <p>Communication with the card should take place here.
     *
     * @param tag Discovered tag
     */
    public void onTagDiscovered(Tag tag){

        LogUtil.i(TAG, "======= Discovered Tag:" + tag + "==========");

        // Android's Host-based Card Emulation (HCE) feature implements the ISO-DEP (ISO 14443-4)
        // protocol.
        //
        // In order to communicate with a device using HCE, the discovered tag should be processed
        // using the IsoDep class.

        mIsoDep = IsoDep.get(tag);

        // Connect to the remote NFC device
        try {
            mIsoDep.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mIsoDep.setTimeout(TIMEOUT);

    }

    @Override
    public int _PowerOn()
    {
        return (mIsoDep != null) ? 0 : -1;
    }

    @Override
    public int _PowerOff()
    {
        try {
            if(mIsoDep != null) {
                mIsoDep.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }


    /**
     * Apdu Communication
     *
     * Communication with the card should take place here.
     *
     * @param apdu  Apdu Command to Card
     * @param rsp  Card response
     * @return
     *         >0  sw eg. 0x9000
     *         <=  error code
     */
    @Override
    public int _ApduComm(ICCApdu apdu , ICCResponse rsp)
    {
        if (mIsoDep != null)
        {
            try
            {

                LogUtil.i(TAG, "ApduSend: [" + HexUtil.ByteArrayToHexString(apdu.build()) + "]");

                byte[] buf = mIsoDep.transceive(apdu.build());

                // If AID is successfully selected, 0x9000 is returned as the status word (last 2
                // bytes of the result) by convention. Everything before the status word is
                // optional payload, which is used here to hold the account number.
                int resultLength = buf.length;
                int sw = 0x0;

                if(resultLength >= 2)
                {
                    sw |= buf[resultLength - 2];
                    sw <<= 8;
                    sw |= buf[resultLength - 1] ;
                    sw &= 0xFFFF;
                }

                LogUtil.i(TAG, "ApduRecv: [" + HexUtil.ByteArrayToHexString(buf) + "] - sw -[" + String.format("%04X",sw) +"]");

                rsp.data = buf;
                rsp.sw = sw;

                return sw;

            } catch (IOException e) {
                LogUtil.e(TAG, "Error Communicating With Card: " + e.toString());
            }
        }

        return 0;
    }

}
