package com.example.depotakipai.ui.catalog

import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.depotakipai.data.catalog.CatalogRepository
import com.example.depotakipai.data.local.DatabaseProvider

import com.example.depotakipai.ui.catalog.components.CatalogModelCard
import com.example.depotakipai.ui.catalog.components.CatalogModelCardItem
import com.example.depotakipai.ui.catalog.components.CatalogSearchBar

import com.example.depotakipai.ui.catalog.dialog.CatalogCameraCandidate
import com.example.depotakipai.ui.catalog.dialog.CatalogCameraResultDialog
import com.example.depotakipai.ui.catalog.dialog.CatalogColorEditDialog
import com.example.depotakipai.ui.catalog.dialog.CatalogModelDetailDialog
import com.example.depotakipai.ui.catalog.dialog.CatalogModelEditDialog

import com.example.depotakipai.ui.catalog.model.CatalogModelGroup

import kotlinx.coroutines.launch

import java.util.UUID


private val CatalogBackground = Color(0xFFF6F7F9)
private val DarkText = Color(0xFF263238)
private val SoftText = Color(0xFF607D8B)


data class CatalogPreviewItem(
    val productNumber: String,
    val company: String,
    val color: String,
    val size: String
)


@Composable
fun CatalogScreen(
    onBack: () -> Unit = {},
    onAddCatalogItem: () -> Unit = {},
    items: List<CatalogPreviewItem> = emptyList()
) {
    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    val repository = remember(database) {
        CatalogRepository(
            catalogItemDao = database.catalogItemDao()
        )
    }

    val catalogItems by repository
        .getAll()
        .collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()


    // ------------------------------------------------------------
    // ARAMA
    // ------------------------------------------------------------

    var searchText by remember {
        mutableStateOf("")
    }


    // ------------------------------------------------------------
    // SEÇİLİ MODEL
    // ------------------------------------------------------------

    var selectedModel by remember {
        mutableStateOf<CatalogModelGroup?>(null)
    }

    var selectedColor by remember {
        mutableStateOf("")
    }

    var selectedSize by remember {
        mutableStateOf("")
    }


    // ------------------------------------------------------------
    // RENK DÜZENLEME
    // ------------------------------------------------------------

    var colorDialogVisible by remember {
        mutableStateOf(false)
    }

    var colorDialogInitialValue by remember {
        mutableStateOf("")
    }


    // ------------------------------------------------------------
    // MODEL KODU DÜZENLEME
    // ------------------------------------------------------------

    var modelEditDialogVisible by remember {
        mutableStateOf(false)
    }

    var modelEditInitialValue by remember {
        mutableStateOf("")
    }


    // ------------------------------------------------------------
    // KAMERA
    // ------------------------------------------------------------

    var cameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var cameraResultVisible by remember {
        mutableStateOf(false)
    }


    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->

            if (success && cameraUri != null) {
                cameraResultVisible = true
            } else {
                cameraUri = null
            }
        }


    fun openCatalogCamera() {

        val values = ContentValues().apply {

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


    // ------------------------------------------------------------
    // MODEL GRUPLARI
    // ------------------------------------------------------------

    val modelGroups = remember(catalogItems) {

        catalogItems
            .groupBy { item ->
                normalizeModelNumber(item.productNumber)
            }
            .mapNotNull { entry ->

                val modelNumber = entry.key
                val modelItems = entry.value

                if (
                    modelNumber.isBlank() ||
                    modelItems.isEmpty()
                ) {
                    null
                } else {

                    val firstImage =
                        modelItems.firstOrNull {
                            it.imagePath.isNotBlank()
                        }

                    val first =
                        firstImage
                            ?: modelItems.first()

                    CatalogModelGroup(

                        modelNumber = modelNumber,

                        displayModel =
                            "SNZ-$modelNumber",

                        imagePath =
                            first.imagePath,

                        colors =
                            modelItems
                                .map {
                                    it.color.trim()
                                }
                                .filter {
                                    it.isNotBlank()
                                }
                                .distinct()
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

                        items = modelItems
                    )
                }
            }
            .sortedWith(

                compareBy<CatalogModelGroup> {

                    it.modelNumber.toIntOrNull()
                        ?: Int.MAX_VALUE

                }.thenBy {

                    it.modelNumber
                }
            )
    }


    // ------------------------------------------------------------
    // FİLTRE
    // ------------------------------------------------------------

    val filteredModels =
        remember(
            searchText,
            modelGroups
        ) {

            val query =
                searchText.trim()

            if (query.isBlank()) {

                modelGroups

            } else {

                val normalizedQuery =
                    normalizeSearchQuery(query)

                modelGroups.filter { model ->

                    val modelMatch =
                        normalizedQuery.isNotBlank() &&
                                model.modelNumber.contains(
                                    normalizedQuery,
                                    ignoreCase = true
                                )

                    val displayMatch =
                        model.displayModel.contains(
                            query,
                            ignoreCase = true
                        )

                    val colorMatch =
                        model.colors.any { color ->

                            color.contains(
                                query,
                                ignoreCase = true
                            )
                        }

                    val sizeMatch =
                        model.sizes.any { size ->

                            size.contains(
                                query,
                                ignoreCase = true
                            )
                        }

                    modelMatch ||
                            displayMatch ||
                            colorMatch ||
                            sizeMatch
                }
            }
        }


    // ------------------------------------------------------------
    // ANA EKRAN
    // ------------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CatalogBackground)
    ) {

        // --------------------------------------------------------
        // ÜST BAR
        // --------------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(
                    start = 6.dp,
                    end = 12.dp,
                    top = 16.dp,
                    bottom = 12.dp
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
                    color = Color(0xFF37474F)
                )
            }


            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = "KATALOG",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(
                    text = "Ürün modelleri",
                    fontSize = 13.sp,
                    color = SoftText
                )
            }


            OutlinedButton(
                onClick =
                    onAddCatalogItem,

                shape =
                    RoundedCornerShape(10.dp)
            ) {

                Text(
                    text = "+ MODEL",
                    fontWeight = FontWeight.Bold
                )
            }
        }


        // --------------------------------------------------------
        // ARAMA
        // --------------------------------------------------------

        CatalogSearchBar(

            query = searchText,

            onQueryChange = {
                searchText = it
            },

            onCameraClick = {
                openCatalogCamera()
            },

            modifier =
                Modifier.padding(
                    top = 12.dp,
                    bottom = 6.dp
                )
        )


        // --------------------------------------------------------
        // MODEL SAYISI
        // --------------------------------------------------------

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 18.dp,
                    vertical = 6.dp
                ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "KATALOG MODELLERİ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF455A64)
            )

            Spacer(
                modifier =
                    Modifier.weight(1f)
            )

            Text(
                text =
                    "${filteredModels.size} model",

                fontSize = 13.sp,
                color = Color.Gray
            )
        }


        // --------------------------------------------------------
        // BOŞ KATALOG
        // --------------------------------------------------------

        if (filteredModels.isEmpty()) {

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
                        text =
                            "Katalogda model bulunamadı",

                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )

                    Text(
                        text =
                            "Model, renk veya beden arayın.",

                        fontSize = 13.sp,
                        color = SoftText
                    )
                }
            }

        } else {

            // ----------------------------------------------------
            // MODEL GRID
            // ----------------------------------------------------

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

                        model =
                            CatalogModelCardItem(

                                modelNumber =
                                    model.modelNumber,

                                displayModel =
                                    model.displayModel,

                                imagePath =
                                    model.imagePath,

                                colors =
                                    model.colors,

                                sizes =
                                    model.sizes
                            ),

                        onClick = {

                            selectedModel =
                                model

                            selectedColor =
                                model.colors
                                    .firstOrNull()
                                    ?: ""

                            selectedSize =
                                model.sizes
                                    .firstOrNull()
                                    ?: ""
                        }
                    )
                }
            }
        }
    }


    // ============================================================
    // MODEL DETAY
    // ============================================================

    selectedModel?.let { model ->

        val selectedRealImage =
            findImageForColor(
                model = model,
                color = selectedColor
            )


        CatalogModelDetailDialog(

            visible = true,

            modelNumber =
                model.displayModel,

            imagePath =
                model.imagePath,

            colors =
                model.colors,

            sizes =
                model.sizes,

            selectedColor =
                selectedColor,

            selectedSize =
                selectedSize,

            onColorSelected = { color ->

                selectedColor =
                    color
            },

            onSizeSelected = { size ->

                selectedSize =
                    size
            },


            // ----------------------------------------------------
            // BURASI ARTIK MODEL KODU DÜZENLEME
            // ----------------------------------------------------

            onEdit = {

                modelEditInitialValue =
                    "SNZ-${model.modelNumber}"

                modelEditDialogVisible =
                    true
            },


            // ----------------------------------------------------
            // YENİ RENK EKLE
            // ----------------------------------------------------

            onAddColor = {

                colorDialogInitialValue =
                    ""

                colorDialogVisible =
                    true
            },


            onClose = {

                selectedModel =
                    null
            },


            imageOverride =
                selectedRealImage
        )
    }


    // ============================================================
    // MODEL KODU DÜZENLEME DİYALOĞU
    // ============================================================

    if (modelEditDialogVisible) {

        CatalogModelEditDialog(

            visible = true,

            initialModelNumber =
                modelEditInitialValue,

            onSave = { newModelNumber ->

                val currentModel =
                    selectedModel

                if (currentModel == null) {

                    modelEditDialogVisible =
                        false

                } else {

                    val normalizedNewModel =
                        normalizeModelNumber(
                            newModelNumber
                        )

                    if (
                        normalizedNewModel.isBlank()
                    ) {

                        Toast.makeText(
                            context,
                            "Geçerli bir model kodu girin.",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        scope.launch {

                            // ------------------------------------
                            // AYNI MODEL KODU KONTROLÜ
                            // ------------------------------------

                            val targetAlreadyExists =
                                catalogItems.any { item ->

                                    val itemModel =
                                        normalizeModelNumber(
                                            item.productNumber
                                        )

                                    itemModel ==
                                            normalizedNewModel &&
                                            itemModel !=
                                            currentModel.modelNumber
                                }


                            if (targetAlreadyExists) {

                                Toast.makeText(
                                    context,
                                    "SNZ-$normalizedNewModel zaten katalogda var.",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {

                                // -------------------------------
                                // TÜM RENK / BEDEN KAYITLARINI
                                // YENİ MODEL KODUNA TAŞI
                                // -------------------------------

                                currentModel.items.forEach { item ->

                                    repository.insert(

                                        item.copy(

                                            productNumber =
                                                normalizedNewModel,

                                            company =
                                                "SNZ"
                                        )
                                    )
                                }


                                Toast.makeText(
                                    context,
                                    "Model kodu SNZ-$normalizedNewModel olarak güncellendi.",
                                    Toast.LENGTH_SHORT
                                ).show()


                                // -------------------------------
                                // DETAYI KAPAT
                                // -------------------------------

                                selectedModel =
                                    null

                                selectedColor =
                                    ""

                                selectedSize =
                                    ""

                                modelEditDialogVisible =
                                    false
                            }
                        }
                    }
                }
            },


            onCancel = {

                modelEditDialogVisible =
                    false
            }
        )
    }


    // ============================================================
    // RENK DÜZENLE / EKLE
    // ============================================================

    if (colorDialogVisible) {

        CatalogColorEditDialog(

            visible = true,

            initialColor =
                colorDialogInitialValue,

            title =
                if (
                    colorDialogInitialValue.isBlank()
                ) {

                    "Kataloğa Renk Ekle"

                } else {

                    "Renk Düzenle"
                },


            onSave = { newColor ->

                val cleanedColor =
                    newColor.trim()


                if (cleanedColor.isNotBlank()) {

                    val model =
                        selectedModel


                    if (model != null) {

                        scope.launch {

                            val existingItem =
                                model.items.firstOrNull {

                                    it.color
                                        .trim()
                                        .equals(
                                            cleanedColor,
                                            ignoreCase = true
                                        )
                                }


                            // --------------------------------
                            // YENİ RENK
                            // --------------------------------

                            if (existingItem == null) {

                                val baseItem =
                                    model.items.firstOrNull()


                                if (baseItem != null) {

                                    val newItem =
                                        baseItem.copy(

                                            id =
                                                UUID.randomUUID()
                                                    .toString(),

                                            productNumber =
                                                model.modelNumber,

                                            company =
                                                "SNZ",

                                            color =
                                                cleanedColor,

                                            size =
                                                selectedSize,

                                            createdAt =
                                                System.currentTimeMillis()
                                        )


                                    repository.insert(
                                        newItem
                                    )
                                }


                            } else {

                                // -----------------------------
                                // MEVCUT RENGİ DEĞİŞTİR
                                // -----------------------------

                                if (
                                    colorDialogInitialValue.isNotBlank() &&

                                    !existingItem.color.equals(
                                        colorDialogInitialValue,
                                        ignoreCase = true
                                    )
                                ) {

                                    repository.insert(

                                        existingItem.copy(
                                            color =
                                                cleanedColor
                                        )
                                    )
                                }
                            }


                            colorDialogVisible =
                                false
                        }

                    } else {

                        colorDialogVisible =
                            false
                    }

                } else {

                    colorDialogVisible =
                        false
                }
            },


            onCancel = {

                colorDialogVisible =
                    false
            }
        )
    }


    // ============================================================
    // KAMERA SONUCU
    // ============================================================

    if (cameraResultVisible) {

        CatalogCameraResultDialog(

            visible = true,

            candidates =
                findCameraCandidates(

                    models =
                        modelGroups,

                    scannedText =
                        ""
                ),

            scannedText =
                "",


            onCandidateSelected = { candidate ->

                val selected =
                    modelGroups.firstOrNull {

                        it.modelNumber ==
                                candidate.modelNumber
                    }


                if (selected != null) {

                    selectedModel =
                        selected

                    selectedColor =
                        selected.colors
                            .firstOrNull()
                            ?: ""

                    selectedSize =
                        selected.sizes
                            .firstOrNull()
                            ?: ""
                }


                cameraResultVisible =
                    false

                cameraUri =
                    null
            },


            onNewModel = {

                cameraResultVisible =
                    false

                cameraUri =
                    null

                onAddCatalogItem()
            },


            onClose = {

                cameraResultVisible =
                    false

                cameraUri =
                    null
            }
        )
    }
}


// =================================================================
// RENK İÇİN GERÇEK KATALOG RESMİNİ BUL
// =================================================================

private fun findImageForColor(
    model: CatalogModelGroup,
    color: String
): String? {

    if (color.isBlank()) {
        return null
    }


    return model.items.firstOrNull { item ->

        item.color
            .trim()
            .equals(
                color.trim(),
                ignoreCase = true
            ) &&

                item.imagePath.isNotBlank()

    }?.imagePath
}


// =================================================================
// KAMERA ADAYLARI
// =================================================================

private fun findCameraCandidates(
    models: List<CatalogModelGroup>,
    scannedText: String
): List<CatalogCameraCandidate> {

    val query =
        normalizeSearchQuery(
            scannedText
        )


    if (query.isBlank()) {
        return emptyList()
    }


    return models

        .filter {

            it.modelNumber.contains(
                query,
                ignoreCase = true
            )
        }

        .take(5)

        .map {

            CatalogCameraCandidate(

                modelNumber =
                    it.modelNumber,

                displayModel =
                    it.displayModel,

                color =
                    it.colors.firstOrNull()
                        ?: "",

                similarity =
                    0.90f
            )
        }
}


// =================================================================
// MODEL NUMARASINI NORMALİZE ET
// =================================================================

private fun normalizeModelNumber(
    value: String
): String {

    return value

        .trim()

        .uppercase()

        .replace("İ", "I")

        .replace(" ", "")

        .replace("-", "")

        .replace("_", "")

        .replace(
            Regex("^[A-Z]+"),
            ""
        )

        .filter {
            it.isDigit()
        }
}


// =================================================================
// ARAMA METNİNİ NORMALİZE ET
// =================================================================

private fun normalizeSearchQuery(
    value: String
): String {

    return value

        .trim()

        .uppercase()

        .replace("İ", "I")

        .replace(" ", "")

        .replace("-", "")

        .replace("_", "")

        .replace(
            Regex("^[A-Z]+"),
            ""
        )

        .filter {
            it.isDigit()
        }
}