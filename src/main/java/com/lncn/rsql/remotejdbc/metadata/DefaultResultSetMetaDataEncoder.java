package com.lncn.rsql.remotejdbc.metadata;


import com.google.protobuf.CodedOutputStream;
import com.lncn.rsql.Session;
import com.lncn.rsql.remotejdbc.encode.FastByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Classname DefaultResultSetMetaDataEncoder
 * @Description TODO
 * @Date 2022/7/8 15:11
 * @Created by byco
 */
public class DefaultResultSetMetaDataEncoder implements ResultSetMetaDataEncoder{
    private static final Logger log = LoggerFactory.getLogger(Session.class);
    @Override
    public byte[] encode(ResultSetMetaData metaData) throws SQLException, IOException {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(outputStream);
        int count = metaData.getColumnCount();
        output.writeUInt32NoTag(count);
        //log.debug("MetaData encode");
        for( int i = 1 ; i <= count ; i++ ){
            output.writeStringNoTag(metaData.getColumnLabel(i));
            output.writeStringNoTag(metaData.getColumnName(i));
            output.writeUInt32NoTag(metaData.getScale(i));
            output.writeUInt32NoTag(metaData.getColumnType(i));
//            log.debug("ColumnLabel {} , ColumnName {} ,Scale {} ,ColumnType {} ,,ColumnTypeName {}"
//                ,metaData.getColumnLabel(i)
//                ,metaData.getColumnName(i)
//                ,metaData.getScale(i)
//                ,metaData.getColumnType(i)
//            ,metaData.getColumnTypeName(i) + "  " + metaData.getPrecision(i));
        }

        output.flush();
        return outputStream.toByteArray();
    }
}
