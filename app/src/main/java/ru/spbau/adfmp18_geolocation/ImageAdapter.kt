package ru.spbau.adfmp18_geolocation

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView

// references to our images
private val mThumbIds = arrayOf<Int>(
        R.drawable.admiral_1, R.drawable.cons_1,
        R.drawable.isaak_1, R.drawable.kazan_1,
        R.drawable.med_acad_1, R.drawable.sea_cath_1,
        R.drawable.winter_palace_1,
        R.drawable.admiral_1, R.drawable.cons_1,
        R.drawable.isaak_1, R.drawable.kazan_1,
        R.drawable.med_acad_1, R.drawable.sea_cath_1,
        R.drawable.winter_palace_1)

class ImageAdapter(private val mContext: Context) : BaseAdapter() {

    override fun getCount(): Int = mThumbIds.size

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0L

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = ImageView(mContext)
            imageView.layoutParams = AbsListView.LayoutParams(200, 200)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

        imageView.setImageResource(mThumbIds[position])
        return imageView
    }
}