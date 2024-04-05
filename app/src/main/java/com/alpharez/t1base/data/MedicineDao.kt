package com.alpharez.t1base.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.lifecycle.LiveData

@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(medicine: Medicine)

    @Update
    suspend fun update(medicine: Medicine)

    @Delete
    suspend fun delete(medicine: Medicine)

    @Query("SELECT * FROM medicine")
    fun getMedicines(): LiveData<List<Medicine>>

    @Query("SELECT * FROM medicine WHERE medicineName = :name")
    fun findMedicine(name: String): List<Medicine>

    @Query("SELECT * FROM medicine WHERE id = :id")
    fun getMedicine(id: Int): LiveData<Medicine>

    //@Query("SELECT * FROM medicine WHERE id = :id")
    //fun takeMedicine(id: Int): LiveData<Medicine>

}