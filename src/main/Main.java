package main;

import database.ManyToOneSelectData;
import database.MySQLJSONParser;


public class Main 
{

    public static void main(String[] args) throws Exception
    {
        MySQLJSONParser parser = new MySQLJSONParser("localhost:3306", "primer", "root", "");
        parser.setQuery("SELECT * FROM profesori");
        ManyToOneSelectData pre = ManyToOneSelectData.setAllData("predmeti", "pre_id", "profesori", "prf_id", "predmeti", "*");
        parser.addManyToOneSelectData(pre);
        String json = parser.parse();
        System.out.println(json);
    }
    
}
