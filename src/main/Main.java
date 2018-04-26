package main;

import baza.MySQLJSONParser;
import baza.ReferencedTable;

public class Main 
{

    public static void main(String[] args) throws Exception
    {
        MySQLJSONParser db = new MySQLJSONParser("primer", "root", "");
        db.setQuery("SELECT * FROM ocene");
        db.addReferencedTable(new ReferencedTable("std_id"));
//        db.addReferencedTable(new ReferencedTable("pre_id"));
        db.executeQueryAndParse();
    }
    
}
