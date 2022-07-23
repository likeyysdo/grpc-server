package com.lncn.rsql.remotejdbc.encode;


import com.google.protobuf.CodedOutputStream;
import com.lncn.rsql.remotejdbc.type.RemoteType;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * @Classname RemoteTypeConverter
 * @Description TODO
 * @Date 2022/7/6 15:48
 * @Created by byco
 */
public class RawDoubleResultRowEncoder implements ResultRowEncoder {

    private static final HashMap<Integer, ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream>>
        directEncodeMap;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeInteger;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeLong;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeFloat;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeDouble;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeString;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeBoolean;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeBigDecimal;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeDate;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeTime;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeTimestamp;
    private static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeByteArray;

    static {
        encodeInteger = (x, y, z) -> {
            z.writeSInt32NoTag(y.getInt(x));
        };
        encodeLong = (x, y, z) -> {
            z.writeSInt64NoTag(y.getInt(x));
        };
        encodeFloat = (x, y, z) -> {
            z.writeFloatNoTag(y.getFloat(x));
        };
        encodeDouble = (x, y, z) -> {
            z.writeDoubleNoTag(y.getDouble(x));
        };
        encodeString = (x, y, z) -> {
            String s = y.getString(x);
            z.writeStringNoTag(s == null ? "":s);
        };
        encodeBoolean = (x, y, z) -> {
            z.writeBoolNoTag(y.getBoolean(x));
        };
        encodeBigDecimal = (x, y, z) -> {
            z.writeSInt64NoTag(y.getBigDecimal(x).unscaledValue().longValue());
        };
        encodeDate = (x, y, z) -> {
            z.writeUInt64NoTag(y.getDate(x).getTime());
        };
        encodeTime = (x, y, z) -> {
            z.writeUInt64NoTag(y.getDate(x).getTime());
        };
        encodeTimestamp = (x, y, z) -> {
            z.writeUInt64NoTag(y.getDate(x).getTime());
        };
        encodeByteArray = (x, y, z) -> {
            z.writeByteArrayNoTag(y.getBytes(x));
        };

        directEncodeMap = new HashMap<>(27);
        directEncodeMap.put(RemoteType.CHAR.jdbcType, RawDoubleResultRowEncoder.encodeString);
        directEncodeMap.put(RemoteType.VARCHAR.jdbcType, RawDoubleResultRowEncoder.encodeString);
        directEncodeMap.put(RemoteType.LONGVARCHAR.jdbcType, RawDoubleResultRowEncoder.encodeString);
        directEncodeMap.put(RemoteType.NUMERIC.jdbcType, RawDoubleResultRowEncoder.encodeBigDecimal);
        directEncodeMap.put(RemoteType.DECIMAL.jdbcType, RawDoubleResultRowEncoder.encodeBigDecimal);
        directEncodeMap.put(RemoteType.BIT.jdbcType, RawDoubleResultRowEncoder.encodeBoolean);
        directEncodeMap.put(RemoteType.BOOLEAN.jdbcType, RawDoubleResultRowEncoder.encodeBoolean);
        directEncodeMap.put(RemoteType.TINYINT.jdbcType, RawDoubleResultRowEncoder.encodeInteger);
        directEncodeMap.put(RemoteType.SMALLINT.jdbcType, RawDoubleResultRowEncoder.encodeInteger);
        directEncodeMap.put(RemoteType.INTEGER.jdbcType, RawDoubleResultRowEncoder.encodeInteger);
        directEncodeMap.put(RemoteType.BIGINT.jdbcType, RawDoubleResultRowEncoder.encodeLong);
        directEncodeMap.put(RemoteType.REAL.jdbcType, RawDoubleResultRowEncoder.encodeFloat);
        directEncodeMap.put(RemoteType.FLOAT.jdbcType, RawDoubleResultRowEncoder.encodeDouble);
        directEncodeMap.put(RemoteType.DOUBLE.jdbcType, RawDoubleResultRowEncoder.encodeDouble);
        directEncodeMap.put(RemoteType.BINARY.jdbcType, RawDoubleResultRowEncoder.encodeByteArray);
        directEncodeMap.put(RemoteType.VARBINARY.jdbcType, RawDoubleResultRowEncoder.encodeByteArray);
        directEncodeMap.put(RemoteType.LONGVARBINARY.jdbcType,
            RawDoubleResultRowEncoder.encodeByteArray);
        directEncodeMap.put(RemoteType.DATE.jdbcType, RawDoubleResultRowEncoder.encodeDate);
        directEncodeMap.put(RemoteType.TIME.jdbcType, RawDoubleResultRowEncoder.encodeTime);
        directEncodeMap.put(RemoteType.TIMESTAMP.jdbcType, RawDoubleResultRowEncoder.encodeTimestamp);
    }

    public ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> getEncoder(Integer i) {
        return RawDoubleResultRowEncoder.directEncodeMap.get(i);
    }

}
