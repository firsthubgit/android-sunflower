litepal框架一样功能


## 实体
用以下方式创建一个表和对应的实体
 ```kotlin
   @Entity(tableName = "userDataBase", primaryKeys = ["id", "name"]) //定义表格名称
   class User {
        @PrimaryKey(autoGenerate = true)// 单个主键设置为自增长
        @ColumnInfo(name = "user_id")  //列信息
        public var id = 0
        
        @ColumnInfo(name = "nameUser")
        public var name: String? = null
    }

```




## DAO （data access objects，数据访问对象）
创建一个用@Dao注释的接口。 这些操作都是同步的，运行在调用他的线程。 支持RXJava和LiveData的异步查询。
```kotlin
@Dao                   
public interface UserDao {
    @Insert      // 添加数据注解
    open fun insertAll(vararg users : User) : Boolean
 
    @Delete    // 删除数据注解
    open fun delete(user : User)
    
    @Delete
    open fun deleteAll(users : List<User>)
    
    @Update
    open fun update(user : User) : Int
    
    @Query("SELECT * FROM userDataBase" + 
        "WHERE name LIKE  :name")
    open fun findUsersByName(name: String) : List<User>
    
    
}
```


## 数据库
创建一个扩展RoomDatabase的抽象类，对它进行注释，
```kotlin
open class MyDatabase : RoomDatabase(){
    open fun getUserDao() : UserDao
    
}
    
```

## 数据库迁移
   无论是添加或重命名了某个列或某个表，每次改变数据库的Schema时
   
   1.更新数据库版本
   
   2.实现一个Migration类，定义如果处理从旧版本到新版本的迁移
   
   3.将此Migration类添加为Database 构建器的一个参数
    

## 测试
 1.需要实现AndroidJUnitTest来创建一个内存数据库，内存数据库仅会在进程处于活动状态时保留数据，
 也就是说，每次测试后，数据库都将被清除
 
 2.要测试异步查询，请添加测试规则InstantTaskExecutorRule以同步执行每个任务
 
 3.如果要测试DAO，需要借助像Mockito之类的框架来模拟DAO
 
 4.扩展CountingTaskExecutorRule，并在Espresso测试中使用它，以便在任务开始和结束时进行计数
 
 5.测试迁移，MigrationTestHelper，它允许你是用旧版本创建数据库，然后运行和验证迁移，你只需要检查在旧版本中插入的数据，
 在迁移后是否存在。
 
 
     


