package baza;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import static main.Utility.*;

public class ReferencedTable
{
    private String columnName;
    private String columnaLabel;
    private String selectColumns = " * ";   // Part of the SQL query, where you choose columns to be selected
    private String referencedTableName;
    
    public ReferencedTable(String columnName)
    {
        this.columnName = columnName;
    }

    
    public String createQuery(ResultSet r) throws SQLException
    {
        String data = r.getString(columnName);
        String sql = "SELECT " + selectColumns +" FROM " + referencedTableName + " WHERE " + columnName + " = " + data;
        return sql;
    }
    
    public void prepare(ResultSet r) throws SQLException
    {
        if(columnaLabel==null)
            columnaLabel = columnName;
        
        ResultSetMetaData m = r.getMetaData();
        int columnCount = m.getColumnCount();
        
        int straniKljucId = -1;
        for(int i=1; i<=columnCount; i++)
        {
            if( m.getColumnName(i).equals(columnName))
            {
                straniKljucId = i;
                break;
            }
        }
        if(straniKljucId==-1)
        {
            referencedTableName = null;
            return;
        }
        
        String tableName = m.getTableName(straniKljucId);
        
        ResultSet importedKeys = r.getStatement().getConnection().getMetaData().getImportedKeys(null, null, tableName);
        
        String foreignTableName = null;
        while( importedKeys.next() )
        {
//            ispisiRedRezultSeta(importedKeys);
            
            String columnaName = importedKeys.getString("FKCOLUMN_NAME");
            if( !columnaName.equals(columnName) )
                continue;
            foreignTableName = importedKeys.getString("PKTABLE_NAME");
//            ispisiRedRezultSeta(importedKeys);
        }
        
        this.referencedTableName = foreignTableName;
    }
    
    public String toString()
    {
        return "Column name: "+columnName+" Column label: "+columnaLabel+" select columns '" + selectColumns+" ' "
                + "Referenced table name: " + referencedTableName;  
    }
    
    public String getColumnName()
    {
        return columnName;
    }

    public void setColumnName(String columnName)
    {
        this.columnName = columnName;
    }

    public String getColumnaLabel()
    {
        return columnaLabel;
    }

    public void setColumnaLabel(String columnaLabel)
    {
        this.columnaLabel = columnaLabel;
    }

    public String getSelectColumns()
    {
        return selectColumns;
    }

    public void setSelectColumns(String selectColumns)
    {
        this.selectColumns = selectColumns;
    }

    public String getReferencedTableName()
    {
        return referencedTableName;
    }
    
    
}
