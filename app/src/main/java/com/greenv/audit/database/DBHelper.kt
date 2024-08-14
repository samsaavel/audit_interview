package com.greenv.audit.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.greenv.audit.data.Audit
import com.greenv.audit.data.AuditFromDB
import com.greenv.audit.database.AuditContract.AuditEntry.COL_AUDIT
import com.greenv.audit.database.AuditContract.AuditEntry.COL_BRANCH
import com.greenv.audit.database.AuditContract.AuditEntry.COL_ID
import com.greenv.audit.database.AuditContract.AuditEntry.COL_PHASE
import com.greenv.audit.database.AuditContract.AuditEntry.COL_START_DATE
import com.greenv.audit.database.AuditContract.AuditEntry.COL_TYPE
import com.greenv.audit.database.AuditContract.AuditEntry.TABLE_NAME

const val DATABASE_NAME = "AuditDB"

object AuditContract {
    object AuditEntry : BaseColumns {
        const val TABLE_NAME = "audits"
        const val COL_AUDIT = "audit"
        const val COL_TYPE = "type"
        const val COL_BRANCH = "branch"
        const val COL_PHASE = "phase"
        const val COL_START_DATE = "start_date"
        const val COL_ID = "id"
    }
}

private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE $TABLE_NAME (" +
            "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "$COL_AUDIT TEXT, " +
            "$COL_TYPE TEXT, " +
            "$COL_BRANCH TEXT, " +
            "$COL_PHASE TEXT, " +
            "$COL_START_DATE TEXT )"

private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("*****DBHelper", "Creating database and table")
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun insertData(audits: List<Audit>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            for (audit in audits) {
                val values = ContentValues().apply {
                    put(COL_AUDIT, audit.auditoria)
                    put(COL_TYPE, audit.TIPO_AUDI)
                    put(COL_BRANCH, audit.sucursal)
                    put(COL_PHASE, audit.etapa)
                    put(COL_START_DATE, audit.fecha_inicio_real)
                }
                val result = db.insert(TABLE_NAME, null, values)
                if (result == -1L)
                    Log.e("*****DBHelper", "Failed to insert row: $audit")
                else
                    Log.d("*****DBHelper", "Successfully inserted row with ID: $result")
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e("*****DBHelper", "Error inserting data", e)
            e.printStackTrace()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun readTable(): List<AuditFromDB> {
        val db = this.readableDatabase

        val projection = arrayOf(
            COL_ID,
            COL_AUDIT,
            COL_TYPE,
            COL_BRANCH,
            COL_PHASE,
            COL_START_DATE
        )

        val cursor = db.query(
            TABLE_NAME,
            projection,
            null, null, null, null, null,
        )

        val audits = mutableListOf<AuditFromDB>()
        with(cursor) {
            while (moveToNext()) {
                val audit = AuditFromDB(
                    id = getInt(getColumnIndexOrThrow(COL_ID)),
                    auditoria = getString(getColumnIndexOrThrow(COL_AUDIT)),
                    TIPO_AUDI = getString(getColumnIndexOrThrow(COL_TYPE)),
                    sucursal = getString(getColumnIndexOrThrow(COL_BRANCH)),
                    etapa = getString(getColumnIndexOrThrow(COL_PHASE)),
                    fecha_inicio_real = getString(getColumnIndexOrThrow(COL_START_DATE))
                )
                audits.add(audit)
            }
        }
        cursor.close()
        db.close()
        return audits
    }
}

