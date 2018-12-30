package com.example.parkseunghyun.achievementofall.Configurations

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.LinearLayout

/**
    REFACTORED
 */


// CircleIndicator
// 하단 3개의 동그라미를 설정합니다.
// 화면을 좌우로 슬라이드할때 필요합니다.
class CircleIndicator: LinearLayout {

    private var mContext: Context? = null
    private var mDefaultCircle: Int = 0
    private var mSelectCircle: Int = 0

    private var imageDot: MutableList<ImageView> = mutableListOf()

    private val temp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.5f, resources.displayMetrics)

    constructor(context: Context) : super(context) {

        mContext = context

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

        mContext = context

    }

    fun createDotPanel(count: Int, defaultCircle: Int, selectCircle: Int, position: Int) {

        this.removeAllViews()

        mDefaultCircle = defaultCircle
        mSelectCircle = selectCircle

        for (indexOfCircleNum in 0 until count) {

            imageDot.add(ImageView(mContext).apply { setPadding(temp.toInt(), 0, temp.toInt(), 0) })
            this.addView(imageDot[indexOfCircleNum])

        }

        selectDot(position)

    }

    fun selectDot(position: Int) {

        for (indexOfCircle in imageDot.indices) {

            if (indexOfCircle == position) {

                imageDot[indexOfCircle].setImageResource(mSelectCircle)

            } else {

                imageDot[indexOfCircle].setImageResource(mDefaultCircle)
            }

        }

    }
}
