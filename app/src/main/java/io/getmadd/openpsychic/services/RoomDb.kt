import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import android.util.Log
import io.getmadd.openpsychic.model.PsychicsOnDisplay
import io.getmadd.openpsychic.model.User
import io.getmadd.openpsychic.model.UserHistoryObject
import io.getmadd.openpsychic.services.PsychicsOnDisplayDao

@Database(entities = [UserHistoryObject::class, PsychicsOnDisplay::class, User::class], version = 3, exportSchema = false)
abstract class RoomDb : RoomDatabase() {

    abstract fun psychicsOnDisplayDao(): PsychicsOnDisplayDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDb? = null

        fun getDatabase(context: Context): RoomDb {
            return INSTANCE ?: synchronized(this) {
                try {
                    val appContext = context.applicationContext
                        ?: throw IllegalStateException("Context is null during database initialization")

                    val instance = Room.databaseBuilder(
                        appContext,
                        RoomDb::class.java,
                        "App Database"
                    ).build()
                    INSTANCE = instance
                    instance
                } catch (e: Exception) {
                    Log.e("DatabaseInit", "Error initializing database", e)
                    throw e
                }
            }
        }
    }
}