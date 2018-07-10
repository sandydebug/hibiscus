package com.anmol.hibiscus.Helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteCantOpenDatabaseException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.openOrCreateDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteReadOnlyDatabaseException
import android.widget.Toast


val DB = "booksdb"
val TB = "books_table"
val NOTICE_ID = "noticeid"
class Dbbookshelper (context: Context):SQLiteOpenHelper(context, DB,null,1){

    override fun onCreate(p0: SQLiteDatabase?) {
        val createtable = "CREATE TABLE " + TB + " (" +
                NOTICE_ID + " INTEGER PRIMARY KEY NOT NULL UNIQUE)"

        p0?.execSQL(createtable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TB")
        onCreate(p0)
    }

    fun insertBook(noticeid:String){
        try{
        val db = this.writableDatabase
            db.enableWriteAheadLogging()
        val cv = ContentValues()
        cv.put(NOTICE_ID,noticeid)

            val result = db.insert(TB,null,cv)
            if(result == (-1).toLong())
                System.out.println("coinstatus is failed")
            else
                System.out.println("coinstatus is successs")
        }catch (e:SQLiteCantOpenDatabaseException){
            e.printStackTrace()
        }

    }
    fun readbook():ArrayList<String>{
        val noticeids :ArrayList<String> = ArrayList()
        try {
            val db = this.readableDatabase
            val query = "Select * from $TB"
            val result = db.rawQuery(query,null)
            if(result.moveToFirst()){
                do{
                    val noticeid = result.getString(result.getColumnIndex(NOTICE_ID))
                    noticeids.add(noticeid)
                }while (result.moveToNext())
            }
            result.close()
            db.close()

        }
        catch (e:SQLiteCantOpenDatabaseException){
            e.printStackTrace()
        }
        catch (e:SQLiteReadOnlyDatabaseException){
            e.printStackTrace()
        }
        return noticeids
    }

    fun deletebook(noticeid:String) {
        val db = this.writableDatabase
        db.enableWriteAheadLogging()
        db.delete(TB, "$NOTICE_ID = ?", arrayOf(noticeid))
        db.close()
    }

}

