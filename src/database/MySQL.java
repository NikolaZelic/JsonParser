package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

public class MySQL
{

    protected Connection conn;

    public MySQL(String host, String schema, String user, String pass) throws SQLException
    {
        Properties p = new Properties();
        p.setProperty("user", user);
        p.setProperty("password", pass);
        p.setProperty("useUnicode", "yes");
        p.setProperty("characterEncoding", "UTF-8");
        String url = "jdbc:mysql://" + host + "/" + schema;
        conn = DriverManager.getConnection(url, p);
    }

    public ResultSet query(String sql) throws SQLException
    {
        Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return s.executeQuery(sql);
    }

    public ArrayList<Integer> execute(String sql) throws SQLException
    {
        Statement s = conn.createStatement();
        s.execute(sql, Statement.RETURN_GENERATED_KEYS);
        ResultSet r = s.getGeneratedKeys();
        ArrayList<Integer> keys = new ArrayList<>();
        while (r.next())
        {
            keys.add(r.getInt(1));
        }
        r.close();
        s.close();
        return keys;
    }

    public void close() throws SQLException
    {
        conn.close();
    }
}
