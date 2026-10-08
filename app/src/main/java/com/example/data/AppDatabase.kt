package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.AIGenerationItem
import com.example.model.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Project::class, AIGenerationItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "omnicut_studio.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate starter projects
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).projectDao()
                            dao.insertProject(
                                Project(
                                    title = "Phim ngắn Cyberpunk Tokyo Vietsub",
                                    aspectRatio = "9:16",
                                    durationSeconds = 18.5f,
                                    resolution = "4K",
                                    fps = 60,
                                    isCloudSynced = true,
                                    cloudSyncTimestamp = "Đã đồng bộ vừa xong",
                                    thumbnailKey = "banner",
                                    trackCount = 5,
                                    lastEdited = "Hôm nay 10:45"
                                )
                            )
                            dao.insertProject(
                                Project(
                                    title = "Lookbook Thời trang Hè 2026",
                                    aspectRatio = "9:16",
                                    durationSeconds = 24.0f,
                                    resolution = "4K",
                                    fps = 60,
                                    isCloudSynced = true,
                                    cloudSyncTimestamp = "Đã đồng bộ 2 giờ trước",
                                    thumbnailKey = "model",
                                    trackCount = 4,
                                    lastEdited = "Hôm qua"
                                )
                            )
                            dao.insertProject(
                                Project(
                                    title = "Quảng cáo Nước hoa Cao cấp Aurora",
                                    aspectRatio = "16:9",
                                    durationSeconds = 15.0f,
                                    resolution = "4K",
                                    fps = 60,
                                    isCloudSynced = true,
                                    cloudSyncTimestamp = "Đã đồng bộ 1 ngày trước",
                                    thumbnailKey = "product",
                                    trackCount = 4,
                                    lastEdited = "2 ngày trước"
                                )
                            )

                            // Prepopulate sample generation history
                            dao.insertGeneration(
                                AIGenerationItem(
                                    type = "KLING_3_0",
                                    modelName = "Kling 3.0 Ultra",
                                    prompt = "Siêu xe điện tương lai drift trên đại lộ Tokyo dưới trời mưa neon phản chiếu điện ảnh",
                                    aspectRatio = "9:16",
                                    status = "Hoàn thành",
                                    resultTitle = "Siêu xe Drift Đường phố Neon",
                                    resultPreview = "4K • 60fps • Vật lý chân thực • Quỹ đạo Camera xoay"
                                )
                            )
                            dao.insertGeneration(
                                AIGenerationItem(
                                    type = "VEO_3_1",
                                    modelName = "VEO 3.1 AudioSync",
                                    prompt = "Vách đá đại dương điện ảnh với sóng biển dâng trào và tiếng sấm sét âm thanh đồng bộ",
                                    aspectRatio = "16:9",
                                    status = "Hoàn thành",
                                    resultTitle = "Vách đá Bão biển & Âm thanh Đồng bộ",
                                    resultPreview = "Điện ảnh 4K • Hiệu ứng âm thanh sóng vỗ khớp thời gian"
                                )
                            )
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
