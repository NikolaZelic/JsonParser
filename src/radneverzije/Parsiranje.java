package radneverzije;

import database.ManyToOneSelectData;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import static main.Utility.*;

public class Parsiranje
{
    private Connection connection;
    private Parsiranje() throws SQLException
    {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/primer", "root", "");
    }
    
    private String parsirajProstUpit(String query) throws SQLException
    {
        ResultSet r = connection.createStatement().executeQuery(query);
        int columnCount = r.getMetaData().getColumnCount();
        ResultSetMetaData m = r.getMetaData();
        StringBuilder bld = new StringBuilder();
        
        boolean first = true;
        boolean isArray = false;
        while(r.next())
        {
            if(first)
                first = false;
            else
            {
                bld.append(",");
                isArray = true;
            }
            bld.append("{");
            for(int i=1; i<=columnCount; i++)
                bld.append( i!=1?",":"" ).append("\"").append(m.getColumnLabel(i)).append("\":\"").append(r.getString(i)).append("\"");
            bld.append("}");
        }
        if(isArray)
        {
            bld.insert(0, "[");
            bld.append("]");
        }
        return bld.toString();
    }
    
    private String parsirajSlozenUpit(String query, String table, String key) throws Exception
    {
        ResultSet r = connection.createStatement().executeQuery(query);
        int columnCount = r.getMetaData().getColumnCount();
        ResultSetMetaData m = r.getMetaData();
        StringBuilder bld = new StringBuilder();
        
        boolean first = true;
        boolean isArray = false;
        while(r.next())
        {
            if(first)
                first = false;
            else
            {
                bld.append(",");
                isArray = true;
            }
            
            bld.append("{");
            for(int i=1; i<=columnCount; i++)
            {
                // Parsiranje obicnog podatka
                bld.append( i!=1?",":"" ).append("\"").append(m.getColumnLabel(i)).append("\":\"").append(r.getString(i)).append("\"");
            }

            if ( table != null )
            {
                // Parsiranje slozenog podatka
                bld.append(",\"").append(table).append("\":");
                String sql = "SELECT * FROM " + table + " WHERE " + key + " = " + r.getString(key);
//                System.out.println(sql);
              bld.append(parsirajSlozenUpit(sql, null, null));
            }

            bld.append("}");
        }
        if(isArray)
        {
            bld.insert(0, "[");
            bld.append("]");
        }
        return bld.toString();
    }
    
    public String getReferencingColumn(String mainTable, String refTable) throws SQLException
    {
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE REFERENCED_TABLE_NAME = '"+mainTable+"' and table_name = '"+refTable+"';";
        ResultSet r = connection.createStatement().executeQuery(sql);
        if(r.next())
            return r.getString(1);
        return null;
    }
    
    /**
     * Vraca ime tabele selektovane u kveriju. Ako je u kveriju selektovano
     * vise tabela vraca null.
     */
    public String getQueryTable( ResultSet r ) throws SQLException
    {
        ResultSetMetaData m = r.getMetaData();
        int columnCount = m.getColumnCount();
        String tableName = m.getTableName(1);
        for(int i=2; i<=columnCount; i++)
        {
            String tek = m.getTableName(i);
            if(!tek.equals(tableName))
                return null;
        }
        return tableName;   
    }
    
    /**
     * Ovo ce raditi jedino ako u query-ju iamamo samo jednu tabelu
     */
    private String parsirajSlozenUpit(String query, String table) throws Exception
    {
        ResultSet r = connection.createStatement().executeQuery(query);
        String mainTable = getQueryTable(r);
        if(mainTable==null)
            throw new IllegalArgumentException("QUERY mora da selektuje samo jednu tabelu");
        
        String refColumn = getReferencingColumn(mainTable, table);
        StringBuilder bld = new StringBuilder();
        
        ResultSetMetaData m = r.getMetaData();
        int columnCount = m.getColumnCount();
        
        boolean first = true;
        boolean isArray = false;
        while(r.next())
        {
            if(first)
                first = false;
            else
            {
                bld.append(",");
                isArray = true;
            }
            
            bld.append("{");
            for(int i=1; i<=columnCount; i++)
            {
                // Parsiranje obicnog podatka
                bld.append( i!=1?",":"" ).append("\"").append(m.getColumnLabel(i)).append("\":\"").append(r.getString(i)).append("\"");
            }

            if ( table != null )
            {
                // Parsiranje slozenog podatka
                bld.append(",\"").append(table).append("\":");
                String sql = "SELECT * FROM " + table + " WHERE " + refColumn + " = " + r.getString(refColumn);
//                System.out.println(sql);
              bld.append(parsirajSlozenUpit( sql, null ));
            }

            bld.append("}");
        }
        if(isArray)
        {
            bld.insert(0, "[");
            bld.append("]");
        }
        return bld.toString();
    }
    
    private String parsirajPomocuPodataka(String query, ManyToOneSelectData data) throws SQLException
    {
        ResultSet r = connection.createStatement().executeQuery(query);
        StringBuilder bld = new StringBuilder();
        ResultSetMetaData m = r.getMetaData();
        int columnCount = m.getColumnCount();
        
        boolean first = true;
        boolean isArray = false;
        while(r.next())
        {
            if(first)
                first = false;
            else
            {
                bld.append(",");
                isArray = true;
            }
            
            bld.append("{");
            for(int i=1; i<=columnCount; i++)
            {
                // Parsiranje obicnog podatka
                bld.append( i!=1?",":"" ).append("\"").append(m.getColumnLabel(i)).append("\":\"").append(r.getString(i)).append("\"");
            }

            if ( data != null )
            {
                // Parsiranje slozenog podatka
                bld.append(",\"").append(data.getReferencedTableObjectName()).append("\":");
                String sql = "SELECT "+data.getForeignTableSelectColumnsQuery()+" FROM " + data.getForeignTable() + " WHERE " + data.getReferencedTableKey() + " = " + r.getString(data.getForeignKey());
//                System.out.println(sql);
                bld.append( parsirajPomocuPodataka( sql, null ) );
            }

            bld.append("}");
        }
        if(isArray)
        {
            bld.insert(0, "[");
            bld.append("]");
        }
        return bld.toString();
    }
    
    private String parsirajPomocuPodatakaNiz(String query, ManyToOneSelectData...data) throws SQLException
    {
//        System.out.println(query);
        ResultSet r = connection.createStatement().executeQuery(query);
        StringBuilder bld = new StringBuilder();
        ResultSetMetaData m = r.getMetaData();
        int columnCount = m.getColumnCount();
        
        boolean first = true;
        boolean isArray = false;
        while(r.next())
        {
            if(first)
                first = false;
            else
            {
                bld.append(",");
                isArray = true;
            }
            
            bld.append("{");
            for(int i=1; i<=columnCount; i++)
            {
                // Parsiranje obicnog podatka
                bld.append( i!=1?",":"" ).append("\"").append(m.getColumnLabel(i)).append("\":\"").append(r.getString(i)).append("\"");
            }

            if (data != null)
            {
                for (ManyToOneSelectData i : data)
                {
                    // Parsiranje slozenog podatka
                    bld.append(",\"").append(i.getReferencedTableObjectName()).append("\":");
                    String sql = "SELECT " + i.getForeignTableSelectColumnsQuery() + " FROM " + i.getForeignTable() + " WHERE " + i.getReferencedTableKey() + " = " + r.getString(i.getReferencedTableKey());
//                System.out.println(sql);
                    bld.append(parsirajPomocuPodatakaNiz(sql, null));
                }
            }

            bld.append("}");
        }
        if(isArray)
        {
            bld.insert(0, "[");
            bld.append("]");
        }
        return bld.toString();
    }
    
    public static void main(String[] args) throws Exception
    {
        Parsiranje ob = new Parsiranje();
        ManyToOneSelectData ocene = ManyToOneSelectData.setAllData("ocene", "ocn_id", "predmeti", "pre_id", "predmeti", "*");
        System.out.println( ob.parsirajPomocuPodatakaNiz("SELECT * FROM predmeti") );
        
    }
    
}
