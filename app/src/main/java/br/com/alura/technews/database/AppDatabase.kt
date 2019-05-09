package br.com.alura.technews.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia

@Database(entities = [Noticia::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val noticiaDAO: NoticiaDAO

}