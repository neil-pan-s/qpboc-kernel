package org.ichanging.qpboc.callback;

/**
 * Created by ChangingP on 16/6/17.
 *
 * Not Used , ICC Communication always in sync mode
 *
 */
public interface ICCCallback {

    /**
     * ICC Communication Succ
     *
     */
    void onSuccess(int sw ,byte[] data);

    /**
     * ICC Communication Error
     *
     */
    void onError(int errCode);

    /**
     * ICC Communication Timeout
     *
     */
    void onTimeout();

}
