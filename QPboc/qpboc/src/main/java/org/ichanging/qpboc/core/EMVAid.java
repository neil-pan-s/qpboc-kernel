package org.ichanging.qpboc.core;

import androidx.annotation.NonNull;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.FileUtil;
import org.ichanging.qpboc.util.HexUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by ChangingP on 16/6/16.
 */
public class EMVAid
{
    public byte[] _aid;         	        // 4F(ICC), 9F06(Terminal), b, 5-16 bytes
    public byte   _app_sel_indicator;	    // ASI Application selection indicator to indicate whether the AID on the terminal matches the AID in the card or is partially matched when the application is selected
    public byte[] _app_ver;                 // 9F09(Terminal), b, 2 bytes ，Application version number
    public byte[] _tac_default;		        // DF11Terminal behavior code - default
    public byte[] _tac_denial;		        // DF13Terminal behavior code - rejection
    public byte[] _tac_online;		        // DF12Terminal behavior code - online
    public byte[] _floorlimit;              // 9F1B(Terminal), b, 4 bytes ，Terminal minimum
    public byte[] _threshold_value;         // DF15Offset randomly selected threshold
    public byte   _max_target_percent;	    // DF16Offset randomly selected maximum target percentage
    public byte   _target_percent;		    // DF17Random selection of target percentage
    public byte[] _default_ddol;            // DF14Default Dynamic Data Authentication Data Object List (DDOL)
    public byte   _online_pin_indicator;	// DF18Terminal online PIN support capability
    public byte[] _ec_trans_limit;			// 9F7B,Terminal electronic cash transaction limit
    public byte[] _qpboc_offline_limit; 	// DF19,Contactless reader offline minimum
    public byte[] _qpboc_trans_limit;		// DF20,Contactless reader transaction limit
    public byte[] _qpboc_cvm_limit;			// DF21,Limitations required for the reader card verification method (CVM)
    public byte[] _trans_type_support;      // The transaction type supported by this AID

    public static final String _aids_folder     = "aids/";      //AID list file directory
    private static final String TAG = "EMVAID";

    private String mDataPath = null;

    public static EMVAid genetate4Filed62(byte[] field62)
    {

        /*
        9F 06  08  A0 00 00 03 33 01 01 01 	// AID
        DF 01 01 00 	// Application selection indicator (ASI)
        9F 08 02 00 20  //Application version number
        DF 11 05 D8 40 00 A8 00  //TAC－Default
        DF 12 05 D8 40 04 F8 00  //TAC－Online
        DF 13 05 00 10 00 00 00	//TAC－Reject
        9F 1B 04 00 00 00 01 		//Terminal minimum
        DF 15 04 00 00 00 00 		//Offset randomly selected threshold
        DF 16 01 99 				//Offset randomly selected maximum target percentage
        DF 17 01 99 				//Randomly selected target percentage
        DF 14 03  		//缺省DDOL
        9F 37 04	//终端不可预知数
        DF 18 01 01 	//终端联机PIN支持能力  支持联机PIN
        9F 7B 06 00 00 00 10 00 00 	//Terminal electronic cash transaction limit
        DF 19 06 00 00 00 10 00 00 	//Contactless reader offline minimum
        DF 20 06 00 00 00 10 00 00 	//Contactless reader transaction limit
        DF 21 06 00 00 00 10 00 00	//读写器持卡人验证方法（CVM）所需限制	如果非接触交易超过此值，读写器要求一个持卡人验证方法（CVM）。
        */

        if(field62 == null) return null;

        EMVAid aid = new EMVAid();
        EMVTlv tlv = new EMVTlv(field62);
        TLVTag tag;

        if(!tlv.validTlv())
        {
            LogUtil.i(TAG,"Genetate EMVAID Fail - field62 [ " + HexUtil.ByteArrayToHexString(field62) + " ]");
            return null;
        }


        //Get the app AID
        LogUtil.i(TAG,"Genetate EMVAID - Parse 0x9F06 Apply AID");
        if((tag = tlv.childTag("9F06")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing 9F06");
            return null;
        }
        aid._aid = tag.value;

        LogUtil.i(TAG,"Genetate EMVAID - Parsing 0xDF01 app selection indicator");
        if((tag = tlv.childTag("DF01")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF01");
            return null;
        }

        if(tag.value[0] == 1)
        {
            aid._app_sel_indicator = 0;
        }
        else
        {
            aid._app_sel_indicator = 1;
        }

        //Get the app version number
        LogUtil.i(TAG,"Genetate EMVAID - Parsing 0x9F08 application version number");
        if((tag = tlv.childTag("9F08")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing 9F08");
            return null;
        }
        aid._app_ver = tag.value;

        //TAC default
        LogUtil.i(TAG,"Genetate EMVAID -Parse 0xDF11 TAC default");
        if((tag = tlv.childTag("DF11")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF11");
            return null;
        }
        aid._tac_default = tag.value;

        //TAC refused
        LogUtil.i(TAG,"Genetate EMVAID - Resolution 0xDF13 TAC rejection");
        if((tag = tlv.childTag("DF13")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF13");
            return null;
        }
        aid._tac_denial = tag.value;

        //TAC online
        LogUtil.i(TAG,"Genetate EMVAID - Resolution 0xDF12 TAC connection");
        if((tag = tlv.childTag("DF12")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF12");
            return null;
        }
        aid._tac_online = tag.value;

        //floor limit , may be empty
        LogUtil.i(TAG,"Genetate EMVAID - Resolve 0x9F1B terminal minimum");
        if((tag = tlv.childTag("9F1B")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing 9F1B");
            //return null;
        }else {
            aid._floorlimit = tag.value;
        }

        //threshold value
        LogUtil.i(TAG,"Genetate EMVAID - Parse 0xDF15 Offset Randomly Selected Threshold");
        if((tag = tlv.childTag("DF15")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF15");
            return null;
        }
        aid._threshold_value = tag.value;

        //max target percent
        LogUtil.i(TAG,"Genetate EMVAID - Parse 0xDF16 Offset Randomly Selected Maximum Target Percentage");
        if((tag = tlv.childTag("DF16")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF16");
            return null;
        }
        aid._max_target_percent = (byte) Integer.parseInt(HexUtil.ByteArrayToHexString(tag.value));

        //target percent
        LogUtil.i(TAG,"Genetate EMVAID - Parse 0xDF17 randomly selected target percentage");
        if((tag = tlv.childTag("DF17")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF17");
            return null;
        }
        aid._target_percent = (byte) Integer.parseInt(HexUtil.ByteArrayToHexString(tag.value));

        //default ddol
        LogUtil.i(TAG,"Genetate EMVAID - Parse 0xDF14 default DDOL");
        if((tag = tlv.childTag("DF14")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF14");
            return null;
        }
        aid._default_ddol = tag.value;

        //pos entry
        LogUtil.i(TAG,"Genetate EMVAID - Analyze 0xDF18 terminal online PIN support capability");
        if((tag = tlv.childTag("DF18")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF18");
            return null;
        }
        aid._online_pin_indicator = tag.value[0];


        //Electronic cash parameters
        //Terminal electronic cash transaction limit
        LogUtil.i(TAG,"Genetate EMVAID - Analyze 0x9F7B terminal electronic cash transaction limit");
        if((tag = tlv.childTag("9F7B")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing 9F7B");
            //return null;
        }else {
            aid._ec_trans_limit = tag.value;
        }

        //QPBOC parameters
        //Contactless reader offline minimum
        LogUtil.i(TAG,"Genetate EMVAID - Resolve 0xDF19 contactless reader offline minimum");
        if((tag = tlv.childTag("DF19")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF19");
            //return null;
        }else {
            aid._qpboc_offline_limit = tag.value;
        }

        //Contactless reader transaction limit
        LogUtil.i(TAG,"Genetate EMVAID - Resolve 0xDF20 contactless reader transaction limit");
        if((tag = tlv.childTag("DF20")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF20");
            //return null;
        }else {
            aid._qpboc_trans_limit = tag.value;
        }


        //Limitations required for the reader card verification method (CVM)
        LogUtil.i(TAG,"Genetate EMVAID - Resolve the restrictions required by the 0xDF21 Reader Cardholder Verification Method (CVM)");
        if((tag = tlv.childTag("DF21")) == null || tag.value == null)
        {
            LogUtil.i(TAG,"Genetate EMVAID - Missing DF21");
            //return null;
        }else {
            aid._qpboc_cvm_limit = tag.value;
        }

        return aid;
    }

    public static EMVAid genetateFromFile(String path)
    {
        byte[] data = FileUtil.readAidFile(path);

        if(data == null)
        {
            return null;
        }

        return json2aid(new String(data));
    }


    public void setDataPath(String path)
    {
        mDataPath = path;
    }

    public void save()
    {
        String name = getAidFilePath();
        String data = aid2json();

        File file = new File( mDataPath + "/" + _aids_folder );
        if(!file.exists())
        {
            file.mkdir();
        }

        LogUtil.i(TAG,"Save Aid to [ " + name + " ] - value - [ " + data + " ]");

        FileUtil.writeAidFile(name,data.getBytes());
    }

    private String getAidFilePath()
    {
        return  mDataPath + "/" + _aids_folder + HexUtil.ByteArrayToHexString(_aid) + ".file";
    }

    private String aid2json()
    {

        JSONObject json =  new JSONObject();

        try {
            json.put("_aid",HexUtil.ByteArrayToHexString(_aid));
            json.put("_app_sel_indicator",_app_sel_indicator);
            json.put("_app_ver",HexUtil.ByteArrayToHexString(_app_ver));
            json.put("_tac_default",HexUtil.ByteArrayToHexString(_tac_default));
            json.put("_tac_denial",HexUtil.ByteArrayToHexString(_tac_denial));
            json.put("_tac_online",HexUtil.ByteArrayToHexString(_tac_online));
            json.put("_floorlimit",HexUtil.ByteArrayToHexString(_floorlimit));
            json.put("_threshold_value",HexUtil.ByteArrayToHexString(_threshold_value));
            json.put("_max_target_percent",_max_target_percent);
            json.put("_target_percent",_target_percent);
            json.put("_default_ddol",HexUtil.ByteArrayToHexString(_default_ddol));
            json.put("_online_pin_indicator", _online_pin_indicator);
            json.put("_ec_trans_limit",HexUtil.ByteArrayToHexString(_ec_trans_limit));
            json.put("_qpboc_offline_limit",HexUtil.ByteArrayToHexString(_qpboc_offline_limit));
            json.put("_qpboc_trans_limit",HexUtil.ByteArrayToHexString(_qpboc_trans_limit));
            json.put("_qpboc_cvm_limit",HexUtil.ByteArrayToHexString(_qpboc_cvm_limit));
            json.put("_trans_type_support",HexUtil.ByteArrayToHexString(_trans_type_support));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    private static EMVAid json2aid(String data)
    {
        EMVAid aid = new EMVAid();

        try {
            JSONObject json =  new JSONObject(data);

            aid._aid = HexUtil.HexStringToByteArray((String)json.get("_aid"));
            aid._app_sel_indicator = (byte) (json.getInt("_app_sel_indicator") & 0xFF);
            aid._app_ver = HexUtil.HexStringToByteArray((String)json.get("_app_ver"));
            aid._tac_default = HexUtil.HexStringToByteArray((String)json.get("_tac_default"));
            aid._tac_denial = HexUtil.HexStringToByteArray((String)json.get("_tac_denial"));
            aid._tac_online = HexUtil.HexStringToByteArray((String)json.get("_tac_online"));
            aid._floorlimit = HexUtil.HexStringToByteArray((String)json.get("_floorlimit"));
            aid._threshold_value = HexUtil.HexStringToByteArray((String)json.get("_threshold_value"));
            aid._max_target_percent = (byte) (json.getInt("_max_target_percent") & 0xFF);
            aid._target_percent = (byte) ( json.getInt("_target_percent") & 0xFF );
            aid._default_ddol = HexUtil.HexStringToByteArray((String)json.get("_default_ddol"));
            aid._online_pin_indicator = (byte) ( json.getInt("_online_pin_indicator") & 0xFF );
            aid._ec_trans_limit = HexUtil.HexStringToByteArray((String)json.get("_ec_trans_limit"));
            aid._qpboc_offline_limit = HexUtil.HexStringToByteArray((String)json.get("_qpboc_offline_limit"));
            aid._qpboc_trans_limit = HexUtil.HexStringToByteArray((String)json.get("_qpboc_trans_limit"));
            aid._qpboc_cvm_limit = HexUtil.HexStringToByteArray((String)json.get("_qpboc_cvm_limit"));
            aid._trans_type_support = HexUtil.HexStringToByteArray((String)json.get("_trans_type_support"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return aid;
    }

    @Override
    @NonNull
    public String toString() {
        return aid2json();
    }
}
