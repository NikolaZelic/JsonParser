package database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLJSONParser extends MySQL
{
    public MySQLJSONParser(String host, String schema, String user, String pass) throws SQLException
    {
        super(host, schema, user, pass);
    }
    
    private String query;   
    private List<ManyToOneSelectData> manyToOneSelectData;
    public void setQuery(String query)
    {
        reset();
        this.query = query;
    }
    public void addManyToOneSelectData(ManyToOneSelectData data)
    {
        if(data==null)
            return;
        if(manyToOneSelectData==null)
            manyToOneSelectData = new ArrayList<>();
        manyToOneSelectData.add(data);
    }
    private void reset()
    {
        query = null;
        manyToOneSelectData = null;
    }
    private boolean reportQueries = false;
    public void setReportQueries(boolean reportQueries)
    {
        this.reportQueries = reportQueries;
    }
    
    
    public String parse() throws SQLException
    {
        // Check inputs
        if(query==null)
            throw new IllegalArgumentException("You have to input query");
        
        return parseLocal( query, manyToOneSelectData);
    }
    
    private String parseLocal(String query, List<ManyToOneSelectData> data) throws SQLException
    {
        if(reportQueries)
            System.out.println(query);
        
        ResultSet r = conn.createStatement().executeQuery(query);
        StringBuilder bld = new StringBuilder();
        ResultSetMetaData m = r.getMetaData();
        int columnCount = m.getColumnCount();
        
        bld.append("[");
        
        boolean first = true;
        while(r.next())
        {
            if(first)
                first = false;
            else
                bld.append(",");
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
                    if(reportQueries)
                        System.out.println(sql);
                    bld.append(parseLocal(sql, null));
                }
            }

            bld.append("}");
        }
        
        bld.append("]");
        return bld.toString();
    }
}
