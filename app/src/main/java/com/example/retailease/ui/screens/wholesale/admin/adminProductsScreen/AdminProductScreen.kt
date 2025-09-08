package com.example.retailease.ui.screens.wholesale.admin.adminProductsScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.retailease.models.Product
import com.example.retailease.models.WholesaleProduct
import com.example.retailease.ui.screens.wholesale.admin.adminViewModels.AdminProductViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductScreen(
    adminProductViewModel: AdminProductViewModel = hiltViewModel(),
    onAddWholesale: (Int?) -> Unit = {},
    onAddRetail: (Int?) -> Unit = {},
) {
    val tabTitles = listOf("Wholesale", "Retail")
    val pagerState = rememberPagerState { tabTitles.size }
    val selectedTabIndex by remember { derivedStateOf { pagerState.currentPage } }
    val coroutineScope = rememberCoroutineScope()

    val productsList = adminProductViewModel.productsList.collectAsStateWithLifecycle()
    val wholesaleProductsList = adminProductViewModel.wholesaleProductsList.collectAsStateWithLifecycle()


    Column(modifier = Modifier.fillMaxSize()) {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = { Text(text = title) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> ProductList(
                        items = wholesaleProductsList.value,
                        onEdit = { wholesaleProductId ->
                            onAddWholesale(wholesaleProductId)
                        },
                        onDelete = { wholesaleProduct ->
                            adminProductViewModel.deleteWholesaleProduct(wholesaleProduct)
                        }
                    )

                    1 -> ProductGrid(
                        products = productsList.value,
                        onEdit = { productId ->
                            onAddRetail(productId)
                        },
                        onDelete = { product ->
                            adminProductViewModel.deleteProduct(product)
                        }
                    )
                }
            }

            FloatingActionButton(
                onClick = {
                    if (selectedTabIndex == 0) onAddWholesale(null) else onAddRetail(null)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)

            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                    Text(if (selectedTabIndex == 0) "Add Wholesale Product" else "Add Retail Product")
                }
            }
        }
    }
}

@Composable
fun ProductList(
    items: List<WholesaleProduct>,
    onEdit: (Int) -> Unit,
    onDelete: (WholesaleProduct) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
    ) {
        items(items, key = { it.productId }) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ProductRow(
                    wholesaleProduct = it,
                    onEdit = { onEdit(it.productId) },
                    onDelete = { onDelete(it) }
                )
                HorizontalDivider(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun ProductRow(
    wholesaleProduct: WholesaleProduct,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val imageFile = wholesaleProduct.imagePath?.let { path -> File(path) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageFile,
            contentDescription = wholesaleProduct.productName,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = wholesaleProduct.productName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2
            )
            Text(
                text = "200gm : ₹${wholesaleProduct.wholesalePrice200gm}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "500gm : ₹${wholesaleProduct.wholesalePrice500gm}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Product",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Product",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ProductGrid(
    products: List<Product>,
    onEdit: (Int) -> Unit,
    onDelete: (Product) -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(products, key = { it.productId }) { product ->
            ProductCard(
                product = product,
                onEdit = { onEdit(product.productId) },
                onDelete = { onDelete(product) }
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val imageFile = product.imagePath?.let { path -> File(path) }
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            AsyncImage(
                model = imageFile,
                contentDescription = product.productName,
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
            Text(product.productName, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "₹ ${"%.2f".format(product.price)}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1668F2),
                    fontSize = 14.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Product",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Product",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}
