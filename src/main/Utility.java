package main;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Utility
{
    public static void ispisiRezultSet(ResultSet r) throws SQLException
    {
        ResultSetMetaData meta = r.getMetaData();
        int columnCount = meta.getColumnCount();
        String[] names = new String[columnCount];
        for(int i=1; i<=columnCount; i++)
            names[i-1] = meta.getColumnLabel(i);
        
        while(r.next())
        {
            for(int i=1; i<=columnCount; i++)
                System.out.print( (i!=1?", ":"") + names[i-1]+" : "+r.getString(i));
            System.out.println();
        }
    }
    
    public static void ispisiRedRezultSeta(ResultSet r) throws SQLException
    {
        ResultSetMetaData m = r.getMetaData();
        int columnCount = m.getColumnCount();
        for(int i=1; i<=columnCount; i++)
            System.out.print( (i!=1?", ":"")+m.getColumnLabel(i)+" : "+r.getString(i) );
        System.out.println();
    }
    
    public static String tabelaStranogKluca(ResultSet r, String straniKljuc) throws SQLException
    {
        ResultSetMetaData m = r.getMetaData();
        int columnCount = m.getColumnCount();
        
        int straniKljucId = -1;
        for(int i=1; i<=columnCount; i++)
        {
            if( m.getColumnLabel(i).equals(straniKljuc))
            {
                straniKljucId = i;
                break;
            }
        }
        if(straniKljucId==-1)
            return null;
        
        String tableName = m.getTableName(straniKljucId);
        
        ResultSet importedKeys = r.getStatement().getConnection().getMetaData().getImportedKeys(null, null, tableName);
        
        String foreignTableName = null;
        while( importedKeys.next() )
        {
            String columnaName = importedKeys.getString("FKCOLUMN_NAME");
            if( !columnaName.equals(straniKljuc) )
                continue;
            foreignTableName = importedKeys.getString("PKTABLE_NAME");
//            ispisiRedRezultSeta(importedKeys);
        }
        
        return foreignTableName;
    }


}
