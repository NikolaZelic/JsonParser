package baza;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import main.Utility;
import static main.Utility.*;

public class MySQLJSONParser
{
    private Connection connection;
    public MySQLJSONParser(String schema, String username, String password) throws SQLException
    {
        String url = "jdbc:mysql://localhost:3306/"+schema;
        connection = DriverManager.getConnection(url, username, password);
    }

    private String query;
    private List<ReferencedTable> referencedTables;
    public void setQuery(String query)
    {
        referencedTables = null;
        this.query = query;
        prepearExecution = false;
    }
    public void addReferencedTable(ReferencedTable referencedTable)
    {
        if(referencedTables==null)
            referencedTables = new ArrayList<>();
        referencedTables.add(referencedTable);
    }

    boolean prepearExecution = false;
    private void prepare(final ResultSet r) throws SQLException
    {
        // Mechanization to execute prepear only once per query
        if(prepearExecution)
            return;
        prepearExecution = true;
        
        // Prepear
        if(referencedTables!=null)
            for(ReferencedTable i:referencedTables)
                i.prepare(r);
//        referencedTables.forEach(System.out::println);
            bld = new StringBuilder();
    }
    
    StringBuilder bld;
    
    public String executeQueryAndParse() throws SQLException
    {
        ResultSet mainResult = connection.createStatement().executeQuery(query);
        prepare(mainResult);
        
        // Check 
        
        // Write tables
        while( mainResult.next() )
        {
//            ispisiRedRezultSeta(mainResult);
            
            // Write referenced tables
            if( referencedTables!=null )
            {
                for( ReferencedTable t : referencedTables )
                {
                    String sql = t.createQuery(mainResult);
//                    System.out.println(sql);
                    ResultSet referencedTable = connection.createStatement().executeQuery(sql);
//                    ispisiRezultSet(referencedTable);
                }
            }
        }
        
        return bld.toString();
    }
}
