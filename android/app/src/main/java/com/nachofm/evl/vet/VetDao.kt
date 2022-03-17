package com.nachofm.evl.vet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VetDao {
    @Query("SELECT * FROM vet ORDER BY name ASC")
    fun getAlphabetizedVets(): Flow<List<Vet>>

    // @Insert(onConflict = OnConflictStrategy.IGNORE)
    // suspend fun insert(vet: Vet)

    @Insert
    fun insertAll(vararg vets: Vet)

    // @Query("DELETE FROM vet")
    // suspend fun deleteAll()
}
