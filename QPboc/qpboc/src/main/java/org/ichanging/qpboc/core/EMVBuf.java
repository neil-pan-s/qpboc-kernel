package org.ichanging.qpboc.core;

import org.ichanging.qpboc.util.HexUtil;
import org.ichanging.qpboc.platform.LogUtil;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ChangingP on 16/6/14.
 */
public class EMVBuf {

    private static final String TAG = "EMVBuf";

    private static HashMap<String,EMVTag> mTlvMap = new HashMap<String , EMVTag>();

    /**
     *
     * TODO: Check out the tag which is EMVTag.TagFrom.FROM_TERMINAL
     *
     * The coding of primitive context-specific class data objects in the ranges '80' to '9E' and '9F00' to '9F4F' is reserved for EMV specification.
     * The coding of primitive context-specific class data objects in the range '9F50' to '9F7F' is reserved for the payment systems.
     *
     */

    //One byte tags
    //7816-4 Interindustry data object for tag allocation authority
    public static final EMVTag UNIVERSAL_TAG_FOR_OID                   = new EMVTag("06", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Object Identifier (OID)", "Universal tag for OID");
    public static final EMVTag COUNTRY_CODE                            = new EMVTag("41", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Country Code", "Country code (encoding specified in ISO 3166-1) and optional national data");
    public static final EMVTag ISSUER_IDENTIFICATION_NUMBER            = new EMVTag("42", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Issuer Identification Number (IIN)", "The number that identifies the major industry and the card issuer and that forms the first part of the Primary Account Number (PAN)");

    //7816-4 Interindustry data objects for application identification and selection
    public static final EMVTag AID_CARD                                = new EMVTag("4f", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Application Identifier (AID) - card", "Identifies the application as described in ISO/IEC 7816-5");
    public static final EMVTag APPLICATION_LABEL                       = new EMVTag("50", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Application Label", "Mnemonic associated with the AID according to ISO/IEC 7816-5");
    public static final EMVTag PATH                                    = new EMVTag("51", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "File reference data element", "ISO-7816 Path");
    public static final EMVTag COMMAND_APDU                            = new EMVTag("52", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Command APDU", "");
    public static final EMVTag DISCRETIONARY_DATA_OR_TEMPLATE          = new EMVTag("53", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Discretionary data (or template)", "");
    public static final EMVTag APPLICATION_TEMPLATE                    = new EMVTag("61", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Application Template", "Contains one or more data objects relevant to an application directory entry according to ISO/IEC 7816-5");
    public static final EMVTag FCI_TEMPLATE                            = new EMVTag("6f", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "File Control Information (FCI) Template", "Set of file control parameters and file management data (according to ISO/IEC 7816-4)");
    public static final EMVTag DD_TEMPLATE                             = new EMVTag("73", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Directory Discretionary Template", "Issuer discretionary part of the directory according to ISO/IEC 7816-5");
    public static final EMVTag DEDICATED_FILE_NAME                     = new EMVTag("84", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Dedicated File (DF) Name", "Identifies the name of the DF as described in ISO/IEC 7816-4");
    public static final EMVTag SFI                                     = new EMVTag("88", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Short File Identifier (SFI)", "Identifies the SFI to be used in the commands related to a given AEF or DDF. The SFI data object is a binary field with the three high order bits set to zero");

    public static final EMVTag FCI_PROPRIETARY_TEMPLATE                = new EMVTag("a5", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "File Control Information (FCI) Proprietary Template", "Identifies the data object proprietary to this specification in the FCI template according to ISO/IEC 7816-4");
    public static final EMVTag ISSUER_URL                              = new EMVTag("5f50", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Issuer URL", "The URL provides the location of the Issuer’s Library Server on the Internet");

    //EMV
    public static final EMVTag TRACK1_DATA                             = new EMVTag("56", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC, "Track 1 Data", "Track 1 Data contains the data objects of the track 1 according to [ISO/IEC 7813] Structure B, excluding start sentinel, end sentinel and LRC.");
    public static final EMVTag TRACK_2_EQV_DATA                        = new EMVTag("57", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC, "Track 2 Equivalent Data", "Contains the data elements of track 2 according to ISO/IEC 7813, excluding start sentinel, end sentinel, and Longitudinal Redundancy Check (LRC)");
    public static final EMVTag PAN                                     = new EMVTag("5a", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Application Primary Account Number (PAN)", "Valid cardholder account number");
    public static final EMVTag RECORD_TEMPLATE                         = new EMVTag("70", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Record Template (EMV Proprietary)", "Template proprietary to the EMV specification");
    public static final EMVTag ISSUER_SCRIPT_TEMPLATE_1                = new EMVTag("71", null ,EMVTag.TagFrom.FROM_TERMINAL, EMVTag.TagFormat.VARIABLE, "Issuer Script Template 1", "Contains proprietary issuer data for transmission to the ICC before the second GENERATE AC command");
    public static final EMVTag ISSUER_SCRIPT_TEMPLATE_2                = new EMVTag("72", null ,EMVTag.TagFrom.FROM_TERMINAL, EMVTag.TagFormat.VARIABLE, "Issuer Script Template 2", "Contains proprietary issuer data for transmission to the ICC after the second GENERATE AC command");
    public static final EMVTag RESPONSE_MESSAGE_TEMPLATE_2             = new EMVTag("77", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Response Message Template Format 2", "Contains the data objects (with tags and lengths) returned by the ICC in response to a command");
    public static final EMVTag RESPONSE_MESSAGE_TEMPLATE_1             = new EMVTag("80", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Response Message Template Format 1", "Contains the data objects (without tags and lengths) returned by the ICC in response to a command");
    public static final EMVTag AMOUNT_AUTHORISED_BINARY                = new EMVTag("81", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Amount, Authorised (Binary)", "Authorised amount of the transaction (excluding adjustments)");
    public static final EMVTag APPLICATION_INTERCHANGE_PROFILE         = new EMVTag("82", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Application Interchange Profile", "Indicates the capabilities of the card to support specific functions in the application");
    public static final EMVTag COMMAND_TEMPLATE                        = new EMVTag("83", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Command Template", "Identifies the data field of a command message");
    public static final EMVTag ISSUER_SCRIPT_COMMAND                   = new EMVTag("86", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Issuer Script Command", "Contains a command for transmission to the ICC");
    public static final EMVTag APPLICATION_PRIORITY_INDICATOR          = new EMVTag("87", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Application Priority Indicator", "Indicates the priority of a given application or group of applications in a directory");
    public static final EMVTag AUTHORISATION_CODE                      = new EMVTag("89", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Authorisation Code", "Value generated by the authorisation authority for an approved transaction");
    public static final EMVTag AUTHORISATION_RESPONSE_CODE             = new EMVTag("8a", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Authorisation Response Code", "Code that defines the disposition of a message");
    public static final EMVTag CDOL1                                   = new EMVTag("8c", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Card Risk Management Data Object List 1 (CDOL1)", "List of data objects (tag and length) to be passed to the ICC in the first GENERATE AC command");
    public static final EMVTag CDOL2                                   = new EMVTag("8d", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Card Risk Management Data Object List 2 (CDOL2)", "List of data objects (tag and length) to be passed to the ICC in the second GENERATE AC command");
    public static final EMVTag CVM_LIST                                = new EMVTag("8e", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Cardholder Verification Method (CVM) List", "Identifies a method of verification of the cardholder supported by the application");
    public static final EMVTag CA_PUBLIC_KEY_INDEX_CARD                = new EMVTag("8f", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Certification Authority Public Key Index - card", "Identifies the certification authority’s public key in conjunction with the RID");
    public static final EMVTag ISSUER_PUBLIC_KEY_CERT                  = new EMVTag("90", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Issuer Public Key Certificate", "Issuer public key certified by a certification authority");
    public static final EMVTag ISSUER_AUTHENTICATION_DATA              = new EMVTag("91", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Issuer Authentication Data", "Data sent to the ICC for online issuer authentication");
    public static final EMVTag ISSUER_PUBLIC_KEY_REMAINDER             = new EMVTag("92", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Issuer Public Key Remainder", "Remaining digits of the Issuer Public Key Modulus");
    public static final EMVTag SIGNED_STATIC_APP_DATA                  = new EMVTag("93", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Signed Static Application Data", "Digital signature on critical application parameters for SDA");
    public static final EMVTag APPLICATION_FILE_LOCATOR                = new EMVTag("94", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Application File Locator (AFL)", "Indicates the location (SFI, range of records) of the AEFs related to a given application");
    public static final EMVTag TERMINAL_VERIFICATION_RESULTS           = new EMVTag("95", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Terminal Verification Results (TVR)", "Status of the different functions as seen from the terminal");
    public static final EMVTag TDOL                                    = new EMVTag("97", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Transaction Certificate Data Object List (TDOL)", "List of data objects (tag and length) to be used by the terminal in generating the TC Hash Value");
    public static final EMVTag TC_HASH_VALUE                           = new EMVTag("98", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Transaction Certificate (TC) Hash Value", "Result of a hash function specified in Book 2, Annex B3.1");
    public static final EMVTag TRANSACTION_PIN_DATA                    = new EMVTag("99", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Transaction Personal Identification Number (PIN) Data", "Data entered by the cardholder for the purpose of the PIN verification");
    public static final EMVTag TRANSACTION_DATE                        = new EMVTag("9a", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Transaction Date", "Local date that the transaction was authorised");
    public static final EMVTag TRANSACTION_STATUS_INFORMATION          = new EMVTag("9b", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Transaction Status Information", "Indicates the functions performed in a transaction");
    public static final EMVTag TRANSACTION_TYPE                        = new EMVTag("9c", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Transaction Type", "Indicates the type of financial transaction, represented by the first two digits of ISO 8583:1987 Processing Code");
    public static final EMVTag DDF_NAME                                = new EMVTag("9d", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Directory Definition File (DDF) Name", "Identifies the name of a DF associated with a directory");
    //Two byte tags
    public static final EMVTag CARDHOLDER_NAME                         = new EMVTag("5f20", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Cardholder Name", "Indicates cardholder name according to ISO 7813");
    public static final EMVTag APP_EXPIRATION_DATE                     = new EMVTag("5f24", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Application Expiration Date", "Date after which application expires");
    public static final EMVTag APP_EFFECTIVE_DATE                      = new EMVTag("5f25", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Application Effective Date", "Date from which the application may be used");
    public static final EMVTag ISSUER_COUNTRY_CODE                     = new EMVTag("5f28", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Issuer Country Code", "Indicates the country of the issuer according to ISO 3166");
    public static final EMVTag TRANSACTION_CURRENCY_CODE               = new EMVTag("5f2a", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Transaction Currency Code", "Indicates the currency code of the transaction according to ISO 4217");
    public static final EMVTag LANGUAGE_PREFERENCE                     = new EMVTag("5f2d", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Language Preference", "1–4 languages stored in order of preference, each represented by 2 alphabetical characters according to ISO 639");
    public static final EMVTag SERVICE_CODE                            = new EMVTag("5f30", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Service Code", "Service code as defined in ISO/IEC 7813 for track 1 and track 2");
    public static final EMVTag PAN_SEQUENCE_NUMBER                     = new EMVTag("5f34", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Application Primary Account Number (PAN) Sequence Number", "Identifies and differentiates cards with the same PAN");
    public static final EMVTag TRANSACTION_CURRENCY_EXP                = new EMVTag("5f36", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Transaction Currency Exponent", "Indicates the implied position of the decimal point from the right of the transaction amount represented according to ISO 4217");
    public static final EMVTag IBAN                                    = new EMVTag("5f53", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "International Bank Account Number (IBAN)", "Uniquely identifies the account of a customer at a financial institution as defined in ISO 13616");
    public static final EMVTag BANK_IDENTIFIER_CODE                    = new EMVTag("5f54", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Bank Identifier Code (BIC)", "Uniquely identifies a bank as defined in ISO 9362");
    public static final EMVTag ISSUER_COUNTRY_CODE_ALPHA2              = new EMVTag("5f55", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Issuer Country Code (alpha2 format)", "Indicates the country of the issuer as defined in ISO 3166 (using a 2 character alphabetic code)");
    public static final EMVTag ISSUER_COUNTRY_CODE_ALPHA3              = new EMVTag("5f56", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Issuer Country Code (alpha3 format)", "Indicates the country of the issuer as defined in ISO 3166 (using a 3 character alphabetic code)");
    public static final EMVTag ACQUIRER_IDENTIFIER                     = new EMVTag("9f01", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Acquirer Identifier", "Uniquely identifies the acquirer within each payment system");
    public static final EMVTag AMOUNT_AUTHORISED_NUMERIC               = new EMVTag("9f02", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Amount, Authorised (Numeric)", "Authorised amount of the transaction (excluding adjustments)");
    public static final EMVTag AMOUNT_OTHER_NUMERIC                    = new EMVTag("9f03", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Amount, Other (Numeric)", "Secondary amount associated with the transaction representing a cashback amount");
    public static final EMVTag AMOUNT_OTHER_BINARY                     = new EMVTag("9f04", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Amount, Other (Binary)", "Secondary amount associated with the transaction representing a cashback amount");
    public static final EMVTag APP_DISCRETIONARY_DATA                  = new EMVTag("9f05", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Application Discretionary Data", "Issuer or payment system specified data relating to the application");
    public static final EMVTag AID_TERMINAL                            = new EMVTag("9f06", null ,EMVTag.TagFrom.FROM_TERMINAL, EMVTag.TagFormat.BINARY, "Application Identifier (AID) - terminal", "Identifies the application as described in ISO/IEC 7816-5");
    public static final EMVTag APP_USAGE_CONTROL                       = new EMVTag("9f07", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Application Usage Control", "Indicates issuer’s specified restrictions on the geographic usage and services allowed for the application");
    public static final EMVTag APP_VERSION_NUMBER_CARD                 = new EMVTag("9f08", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Application Version Number - card", "Version number assigned by the payment system for the application");
    public static final EMVTag APP_VERSION_NUMBER_TERMINAL             = new EMVTag("9f09", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Application Version Number - terminal", "Version number assigned by the payment system for the application");
    public static final EMVTag CARDHOLDER_NAME_EXTENDED                = new EMVTag("9f0b", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Cardholder Name Extended", "Indicates the whole cardholder name when greater than 26 characters using the same coding convention as in ISO 7813");
    public static final EMVTag ISSUER_ACTION_CODE_DEFAULT              = new EMVTag("9f0d", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Issuer Action Code - Default", "Specifies the issuer’s conditions that cause a transaction to be rejected if it might have been approved online, but the terminal is unable to process the transaction online");
    public static final EMVTag ISSUER_ACTION_CODE_DENIAL               = new EMVTag("9f0e", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Issuer Action Code - Denial", "Specifies the issuer’s conditions that cause the denial of a transaction without attempt to go online");
    public static final EMVTag ISSUER_ACTION_CODE_ONLINE               = new EMVTag("9f0f", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Issuer Action Code - Online", "Specifies the issuer’s conditions that cause a transaction to be transmitted online");
    public static final EMVTag ISSUER_APPLICATION_DATA                 = new EMVTag("9f10", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Issuer Application Data", "Contains proprietary application data for transmission to the issuer in an online transaction");
    public static final EMVTag ISSUER_CODE_TABLE_INDEX                 = new EMVTag("9f11", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Issuer Code Table Index", "Indicates the code table according to ISO/IEC 8859 for displaying the Application Preferred Name");
    public static final EMVTag APP_PREFERRED_NAME                      = new EMVTag("9f12", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Application Preferred Name", "Preferred mnemonic associated with the AID");
    public static final EMVTag LAST_ONLINE_ATC_REGISTER                = new EMVTag("9f13", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Last Online Application Transaction Counter (ATC) Register", "ATC value of the last transaction that went online");
    public static final EMVTag LOWER_CONSEC_OFFLINE_LIMIT              = new EMVTag("9f14", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Lower Consecutive Offline Limit", "Issuer-specified preference for the maximum number of consecutive offline transactions for this ICC application allowed in a terminal with online capability");
    public static final EMVTag MERCHANT_CATEGORY_CODE                  = new EMVTag("9f15", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Merchant Category Code", "Classifies the type of business being done by the merchant, represented according to ISO 8583:1993 for Card Acceptor Business Code");
    public static final EMVTag MERCHANT_IDENTIFIER                     = new EMVTag("9f16", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Merchant Identifier", "When concatenated with the Acquirer Identifier, uniquely identifies a given merchant");
    public static final EMVTag PIN_TRY_COUNTER                         = new EMVTag("9f17", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Personal Identification Number (PIN) Try Counter", "Number of PIN tries remaining");
    public static final EMVTag ISSUER_SCRIPT_IDENTIFIER                = new EMVTag("9f18", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Issuer Script Identifier", "Identification of the Issuer Script");
    public static final EMVTag TERMINAL_COUNTRY_CODE                   = new EMVTag("9f1a", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Terminal Country Code", "Indicates the country of the terminal, represented according to ISO 3166");
    public static final EMVTag TERMINAL_FLOOR_LIMIT                    = new EMVTag("9f1b", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Terminal Floor Limit", "Indicates the floor limit in the terminal in conjunction with the AID");
    public static final EMVTag TERMINAL_IDENTIFICATION                 = new EMVTag("9f1c", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Terminal Identification", "Designates the unique location of a terminal at a merchant");
    public static final EMVTag TERMINAL_RISK_MANAGEMENT_DATA           = new EMVTag("9f1d", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Terminal Risk Management Data", "Application-specific value used by the card for risk management purposes");
    public static final EMVTag INTERFACE_DEVICE_SERIAL_NUMBER          = new EMVTag("9f1e", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Interface Device (IFD) Serial Number", "Unique and permanent serial number assigned to the IFD by the manufacturer");
    public static final EMVTag TRACK1_DISCRETIONARY_DATA               = new EMVTag("9f1f", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "[Magnetic Stripe] Track 1 Discretionary Data", "Discretionary part of track 1 according to ISO/IEC 7813");
    public static final EMVTag TRACK2_DISCRETIONARY_DATA               = new EMVTag("9f20", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "[Magnetic Stripe] Track 2 Discretionary Data", "Discretionary part of track 2 according to ISO/IEC 7813");
    public static final EMVTag TRANSACTION_TIME                        = new EMVTag("9f21", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Transaction Time (HHMMSS)", "Local time that the transaction was authorised");
    public static final EMVTag CA_PUBLIC_KEY_INDEX_TERMINAL            = new EMVTag("9f22", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Certification Authority Public Key Index - Terminal", "Identifies the certification authority’s public key in conjunction with the RID");
    public static final EMVTag UPPER_CONSEC_OFFLINE_LIMIT              = new EMVTag("9f23", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Upper Consecutive Offline Limit", "Issuer-specified preference for the maximum number of consecutive offline transactions for this ICC application allowed in a terminal without online capability");
    public static final EMVTag APP_CRYPTOGRAM                          = new EMVTag("9f26", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Application Cryptogram", "Cryptogram returned by the ICC in response of the GENERATE AC command");
    public static final EMVTag CRYPTOGRAM_INFORMATION_DATA             = new EMVTag("9f27", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Cryptogram Information Data", "Indicates the type of cryptogram and the actions to be performed by the terminal");
    public static final EMVTag ICC_PIN_ENCIPHERMENT_PUBLIC_KEY_CERT    = new EMVTag("9f2d", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "ICC PIN Encipherment Public Key Certificate", "ICC PIN Encipherment Public Key certified by the issuer");
    public static final EMVTag ICC_PIN_ENCIPHERMENT_PUBLIC_KEY_EXP     = new EMVTag("9f2e", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "ICC PIN Encipherment Public Key Exponent", "ICC PIN Encipherment Public Key Exponent used for PIN encipherment");
    public static final EMVTag ICC_PIN_ENCIPHERMENT_PUBLIC_KEY_REM     = new EMVTag("9f2f", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "ICC PIN Encipherment Public Key Remainder", "Remaining digits of the ICC PIN Encipherment Public Key Modulus");
    public static final EMVTag ISSUER_PUBLIC_KEY_EXP                   = new EMVTag("9f32", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Issuer Public Key Exponent", "Issuer public key exponent used for the verification of the Signed Static Application Data and the ICC Public Key Certificate");
    public static final EMVTag TERMINAL_CAPABILITIES                   = new EMVTag("9f33", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Terminal Capabilities", "Indicates the card data input, CVM, and security capabilities of the terminal");
    public static final EMVTag CVM_RESULTS                             = new EMVTag("9f34", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Cardholder Verification (CVM) Results", "Indicates the results of the last CVM performed");
    public static final EMVTag TERMINAL_TYPE                           = new EMVTag("9f35", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Terminal Type", "Indicates the environment of the terminal, its communications capability, and its operational control");
    public static final EMVTag APP_TRANSACTION_COUNTER                 = new EMVTag("9f36", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Application Transaction Counter (ATC)", "Counter maintained by the application in the ICC (incrementing the ATC is managed by the ICC)");
    public static final EMVTag UNPREDICTABLE_NUMBER                    = new EMVTag("9f37", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Unpredictable Number", "Value to provide variability and uniqueness to the generation of a cryptogram");
    public static final EMVTag PDOL                                    = new EMVTag("9f38", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Processing Options Data Object List (PDOL)", "Contains a list of terminal resident data objects (tags and lengths) needed by the ICC in processing the GET PROCESSING OPTIONS command");
    public static final EMVTag POINT_OF_SERVICE_ENTRY_MODE             = new EMVTag("9f39", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Point-of-Service (POS) Entry Mode", "Indicates the method by which the PAN was entered, according to the first two digits of the ISO 8583:1987 POS Entry Mode");
    public static final EMVTag AMOUNT_REFERENCE_CURRENCY               = new EMVTag("9f3a", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Amount, Reference Currency", "Authorised amount expressed in the reference currency");
    public static final EMVTag APP_REFERENCE_CURRENCY                  = new EMVTag("9f3b", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Application Reference Currency", "1–4 currency codes used between the terminal and the ICC when the Transaction Currency Code is different from the Application Currency Code; each code is 3 digits according to ISO 4217");
    public static final EMVTag TRANSACTION_REFERENCE_CURRENCY_CODE     = new EMVTag("9f3c", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Transaction Reference Currency Code", "Code defining the common currency used by the terminal in case the Transaction Currency Code is different from the Application Currency Code");
    public static final EMVTag TRANSACTION_REFERENCE_CURRENCY_EXP      = new EMVTag("9f3d", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Transaction Reference Currency Exponent", "Indicates the implied position of the decimal point from the right of the transaction amount, with the Transaction Reference Currency Code represented according to ISO 4217");
    public static final EMVTag ADDITIONAL_TERMINAL_CAPABILITIES        = new EMVTag("9f40", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Additional Terminal Capabilities", "Indicates the data input and output capabilities of the terminal");
    public static final EMVTag TRANSACTION_SEQUENCE_COUNTER            = new EMVTag("9f41", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Transaction Sequence Counter", "Counter maintained by the terminal that is incremented by one for each transaction");
    public static final EMVTag APPLICATION_CURRENCY_CODE               = new EMVTag("9f42", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Application Currency Code", "Indicates the currency in which the account is managed according to ISO 4217");
    public static final EMVTag APP_REFERENCE_CURRECY_EXPONENT          = new EMVTag("9f43", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Application Reference Currency Exponent", "Indicates the implied position of the decimal point from the right of the amount, for each of the 1–4 reference currencies represented according to ISO 4217");
    public static final EMVTag APP_CURRENCY_EXPONENT                   = new EMVTag("9f44", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.NUMERIC, "Application Currency Exponent", "Indicates the implied position of the decimal point from the right of the amount represented according to ISO 4217");
    public static final EMVTag DATA_AUTHENTICATION_CODE                = new EMVTag("9f45", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Data Authentication Code", "An issuer assigned value that is retained by the terminal during the verification process of the Signed Static Application Data");
    public static final EMVTag ICC_PUBLIC_KEY_CERT                     = new EMVTag("9f46", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "ICC Public Key Certificate", "ICC Public Key certified by the issuer");
    public static final EMVTag ICC_PUBLIC_KEY_EXP                      = new EMVTag("9f47", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "ICC Public Key Exponent", "ICC Public Key Exponent used for the verification of the Signed Dynamic Application Data");
    public static final EMVTag ICC_PUBLIC_KEY_REMAINDER                = new EMVTag("9f48", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "ICC Public Key Remainder", "Remaining digits of the ICC Public Key Modulus");
    public static final EMVTag DDOL                                    = new EMVTag("9f49", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Dynamic Data Authentication Data Object List (DDOL)", "List of data objects (tag and length) to be passed to the ICC in the INTERNAL AUTHENTICATE command");
    public static final EMVTag SDA_TAG_LIST                            = new EMVTag("9f4a", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Static Data Authentication Tag List", "List of tags of primitive data objects defined in this specification whose value fields are to be included in the Signed Static or Dynamic Application Data");
    public static final EMVTag SIGNED_DYNAMIC_APPLICATION_DATA         = new EMVTag("9f4b", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Signed Dynamic Application Data", "Digital signature on critical application parameters for DDA or CDA");
    public static final EMVTag ICC_DYNAMIC_NUMBER                      = new EMVTag("9f4c", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "ICC Dynamic Number", "Time-variant number generated by the ICC, to be captured by the terminal");
    public static final EMVTag LOG_ENTRY                               = new EMVTag("9f4d", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Log Entry", "Provides the SFI of the Transaction Log file and its number of records");
    public static final EMVTag MERCHANT_NAME_AND_LOCATION              = new EMVTag("9f4e", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.ALPHANUMERIC_SPECIAL, "Merchant Name and Location", "Indicates the name and location of the merchant");
    public static final EMVTag LOG_FORMAT                              = new EMVTag("9f4f", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "Log Format", "List (in tag and length format) of data objects representing the logged data elements that are passed to the terminal when a transaction log record is read");

    public static final EMVTag FCI_ISSUER_DISCRETIONARY_DATA           = new EMVTag("bf0c", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.VARIABLE, "File Control Information (FCI) Issuer Discretionary Data", "Issuer discretionary part of the FCI (e.g. O/S Manufacturer proprietary data)");

    //'9F50' to '9F7F' are reserved for the payment systems (proprietary)
    public static final EMVTag TERMINAL_TRANSACTION_QUALIFIERS         = new EMVTag("9f66", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Terminal Transaction Qualifiers", "Provided by the reader in the GPO command and used by the card to determine processing choices based on reader functionality");
    public static final EMVTag TRACK2_DATA                             = new EMVTag("9f6b", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Track 2 Data", "Track 2 Data contains the data objects of the track 2 according to [ISO/IEC 7813] Structure B, excluding start sentinel, end sentinel and LRC.");
    public static final EMVTag VLP_ISSUER_AUTHORISATION_CODE           = new EMVTag("9f6e", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Visa Low-Value Payment (VLP) Issuer Authorisation Code", "");
    public static final EMVTag EXTENDED_SELECTION                      = new EMVTag("9f29", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "Indicates the card's preference for the kernel on which the contactless application can be processed", "");
    public static final EMVTag KERNEL_IDENTIFIER                       = new EMVTag("9f2a", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "The value to be appended to the ADF Name in the data field of the SELECT command, if the Extended Selection Support flag is present and set to 1", "");

    public static final EMVTag TAG_9F6C  = new EMVTag("9f6c", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "卡片交易属性", "");
    public static final EMVTag TAG_9F5D  = new EMVTag("9f5d", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "可脱机消费金额", "");
    public static final EMVTag TAG_9F63  = new EMVTag("9f63", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "卡产品标识信息", "");
    public static final EMVTag TAG_9F74  = new EMVTag("9f74", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "电子现金发卡行授权码", "");
    public static final EMVTag TAG_9F79  = new EMVTag("9f79", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "电子现金余额", "");
    public static final EMVTag TAG_9F7B  = new EMVTag("9f7b", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "终端电子现金交易限额", "");


    // 90 tag
    public static final EMVTag TAG_90  = new EMVTag("90", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "", "");
    // null tag
    public static final EMVTag NULL_TAG  = new EMVTag("00", null ,EMVTag.TagFrom.FROM_IC, EMVTag.TagFormat.BINARY, "", "");

    static {
        Field[] fields;

        fields = EMVBuf.class.getFields();
        for (Field f : fields) {
            if (f.getType() == EMVTag.class) {
                try {
                    EMVTag t = (EMVTag) f.get(null);
                    mTlvMap.put(HexUtil.ByteArrayToHexString(t.getTagID()), t);

                    LogUtil.i(TAG," load tag:" + t.toString());

                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private static EMVBuf mEmvBuf = null;
    public static EMVBuf getInstance()
    {
        if(mEmvBuf == null)
        {
            mEmvBuf = new EMVBuf();
        }

        return  mEmvBuf;
    }

    public EMVTag findTag(String tag)
    {
        if( mTlvMap.containsKey(tag) )
        {
            return mTlvMap.get(tag);
        }else{
            LogUtil.w(TAG,"Can't Find Tag:" + tag);

            return null;
        }

    }

    public EMVTag findTag(int tag)
    {
        byte[] c4 = HexUtil.UnsignedIntToByte4(tag);

        String sTag = (c4[2] != 0x00) ? String.format("%02X%02X",c4[2],c4[3]) : String.format("%02X",c4[3]);

        return findTag(sTag);
    }

    public void setTagValue(String tag,byte[] value)
    {
        EMVTag t = findTag(tag);

        if( t != null )
        {
            t.setTagValue(value);
        }
    }

    public byte[] getTagValue(String tag)
    {
        if( findTag(tag) != null ) {
            return findTag(tag).getTagValue();
        }

        return null;
    }

    public void setTagValue(String tag,byte value)
    {
        byte[] bytes = { (byte) value};

        setTagValue(tag,bytes);
    }

    public void setTagValue(int tag,byte[] value)
    {
        byte[] c4 = HexUtil.UnsignedIntToByte4(tag);

        String sTag = (c4[2] != 0x00) ? String.format("%02X%02X",c4[2],c4[3]) : String.format("%02X",c4[3]);

        setTagValue(sTag,value);
    }

    public void setTagValue(int tag,byte value)
    {
        byte[] bytes = { (byte) value};

        setTagValue(tag,bytes);
    }

    public byte[] getTagValue(int tag)
    {
        byte[] c4 = HexUtil.UnsignedIntToByte4(tag);

        String sTag = (c4[2] != 0x00) ? String.format("%02X%02X",c4[2],c4[3]) : String.format("%02X",c4[3]);

        return getTagValue(sTag);
    }


    public void setTransAmount(int iCash , int iCashBack)
    {
        byte[] buf = null;

        // 9f04 返现金额 binary
        buf = HexUtil.UnsignedIntToByte4 (iCashBack);
        setTagValue(0x9F04, buf);

        // 9f03 返现金额 number
        String Cashback =  String.format("%012d", iCashBack);
        buf = HexUtil.HexStringToByteArray(Cashback);
        setTagValue(0x9F03, buf);

        // 81 授权金额 binary
        iCash += iCashBack;
        buf = HexUtil.UnsignedIntToByte4 (iCash);
        setTagValue(0x81, buf);

        // 9f02 授权金额 number
        String Cash =  String.format("%012d", iCash);
        buf = HexUtil.HexStringToByteArray(Cash);
        setTagValue(0x9F02, buf);
    }


    public void setTransDate()
    {
        //Tag 9A 03 eg. 150206
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        String date = df.format(new Date());

        setTagValue(0x9A,HexUtil.HexStringToByteArray(date));
    }

    public void setTransTime()
    {
        //Tag 9F21 03 eg. 160322
        SimpleDateFormat df = new SimpleDateFormat("HHmmss");
        String time = df.format(new Date());

        setTagValue(0x9F21,HexUtil.HexStringToByteArray(time));
    }

    public void setDateTime()
    {
        setTransDate();
        setTransTime();
    }

    public void setUnpredictableNumber(byte[] random)
    {
        //Tag 9F37 04 eg.ED4ED21C
        setTagValue(0x9F37,random);
    }

    public boolean tlv2buf(EMVTlv tlv)
    {
        TLVTag tlvTag = null;
        byte[] btag = null;

        while (tlv.hasNext())
        {
            tlvTag =  tlv.next();

            if(findTag(tlvTag.tag) == null)
            {
                LogUtil.i(TAG,"tlv2buf - Buf No Tag [" + tlvTag.tag + "]");
                return false;
            }

            setTagValue(tlvTag.tag,tlvTag.value);
            LogUtil.i(TAG,"tlv2buf - [" + tlvTag.tag + "] - [" + ((tlvTag.value == null) ? "null" : HexUtil.ByteArrayToHexString(tlvTag.value)) + "]");

            btag = HexUtil.HexStringToByteArray(tlvTag.tag);

            if(EMVTag.isConstructed(btag[0]))
            {
                tlv2buf(new EMVTlv(tlvTag.value));
            }
        }

        return true;
    }

    public void clear()
    {

    }
}
