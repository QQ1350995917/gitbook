# 分片部署

*   查看数据库

    ```
    show dbs;
    ```
*   查看集合

    ```
    show tables;
    ```

    ```
    show collections;
    ```
*   删除集合

    ```
    db.friend.drop();
    ```
*   删除数据库

    ```
    db.dropDatabase();
    ```
*   切换数据库

    ```
    use test;
    ```
*   创建数据库与集合，在插入数据时会自动 创建数据库与集和

    ```
    db.test_collection.insertOne({name:"zhugeliang"，gender:"man"});
    ```
