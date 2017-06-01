package org.ichanging.qpboc.core;


public class EMVException extends RuntimeException{

    public EMVException(String message){
        super(message);
    }

    public EMVException(String message, Throwable cause) {
        super(message, cause);
    }
    public EMVException(Throwable cause){
        super(cause);
    }
}