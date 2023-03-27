package by.bsuir.poit.kosten.qr_tool

import android.content.Context.WINDOW_SERVICE
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder

class QrGeneratorFragment : Fragment() {

    private lateinit var qrEditText: EditText
    private lateinit var qrImg: ImageView
    private lateinit var qrGenerateButton: Button
    private lateinit var qrgEncoder: QRGEncoder
    private lateinit var bitmap: Bitmap

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

        qrGenerateButton.setOnClickListener {
            val manager: WindowManager = requireContext().getSystemService(WINDOW_SERVICE) as WindowManager

            val display = manager.defaultDisplay

            val point = Point()
            display.getSize(point)

            val width = point.x
            val height = point.y

            var dimen = if(width < height)  width else height
            dimen = dimen * 3 / 4

            qrgEncoder = QRGEncoder(qrEditText.text.toString(), null, QRGContents.Type.TEXT, dimen)

            try{
                bitmap = qrgEncoder.bitmap
                qrImg.setImageBitmap(bitmap)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            QrGeneratorFragment()
    }
}