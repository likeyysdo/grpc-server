package com.lncn.rsql.remotejdbc.encode;


import com.google.protobuf.CodedOutputStream;
import com.lncn.rsql.remotejdbc.type.RemoteType;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * @Classname RemoteTypeConverter
 * @Description TODO
 * @Date 2022/7/6 15:48
 * @Created by byco
 */
public class DefaultResultRowEncoder implements ResultRowEncoder {
    private static final int DOUBLE_SCALE = 1000_000;

    protected static final HashMap<Integer, ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream>>
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
            z.writeSInt64NoTag(y.getLong(x));
        };
        encodeFloat = (x, y, z) -> {
            z.writeSInt64NoTag((long) (y.getFloat(x) * DOUBLE_SCALE));
        };
        encodeDouble = (x, y, z) -> {
            z.writeSInt64NoTag((long) (y.getDouble(x) * DOUBLE_SCALE));
        };
        encodeString = (x, y, z) -> {
            String s = y.getString(x);
            z.writeStringNoTag(s == null ? "":s);
        };
        encodeBoolean = (x, y, z) -> {
            z.writeBoolNoTag(y.getBoolean(x));
        };
        encodeBigDecimal = (x, y, z) -> {
            BigDecimal s = y.getBigDecimal(x);
            long r = 0;
            if( s != null  ){
                r = s.unscaledValue().longValue();
            }
            z.writeSInt64NoTag(r);
        };
        encodeDate = (x, y, z) -> {
            java.sql.Date s = y.getDate(x);
            long r = 7L;
            if( s != null ) {
                r = s.getTime();
            }
            z.writeUInt64NoTag(r);
        };
        encodeTime = (x, y, z) -> {
            java.sql.Time s = y.getTime(x);
            long r = 7L;
            if( s != null ) {
                r = s.getTime();
            }
            z.writeUInt64NoTag(r);
        };
        encodeTimestamp = (x, y, z) -> {
            java.sql.Timestamp s = y.getTimestamp(x);
            long r = 7L;
            if( s != null ) {
                r = s.getTime();
            }
            z.writeUInt64NoTag(r);
        };
        encodeByteArray = (x, y, z) -> {
            byte[] s = y.getBytes(x);
            z.writeByteArrayNoTag(s == null ? new byte[0]:s);
        };

        directEncodeMap = new HashMap<>(27);
        directEncodeMap.put(RemoteType.CHAR.jdbcType, DefaultResultRowEncoder.encodeString);
        directEncodeMap.put(RemoteType.VARCHAR.jdbcType, DefaultResultRowEncoder.encodeString);
        directEncodeMap.put(RemoteType.LONGVARCHAR.jdbcType, DefaultResultRowEncoder.encodeString);
        directEncodeMap.put(RemoteType.NUMERIC.jdbcType, DefaultResultRowEncoder.encodeBigDecimal);
        directEncodeMap.put(RemoteType.DECIMAL.jdbcType, DefaultResultRowEncoder.encodeBigDecimal);
        directEncodeMap.put(RemoteType.BIT.jdbcType, DefaultResultRowEncoder.encodeBoolean);
        directEncodeMap.put(RemoteType.BOOLEAN.jdbcType, DefaultResultRowEncoder.encodeBoolean);
        directEncodeMap.put(RemoteType.TINYINT.jdbcType, DefaultResultRowEncoder.encodeInteger);
        directEncodeMap.put(RemoteType.SMALLINT.jdbcType, DefaultResultRowEncoder.encodeInteger);
        directEncodeMap.put(RemoteType.INTEGER.jdbcType, DefaultResultRowEncoder.encodeInteger);
        directEncodeMap.put(RemoteType.BIGINT.jdbcType, DefaultResultRowEncoder.encodeLong);
        directEncodeMap.put(RemoteType.REAL.jdbcType, DefaultResultRowEncoder.encodeFloat);
        directEncodeMap.put(RemoteType.FLOAT.jdbcType, DefaultResultRowEncoder.encodeDouble);
        directEncodeMap.put(RemoteType.DOUBLE.jdbcType, DefaultResultRowEncoder.encodeDouble);
        directEncodeMap.put(RemoteType.BINARY.jdbcType, DefaultResultRowEncoder.encodeByteArray);
        directEncodeMap.put(RemoteType.VARBINARY.jdbcType, DefaultResultRowEncoder.encodeByteArray);
        directEncodeMap.put(RemoteType.LONGVARBINARY.jdbcType,
            DefaultResultRowEncoder.encodeByteArray);
        directEncodeMap.put(RemoteType.DATE.jdbcType, DefaultResultRowEncoder.encodeDate);
        directEncodeMap.put(RemoteType.TIME.jdbcType, DefaultResultRowEncoder.encodeTime);
        directEncodeMap.put(RemoteType.TIMESTAMP.jdbcType, DefaultResultRowEncoder.encodeTimestamp);

        directEncodeMap.put(RemoteType.NCHAR.jdbcType, DefaultResultRowEncoder.encodeString);
        directEncodeMap.put(RemoteType.NVARCHAR.jdbcType, DefaultResultRowEncoder.encodeString);
        directEncodeMap.put(RemoteType.LONGNVARCHAR.jdbcType, DefaultResultRowEncoder.encodeString);

    }

    public ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> getEncoder(Integer i) {
        return DefaultResultRowEncoder.directEncodeMap.get(i);
    }

}
