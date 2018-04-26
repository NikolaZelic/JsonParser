package radneverzije;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static main.Utility.*;

public class SelektovanjeSlozeneTabele
{
    private Connection connection;
    public SelektovanjeSlozeneTabele() throws SQLException
    {
        String url = "jdbc:mysql://localhost:3306/mediji";
        String username = "root";
        String password = "";
        connection = DriverManager.getConnection(url, username, password);
    }
    
    
    
    public void selektujSlozenuTabelu(String query) throws SQLException
    {
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);
        ResultSetMetaData meta = result.getMetaData();
        
        // Citanje stranih kljuceva
        List<ReferecionaTabela> keys = new ArrayList<>();
        String tableName = meta.getTableName(1);
        ResultSet importedKeys = connection.getMetaData().getImportedKeys(null, null, tableName);
        int i = 1;
        while( importedKeys.next() )
        {
            String rtname = importedKeys.getString("PKTABLE_NAME");
            String rcname = importedKeys.getString("PKCOLUMN_NAME");
            keys.add( new ReferecionaTabela(tableName, rcname, i++) );
        }
        
        // Selektovanje tabela
        int columnCount = meta.getColumnCount();
        while(result.next())
        {
            String medId = result.getString(1);
            String glmId = result.getString(2);
            
            
        }
    }
    
    public static void main(String[] args) throws Exception
    {
        SelektovanjeSlozeneTabele ob = new SelektovanjeSlozeneTabele();
        ob.selektujSlozenuTabelu("SELECT med_id, glm_id FROM med_glm");
    }
    
}

class ReferecionaTabela
{
    String tableName;
    String column;
    int columnMapId;
    
    public ReferecionaTabela()
    {
    }

    public ReferecionaTabela(String tableName, String column, int columnMapId )
    {
        this.tableName = tableName;
        this.column = column;
        this.columnMapId = columnMapId;
    }

    public int getColumnMapId()
    {
        return columnMapId;
    }

    public void setColumnMapId(int columnMapId)
    {
        this.columnMapId = columnMapId;
    }
    
    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getColumn()
    {
        return column;
    }

    public void setColumn(String column)
    {
        this.column = column;
    }
    
    
}