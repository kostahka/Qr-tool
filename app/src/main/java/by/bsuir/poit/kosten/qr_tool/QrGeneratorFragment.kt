package by.bsuir.poit.kosten.qr_tool

import android.content.ContentValues
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


const val CREATE_FILE = 1

class QrGeneratorFragment : Fragment() {

    private lateinit var qrEditText: EditText
    private lateinit var qrImg: ImageView
    private lateinit var qrGenerateButton: Button
    private lateinit var qrSaveButton: Button
    private lateinit var qrShareButton: Button

    private lateinit var qrgEncoder: QRGEncoder

    private val fileSaveLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("image/jpeg")){
        result ->
        if(result != null){
            try {
                val out = requireActivity().contentResolver.openOutputStream(result)
                viewModel.bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
                Toast.makeText(activity, "Image Saved", Toast.LENGTH_LONG).show()
                out?.flush()
                out?.close()
            }catch (e:Exception){
                Toast.makeText(activity, "Image Not Saved", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }

        }
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[QrGeneratorViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qr_generator, container, false)

        qrEditText = view.findViewById(R.id.edit_text_qr)
        qrImg = view.findViewById(R.id.img_generated_qr)
        qrGenerateButton = view.findViewById(R.id.btn_generate)
        qrSaveButton = view.findViewById(R.id.btn_save)
        qrShareButton = view.findViewById(R.id.btn_share)

        qrGenerateButton.setOnClickListener {
            val manager: WindowManager = requireContext().getSystemService(WINDOW_SERVICE) as WindowManager

            val display = manager.defaultDisplay

            val point = Point()
            display.getSize(point)

            val width = point.x
            val height = point.y

            var dimen = if(width < height)  width else height
            dimen = dimen * 3 / 4

            qrgEncoder = QRGEncoder(viewModel.qrText, null, QRGContents.Type.TEXT, dimen)

            try{
                viewModel.bitmap = qrgEncoder.getBitmap(0)
                qrImg.setImageBitmap(viewModel.bitmap)
                qrSaveButton.isEnabled = true
                qrShareButton.isEnabled = true
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        qrSaveButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)
            {
                try {
                    fileSaveLauncher.launch("${viewModel.qrText}.jpg")
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )
            }
        }

        qrShareButton.setOnClickListener {

            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "image/jpeg"
            }

            val values = ContentValues()
            values.put(Images.Media.TITLE, "title")
            values.put(Images.Media.MIME_TYPE, "image/jpeg")
            val uri: Uri = requireContext().contentResolver.insert(
                Images.Media.EXTERNAL_CONTENT_URI,
                values
            )!!

            try {
                val fo = requireContext().contentResolver.openOutputStream(uri)
                viewModel.bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, fo)
            }catch (e:IOException){
                e.printStackTrace()
            }
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(shareIntent, "Share QR"))
        }

        updateUI()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrTextWatcher = object:TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.qrText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

        }
        qrEditText.addTextChangedListener(qrTextWatcher)
    }

    private fun updateUI(){
        qrEditText.setText(viewModel.qrText)
        if(viewModel.bitmap != null)
            qrImg.setImageBitmap(viewModel.bitmap)
        else
        {
            qrSaveButton.isEnabled = false
            qrShareButton.isEnabled = false
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            QrGeneratorFragment()
    }
}