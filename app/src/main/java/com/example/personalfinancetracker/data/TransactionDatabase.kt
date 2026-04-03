package com.example.personalfinancetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 2, exportSchema = false)
abstract class TransactionDatabase: RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO

    companion object{
        @Volatile
        private var Instance: TransactionDatabase? = null

        fun getDatabase(context: Context): TransactionDatabase{
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TransactionDatabase::class.java, "transaction_database")
                    .fallbackToDestructiveMigration().build()
                    .also { Instance = it }
            }
        }
    }
}