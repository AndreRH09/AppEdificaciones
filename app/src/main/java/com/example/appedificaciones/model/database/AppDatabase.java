    package com.example.appedificaciones.model.database;

    import android.app.Application;
    import android.content.Context;

    import androidx.room.Database;
    import androidx.room.RoomDatabase;

    import androidx.room.Database;
    import androidx.room.Room;
    import androidx.room.RoomDatabase;

    import com.example.appedificaciones.model.dao.EdificationDao;
    import com.example.appedificaciones.model.dao.FavoriteDao;
    import com.example.appedificaciones.model.dao.PictureDao;
    import com.example.appedificaciones.model.dao.RoomDao;
    import com.example.appedificaciones.model.ent.DoorEntity;
    import com.example.appedificaciones.model.ent.EdificationEntity;
    import com.example.appedificaciones.model.ent.FavoriteEdificationEntity;
    import com.example.appedificaciones.model.ent.PictureEntity;
    import com.example.appedificaciones.model.ent.RoomEntity;
    import com.example.appedificaciones.model.ent.UserEntity;
    import com.example.appedificaciones.model.ent.VertexEntity;

    import com.example.appedificaciones.model.dao.UserDao;
    import com.example.appedificaciones.model.dao.DoorDao;
    import com.example.appedificaciones.model.dao.VertexDao;

    @Database(version = 11,
            entities = {
                    FavoriteEdificationEntity.class,
                    EdificationEntity.class,
                    UserEntity.class,
                    PictureEntity.class,
                    DoorEntity.class,
                    RoomEntity.class,
                    VertexEntity.class
            }
            )

    public abstract class AppDatabase extends RoomDatabase {


        public abstract UserDao userDao();
        public abstract EdificationDao edificationDao();
        public abstract FavoriteDao favoriteDao();
        public abstract RoomDao roomVertexDao();
        public abstract VertexDao vertexDao();

        public abstract PictureDao pictureDao();
        public abstract DoorDao doorDao();

        private static AppDatabase INSTANCE = null;
    
        public static AppDatabase getInstance(Context context){
            synchronized (context){
                AppDatabase instance = INSTANCE;
                if(instance == null){
                    instance = Room.databaseBuilder(
                                    context,
                                    AppDatabase.class,
                                    "database-name"
                            ).fallbackToDestructiveMigration()
                            .build();

                    INSTANCE = instance;
                }
                return instance;
            }
        }
    }