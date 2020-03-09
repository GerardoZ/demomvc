package com.example.demomvc.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UsersDao{

    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Query("DELETE FROM users_table")
    fun clear()

    @Query("SELECT * FROM users_table ORDER BY userId DESC")
    fun getAllUsers(): LiveData<List<User>>
}
