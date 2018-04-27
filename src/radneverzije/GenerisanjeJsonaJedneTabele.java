//package radneverzije;
//
//import static baza.MySQL.db;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.text.Collator;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//
//public class GenerisanjeJsonaJedneTabele
//{
//    public static String parseTable(String query) throws SQLException
//    {
//        StringBuilder json = new StringBuilder();
//        ResultSet r = db.query(query);
//        
//        ResultSetMetaData meta = r.getMetaData();
//        int columnCount = meta.getColumnCount();
//        String[] name = new String[columnCount];
//        for(int i=1; i<=columnCount; i++)
//            name[i-1] = meta.getColumnLabel(i);
//        String tableName = meta.getTableName(1);
//        
//        json.append("{\"").append(tableName).append("\":[");
//        
//        boolean first = true;
//        while(r.next())
//        {
//            if(first)
//                first = false;
//            else
//                json.append(",");
//            json.append(parsirajObicanRed(r));
////            json.append("{");
////            for(int i=1; i<=columnCount; i++)
////                json.append( i!=1?",":"" ).append("\"").append(name[i-1]).append("\":\"").append(r.getString(i)).append("\"");
////            json.append("}");
//        }
//        
//        json.append("]}");
//        
//        return json.toString();
//    }
//    
//    public static String parsirajObicanRed(ResultSet r) throws SQLException
//    {
//        StringBuilder json = new StringBuilder();
//        ResultSetMetaData meta = r.getMetaData();
//        int columnCount = meta.getColumnCount();
//        String[] names = new String[columnCount];
//        for(int i=1; i<=columnCount; i++)
//            names[i-1] = meta.getColumnLabel(i);
//        
//        json.append("{");
//        for(int i=1; i<=columnCount; i++)
//            json.append( i!=1?",":"" ).append("\"").append(names[i-1]).append("\":\"").append(r.getString(i)).append("\"");
//        json.append("}");
//        
//        return json.toString();
//    } 
//    
//    public static void selektujSlozenuTabelu(String query, String...foregionKeys) throws SQLException
//    {
//        ResultSet r = db.query(query);
//        Set<String> skup = new HashSet<>();
//        for(String i:foregionKeys)
//            skup.add(i);
//        
//        ResultSetMetaData meta = r.getMetaData();
//        int columnCount = meta.getColumnCount();
//        String[] names = new String[columnCount];
//        for(int i=1; i<=columnCount; i++)
//            names[i-1] = meta.getColumnLabel(i);
//        
//        while(r.next())
//        {
//            for(int i=1; i<=columnCount; i++)
//            {
//                System.out.print("\""+names[i-1]+"\":");    // Ispis imena kolone
//                if(skup.contains(names[i-1]))   
//                {
//                    // Slozena tabela
//                    String tabela = meta.getTableName(i);
//                    String sql = "SELECT * FROM " + tabela + " WHERE " + names[i-1] + " = " + r.getString(i);
//                    System.out.println(sql);
//                }
//                else
//                {
//                    // Prosta tabla
//                    System.out.print( parsirajObicanRed(r) );
//                }
//                System.out.println();
//            }
//        }
//    }
//    
//    public static void main(String[] args) throws Exception
//    {
////        String str = parseTable("SELECT * FROM mediji");
////        System.out.println(str);
//        selektujSlozenuTabelu("SELECT * FROM med_glm","med_id");
//        
//    }
//    
//}
