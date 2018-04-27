package radneverzije;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import static main.Utility.*;

public class PronalazenjeVezeIzmedjuTabela
{

    private Connection connection;
    public PronalazenjeVezeIzmedjuTabela() throws SQLException
    {
        String url = "jdbc:mysql://localhost:3306/primer";
        String username = "root";
        String password = "";
        connection = DriverManager.getConnection(url, username, password);
    }
    
    
    private void prikupiPodatkeOVeziIzmedjuTabela(String mainTable, String refTable) throws SQLException
    {
        String sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE REFERENCED_TABLE_NAME = '"+mainTable+"' and table_name = '"+refTable+"';";
        ResultSet r = connection.createStatement().executeQuery(sql);
        ispisiRezultSet(r);
    }
    
    
    public static void main(String[] args) throws Exception
    {
        PronalazenjeVezeIzmedjuTabela ob = new PronalazenjeVezeIzmedjuTabela();
        ob.prikupiPodatkeOVeziIzmedjuTabela("profesori", "predmeti");
    }
    
}
