package by.bsuir.poit.kosten.qr_tool

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel

class QrGeneratorViewModel: ViewModel() {
    var qrText: String = ""
    var bitmap:Bitmap? = null
}