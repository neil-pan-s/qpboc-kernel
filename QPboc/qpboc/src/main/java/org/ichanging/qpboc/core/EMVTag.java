package org.ichanging.qpboc.core;

import androidx.annotation.NonNull;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

/**
 * Created by ChangingP on 16/6/12.
 */
public class EMVTag {

    private static final String TAG = EMVTag.class.getSimpleName();

    /**
     * Seem EMV Book 3 Annex B
     *
     */

    /**
     * tagName b6 Data element structure
     * = 0 Simple data element structure
     * = 1 Structure (composite) data element structure
     */
    public enum TagType {

        /**
         * The value field of a primitive data object contains a
         * data element for financial transaction interchange
         */
        PRIMITIVE,
        /**
         * The value field of a constructed data object contains one
         * or more primitive or constructed data objects.
         * The value field of a constructed data object is called a template.
         */
        CONSTRUCTED
    }

    /**
     * tagName b8,b7 The category that represents the data
     * According to the combination of 2 bits, there are four categories: general category, application category, context context category, and dedicated category. It is mainly used to determine the type of data processing when the terminal device interacts.
     *
     */
    public enum TagClass{
        UNIVERSAL, APPLICATION, CONTEXT_SPECIFIC, PRIVATE
    }

    /**
     * Tag From IC or Terminal
     *
     */
    public enum TagFrom {
        FROM_IC,
        FROM_TERMINAL
    }

    /**
     * Tag Value Format
     *
     */
    public enum TagFormat {

        /**
         * Only alphabetic (a to z and A to Z)
         */
        ALPHABETIC,

        /**
         * Only alphabetic (0 to 9)
         */
        NUMERIC,

        /**
         * Alphabetic and numeric (a to z, A to Z and 0 to 9)
         */
        ALPHANUMERIC,

        /**
         * Alphabetic numeric and special character (a to z, A to Z, 0 to 9 and ; ? = ...)
         */
        ALPHANUMERIC_SPECIAL,

        /**
         * Binary
         */
        BINARY,

        /**
         * Compressed numeric
         */
        COMPRESSED_NUMERIC,

        /**
         * Variable
         */
        VARIABLE
    }

    private byte[] tagID;

    private byte[] tagStringID = null;

    private byte[] tagValue = null;

    private String tagName;

    private String tagDescribe;

    private TagFrom tagFrom;

    private TagFormat tagFormat;

    // Number Format Tag List
    public static final String[] Tag_Format_N =
            {
                    "9A" , "9C" ,
                    "5F24" , "5F25" , "5F28" , "5F2A" , "5F34" , "5F36" ,
                    "9F02" , "9F03" , "9F11" , "9F15" , "9F1A" , "9F21" ,
                    "9F35" , "9F39" , "9F3C" , "9F3D" , "9F41" , "9F42" ,
                    "9F44" , "9F51" , "9F54" , "9F57" , "9F5C" , "9F73" ,
                    "9F75" , "9F76"
            };

    // Compressed Number Format Tag List
    public static final String[] Tag_Format_CN =
            {
                    "5A" , "9F20" , "9F62"
            };


    // support override or not,not use
    private boolean isUnique = false;

    public EMVTag(byte[] tag, byte[] value,TagFrom from,TagFormat type, String name, String describe)
    {
        // EMV Tag Only use low 16 bit
        this.tagID = tag;
        this.tagValue = value;
        this.tagFormat = type;
        this.tagFrom = from;
        this.tagName = name;
        this.tagDescribe = describe;
    }

    public EMVTag(String tag, String value,TagFrom from,TagFormat type, String name, String describe) {

        this.tagID = HexUtil.HexStringToByteArray(tag);

        if(value != null)
            this.tagValue = HexUtil.HexStringToByteArray(value);

        this.tagFormat = type;
        this.tagFrom = from;
        this.tagName = name;
        this.tagDescribe = describe;
    }

    public void setUnique(boolean isUnique)
    {
        this.isUnique = isUnique;
    }

    public byte[] getTagID() {
        return tagID;
    }

    public void setTagValue(byte[] value)
    {
        if(!isUnique) {
            tagValue = value;
        }else{
            LogUtil.i(TAG,"Tag - [" + HexUtil.ByteArrayToHexString(tagID) + "] is Unique, can't be override !");
        }
    }

    public byte[] getTagValue()
    {
        return tagValue;
    }

    public String getTagDescribe() {
        return tagDescribe;
    }

    public String getTagName() {
        return tagName;
    }

    public TagFrom getTagFrom() {
        return tagFrom;
    }

    public TagFormat getTagFormat() {
        return tagFormat;
    }

    public TagClass getTagClass() {

        TagClass tagClass = TagClass.UNIVERSAL;

        switch (tagID[0] & 0xC0)
        {
            case 0x00: tagClass = TagClass.UNIVERSAL; break;
            case 0x01: tagClass = TagClass.APPLICATION; break;
            case 0x10: tagClass = TagClass.CONTEXT_SPECIFIC; break;
            case 0x11: tagClass = TagClass.PRIVATE; break;

            default:break;
        }

        return tagClass;
    }

    public TagType getTagType() {
        return (isConstructed(tagID[0]) ? TagType.CONSTRUCTED : TagType.PRIMITIVE );
    }


    public  static String getTagDescribe_CN(String tagName)
    {
        String str;

        byte[] tag = HexUtil.HexStringToByteArray(tagName);
        int utag = ((tag[0] & 0xFF) << 8) | (tag[1] & 0xFF);

        switch (utag)
        {
            case 0x06:      str = "Terminal model data label"; break;
            case 0x4F:      str = "Application identifier (AID)"; break;
            case 0x50:      str = "Application tag"; break;
            case 0x57:      str = "Magnetic strip 2 equivalent data"; break;
            case 0x5A:      str = "Application master account (PAN)"; break;
            case 0x5F20:    str = "Cardholder's Name"; break;
            case 0x5F24:    str = "Application expiration date"; break;
            case 0x5F25:    str = "Application effective date"; break;
            case 0x5F28:    str = "Issuer country code"; break;
            case 0x5F2D:    str = "Preferred language"; break;
            case 0x5F30:    str = "Service code"; break;
            case 0x5F34:    str = "Application master account serial number"; break;
            case 0x5F50:    str = "Issuer URL"; break;
            case 0x61:      str = "Application template"; break;
            case 0x6F:      str = "File Control Information (FCI) template"; break;
            case 0x72:      str = "Issuer Script Template 2"; break;
            case 0x73:      str = "Directory custom template"; break;
            case 0x77:      str = "Response message template format 2"; break;
            case 0x80:      str = "Response message template format 1"; break;
            case 0x82:      str = "Application Interaction Feature (AIP)"; break;
            case 0x84:      str = "Dedicated file (DF) name"; break;
            case 0x86:      str = "Issuer script command"; break;
            case 0x87:      str = "Application priority indicator"; break;
            case 0x88:      str = "Short file identifier (SFI)"; break;
            case 0x8A:      str = "Authorization response code"; break;
            case 0x8C:      str = "Card Risk Management Data Object List 1 (CDOL1)"; break;
            case 0x8D:      str = "Card Risk Management Data Object List 2 (CDOL2)"; break;
            case 0x8E:      str = "Cardholder Verification Method (CVM) List"; break;
            case 0x8F:      str = "CA Public Key Index (PKI)"; break;
            case 0x90:      str = "Issuer Public Key Certificate"; break;
            case 0x91:      str = "Issuer Certification Data"; break;
            case 0x92:      str = "Issuer public key remainder"; break;
            case 0x93:      str = "Signed Static Application Data (SAD)"; break;
            case 0x94:      str = "Application File Locator (AFL)"; break;
            case 0x97:      str = "Transaction certificate data object"; break;
            case 0x9D:      str = "Directory data file (DDF) name"; break;
            case 0x9F05:    str = "Apply custom data"; break;
            case 0x9F07:    str = "Application control"; break;
            case 0x9F08:    str = "Application version number"; break;
            case 0x9F0A:    str = "APPLICATION_SELECTION_REGISTERED_PROPRIETARY_DATA - NEW"; break;
            case 0x9F0B:    str = "Cardholder name extension"; break;
            case 0x9F0D:    str = "Issuer Behavior Code (IAC) - Default"; break;
            case 0x9F0E:    str = "Issuer Behavior Code (IAC) - Rejection"; break;
            case 0x9F0F:    str = "Issuer Behavior Code (IAC) - Online"; break;
            case 0x9F10:    str = "Issuer application data"; break;
            case 0x9F11:    str = "Issuer Code Table Index"; break;
            case 0x9F12:    str = "Application preferred name"; break;
            case 0x9F13:    str = "Last online application transaction counter (ATC) register"; break;
            case 0x9F14:    str = "Continuous offline trading floor"; break;
            case 0x9F17:    str = "PIN try counter"; break;
            case 0x9F1F:    str = "Magnetic stripe 1 custom data"; break;
            case 0x9F23:    str = "Continuous offline transaction limit"; break;
            case 0x9F26:    str = "Application Ciphertext (AC)"; break;
            case 0x9F27:    str = "Ciphertext information data"; break;
            case 0x9F32:    str = "Issuer Public Key Index"; break;
            case 0x9F36:    str = "Application transaction counter"; break;
            case 0x9F38:    str = "Processing Options Data Object List (PDOL)"; break;
            case 0x9F42:    str = "Application currency code"; break;
            case 0x9F44:    str = "Applied currency index"; break;
            case 0x9F45:    str = "Data authentication code"; break;
            case 0x9F46:    str = "IC card public key certificate"; break;
            case 0x9F47:    str = "IC card public key index"; break;
            case 0x9F48:    str = "IC card public key remainder"; break;
            case 0x9F49:    str = "Dynamic Data Authentication Data Object List (DDOL)"; break;
            case 0x9F4A:    str = "Static data authentication label list"; break;
            case 0x9F4B:    str = "Signed dynamic application data"; break;
            case 0x9F4C:    str = "IC dynamic number"; break;
            case 0x9F4D:    str = "Log entry"; break;
            case 0x9F4F:    str = "Log format"; break;
            case 0x9F51:    str = "Application currency code"; break;
            case 0x9F52:    str = "Apply default behavior (ADA)"; break;
            case 0x9F53:    str = "Continuous offline transaction limit (international - currency)"; break;
            case 0x9F54:    str = "Cumulative offline transaction amount limit"; break;
            case 0x9F56:    str = "Issuer certification indicator"; break;
            case 0x9F57:    str = "Issuer country code"; break;
            case 0x9F58:    str = "Continuous offline trading floor"; break;
            case 0x9F59:    str = "Continuous offline transaction limit"; break;
            case 0x9F5A:    str = "Issuer URL2"; break;
            case 0x9F5C:    str = "Cumulative offline transaction amount cap"; break;
            case 0x9F61:    str = "Cardholder ID number"; break;
            case 0x9F62:    str = "Cardholder ID Type"; break;
            case 0x9F63:    str = "Card product identification information"; break;
            case 0x9F72:    str = "Continuous offline transaction limit (International - Country)"; break;
            case 0x9F73:    str = "Currency conversion factor"; break;
            case 0x9F75:    str = "Cumulative offline transaction amount limit (dual currency)"; break;
            case 0x9F76:    str = "Second application currency"; break;
            case 0xA5:      str = "File Control Information (FCI) specific template"; break;
            case 0xBF0C:    str = "File Control Information (FCI) Issuer Custom Data"; break;

            // Transaction detail record file content
            case 0x9A:      str = "transaction date"; break;
            case 0x9F21:    str = "transaction hour"; break;
            case 0x9F02:    str = "Authorized amount"; break;
            case 0x9F03:    str = "Other amount"; break;
            case 0x9F1A:    str = "Terminal country code"; break;
            case 0x5F2A:    str = "Transaction currency code"; break;
            case 0x9F4E:    str = "Business Name"; break;
            case 0x9C:      str = "Transaction Type"; break;
            case 0x9B:      str = "Transaction Status Information (TSI)"; break;
            case 0x95:      str = "Terminal Verification Result (TVR)"; break;
            case 0x9F01	:   str = "Acquirer line identifier"; break;
            case 0x9F15	:   str = "Merchant classification code"; break;
            case 0x9F16	:   str = "Merchant identification"; break;
            case 0x9F1C	:   str = "Terminal identification"; break;
            case 0x9F1E	:   str = "Interface device serial number"; break;
            case 0x9F33	:   str = "Terminal performance"; break;
            case 0x9F34	:   str = "Cardholder Certification Results"; break;
            case 0x9F35	:   str = "terminal type"; break;
            case 0x9F37	:   str = "Terminal random number"; break;
            case 0x9F39	:   str = "POS Entry Mode"; break;
            case 0xDF31	:   str = "Script execution result"; break;
            case 0xDF41	:   str = "Mandatory acceptance of the logo"; break;
            case 0xDF51	:   str = "Online ciphertext"; break;
            case 0x9F66:    str = "Terminal transaction attribute"; break;
            case 0x9F6C:    str = "Card transaction attribute"; break;
            case 0x9F5D:    str = "Available offline amount"; break;
            case 0x9F41:    str = "Terminal serial number"; break;

            case 0x9F06: str = "Application AID"; break;
            case 0x9F22: str = "Public key index"; break;
            case 0xDF05: str = "Public key validity period"; break;
            case 0xDF06: str = "Public key hash algorithm identification"; break;
            case 0xDF07: str = "Public key algorithm identification"; break;
            case 0xDF02: str = "Public key module"; break;
            case 0xDF04: str = "Public key index"; break;
            case 0xDF03: str = "Public key check value"; break;
            case 0xDF01: str = "Application selection indicator"; break;
            case 0xDF11: str = "TACDefault"; break;
            case 0xDF12: str = "TACOnline"; break;
            case 0xDF13: str = "TACRefused"; break;
            case 0x9F1B: str = "Terminal minimum"; break;
            case 0xDF15: str = "Offset randomly selected threshold"; break;
            case 0xDF16: str = "Offset randomly selected maximum target percentage"; break;
            case 0xDF17: str = "Randomly selected target percentage"; break;
            case 0xDF14: str = "Default DDOL"; break;
            case 0xDF18: str = "Terminal online PIN support capability"; break;
            case 0x9F7B: str = "Terminal electronic cash transaction limit"; break;
            case 0xDF19: str = "Contactless reader offline minimum"; break;
            case 0xDF20: str = "Contactless reader transaction limit"; break;
            case 0xDF21: str = "Contactless reader CVM quota"; break;

            default:
                str = String.format("UndefinedTag [%04X]", utag);
                break;
        }

        return str;
    }

    // judge tag is data object list or not
    public static boolean isDol(String tagName)
    {
        String[] dols = {
                "9F38",  // PDOL
                "8C",    // CDOL1
                "8D",    // CDOL2
                "9F49",  // DDOL
                "DF14",  // Default DOL
                "97"     // TDOL
        };

        for (String dol : dols)
        {
            if(dol.equals(tagName)) return true;
        }

        return  false;
    }

    public static boolean isFormatNTag(String tag)
    {
        for (String t : Tag_Format_N)
        {
            if(t.equals(tag)) return true;
        }

        return  false;
    }

    public static boolean isFormatCNTag(String tag)
    {
        for (String t : Tag_Format_CN)
        {
            if(t.equals(tag)) return true;
        }

        return  false;
    }

    // judge tag name is one byte or more  such as: 9F02
    // emv/pboc just use two byte tagname
    public static boolean isOneByteTagName(byte tagFirstByte)
    {
        return  (tagFirstByte & 0x1F) != 0x1F;
    }

    // judge tag length is one byte or more  such as: 81B0
    public static boolean isOneByteTagLen(byte tagLenFirstByte)
    {
        return (tagLenFirstByte & 0x80) != 0x80;
    }

    public static boolean isConstructed(byte tagFirstByte)
    {
        return ( (tagFirstByte & 0x20) == 0x20);
    }

    @Override
    @NonNull
    public String toString() {

        String strClass,strValue;

        switch (getTagClass())
        {
            case APPLICATION: strClass = "APPLICATION"; break;
            case UNIVERSAL: strClass = "UNIVERSAL"; break;
            case CONTEXT_SPECIFIC: strClass = "CONTEXT_SPECIFIC"; break;
            case PRIVATE: strClass = "PRIVATE"; break;

            default: strClass = "unknow tagclass"; break;
        }

        if(tagValue == null )
        {
            strValue = "null";
        }else {
            switch (getTagFormat()) {
                case BINARY:
                case COMPRESSED_NUMERIC:
                case VARIABLE:
                    strValue = HexUtil.ByteArrayToHexString(tagValue);
                    break;

                default:
                    strValue = new String(tagValue);
                    break;
            }
        }

        return "Tag["+ HexUtil.ByteArrayToHexString(tagID) + "]" +
                " - Value [" + strValue + "]" +
                " - From [" + (tagFrom == TagFrom.FROM_IC ? "FROM_IC" : "FROM_TERMINAL") + "] " +
                " - Type [" + (isConstructed(tagID[0]) ? "CONSTRUCTED" : "PRIMITIVE") + "] " +
                " - Class [" + strClass +"]" +
                " - Name [" + tagName +"]" +
                " - Describe [" + tagDescribe + "]"
                ;
    }

}
