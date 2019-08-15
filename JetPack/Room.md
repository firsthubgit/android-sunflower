litepal框架一样功能

[csdn链接](https://blog.csdn.net/Alexwll/article/details/83033460)
## 实体
用以下方式创建一个表和对应的实体。不指定表名，默认使用类名作为表名（SQLite中的表名称不区分大小写）。不指定列名，默认使用字段名成作为列名
 ```kotlin
   @Entity(tableName = "userDataBase", primaryKeys = ["id", "name"]) //定义表格名称
   class User {
        @PrimaryKey(autoGenerate = true)// 单个主键设置为自增长
        @ColumnInfo(name = "user_id")  //列信息
        var id = 0
        
        @ColumnInfo(name = "nameUser")
        var name: String? = null
        
        @Ignore //这个字段将不会出现在表的列中
        var pictureUrl : String? = null 
        
    }
    
//如过有继承关系，可以使用 @Entity(ignoredColumns = arrayOf ("picture")) 来忽略来自父类的属性

    
@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?
)

```

&nbsp;
>* 添加索引@Entity

1.使用 @Entity 的indices 属性，列出要包含在索引或复合索引中的列的名称
```
@Entity(indices = [Index("nameUser"), Index(value = ["name"])])  // 创建索引

@Entity(indices = [Index("nameUser"), Index(value = ["name"] ,unique = true)]) //唯一索引
```

&nbsp;
>* 外键约束@ForeignKey

1.使用@ForeignKey 注释定义其与实体的 关系；ForeignKey中 entity 为要关联的父实体类；
parentColumns 为关联父实体类的列名；childColumns此实体类中的列名
```

@Entity(foreignKeys = [ForeignKey(entity = User::class,
        parentColumns = ["id"],
        childColumns = ["user_id"])])
class Book {
    @PrimaryKey
    var bookId: Int = 0
    var title: String? = null
    @ColumnInfo(name = "user_id")
    var userId: Int = 0
}
```

&nbsp;
>* 嵌套对象@Embedded

1.使用 @Embedded 注释来表示要分解到表中子字段的对象（此时数据库的列为两个类中所有的字段）
```
class Address {
    public var street: String? = null
    public var state: String? = null
    public var city: String? = null
    @ColumnInfo(name = "post_code")
    public var postCode = 0
}
 
// 在User实体中引入Address
@Embedded
public var address: Address? = null

```

&nbsp;
&nbsp;
## DAO （data access objects，数据访问对象）
创建一个用@Dao注释的接口。 这些操作都是同步的，运行在调用他的线程。 支持RXJava和LiveData的异步查询。
```kotlin
@Dao                   
interface UserDao {
    //插入一条返回rowId，插入多条返回long[] or List<Long>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg users: User)
    
    @Insert      // 添加数据注解
    fun insertAll(vararg users : User)
    
    
    //return an int value, indicating the number of rows updated in the database.
    @Delete    // 删除数据注解
    fun deleteUser(user : User)
    
    @Delete
    fun deleteAll(users : List<User>)
    
    
    //return an int value, indicating the number of rows updated in the database.
    @Update
    fun update(user : User) : Int
    
    
    //使用 :参数名 来表示传递的参数
    @Query("SELECT * FROM userDataBase" + 
        "WHERE name LIKE  :name")
    fun findUsersByName(name: String) : List<User>
    
    @Query("SELECT * FROM user WHERE age BETWEEN :minAge AND :maxAge")
    fun loadAllUsersBetweenAges(minAge: Int, maxAge: Int): Array<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :search " +
           "OR last_name LIKE :search")
    fun findUserWithName(search: String): List<User>
    
    //可观察的返回数据，返回一个可操作的Cursor也可以
    @Query("SELECT first_name, last_name FROM user WHERE region IN (:regions)")
    fun loadUsersFromRegionsSync(regions: List<String>): LiveData<List<User>>
    
}
```

&nbsp;
&nbsp;
## 数据库
创建一个扩展RoomDatabase的抽象类，对它进行注释，
```kotlin
@Database(entities = arrayOf(User::class), version = 1)
abstract class AppDatabase : RoomDatabase(){

    open fun getUserDao() : UserDao
    
    
    //注意使用单例
    companion object {
        // For Singleton instantiation
        @Volatile 
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
                            WorkManager.getInstance(context).enqueue(request)
                        }
                    })
                    .build()
        }
    }
}
    
```


```kotlin
val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
```
https://developer.android.com/training/data-storage/room/index.html
如果您的应用程序在多个进程中运行，请 enableMultiInstanceInvalidation()在数据库构建器调用中包含。
这样，当您AppDatabase 在每个进程中都有一个实例时，可以在一个进程中使共享数据库文件无效，
并且此无效自动传播到AppDatabase其他进程中的实例 

&nbsp;&nbsp;
## 数据库迁移
   [官方文档连接][https://developer.android.com/training/data-storage/room/migrating-db-versions]
   
   无论是添加或重命名了某个列或某个表，每次改变数据库的Schema时
   
   1.更新数据库版本
   
   2.实现一个Migration类，定义如果处理从旧版本到新版本的迁移
   
   3.将此Migration类添加为Database 构建器的一个参数
    
&nbsp;&nbsp;
## 测试
 1.需要实现AndroidJUnitTest来创建一个内存数据库，内存数据库仅会在进程处于活动状态时保留数据，
 也就是说，每次测试后，数据库都将被清除
 
 2.要测试异步查询，请添加测试规则InstantTaskExecutorRule以同步执行每个任务
 
 3.如果要测试DAO，需要借助像Mockito之类的框架来模拟DAO
 
 4.扩展CountingTaskExecutorRule，并在Espresso测试中使用它，以便在任务开始和结束时进行计数
 
 5.测试迁移，MigrationTestHelper，它允许你是用旧版本创建数据库，然后运行和验证迁移，你只需要检查在旧版本中插入的数据，
 在迁移后是否存在。
 
 
     


