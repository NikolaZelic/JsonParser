package test;

import baza.MySQL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class TestKonekcije 
{

    public static void main(String[] args) throws Exception
    {
        MySQL db = new MySQL("localhost", "mediji", "root", "");
        
        ResultSet r = db.query("SELECT * FROM mediji");
        
        ResultSetMetaData meta = r.getMetaData();
        int columnCount = meta.getColumnCount();
        String[] name = new String[columnCount];
        for(int i=1; i<=columnCount; i++)
            name[i-1] = meta.getColumnName(i);
        
        while(r.next())
        {
            for(int i=1; i<=columnCount; i++)
                System.out.print(name[i-1] + " : " + r.getString(i) + (i!=columnCount?", ":"") );
            System.out.println();
        }
    }
    
}
