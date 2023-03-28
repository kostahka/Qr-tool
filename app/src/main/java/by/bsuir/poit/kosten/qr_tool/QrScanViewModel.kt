package by.bsuir.poit.kosten.qr_tool

import androidx.lifecycle.ViewModel

class QrScanViewModel : ViewModel() {
    var qrText: String? = null
    var isScanning: Boolean = false
}