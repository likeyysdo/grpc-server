package com.lncn.rsql.remotejdbc.metadata;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @Classname DefaultResultSetMetaDataAdapter
 * @Description TODO
 * @Date 2022/8/11 13:09
 * @Created by byco
 */
public class DefaultResultSetMetaDataAdapter {
    private ResultSetMetaData resultSetMetaData;
    private DefaultResultSetMetaDataAdapter(){

    }
    public static DefaultResultSetMetaDataAdapter newInstance() {
         return new DefaultResultSetMetaDataAdapter();
    }
    public DefaultResultSetMetaDataAdapter setSource( ResultSetMetaData resultSetMetaData ){
        this.resultSetMetaData = resultSetMetaData;
        return this;
    }
    public ResultSetMetaData build() throws SQLException {
        ResultSetMetaData metaData = resultSetMetaData;
        int count = metaData.getColumnCount();
        Field[] curFields = new Field[count];
        for( int i = 1 ; i <= count ; i++){
            String columnLabel = metaData.getColumnLabel(i);
            String columnName = metaData.getColumnName(i);
            int scale = metaData.getScale(i);
            int columnType = metaData.getColumnType(i);
            if( columnType == Types.NUMERIC && scale == -127 ){
                //Oracle Float is JDBC Type NUMERIC with -127 scale
                columnType = Types.FLOAT;
                scale = 0;
            }
            Field curField = new Field(columnLabel
                ,columnName
                ,scale
                ,columnType);
            curFields[i-1] =curField;
        }
        return new DefaultResultSetMetaData(curFields);
    }

}
