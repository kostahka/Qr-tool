package by.bsuir.poit.kosten.qr_tool

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode

class QrScanFragment : Fragment() {

    private lateinit var qrTextLayout: RelativeLayout
    private lateinit var qrTextView: TextView
    private lateinit var qrCopyButton: Button
    private lateinit var qrGoLinkButton: Button
    private lateinit var qrScanButton: Button
    private lateinit var qrScannerView: CodeScannerView

    private lateinit var codeScanner: CodeScanner

    private fun updateUI(){
        if(viewModel.isScanning){
            codeScanner.startPreview()
            qrScannerView.visibility = View.VISIBLE
            qrTextLayout.visibility = View.INVISIBLE
            qrScanButton.isEnabled = false
        }else{
            qrScannerView.visibility = View.INVISIBLE
            if(viewModel.qrText != null){
                qrTextLayout.visibility = View.VISIBLE
                qrTextView.text = viewModel.qrText
                qrGoLinkButton.isEnabled = URLUtil.isValidUrl(viewModel.qrText)
            }else{
                qrTextLayout.visibility = View.INVISIBLE
            }
            qrScanButton.isEnabled = true
        }
    }

    companion object {
        fun newInstance() = QrScanFragment()
    }

    private val viewModel by lazy{
        ViewModelProvider(this)[QrScanViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qr_scan, container, false)


        qrTextLayout = view.findViewById(R.id.qr_text_layout)
        qrTextView = view.findViewById(R.id.qr_text_view)
        qrCopyButton = view.findViewById(R.id.qr_copy_button)
        qrGoLinkButton = view.findViewById(R.id.qr_go_link_button)
        qrScanButton = view.findViewById(R.id.scan_button)

        qrScannerView = view.findViewById(R.id.scanner_view)

        codeScanner = CodeScanner(requireContext(), qrScannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                activity?.runOnUiThread {
                    viewModel.qrText = it.text
                    viewModel.isScanning = false
                    updateUI()
                }
            }

            errorCallback = ErrorCallback {
                activity?.runOnUiThread {
                    Log.e("QrScanFragment", "Camera initialization error: ${it.message}")
                    viewModel.isScanning = false
                    updateUI()
                }
            }
        }

        qrScanButton.setOnClickListener {
            viewModel.isScanning = true
            updateUI()
        }

        qrCopyButton.setOnClickListener {
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(viewModel.qrText, viewModel.qrText)
            clipboard.setPrimaryClip(clip)
        }

        qrGoLinkButton.setOnClickListener {
            val browse = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.qrText))
            startActivity(browse)
        }

        updateUI()

        return view
    }

    override fun onResume() {
        super.onResume()

        updateUI()
    }

    override fun onPause() {
        super.onPause()

        codeScanner.releaseResources()
    }
}