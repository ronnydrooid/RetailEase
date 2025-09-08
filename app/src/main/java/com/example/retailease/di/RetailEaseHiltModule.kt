package com.example.retailease.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.retailease.datasource.dao.CustomerDao
import com.example.retailease.datasource.dao.DraftOrderDao
import com.example.retailease.datasource.dao.DraftOrderItemDao
import com.example.retailease.datasource.dao.EmployeeDao
import com.example.retailease.datasource.dao.LedgerDao
import com.example.retailease.datasource.dao.OrderDao
import com.example.retailease.datasource.dao.OrderItemDao
import com.example.retailease.datasource.dao.ProductDao
import com.example.retailease.datasource.dao.SalesmanDao
import com.example.retailease.datasource.dao.SalesmanDraftOrderDao
import com.example.retailease.datasource.dao.SalesmanDraftOrderItemDao
import com.example.retailease.datasource.dao.SalesmanOrderDao
import com.example.retailease.datasource.dao.SalesmanOrderItemDao
import com.example.retailease.datasource.dao.WholesaleProductDao
import com.example.retailease.datasource.preferences.PreferencesManager
import com.example.retailease.datasource.room.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetailEaseHiltModule {
    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "RetailEase.db"
        ).build()
    }


    @Singleton
    @Provides
    fun providesCustomerDao(database: AppDatabase): CustomerDao {
        return database.customerDao()
    }

    @Singleton
    @Provides
    fun providesEmployeeDao(database: AppDatabase): EmployeeDao{
        return database.employeeDao()
    }

    @Singleton
    @Provides
    fun providesProductDao(database: AppDatabase): ProductDao{
        return database.productDao()
    }

    @Singleton
    @Provides
    fun providesOrderDao(database: AppDatabase): OrderDao {
        return database.orderDao()
    }

    @Singleton
    @Provides
    fun providesOrderItemDao(database: AppDatabase): OrderItemDao {
        return database.orderItemDao()
    }

    @Singleton
    @Provides
    fun providesSalesmanDao(database: AppDatabase): SalesmanDao {
        return database.salesmanDao()
    }

    @Singleton
    @Provides
    fun providesWholesaleProductDao(database: AppDatabase): WholesaleProductDao {
        return database.wholesaleProductDao()
    }

    @Singleton
    @Provides
    fun providesDraftOrderDao(database: AppDatabase): DraftOrderDao {
        return database.draftOrderDao()

    }
    @Singleton
    @Provides
    fun providesDraftOrderItemDao(database: AppDatabase): DraftOrderItemDao {
        return database.draftOrderItemDao()

    }

    @Singleton
    @Provides
    fun providesSalesmanDraftOrderDao(database: AppDatabase): SalesmanDraftOrderDao {
        return database.salesmanDraftOrderDao()
    }

    @Singleton
    @Provides
    fun providesSalesmanDraftOrderItemDao(database: AppDatabase): SalesmanDraftOrderItemDao {
        return database.salesmanDraftOrderItemDao()
    }

    @Singleton
    @Provides
    fun providesSalesmanOrderDao(database: AppDatabase): SalesmanOrderDao {
        return database.salesmanOrderDao()
    }

    @Singleton
    @Provides
    fun providesSalesmanOrderItemDao(database: AppDatabase): SalesmanOrderItemDao {
        return database.salesmanOrderItemDao()

    }

    @Singleton
    @Provides
    fun providesLedgerDao(database: AppDatabase): LedgerDao {
        return database.ledgerDao()
    }

    @Singleton
    @Provides
    fun providesPreferencesManager(@ApplicationContext context: Context): PreferencesManager{
        return PreferencesManager(context)
    }
}