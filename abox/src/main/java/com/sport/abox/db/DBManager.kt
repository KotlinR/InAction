package com.sport.abox.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.sport.abox.db.entities.Training

class DBManager(private val context: Context) {

    private val dbName = "dbToABox"
    private val dbVersion = 1
    private val tableTrainings = "trainings"

    private val columnID = "_id"
    private val columnTrainingTitle = "workout_title"
    private val columnExercises = "exercises"

    private val createDBTrainingCommand = "create table " + tableTrainings + "(" +
            columnID + " integer primary key autoincrement, " +
            columnTrainingTitle + " text, " +
            columnExercises + " text" +
            ");"

    private var mDBHelper: DBHelper? = null
    private var mDB: SQLiteDatabase? = null

    fun open() {
        mDBHelper = DBHelper(context, dbName, null, dbVersion, createDBTrainingCommand)
        mDB = mDBHelper?.writableDatabase
    }

    fun close() {
        mDBHelper?.close()
    }

    fun getAllTrainings(): Cursor? {
        return mDB?.query(tableTrainings, null, null, null, null, null, null)
    }

    fun saveTraining(training: Training) {
        val cv = ContentValues()
        cv.put(columnTrainingTitle, training.title)
        cv.put(columnExercises, training.exercises.joinToString(separator = "|"))
        mDB?.insert(tableTrainings, null, cv)
    }

    fun updateTraining(training: Training) {
        val cv = ContentValues()
        cv.put(columnTrainingTitle, training.title)
        cv.put(columnExercises, training.exercises.joinToString(separator = "|"))
        mDB?.update(tableTrainings, cv, "$columnID = ${training.id}", null)
    }

    fun deleteTraining(training: Training) {
        mDB?.delete(tableTrainings, "$columnID = ${training.id}", null)
    }

    fun deleteAllTrainings() {
        mDB?.delete(tableTrainings, null, null)
    }
}