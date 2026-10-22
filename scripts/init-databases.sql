IF DB_ID(N'product_db') IS NULL
    CREATE DATABASE product_db;
GO

IF DB_ID(N'order_db') IS NULL
    CREATE DATABASE order_db;
GO

IF DB_ID(N'user_db') IS NULL
    CREATE DATABASE user_db;
GO