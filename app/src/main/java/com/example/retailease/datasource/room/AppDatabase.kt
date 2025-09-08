package com.example.retailease.datasource.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
import com.example.retailease.models.Customer
import com.example.retailease.models.DraftOrder
import com.example.retailease.models.DraftOrderItem
import com.example.retailease.models.Employee
import com.example.retailease.models.Order
import com.example.retailease.models.OrderItem
import com.example.retailease.models.Product
import com.example.retailease.models.Salesman
import com.example.retailease.models.SalesmanDraftOrder
import com.example.retailease.models.SalesmanDraftOrderItem
import com.example.retailease.models.SalesmanLedger
import com.example.retailease.models.SalesmanOrder
import com.example.retailease.models.SalesmanOrderItem
import com.example.retailease.models.WholesaleProduct
import com.example.retailease.models.converters.Converters

@Database(
    entities = [Customer::class, Employee::class, Product::class, Order::class, OrderItem::class, WholesaleProduct::class, Salesman::class, DraftOrder::class, DraftOrderItem::class, SalesmanDraftOrder::class, SalesmanDraftOrderItem::class, SalesmanOrder::class, SalesmanOrderItem::class, SalesmanLedger::class ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun salesmanDao(): SalesmanDao
    abstract fun wholesaleProductDao(): WholesaleProductDao
    abstract fun draftOrderDao(): DraftOrderDao
    abstract fun draftOrderItemDao(): DraftOrderItemDao
    abstract fun salesmanDraftOrderDao(): SalesmanDraftOrderDao
    abstract fun salesmanDraftOrderItemDao(): SalesmanDraftOrderItemDao
    abstract fun salesmanOrderDao(): SalesmanOrderDao
    abstract fun salesmanOrderItemDao(): SalesmanOrderItemDao
    abstract fun ledgerDao(): LedgerDao

}