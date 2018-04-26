package radneverzije;

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
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kviz", "root", "");
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
        
        // Odredjivanje ime referencijlane tabele 
        
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
    
    public static void main(String[] args) throws Exception
    {
        Parsiranje ob = new Parsiranje();
        System.out.println( ob.parsirajSlozenUpit("SELECT * FROM pitanja","odgovori","pit_id") );
        
    }
    
}
