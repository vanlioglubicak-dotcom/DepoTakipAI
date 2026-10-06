package com.example.depotakipai.ui.catalog

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

import coil.compose.AsyncImage

import com.example.depotakipai.data.catalog.CatalogColorCatalog
import com.example.depotakipai.data.catalog.CatalogColorOption
import com.example.depotakipai.data.catalog.CatalogRepository
import com.example.depotakipai.data.local.DatabaseProvider
import com.example.depotakipai.data.local.entity.CatalogItemEntity

import kotlinx.coroutines.launch


private val CatalogBackground =
    Color(0xFFF6F7F9)

private val DarkText =
    Color(0xFF263238)

private val BlueSlate =
    Color(0xFF557392)

private val SoftText =
    Color(0xFF607D8B)


data class CatalogPreviewItem(
    val productNumber: String,
    val company: String,
    val color: String,
    val size: String
)


private data class CatalogModelGroup(
    val modelNumber: String,
    val displayModel: String,
    val imagePath: String,
    val colors: List<String>,
    val sizes: List<String>,
    val items: List<CatalogItemEntity>
)


@Composable
fun CatalogScreen(
    onBack: () -> Unit = {},
    onAddCatalogItem: () -> Unit = {},
    items: List<CatalogPreviewItem> = emptyList()
) {

    val context =
        LocalContext.current

    val database =
        remember {
            DatabaseProvider.getDatabase(context)
        }

    val repository =
        remember(database) {

            CatalogRepository(
                catalogItemDao =
                    database.catalogItemDao()
            )
        }

    val catalogItems by
    repository
        .getAll()
        .collectAsState(
            initial = emptyList()
        )

    val scope =
        rememberCoroutineScope()


    var searchText by remember {
        mutableStateOf("")
    }

    var selectedModel by remember {
        mutableStateOf<CatalogModelGroup?>(null)
    }

    var capturedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var showCameraResult by remember {
        mutableStateOf(false)
    }

    var cameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var colorToEdit by remember {
        mutableStateOf<String?>(null)
    }


    // ============================================================
    // KAMERA
    // ============================================================

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.TakePicture()
        ) { success ->

            if (
                success &&
                cameraUri != null
            ) {

                capturedImageUri =
                    cameraUri

                showCameraResult =
                    true

            } else {

                cameraUri = null
            }
        }


    fun openCatalogSearchCamera() {

        val values =
            ContentValues().apply {

                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "katalog_arama_${System.currentTimeMillis()}.jpg"
                )

                put(
                    MediaStore.Images.Media.MIME_TYPE,
                    "image/jpeg"
                )
            }

        val uri =
            context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )

        if (uri != null) {

            cameraUri = uri

            cameraLauncher.launch(uri)
        }
    }


    // ============================================================
    // MODEL GRUPLARI
    // ============================================================

    val modelGroups =
        remember(catalogItems) {

            catalogItems

                .groupBy {
                    normalizeModelNumber(
                        it.productNumber
                    )
                }

                .mapNotNull { (modelNumber, modelItems) ->

                    if (
                        modelNumber.isBlank()
                    ) {

                        null

                    } else {

                        val first =
                            modelItems.first()

                        CatalogModelGroup(

                            modelNumber =
                                modelNumber,

                            displayModel =
                                "SNZ-$modelNumber",

                            imagePath =
                                modelItems
                                    .firstOrNull {
                                        it.imagePath.isNotBlank()
                                    }
                                    ?.imagePath
                                    ?: first.imagePath,

                            colors =
                                modelItems
                                    .map {
                                        it.color.trim()
                                    }
                                    .filter {
                                        it.isNotBlank()
                                    }
                                    .distinctBy {
                                        CatalogColorCatalog.normalize(
                                            it
                                        )
                                    }
                                    .sorted(),

                            sizes =
                                modelItems
                                    .map {
                                        it.size.trim()
                                    }
                                    .filter {
                                        it.isNotBlank()
                                    }
                                    .distinct()
                                    .sorted(),

                            items =
                                modelItems
                        )
                    }
                }

                .sortedWith(

                    compareBy<CatalogModelGroup> {

                        it.modelNumber
                            .toIntOrNull()
                            ?: Int.MAX_VALUE

                    }.thenBy {

                        it.modelNumber
                    }
                )
        }


    // ============================================================
    // ARAMA
    // ============================================================

    val filteredModels =
        remember(
            searchText,
            modelGroups
        ) {

            val rawQuery =
                searchText.trim()

            val normalizedQuery =
                normalizeSearchQuery(
                    rawQuery
                )

            if (
                rawQuery.isBlank()
            ) {

                modelGroups

            } else {

                modelGroups.filter { model ->

                    val modelMatches =
                        normalizedQuery.isNotBlank() &&
                                model.modelNumber.contains(
                                    normalizedQuery,
                                    ignoreCase = true
                                )

                    val displayMatches =
                        model.displayModel.contains(
                            rawQuery,
                            ignoreCase = true
                        )

                    val colorMatches =
                        model.colors.any {

                            it.contains(
                                rawQuery,
                                ignoreCase = true
                            )
                        }

                    val sizeMatches =
                        model.sizes.any {

                            it.contains(
                                rawQuery,
                                ignoreCase = true
                            )
                        }

                    modelMatches ||
                            displayMatches ||
                            colorMatches ||
                            sizeMatches
                }
            }
        }


    // ============================================================
    // ANA KATALOG
    // ============================================================

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    CatalogBackground
                )
    ) {


        // ========================================================
        // BAŞLIK
        // ========================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White
                    )
                    .padding(
                        start = 8.dp,
                        end = 12.dp,
                        top = 18.dp,
                        bottom = 14.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Text(
                    text = "‹",
                    fontSize = 38.sp,
                    color =
                        Color(0xFF37474F)
                )
            }


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = "KATALOG",
                    fontSize = 23.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        DarkText
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text = "Ürün modelleri",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }


        // ========================================================
        // ARAMA + KAMERA
        // ========================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 14.dp,
                        bottom = 8.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            OutlinedTextField(

                value =
                    searchText,

                onValueChange = {
                    searchText = it
                },

                modifier =
                    Modifier.weight(1f),

                singleLine = true,

                leadingIcon = {

                    Text(
                        text = "⌕",
                        fontSize = 27.sp,
                        color =
                            BlueSlate
                    )
                },

                placeholder = {

                    Text(
                        text =
                            "Model no, renk veya beden ara..."
                    )
                },

                shape =
                    RoundedCornerShape(
                        14.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.width(8.dp)
            )


            Box(

                modifier =
                    Modifier
                        .size(58.dp)
                        .background(
                            BlueSlate,
                            RoundedCornerShape(
                                14.dp
                            )
                        )
                        .clickable {

                            openCatalogSearchCamera()
                        },

                contentAlignment =
                    Alignment.Center

            ) {

                Text(
                    text = "📷",
                    fontSize = 26.sp
                )
            }
        }


        // ========================================================
        // MODEL SAYISI
        // ========================================================

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 18.dp,
                        vertical = 6.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text =
                    "KATALOG MODELLERİ",

                fontSize = 15.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Color(0xFF455A64)
            )


            Spacer(
                modifier =
                    Modifier.weight(1f)
            )


            Text(
                text =
                    "${filteredModels.size} model",

                fontSize = 14.sp,

                color =
                    Color.Gray
            )
        }


        // ========================================================
        // MODEL LİSTESİ
        // ========================================================

        if (
            filteredModels.isEmpty()
        ) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),

                contentAlignment =
                    Alignment.Center
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "⌕",
                        fontSize = 44.sp,
                        color = Color.Gray
                    )

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            "Katalogda model bulunamadı",

                        fontSize = 18.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            DarkText
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "Model numarası, renk veya beden deneyin.",

                        fontSize = 14.sp,

                        color =
                            Color.Gray
                    )
                }
            }

        } else {

            LazyVerticalGrid(

                columns =
                    GridCells.Fixed(2),

                modifier =
                    Modifier.fillMaxSize(),

                contentPadding =
                    PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        top = 4.dp,
                        bottom = 28.dp
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp),

                verticalArrangement =
                    Arrangement.spacedBy(10.dp)

            ) {

                items(

                    items =
                        filteredModels,

                    key = {
                        it.modelNumber
                    }

                ) { model ->

                    CatalogModelCard(

                        model = model,

                        onClick = {

                            selectedModel =
                                model
                        }
                    )
                }
            }
        }
    }


    // ============================================================
    // MODEL DETAYI
    // ============================================================

    selectedModel?.let { model ->

        CatalogModelDetailDialog(

            model = model,

            onDismiss = {

                selectedModel =
                    null
            },

            onColorEdit = { color ->

                colorToEdit =
                    color
            },

            onAddColor = { newColor ->

                scope.launch {

                    val alreadyExists =
                        model.items.any { item ->

                            CatalogColorCatalog.normalize(
                                item.color
                            ) ==
                                    CatalogColorCatalog.normalize(
                                        newColor
                                    )
                        }

                    if (!alreadyExists) {

                        val baseItem =
                            model.items.firstOrNull()

                        if (baseItem != null) {

                            repository.insert(

                                baseItem.copy(

                                    id =
                                        "${baseItem.id}_color_${System.currentTimeMillis()}",

                                    productNumber =
                                        model.modelNumber,

                                    company =
                                        "SNZ",

                                    color =
                                        newColor,

                                    // Gerçek katalog fotoğrafı
                                    // olmadığı için BOŞ bırakıyoruz.
                                    //
                                    // Böylece sistem bu rengi
                                    // gerçek katalog görseli
                                    // sanmıyor.
                                    imagePath =
                                        "",

                                    size =
                                        ""
                                )
                            )
                        }
                    }
                }
            },

            onSaveModel = { newModelNumber ->

                val cleanedNumber =
                    normalizeModelNumber(
                        newModelNumber
                    )

                if (
                    cleanedNumber.isNotBlank()
                ) {

                    scope.launch {

                        model.items.forEach { item ->

                            repository.insert(

                                item.copy(

                                    productNumber =
                                        cleanedNumber,

                                    company =
                                        "SNZ"
                                )
                            )
                        }

                        selectedModel =
                            null
                    }
                }
            }
        )
    }


    // ============================================================
    // RENK DÜZENLEME
    // ============================================================

    colorToEdit?.let { oldColor ->

        CatalogColorEditDialog(

            oldColor =
                oldColor,

            onDismiss = {

                colorToEdit =
                    null
            },

            onSave = { newColor ->

                val cleanedColor =
                    newColor.trim()

                if (
                    cleanedColor.isNotBlank()
                ) {

                    selectedModel?.let { model ->

                        scope.launch {

                            model.items.forEach { item ->

                                if (
                                    item.color
                                        .trim()
                                        .equals(
                                            oldColor.trim(),
                                            ignoreCase = true
                                        )
                                ) {

                                    repository.insert(

                                        item.copy(
                                            color =
                                                cleanedColor
                                        )
                                    )
                                }
                            }

                            colorToEdit =
                                null
                        }
                    }
                }
            }
        )
    }


    // ============================================================
    // KAMERA SONUCU
    // ============================================================

    if (
        showCameraResult
    ) {

        CatalogCameraResultDialog(

            imageUri =
                capturedImageUri,

            onDismiss = {

                showCameraResult =
                    false

                capturedImageUri =
                    null
            },

            onSearch = {

                showCameraResult =
                    false
            }
        )
    }
}


// =================================================================
// MODEL KARTI
// =================================================================

@Composable
private fun CatalogModelCard(
    model: CatalogModelGroup,
    onClick: () -> Unit
) {

    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },

        shape =
            RoundedCornerShape(14.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.fillMaxWidth()
        ) {

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .background(
                            Color(0xFFE9ECEF)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                AsyncImage(

                    model =
                        buildAssetUri(
                            model.imagePath
                        ),

                    contentDescription =
                        model.displayModel,

                    modifier =
                        Modifier.fillMaxSize(),

                    contentScale =
                        ContentScale.Fit
                )
            }


            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
            ) {

                Text(

                    text =
                        model.displayModel,

                    fontSize = 19.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        DarkText
                )


                if (
                    model.colors.isNotEmpty()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(5.dp)
                    )

                    Text(

                        text =
                            "Renk: ${model.colors.joinToString(" • ")}",

                        fontSize = 12.sp,

                        color =
                            SoftText,

                        maxLines = 2
                    )
                }


                if (
                    model.sizes.isNotEmpty()
                ) {

                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )

                    Text(

                        text =
                            "Beden: ${model.sizes.joinToString(" • ")}",

                        fontSize = 12.sp,

                        color =
                            SoftText,

                        maxLines = 1
                    )
                }
            }
        }
    }
}


// =================================================================
// MODEL DETAY
// =================================================================

@Composable
private fun CatalogModelDetailDialog(

    model: CatalogModelGroup,

    onDismiss: () -> Unit,

    onColorEdit: (String) -> Unit,

    onAddColor: (String) -> Unit,

    onSaveModel: (String) -> Unit
) {

    var editedModelNumber by remember(
        model.modelNumber
    ) {

        mutableStateOf(
            "SNZ-${model.modelNumber}"
        )
    }


    var selectedColor by remember(
        model.modelNumber
    ) {

        mutableStateOf(
            model.colors.firstOrNull()
        )
    }


    var availableColors by remember(
        model.modelNumber,
        model.colors
    ) {

        mutableStateOf(
            model.colors
        )
    }


    // =============================================================
    // SEÇİLEN RENGİN GÖRSELİ
    // =============================================================

    val selectedImagePath = remember(

        selectedColor,

        model.items,

        model.imagePath

    ) {

        val currentColor =
            selectedColor

        if (
            currentColor.isNullOrBlank()
        ) {

            model.imagePath

        } else {

            val normalizedSelected =
                CatalogColorCatalog.normalize(
                    currentColor
                )

            val matchingItem =
                model.items.firstOrNull { item ->

                    item.imagePath.isNotBlank() &&

                            CatalogColorCatalog.normalize(
                                item.color
                            ) ==
                            normalizedSelected
                }

            matchingItem?.imagePath
                ?: model.imagePath
        }
    }


    // =============================================================
    // GERÇEK KATALOG GÖRSELİ VAR MI?
    // =============================================================

    val selectedColorHasRealImage = remember(

        selectedColor,

        model.items

    ) {

        val currentColor =
            selectedColor

        if (
            currentColor.isNullOrBlank()
        ) {

            false

        } else {

            val normalizedSelected =
                CatalogColorCatalog.normalize(
                    currentColor
                )

            model.items.any { item ->

                item.imagePath.isNotBlank() &&

                        CatalogColorCatalog.normalize(
                            item.color
                        ) ==
                        normalizedSelected
            }
        }
    }


    val imageDescription =
        selectedColor?.let { color ->

            "${model.displayModel} - $color"

        } ?: model.displayModel


    Dialog(

        onDismissRequest =
            onDismiss,

        properties =
            DialogProperties(
                usePlatformDefaultWidth =
                    false
            )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        CatalogBackground
                    )
        ) {


            // =====================================================
            // BAŞLIK
            // =====================================================

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White
                        )
                        .padding(
                            start = 4.dp,
                            end = 12.dp,
                            top = 16.dp,
                            bottom = 12.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick =
                        onDismiss
                ) {

                    Text(
                        text = "×",
                        fontSize = 32.sp,
                        color = DarkText
                    )
                }


                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text = "MODEL DETAYI",
                        fontSize = 21.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color = DarkText
                    )

                    Text(
                        text =
                            "Katalog modelini düzenle",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }


            // =====================================================
            // ÜRÜN GÖRSELİ
            // =====================================================

            Box(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(470.dp)
                        .background(
                            Color(0xFFE9ECEF)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                AsyncImage(

                    model =
                        buildAssetUri(
                            selectedImagePath
                        ),

                    contentDescription =
                        imageDescription,

                    modifier =
                        Modifier.fillMaxSize(),

                    contentScale =
                        ContentScale.Fit
                )
            }


            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(18.dp)
            ) {


                // =================================================
                // MODEL KODU
                // =================================================

                Text(

                    text = "MODEL KODU",

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        SoftText
                )


                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )


                OutlinedTextField(

                    value =
                        editedModelNumber,

                    onValueChange = {

                        editedModelNumber =
                            it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    trailingIcon = {

                        Text(
                            text = "✎",
                            fontSize = 22.sp,
                            color =
                                BlueSlate
                        )
                    },

                    shape =
                        RoundedCornerShape(
                            12.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(20.dp)
                )


                // =================================================
                // MODELİN MEVCUT RENKLERİ
                // =================================================

                Text(

                    text =
                        "MODEL RENKLERİ",

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        SoftText
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                if (
                    availableColors.isEmpty()
                ) {

                    Text(

                        text =
                            "Bu model için henüz renk eklenmemiş.",

                        fontSize = 14.sp,

                        color =
                            Color.Gray
                    )

                } else {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(
                                    rememberScrollState()
                                ),

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                8.dp
                            )
                    ) {

                        availableColors.forEach { color ->

                            CatalogColorChip(

                                color =
                                    color,

                                selected =
                                    selectedColor?.let {

                                            current ->

                                        CatalogColorCatalog.normalize(
                                            current
                                        ) ==
                                                CatalogColorCatalog.normalize(
                                                    color
                                                )

                                    } ?: false,

                                onClick = {

                                    selectedColor =
                                        color
                                },

                                onEdit = {

                                    onColorEdit(
                                        color
                                    )
                                }
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    if (
                        selectedColorHasRealImage
                    ) {

                        Text(

                            text =
                                "✓ Katalogdaki gerçek $selectedColor görseli gösteriliyor.",

                            fontSize = 12.sp,

                            color =
                                Color(0xFF2E7D32),

                            fontWeight =
                                FontWeight.Medium
                        )

                    } else {

                        Text(

                            text =
                                "Bu renk için gerçek katalog fotoğrafı yok. Orijinal model görseli korunuyor.",

                            fontSize = 12.sp,

                            color =
                                Color.Gray
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // =================================================
                // RENK KATALOĞU
                // =================================================

                Text(

                    text =
                        "RENK KATALOĞU",

                    fontSize = 15.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color(0xFF607D8B)
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(

                    text =
                        "Tüm modeller için ortak 25 renk paleti",

                    fontSize = 12.sp,

                    color =
                        Color.Gray
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                // =================================================
                // 25 RENK PALETİ
                // =================================================

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(
                                rememberScrollState()
                            ),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            8.dp
                        )
                ) {

                    CatalogColorCatalog.colors.forEach { catalogColor ->

                        val alreadyAdded =
                            availableColors.any {

                                CatalogColorCatalog.normalize(
                                    it
                                ) ==
                                        CatalogColorCatalog.normalize(
                                            catalogColor.name
                                        )
                            }


                        CatalogPaletteChip(

                            color =
                                catalogColor,

                            selected =
                                selectedColor?.let {

                                        current ->

                                    CatalogColorCatalog.normalize(
                                        current
                                    ) ==
                                            CatalogColorCatalog.normalize(
                                                catalogColor.name
                                            )

                                } ?: false,

                            alreadyAdded =
                                alreadyAdded,

                            onClick = {

                                selectedColor =
                                    catalogColor.name


                                if (
                                    !alreadyAdded
                                ) {

                                    availableColors =
                                        (
                                                availableColors +
                                                        catalogColor.name
                                                )
                                            .distinctBy {

                                                CatalogColorCatalog.normalize(
                                                    it
                                                )
                                            }


                                    onAddColor(
                                        catalogColor.name
                                    )
                                }
                            }
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                Text(

                    text =
                        "Renge bastığında model renklerine eklenir. Gerçek katalog görseli varsa o görsel kullanılır.",

                    fontSize = 12.sp,

                    color =
                        Color.Gray
                )


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // =================================================
                // BEDENLER
                // =================================================

                Text(

                    text =
                        "BEDENLER",

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        SoftText
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                if (
                    model.sizes.isEmpty()
                ) {

                    Text(

                        text =
                            "Katalogda beden bilgisi yok.",

                        fontSize = 14.sp,

                        color =
                            Color.Gray
                    )

                } else {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(
                                    rememberScrollState()
                                ),

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                8.dp
                            )
                    ) {

                        model.sizes.forEach { size ->

                            Box(

                                modifier =
                                    Modifier
                                        .background(
                                            Color.White,
                                            RoundedCornerShape(
                                                10.dp
                                            )
                                        )
                                        .padding(
                                            horizontal = 14.dp,
                                            vertical = 8.dp
                                        )
                            ) {

                                Text(

                                    text =
                                        size,

                                    fontSize =
                                        13.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        DarkText,

                                    maxLines =
                                        1
                                )
                            }
                        }
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )


                // =================================================
                // MODEL KODU KAYDET
                // =================================================

                Button(

                    onClick = {

                        onSaveModel(
                            editedModelNumber
                        )
                    },

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .navigationBarsPadding(),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                BlueSlate
                        )
                ) {

                    Text(

                        text =
                            "MODEL KODUNU KAYDET",

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )
            }
        }
    }
}


// =================================================================
// MODEL RENK CHIP
// =================================================================

@Composable
private fun CatalogColorChip(

    color: String,

    selected: Boolean,

    onClick: () -> Unit,

    onEdit: () -> Unit
) {

    val catalogColor =
        CatalogColorCatalog.findByName(
            color
        )


    val backgroundColor =
        if (selected) {

            BlueSlate

        } else {

            Color.White
        }


    val textColor =
        if (selected) {

            Color.White

        } else {

            DarkText
        }


    Row(

        modifier =
            Modifier
                .background(
                    backgroundColor,
                    RoundedCornerShape(
                        10.dp
                    )
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {


        Row(

            modifier =
                Modifier
                    .clickable {
                        onClick()
                    }
                    .padding(
                        start = 10.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 4.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            if (
                catalogColor != null
            ) {

                Box(

                    modifier =
                        Modifier
                            .size(18.dp)
                            .background(
                                catalogColor.color,
                                RoundedCornerShape(
                                    50
                                )
                            )
                )

                Spacer(
                    modifier =
                        Modifier.width(6.dp)
                )
            }


            Text(

                text =
                    color,

                fontSize =
                    13.sp,

                color =
                    textColor,

                fontWeight =
                    if (selected) {

                        FontWeight.Bold

                    } else {

                        FontWeight.Normal
                    },

                maxLines =
                    1
            )
        }


        Text(

            text =
                "✎",

            modifier =
                Modifier
                    .clickable {
                        onEdit()
                    }
                    .padding(
                        horizontal = 7.dp,
                        vertical = 8.dp
                    ),

            fontSize =
                14.sp,

            color =
                if (selected) {

                    Color.White

                } else {

                    BlueSlate
                }
        )
    }
}


// =================================================================
// RENK KATALOĞU CHIP
// =================================================================

@Composable
private fun CatalogPaletteChip(

    color:
    com.example.depotakipai.data.catalog.CatalogColorOption,

    selected: Boolean,

    alreadyAdded: Boolean,

    onClick: () -> Unit
) {

    val backgroundColor =

        when {

            selected ->
                BlueSlate

            alreadyAdded ->
                Color(0xFFE8EEF3)

            else ->
                Color.White
        }


    val textColor =

        if (selected) {

            Color.White

        } else {

            DarkText
        }


    Column(

        modifier =
            Modifier
                .width(82.dp)
                .background(
                    backgroundColor,
                    RoundedCornerShape(
                        12.dp
                    )
                )
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 8.dp,
                    vertical = 9.dp
                ),

        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {


        Box(

            modifier =
                Modifier
                    .size(28.dp)
                    .background(
                        color.color,
                        RoundedCornerShape(
                            50
                        )
                    )
        )


        Spacer(
            modifier =
                Modifier.height(6.dp)
        )


        Text(

            text =
                color.name,

            fontSize =
                11.sp,

            fontWeight =
                if (
                    selected ||
                    alreadyAdded
                ) {

                    FontWeight.Bold

                } else {

                    FontWeight.Normal
                },

            color =
                textColor,

            maxLines =
                1
        )


        Spacer(
            modifier =
                Modifier.height(2.dp)
        )


        Text(

            text =
                if (alreadyAdded) {

                    "EKLİ"

                } else {

                    "EKLE"
                },

            fontSize =
                9.sp,

            color =
                if (selected) {

                    Color.White

                } else {

                    Color(0xFF78909C)
                }
        )
    }
}


// =================================================================
// RENK DÜZENLEME
// =================================================================

@Composable
private fun CatalogColorEditDialog(

    oldColor: String,

    onDismiss: () -> Unit,

    onSave: (String) -> Unit
) {

    var editedColor by remember(
        oldColor
    ) {

        mutableStateOf(
            oldColor
        )
    }


    Dialog(

        onDismissRequest =
            onDismiss,

        properties =
            DialogProperties(
                usePlatformDefaultWidth =
                    true
            )
    ) {

        Card(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

            shape =
                RoundedCornerShape(
                    18.dp
                ),

            colors =
                CardDefaults.cardColors(
                    containerColor =
                        Color.White
                )
        ) {

            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
            ) {

                Text(

                    text =
                        "RENK DÜZENLE",

                    fontSize =
                        21.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        DarkText
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(

                    text =
                        "Mevcut renk: $oldColor",

                    fontSize =
                        13.sp,

                    color =
                        SoftText
                )


                Spacer(
                    modifier =
                        Modifier.height(16.dp)
                )


                OutlinedTextField(

                    value =
                        editedColor,

                    onValueChange = {

                        editedColor =
                            it
                    },

                    modifier =
                        Modifier.fillMaxWidth(),

                    singleLine = true,

                    label = {

                        Text(
                            text =
                                "Yeni renk"
                        )
                    },

                    trailingIcon = {

                        Text(
                            text = "✎",
                            fontSize = 21.sp,
                            color =
                                BlueSlate
                        )
                    },

                    shape =
                        RoundedCornerShape(
                            12.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {

                    Button(

                        onClick =
                            onDismiss,

                        modifier =
                            Modifier.weight(1f),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(

                                containerColor =
                                    Color(0xFFE8ECEF),

                                contentColor =
                                    DarkText
                            )
                    ) {

                        Text(
                            text =
                                "VAZGEÇ"
                        )
                    }


                    Button(

                        onClick = {

                            val cleaned =
                                editedColor.trim()

                            if (
                                cleaned.isNotBlank()
                            ) {

                                onSave(
                                    cleaned
                                )
                            }
                        },

                        modifier =
                            Modifier.weight(1f),

                        shape =
                            RoundedCornerShape(
                                12.dp
                            ),

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    BlueSlate
                            )
                    ) {

                        Text(

                            text =
                                "KAYDET",

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// =================================================================
// KAMERA SONUCU
// =================================================================

@Composable
private fun CatalogCameraResultDialog(

    imageUri: Uri?,

    onDismiss: () -> Unit,

    onSearch: () -> Unit
) {

    Dialog(

        onDismissRequest =
            onDismiss,

        properties =
            DialogProperties(
                usePlatformDefaultWidth =
                    false
            )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black
                    )
        ) {


            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 16.dp,
                            start = 8.dp,
                            end = 8.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                IconButton(
                    onClick =
                        onDismiss
                ) {

                    Text(
                        text = "×",
                        fontSize = 32.sp,
                        color =
                            Color.White
                    )
                }


                Text(

                    text =
                        "KATALOG FOTOĞRAF ARAMA",

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color.White
                )
            }


            Box(

                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),

                contentAlignment =
                    Alignment.Center
            ) {

                if (
                    imageUri != null
                ) {

                    AsyncImage(

                        model =
                            imageUri,

                        contentDescription =
                            "Çekilen ürün",

                        modifier =
                            Modifier.fillMaxSize(),

                        contentScale =
                            ContentScale.Fit
                    )
                }
            }


            Column(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White
                        )
                        .padding(16.dp)
            ) {

                Text(

                    text =
                        "Fotoğraf hazır",

                    fontSize =
                        18.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        DarkText
                )


                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )


                Text(

                    text =
                        "Bu fotoğraf katalogdaki ürünlerle karşılaştırılacak.",

                    fontSize =
                        13.sp,

                    color =
                        SoftText
                )


                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )


                Button(

                    onClick =
                        onSearch,

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        ),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                BlueSlate
                        )
                ) {

                    Text(

                        text =
                            "KATALOGDA ARA",

                        fontSize =
                            16.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


// =================================================================
// MODEL NUMARASI
// =================================================================

private fun normalizeModelNumber(
    value: String
): String {

    return value

        .trim()

        .uppercase()

        .replace(
            "İ",
            "I"
        )

        .replace(
            " ",
            ""
        )

        .replace(
            "-",
            ""
        )

        .replace(
            "_",
            ""
        )

        .replace(
            Regex("^[A-Z]+"),
            ""
        )

        .filter {
            it.isDigit()
        }
}


// =================================================================
// ARAMA NORMALİZASYONU
// =================================================================

private fun normalizeSearchQuery(
    value: String
): String {

    val cleaned =

        value

            .trim()

            .uppercase()

            .replace(
                "İ",
                "I"
            )

            .replace(
                " ",
                ""
            )

            .replace(
                "-",
                ""
            )

            .replace(
                "_",
                ""
            )


    return cleaned

        .replace(
            Regex("^[A-Z]+"),
            ""
        )

        .filter {
            it.isDigit()
        }
}


// =================================================================
// ASSET URI
// =================================================================

private fun buildAssetUri(
    imagePath: String
): String {

    var path =
        imagePath
            .trim()
            .replace(
                "\\",
                "/"
            )


    path =
        path.removePrefix(
            "file:///android_asset/"
        )


    path =
        path.removePrefix(
            "android_asset/"
        )


    if (
        !path.startsWith(
            "catalog/"
        )
    ) {

        path =

            if (
                path.startsWith(
                    "images/"
                )
            ) {

                "catalog/$path"

            } else {

                "catalog/images/$path"
            }
    }


    return "file:///android_asset/$path"
}