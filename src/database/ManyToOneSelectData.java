package database;

/**
 * 
 */
public class ManyToOneSelectData
{
    private String foreignTable;    // Tabel with foreign key
    private String foreignKey;
    private String referencedTable;
    private String referencedTableKey;
    private String referencedTableObjectName;
    private String foreignTableSelectColumnsQuery = " * ";
    
    private ManyToOneSelectData(String foreignTable, String foreignKey, String referencedTable, String referencedTableKey,
            String referencedTableObjectName, String foreignTableSelectColumnsQuery)
    {
        this.foreignTable = foreignTable;
        this.foreignKey = foreignKey;
        this.referencedTable = referencedTable;
        this.referencedTableKey = referencedTableKey;
        this.referencedTableObjectName = referencedTableObjectName;
        this.foreignTableSelectColumnsQuery = foreignTableSelectColumnsQuery;
    }
    
    public static ManyToOneSelectData setAllData(String foreignTable, String foreignKey, String referencedTable, 
            String referencedTableKey, String referencedTableObjectName, String foreignTableSelectColumnsQuery )
    {
        return new ManyToOneSelectData(foreignTable, foreignKey, referencedTable, referencedTableKey, 
                referencedTableObjectName, foreignTableSelectColumnsQuery);
    }
    public String getForeignTable()
    {
        return foreignTable;
    }

    public String getForeignKey()
    {
        return foreignKey;
    }

    public String getReferencedTable()
    {
        return referencedTable;
    }

    public String getReferencedTableKey()
    {
        return referencedTableKey;
    }

    public String getReferencedTableObjectName()
    {
        return referencedTableObjectName;
    }

    public String getForeignTableSelectColumnsQuery()
    {
        return foreignTableSelectColumnsQuery;
    }
    
    
}
