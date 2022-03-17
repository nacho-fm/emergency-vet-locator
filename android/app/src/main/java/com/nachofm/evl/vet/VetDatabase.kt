package com.nachofm.evl.vet

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Vet::class), version = 1, exportSchema = false)
public abstract class VetDatabase : RoomDatabase() {
    abstract fun vetDao(): VetDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: VetDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): VetDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VetDatabase::class.java,
                    "vet_database"
                ).addCallback(VetDatabaseCallback(scope)).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class VetDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.vetDao())
                }
            }
        }

        suspend fun populateDatabase(vetDao: VetDao) {
            // Delete all content here.
            vetDao.deleteAll()

            // Add sample vets.
            vetDao.insert(
                Vet(0, "Hello", "2813308004", "dev@nacho.fm"),
                Vet(2, "World!", "5555555555", "dev@nacho.fm"))
        }
    }
}