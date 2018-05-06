package com.mvvm.demo.db.product;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.mvvm.demo.db.AppDatabase;
import com.mvvm.demo.db.category.CategoryEntity;
import com.mvvm.demo.db.category.CategoryViewModel;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {

    private final LiveData<List<ProductEntity>> list;
    private AppDatabase appDatabase;
    public ProductViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        list = appDatabase.getProductDao().getAll();
    }
    public LiveData<List<ProductEntity>> getList() {
        return list;
    }

    public void insertAll(ProductEntity[] entities){
        new InsertAsyncTask(appDatabase).execute(entities);
    }

    private static class InsertAsyncTask extends AsyncTask<ProductEntity[], Void, Void> {

        private AppDatabase db;

        InsertAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override protected Void doInBackground(ProductEntity[]... productEntities) {
            db.getProductDao().insertAll(productEntities[0]);
            return null;
        }
    }
}
