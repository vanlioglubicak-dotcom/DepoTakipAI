package com.example.depotakipai.ui.catalog.dialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.ImageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.example.depotakipai.data.catalog.CatalogColorCatalog
import com.example.depotakipai.data.catalog.CatalogRecolorEngine
import com.example.depotakipai.ui.catalog.components.CatalogSizeRow


@Composable
fun CatalogModelDetailDialog(
    visible: Boolean,
    modelNumber: String,
    imagePath: String,
    colors: List<String>,
    sizes: List<String>,
    selectedColor: String,
    selectedSize: String,
    onColorSelected: (String) -> Unit,
    onSizeSelected: (String) -> Unit,
    onEdit: () -> Unit,
    onAddColor: () -> Unit,
    onClose: () -> Unit,
    imageOverride: String? = null
) {
    if (!visible) {
        return
    }

    val context = LocalContext.current

    val displayedRealImage = imageOverride
        ?.takeIf { it.isNotBlank() }

    val baseImage = imagePath
        .takeIf { it.isNotBlank() }

    /*
     * Gerçek katalog renk fotoğrafı varsa
     * renk üretmeye gerek yok.
     *
     * Gerçek fotoğraf yoksa ana model görselinden
     * seçilen renk için önizleme oluşturuyoruz.
     */
    val shouldGenerateRecolor =
        displayedRealImage == null &&
                baseImage != null &&
                selectedColor.isNotBlank()

    var recoloredBitmap by remember(
        baseImage,
        selectedColor,
        displayedRealImage
    ) {
        mutableStateOf<Bitmap?>(null)
    }

    var recolorLoading by remember(
        baseImage,
        selectedColor,
        displayedRealImage
    ) {
        mutableStateOf(false)
    }

    LaunchedEffect(
        baseImage,
        selectedColor,
        displayedRealImage
    ) {
        recoloredBitmap = null

        if (!shouldGenerateRecolor) {
            recolorLoading = false
            return@LaunchedEffect
        }

        val targetColor =
            CatalogColorCatalog
                .findByName(selectedColor)
                ?.color

        if (targetColor == null) {
            recolorLoading = false
            return@LaunchedEffect
        }

        recolorLoading = true

        recoloredBitmap = loadAndRecolorImage(
            context = context,
            imagePath = baseImage,
            targetColor = targetColor
        )

        recolorLoading = false
    }

    Dialog(
        onDismissRequest = onClose
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.94f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                // =====================================================
                // BAŞLIK
                // =====================================================

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "KATALOG MODELİ",
                            fontSize = 13.sp,
                            color = Color(0xFF78909C),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(
                            modifier = Modifier.height(2.dp)
                        )

                        Text(
                            text = modelNumber,
                            fontSize = 24.sp,
                            color = Color(0xFF263238),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(
                        onClick = onClose
                    ) {
                        Text(
                            text = "KAPAT",
                            color = Color(0xFF455A64)
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // =====================================================
                // ÜRÜN GÖRSELİ
                // =====================================================

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF3F5F6)
                ) {
                    when {

                        // -------------------------------------------------
                        // 1. GERÇEK RENK FOTOĞRAFI
                        // -------------------------------------------------

                        displayedRealImage != null -> {
                            CatalogAssetImage(
                                imagePath = displayedRealImage,
                                contentDescription = modelNumber
                            )
                        }

                        // -------------------------------------------------
                        // 2. RENK ÖNİZLEMESİ HAZIR
                        // -------------------------------------------------

                        recoloredBitmap != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = BitmapPainter(
                                        recoloredBitmap!!.asImageBitmap()
                                    ),
                                    contentDescription =
                                        "$modelNumber $selectedColor",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )

                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(10.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(
                                        alpha = 0.90f
                                    )
                                ) {
                                    Text(
                                        text = "RENK ÖNİZLEME",
                                        modifier = Modifier.padding(
                                            horizontal = 9.dp,
                                            vertical = 5.dp
                                        ),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF455A64)
                                    )
                                }
                            }
                        }

                        // -------------------------------------------------
                        // 3. RENK ÖNİZLEMESİ HAZIRLANIYOR
                        // -------------------------------------------------

                        recolorLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text =
                                            "Renk önizlemesi hazırlanıyor...",
                                        fontSize = 14.sp,
                                        color = Color(0xFF607D8B),
                                        fontWeight = FontWeight.Medium
                                    )

                                    Spacer(
                                        modifier = Modifier.height(5.dp)
                                    )

                                    Text(
                                        text = selectedColor,
                                        fontSize = 13.sp,
                                        color = Color(0xFF90A4AE)
                                    )
                                }
                            }
                        }

                        // -------------------------------------------------
                        // 4. NORMAL ANA GÖRSEL
                        // -------------------------------------------------

                        baseImage != null -> {
                            CatalogAssetImage(
                                imagePath = baseImage,
                                contentDescription = modelNumber
                            )
                        }

                        // -------------------------------------------------
                        // 5. GÖRSEL YOK
                        // -------------------------------------------------

                        else -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ürün görseli yok",
                                    fontSize = 15.sp,
                                    color = Color(0xFF78909C)
                                )
                            }
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // =====================================================
                // RENKLER
                // =====================================================

                Text(
                    text = "RENKLER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF455A64)
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                if (colors.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(
                                rememberScrollState()
                            ),
                        horizontalArrangement =
                            Arrangement.spacedBy(9.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        colors
                            .filter { it.isNotBlank() }
                            .distinct()
                            .forEach { colorName ->

                                CatalogRealColorButton(
                                    colorName = colorName,
                                    selected = colorName.equals(
                                        selectedColor,
                                        ignoreCase = true
                                    ),
                                    onClick = {
                                        onColorSelected(colorName)
                                    }
                                )
                            }
                    }
                } else {
                    Text(
                        text = "Henüz renk eklenmemiş",
                        fontSize = 13.sp,
                        color = Color(0xFF78909C)
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // =====================================================
                // RENK EKLE
                // =====================================================

                OutlinedButton(
                    onClick = onAddColor,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "+ Renk Ekle",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // =====================================================
                // BEDENLER
                // =====================================================

                Text(
                    text = "BEDENLER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF455A64)
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                CatalogSizeRow(
                    sizes = sizes,
                    selectedSize = selectedSize,
                    onSizeSelected = onSizeSelected,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                // =====================================================
                // ALT BUTONLAR
                // =====================================================

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "DÜZENLE",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onClose,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "KAPAT",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// =====================================================================
// KATALOG ASSET GÖRSELİ
// =====================================================================

@Composable
private fun CatalogAssetImage(
    imagePath: String,
    contentDescription: String
) {
    coil.compose.AsyncImage(
        model = buildAssetUri(imagePath),
        contentDescription = contentDescription,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}


// =====================================================================
// GÖRSELİ YÜKLE + RENKLENDİR
// =====================================================================

private suspend fun loadAndRecolorImage(
    context: Context,
    imagePath: String,
    targetColor: Color
): Bitmap? {

    return withContext(Dispatchers.IO) {
        try {

            val imageLoader = ImageLoader(context)

            val request = ImageRequest.Builder(context)
                .data(
                    buildAssetUri(imagePath)
                )
                .allowHardware(false)
                .build()

            val result =
                imageLoader.execute(request)

            val drawable =
                result.drawable
                    ?: return@withContext null

            val bitmap =
                (drawable as? BitmapDrawable)
                    ?.bitmap
                    ?: return@withContext null

            CatalogRecolorEngine.recolorSafely(
                source = bitmap,
                targetColor = targetColor
            )

        } catch (_: Exception) {
            null
        }
    }
}


// =====================================================================
// RENK BUTONU
// =====================================================================

@Composable
private fun CatalogRealColorButton(
    colorName: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    val catalogColor =
        CatalogColorCatalog.findByName(
            colorName
        )

    val realColor =
        catalogColor?.color
            ?: fallbackColorForName(
                colorName
            )

    Surface(
        onClick = onClick,
        modifier = Modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color =
            if (selected) {
                Color(0xFFE8EEF0)
            } else {
                Color.White
            },
        tonalElevation =
            if (selected) {
                2.dp
            } else {
                0.dp
            },
        shadowElevation =
            if (selected) {
                2.dp
            } else {
                0.dp
            }
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 6.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(7.dp)
        ) {

            Box(
                modifier = Modifier.size(30.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = realColor,
                    shadowElevation = 1.dp
                ) {
                }
            }

            Text(
                text = colorName,
                fontSize = 12.sp,
                fontWeight =
                    if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    },
                color = Color(0xFF263238),
                maxLines = 1
            )
        }
    }
}


// =====================================================================
// MERKEZİ RENK KATALOĞUNDA YOKSA YEDEK RENK
// =====================================================================

private fun fallbackColorForName(
    colorName: String
): Color {

    val name =
        colorName
            .trim()
            .uppercase()
            .replace("İ", "I")
            .replace("Ğ", "G")
            .replace("Ü", "U")
            .replace("Ş", "S")
            .replace("Ö", "O")
            .replace("Ç", "C")

    return when {

        name.contains("SIYAH") ->
            Color(0xFF151515)

        name.contains("BEYAZ") ->
            Color(0xFFFFFFFF)

        name.contains("KIRIK") &&
                name.contains("BEYAZ") ->
            Color(0xFFF4F1E8)

        name.contains("BORDO") ->
            Color(0xFF800020)

        name.contains("KIRMIZI") ->
            Color(0xFFD32F2F)

        name.contains("LACIVERT") ->
            Color(0xFF172A5C)

        name.contains("MAVI") ->
            Color(0xFF1976D2)

        name.contains("ACIK MAVI") ->
            Color(0xFF64B5F6)

        name.contains("YESIL") ->
            Color(0xFF388E3C)

        name.contains("ZUMRUT") ->
            Color(0xFF00897B)

        name.contains("PETROL") ->
            Color(0xFF006064)

        name.contains("HAKI") ->
            Color(0xFF6B7043)

        name.contains("PEMBE") ->
            Color(0xFFE91E63)

        name.contains("PUDRA") ->
            Color(0xFFE8B7B7)

        name.contains("FUSYA") ->
            Color(0xFFC2185B)

        name.contains("MOR") ->
            Color(0xFF7B1FA2)

        name.contains("LILA") ->
            Color(0xFFB39DDB)

        name.contains("TURUNCU") ->
            Color(0xFFF57C00)

        name.contains("SARI") ->
            Color(0xFFFBC02D)

        name.contains("BEJ") ->
            Color(0xFFD7C4A3)

        name.contains("KREM") ->
            Color(0xFFFFF1D0)

        name.contains("VANILYA") ->
            Color(0xFFF3E5AB)

        name.contains("VIZON") ->
            Color(0xFF8C7561)

        name.contains("KAHVE") ||
                name.contains("KAHVERENGI") ->
            Color(0xFF6D4C41)

        name.contains("TABA") ->
            Color(0xFF9A6A3A)

        name.contains("GRI") ->
            Color(0xFF808080)

        name.contains("GUMUS") ->
            Color(0xFFBDBDBD)

        name.contains("ANTRASIT") ->
            Color(0xFF424242)

        name.contains("ALTIN") ->
            Color(0xFFC9A227)

        else ->
            Color(0xFFD0D5D8)
    }
}


// =====================================================================
// ASSET GÖRSEL YOLU
// =====================================================================

private fun buildAssetUri(
    imagePath: String
): String {

    var path =
        imagePath
            .trim()
            .replace("\\", "/")

    path =
        path.removePrefix(
            "file:///android_asset/"
        )

    path =
        path.removePrefix(
            "android_asset/"
        )

    if (!path.startsWith("catalog/")) {

        path =
            if (path.startsWith("images/")) {

                "catalog/$path"

            } else {

                "catalog/images/$path"
            }
    }

    return "file:///android_asset/$path"
}