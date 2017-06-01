package org.ichanging.qpboc.core;

import org.ichanging.qpboc.platform.LogUtil;
import org.ichanging.qpboc.util.HexUtil;

import java.util.Enumeration;

/**
 * Created by ChangingP on 16/6/12.
 */
public class EMVTag {

    private static final String TAG = "EMVTag";

    /**
     * Seem EMV Book 3 Annex B
     *
     */

    /**
     * tagName b6 代表的是数据元结构
     * = 0 简单数据元结构
     * = 1 结构(复合)数据元结构
     */
    public static enum TagType {

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
     * tagName b8,b7 代表数据的类别
     * 根据2个位的组合,有四种类别:通用类别,应用类别,上下文语境类别,专用类别 主要用于在于终端设备交互的时候 确定数据处理的类型
     *
     */
    public static enum TagClass{
        UNIVERSAL, APPLICATION, CONTEXT_SPECIFIC, PRIVATE
    }

    /**
     * Tag From IC or Terminal
     *
     */
    public static enum TagFrom {
        FROM_IC,
        FROM_TERMINAL
    }

    /**
     * Tag Value Format
     *
     */
    public static enum TagFormat {

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
        VARIABLE;
    }

    private byte[] tagID = null;

    private byte[] tagStringID = null;

    private byte[] tagValue = null;

    private String tagName = "";

    private String tagDescribe = "";

    private TagFrom tagFrom = TagFrom.FROM_IC;

    private TagFormat tagFormat = TagFormat.BINARY;

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
            LogUtil.i(TAG,"Tag - [" + HexUtil.ByteArrayToHexString(tagID) + "] is Unique,Can't be override!");
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
        String str = null;

        byte[] tag = HexUtil.HexStringToByteArray(tagName);
        int utag = ((int)(tag[0] & 0xFF) << 8) | (tag[1] & 0xFF);

        switch (utag)
        {
            case 0x06:      str = "终端型号数据标签"; break;
            case 0x4F:      str = "应用标识符（AID）"; break;
            case 0x50:      str = "应用标签"; break;
            case 0x57:      str = "磁条2等效数据"; break;
            case 0x5A:      str = "应用主账号（PAN）"; break;
            case 0x5F20:    str = "持卡人姓名"; break;
            case 0x5F24:    str = "应用失效日期"; break;
            case 0x5F25:    str = "应用生效日期"; break;
            case 0x5F28:    str = "发卡行国家代码"; break;
            case 0x5F2D:    str = "首选语言"; break;
            case 0x5F30:    str = "服务码"; break;
            case 0x5F34:    str = "应用主账号序列号"; break;
            case 0x5F50:    str = "发卡行URL"; break;
            case 0x61:      str = "应用模板"; break;
            case 0x6F:      str = "文件控制信息 （FCI）模板"; break;
            case 0x72:      str = "发卡行脚本模板2"; break;
            case 0x73:      str = "目录自定义模板"; break;
            case 0x77:      str = "响应报文模板格式2"; break;
            case 0x80:      str = "响应报文模板格式1"; break;
            case 0x82:      str = "应用交互特征（AIP）"; break;
            case 0x84:      str = "专用文件（DF）名称"; break;
            case 0x86:      str = "发卡行脚本命令"; break;
            case 0x87:      str = "应用优先指示器"; break;
            case 0x88:      str = "短文件标识符 （SFI）"; break;
            case 0x8A:      str = "授权响应码"; break;
            case 0x8C:      str = "卡片风险管理数据对象列表1（CDOL1）"; break;
            case 0x8D:      str = "卡片风险管理数据对象列表 2（CDOL2）"; break;
            case 0x8E:      str = "持卡人验证方法（CVM）列表"; break;
            case 0x8F:      str = "CA公钥索引（PKI）"; break;
            case 0x90:      str = "发卡行公钥证书"; break;
            case 0x91:      str = "发卡行认证数据"; break;
            case 0x92:      str = "发卡行公钥余数"; break;
            case 0x93:      str = "签名的静态应用数据（SAD）"; break;
            case 0x94:      str = "应用文件定位器（AFL）"; break;
            case 0x97:      str = "交易证书数据对象"; break;
            case 0x9D:      str = "目录数据文件 （DDF）名称"; break;
            case 0x9F05:    str = "应用自定义数据"; break;
            case 0x9F07:    str = "应用用途控制"; break;
            case 0x9F08:    str = "应用版本号"; break;
            case 0x9F0B:    str = "持卡人姓名扩展"; break;
            case 0x9F0D:    str = "发卡行行为代码（IAC）-缺省"; break;
            case 0x9F0E:    str = "发卡行行为代码（IAC）-拒绝"; break;
            case 0x9F0F:    str = "发卡行行为代码（IAC）-联机"; break;
            case 0x9F10:    str = "发卡行应用数据"; break;
            case 0x9F11:    str = "发卡行代码表索引"; break;
            case 0x9F12:    str = "应用首选名称"; break;
            case 0x9F13:    str = "上次联机应用交易计数器（ATC）寄存器"; break;
            case 0x9F14:    str = "连续脱机交易下限"; break;
            case 0x9F17:    str = "PIN尝试计数器"; break;
            case 0x9F1F:    str = "磁条1自定义数据"; break;
            case 0x9F23:    str = "连续脱机交易上限"; break;
            case 0x9F26:    str = "应用密文（AC）"; break;
            case 0x9F27:    str = "密文信息数据"; break;
            case 0x9F32:    str = "发卡行公钥指数"; break;
            case 0x9F36:    str = "应用交易计数器"; break;
            case 0x9F38:    str = "处理选项数据对象列表（PDOL）"; break;
            case 0x9F42:    str = "应用货币代码"; break;
            case 0x9F44:    str = "应用货币指数"; break;
            case 0x9F45:    str = "数据认证码"; break;
            case 0x9F46:    str = "IC卡公钥证书"; break;
            case 0x9F47:    str = "IC卡公钥指数"; break;
            case 0x9F48:    str = "IC卡公钥余数"; break;
            case 0x9F49:    str = "动态数据认证数据对象列表（DDOL）"; break;
            case 0x9F4A:    str = "静态数据认证标签列表"; break;
            case 0x9F4B:    str = "签名的动态应用数据"; break;
            case 0x9F4C:    str = "IC动态数"; break;
            case 0x9F4D:    str = "日志入口"; break;
            case 0x9F4F:    str = "日志格式"; break;
            case 0x9F51:    str = "应用货币代码"; break;
            case 0x9F52:    str = "应用缺省行为（ADA）"; break;
            case 0x9F53:    str = "连续脱机交易限制数（国际-货币）"; break;
            case 0x9F54:    str = "累计脱机交易金额限制数"; break;
            case 0x9F56:    str = "发卡行认证指示位"; break;
            case 0x9F57:    str = "发卡行国家代码"; break;
            case 0x9F58:    str = "连续脱机交易下限"; break;
            case 0x9F59:    str = "连续脱机交易上限"; break;
            case 0x9F5A:    str = "发卡行URL2"; break;
            case 0x9F5C:    str = "累计脱机交易金额上限"; break;
            case 0x9F61:    str = "持卡人证件号"; break;
            case 0x9F62:    str = "持卡人证件类型"; break;
            case 0x9F63:    str = "卡产品标识信息"; break;
            case 0x9F72:    str = "连续脱机交易限制数（国际-国家）"; break;
            case 0x9F73:    str = "货币转换因子"; break;
            case 0x9F75:    str = "累计脱机交易金额限制数（双货币）"; break;
            case 0x9F76:    str = "第2应用货币"; break;
            case 0xA5:      str = "文件控制信息 （FCI）专用模板"; break;
            case 0xBF0C:    str = "文件控制信息 （FCI）发卡行自定义数据"; break;

            // 交易明细记录文件内容
            case 0x9A:      str = "交易日期"; break;
            case 0x9F21:    str = "交易时间"; break;
            case 0x9F02:    str = "授权金额"; break;
            case 0x9F03:    str = "其它金额"; break;
            case 0x9F1A:    str = "终端国家代码"; break;
            case 0x5F2A:    str = "交易货币代码"; break;
            case 0x9F4E:    str = "商户名称"; break;
            case 0x9C:      str = "交易类型"; break;
            case 0x9B:      str = "交易状态信息(TSI)"; break;
            case 0x95:      str = "终端验证结果(TVR)"; break;
            case 0x9F01	:   str = "收单行标识"; break;
            case 0x9F15	:   str = "商户分类码"; break;
            case 0x9F16	:   str = "商户标识"; break;
            case 0x9F1C	:   str = "终端标识"; break;
            case 0x9F1E	:   str = "接口设备序列号"; break;
            case 0x9F33	:   str = "终端性能"; break;
            case 0x9F34	:   str = "持卡人认证结果"; break;
            case 0x9F35	:   str = "终端类型"; break;
            case 0x9F37	:   str = "终端随机数"; break;
            case 0x9F39	:   str = "POS Entry Mode"; break;
            case 0xDF31	:   str = "脚本执行结果"; break;
            case 0xDF41	:   str = "强制接受标识"; break;
            case 0xDF51	:   str = "联机密文"; break;
            case 0x9F66:    str = "终端交易属性"; break;
            case 0x9F6C:    str = "卡片交易属性"; break;
            case 0x9F5D:    str = "可脱机消费金额"; break;
            case 0x9F41:    str = "终端流水号"; break;

            case 0x9F06: str = "应用AID"; break;
            case 0x9F22: str = "公钥索引"; break;
            case 0xDF05: str = "公钥有效期"; break;
            case 0xDF06: str = "公钥哈什算法标识"; break;
            case 0xDF07: str = "公钥算法标识"; break;
            case 0xDF02: str = "公钥模"; break;
            case 0xDF04: str = "公钥指数"; break;
            case 0xDF03: str = "公钥校验值"; break;
            case 0xDF01: str = "应用选择指示符"; break;
            case 0xDF11: str = "TAC缺省"; break;
            case 0xDF12: str = "TAC联机"; break;
            case 0xDF13: str = "TAC拒绝"; break;
            case 0x9F1B: str = "终端最低限额"; break;
            case 0xDF15: str = "偏置随机选择的阈值"; break;
            case 0xDF16: str = "偏置随机选择的最大目标百分数"; break;
            case 0xDF17: str = "随机选择的目标百分数"; break;
            case 0xDF14: str = "缺省DDOL"; break;
            case 0xDF18: str = "终端联机PIN支持能力"; break;
            case 0x9F7B: str = "终端电子现金交易限额"; break;
            case 0xDF19: str = "非接触读写器脱机最低限额"; break;
            case 0xDF20: str = "非接触读写器交易限额"; break;
            case 0xDF21: str = "非接触读写器CVM限额"; break;

            default:
                str = String.format("未定义tag[%04X]", utag);
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
    public String toString() {

        String strClass = "",strValue = "";

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
