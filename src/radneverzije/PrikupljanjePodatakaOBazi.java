package radneverzije;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import static main.Utility.*;

public class PrikupljanjePodatakaOBazi
{

    private Connection connection;
    public PrikupljanjePodatakaOBazi() throws SQLException
    {
        String url = "jdbc:mysql://localhost:3306/primer";
        String username = "root";
        String password = "";
        connection = DriverManager.getConnection(url, username, password);
    }
    
    
    private void ispisMetapodatakaKonekcije() throws SQLException
    {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet columns = meta.getColumns(null, null, null, null);
        ispisiRezultSet(columns);
    }
    
    private void ispisMetapodatakaKolona() throws SQLException
    {
        ResultSet columns = connection.getMetaData().getColumns(null, null, "med_glm", null);
        ispisiRezultSet(columns);
    }
    
    private void ispisPrimarnihkljuceva() throws SQLException
    {
        ResultSet primaryKeys = connection.getMetaData().getPrimaryKeys(null, null, "med_glm");
        ispisiRezultSet(primaryKeys);
    }
    
    private void ispisSpoljasnjihKljuceva() throws SQLException
    {
        ResultSet importedKeys = connection.getMetaData().getImportedKeys(null, null, "med_glm");
        ispisiRezultSet(importedKeys);
    }
    
    public static void main(String[] args) throws Exception
    {
        PrikupljanjePodatakaOBazi ob = new PrikupljanjePodatakaOBazi();
        ob.ispisSpoljasnjihKljuceva();
    }

}
