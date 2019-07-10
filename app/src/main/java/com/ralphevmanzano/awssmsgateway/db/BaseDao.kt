package com.ralphevmanzano.awssmsgateway.db

import androidx.room.*
import io.reactivex.Completable


@Dao
abstract class BaseDao<T> {
  /**
   * Insert an object in the database.
   *
   * @param obj the object to be inserted.
   * @return The SQLite row id
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract fun insert(obj: T): Completable

  /**
   * Insert an array of objects in the database.
   *
   * @param obj the objects to be inserted.
   * @return The SQLite row ids
   */
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  abstract fun insert(obj: List<T>): Completable

  /**
   * Update an object from the database.
   *
   * @param obj the object to be updated
   */
  @Update
  abstract fun update(obj: T): Completable

  /**
   * Update an array of objects from the database.
   *
   * @param obj the object to be updated
   */
  @Update
  abstract fun update(obj: List<T>): Completable

  /**
   * Delete an object from the database
   *
   * @param obj the object to be deleted
   */
  @Delete
  abstract fun delete(obj: T): Completable
}