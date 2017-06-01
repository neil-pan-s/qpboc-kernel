package org.ichanging.qpboc.core;

/**
 * Certification Authority Public Key
 *
 * Created by ChangingP on 16/6/16.
 */
public class EMVCapk {

    public byte[] pk_modulus;
    public byte[] pk_exponent;
    public byte[] _hashvalue;
    public byte[] _expired_date;     /* Expired Date */
    public byte[] _rid;
    public byte   _index;            /* Certification Authority Public Key Index */
    public byte   _pk_algorithm;
    public byte   _hash_algorithm;
}
