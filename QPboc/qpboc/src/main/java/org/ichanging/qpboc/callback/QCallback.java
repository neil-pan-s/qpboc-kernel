package org.ichanging.qpboc.callback;

/**
 * Created by ChangingP on 16/6/22.
 */
public interface QCallback {

    void onAccept();

    void onDecline();

    void onOnline();

    void onTermination(int errCode);
}
