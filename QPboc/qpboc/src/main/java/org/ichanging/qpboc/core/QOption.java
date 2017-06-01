package org.ichanging.qpboc.core;

import org.ichanging.qpboc.callback.QCallback;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ChangingP on 16/6/22.
 */
public class QOption{

    public byte        _transType;                 /* in, trade type */
    public int         _amount;                    /* in, trade amount */

    public byte[]      _onlinePIN;                 /* out, if online pin is entered */
    public boolean     _isSignatureRequest;        /* out, if the CVM finally request a signature */

    // unused
    public byte[]      _issScriptResult;           /* out, if issuer script result exists */
    public boolean     _isAdviceRequest;           /* out, if advice is required (must be supported by ics) */
    public boolean     _isForceAcceptSupported;    /* out, if ICS support it */

    private CoreInterface   _adapter;
    private QCallback       _callback;
    private ProcessSwitch   _switch;

    public void clear()
    {
        _transType = 0x0;
        _amount = 0x0;
        _onlinePIN = null;
        _isSignatureRequest = false;

        _issScriptResult = null;
        _isAdviceRequest = false;
        _isForceAcceptSupported = false;
    }


    public void setProcessSwitch(ProcessSwitch processSwitch)
    {
        _switch = processSwitch;
    }

    public ProcessSwitch getProcessSwitch()
    {
        return _switch;
    }

    public CoreInterface getCoreInterface()
    {
        return _adapter;
    }

    public QCallback getQCallback()
    {
        return _callback;
    }

    public void setCoreInterface(CoreInterface adapter)
    {
        _adapter =  adapter;
    }

    public void setQCallback(QCallback callback)
    {
        _callback =  callback;
    }

}
