package radneverzije;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import static main.Utility.*;

public class SelektovanjeSlozeneTabeleManuelno
{

    private Connection connection;
    public SelektovanjeSlozeneTabeleManuelno() throws SQLException
    {
        String url = "jdbc:mysql://localhost:3306/primer";
        String username = "root";
        String password = "";
        connection = DriverManager.getConnection(url, username, password);
    }
    
    private void selektujManuelno(String query) throws SQLException
    {
        Statement statement = connection.createStatement();
        Statement statement2 = connection.createStatement();
        ResultSet result = statement.executeQuery(query);
        
        while( result.next() )
        {
            ispisiRedRezultSeta(result);
            String stdId = result.getString("std_id");
            String sql = "SELECT * FROM studenti WHERE std_id = " + stdId;
            ResultSet referencedTable = statement2.executeQuery(sql);
            ispisiRezultSet(referencedTable);
            System.out.println("---------------------------------------");
        }
    }
    
    private void selektujZadatKljuc(String query, String foreignKey) throws SQLException
    {
        Statement statement = connection.createStatement();
        Statement statement2 = connection.createStatement();
        ResultSet result = statement.executeQuery(query);
        
        // Odredjivanje imena kolone stranog kljuca i omotaca
        
        // Pronalazenje imena tabele stranog kljuca
        String tableName = tabelaStranogKluca(result, foreignKey);
//        System.out.println(tableName);
        
        while(result.next())
        {
            ispisiRedRezultSeta(result);
            String stdId = result.getString(foreignKey);
            String sql = "SELECT * FROM " + tableName + " WHERE "+foreignKey+" = " + stdId;
//            System.out.println("\t"+sql);
            ResultSet referencedTable = statement2.executeQuery(sql);
            ispisiRezultSet(referencedTable);
            System.out.println("---------------------------------------");
        }
    }
    
    /**
     * Selektuje referencijalne tabee zadate stranim klucevima, 
     * ali samo one koje imaju strani kljuc unutar prvobitne tabele
     */
    private void selektujKljuceve(String query, String...foreignKeys) throws SQLException
    {
        // Izvrsavanje glavnog kverija
        Statement mainStatement = connection.createStatement();
        ResultSet mainResult = mainStatement.executeQuery(query);
        
        // Statement za referencione tabele
        Statement refSTatement = connection.createStatement();
        
        // Pronalazenja imena tabela stranih kljuceva
        String[] refTabeImena = new String[foreignKeys.length];
        for(int i=0; i<foreignKeys.length; i++)
        {
            refTabeImena[i] = tabelaStranogKluca(mainResult, foreignKeys[i]);
//            System.out.println(refTabeImena[i]);
        }
            
        // Ispis tabela
        while(mainResult.next())
        {
            ispisiRedRezultSeta(mainResult);
            
            // Ispis referencionalnij tabela
            for(int i=0; i<foreignKeys.length; i++)
            {
                String sql = "SELECT * FROM " + refTabeImena[i] + " WHERE " + foreignKeys[i] + " = " + mainResult.getString(foreignKeys[i]);
//                System.out.println("\t"+sql);
                ResultSet refTable = refSTatement.executeQuery(sql);
                ispisiRezultSet(refTable);
            }
            
            System.out.println("---------------------------------------");
        }
            
    }
    
    /**
     * Selektuje referencijalne tabele zadate stranim kljucevima,
     * pri cemu strani kljucevi ne moraju da se nalaze unutar prvobitne tabele,
     * vec mogu biti i u referencijalnim tabelama. Tj. moze da pravi vozic referencijalnih 
     * tabela.
     * Ovo ce raditi ukoliko ne postoji povratni kljuc. Tj. da
     * jedna tabela referencira drugu a druga referencira prvu.
     * Isto vazi i ako postoji krug tabela koji se medjusobno referenciraju.
     */
    private void visestrukoSelektovanjeKljuceva(String query, String...foreignKeys) throws SQLException
    {
        // Izvrsavanje glavnog kverija
        Statement mainStatement = connection.createStatement();
        ResultSet mainResult = mainStatement.executeQuery(query);
        
        // Statement za referencione tabele
        Statement refSTatement = connection.createStatement();
        
        // Pronalazenja imena tabela stranih kljuceva
        String[] refTabeImena = new String[foreignKeys.length];
        for(int i=0; i<foreignKeys.length; i++)
        {
            refTabeImena[i] = tabelaStranogKluca(mainResult, foreignKeys[i]);
//            System.out.println(refTabeImena[i]);
        }
            
        // Ispis tabela
        while(mainResult.next())
        {
            ispisiRedRezultSeta(mainResult);
            
            // Ispis referencionalnij tabela
            for(int i=0; i<foreignKeys.length; i++)
            {
                if(refTabeImena[i]==null)
                    continue;
                
                String sql = "SELECT * FROM " + refTabeImena[i] + " WHERE " + foreignKeys[i] + " = " + mainResult.getString(foreignKeys[i]);
//                System.out.println("\t"+sql);
                visestrukoSelektovanjeKljuceva(sql, foreignKeys);
            }
            
            System.out.println("---------------------------------------");
        }
            
    }
    
    
    public static void main(String[] args) throws Exception
    {
        SelektovanjeSlozeneTabeleManuelno ob = new SelektovanjeSlozeneTabeleManuelno();
        ob.selektujKljuceve("select * from ocene where std_id=5;","std_id");
    }
    
}
