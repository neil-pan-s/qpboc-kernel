package org.ichanging.qpboc.core;

/**
 * Created by ChangingP on 16/6/8.
 */
public interface ICCInterface {


    int _PowerOn();

    int _PowerOff();

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

    int _ApduComm(ICCApdu apdu, ICCResponse rsp);

}
