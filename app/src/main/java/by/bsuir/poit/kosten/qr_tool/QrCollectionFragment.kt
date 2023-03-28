package by.bsuir.poit.kosten.qr_tool

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

const val ITEMS_COUNT = 2

class QrCollectionFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: QrCollectionPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qr_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = QrCollectionPagerAdapter(childFragmentManager)
        viewPager = view.findViewById(R.id.qr_pager)
        viewPager.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            QrCollectionFragment()
    }

    private inner class QrCollectionPagerAdapter(fm: FragmentManager) : FragmentStateAdapter(fm, viewLifecycleOwner.lifecycle){
        override fun getItemCount(): Int {
            return ITEMS_COUNT
        }

        override fun createFragment(position: Int): Fragment {
            return if(position == 0)
                QrGeneratorFragment.newInstance()
            else
                QrScanFragment.newInstance()
        }

    }
}