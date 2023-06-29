package com.backendabstractmodel.demo.domain.entity.dbconfig;

public class ColumnDefinition {

/*
    // MySQL Server
    public static final String TINYINT = "TINYINT";
    public static final String SMALLINT = "SMALLINT";
    public static final String FLOAT = "FLOAT(5,2)";
    public static final String CHAR = "CHAR(1)";
    public static final String FILE_SMALL = "BLOB";       // 2^16 = 65,535 bytes = 65.5 KB
    public static final String FILE_MEDIUM = "MEDIUMBLOB";  // 2^24 - 1 = 16,777,215 bytes = 16.7 MB
    public static final String FILE_BIG = "LONGBLOB";       // 2^32 -1 = 4,294,967,295 bytes = 2^32 = 4.3 GB
*/

    // SQL Server
    public static final String UUID = "VARBINARY(255)";
    public static final String TINYINT = "TINYINT";
    public static final String SMALLINT = "SMALLINT";
    public static final String FLOAT = "DECIMAL(5,2)"; // Ex: 123.45
    public static final String CHAR = "CHAR(1)";
    public static final String FILE_SMALL = "VARBINARY(8000)";  // 8000 bytes = 8 KB
    public static final String FILE_LARGE = "VARBINARY(max)";   // 2 GB

}
