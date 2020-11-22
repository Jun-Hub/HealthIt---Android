package io.jun.healthit.model

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.jun.healthit.model.data.Inbody
import io.jun.healthit.model.data.Memo

@Database(entities = [Memo::class, Inbody::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MemoDatabase : RoomDatabase() {
    abstract fun memoDao(): MemoDao
    abstract fun inbodyDao(): InbodyDao

    //Instance 싱글톤 사용
    companion object {
        @Volatile
        private var INSTANCE: MemoDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `Inbody` (`date` TEXT PRIMARY KEY NOT NULL, " +
                        "`weight` FLOAT, " +
                        "`skeletalMuscle` FLOAT, " +
                        "`percentFat` FLOAT)")
            }
        }

        fun getDatabase(context: Context): MemoDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MemoDatabase::class.java,
                    "memo_database"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
