/*
 * Copyright (c) 2020 Razeware LLC
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 *  distribute, sublicense, create a derivative work, and/or sell copies of the
 *  Software in any work that is designed, intended, or marketed for pedagogical or
 *  instructional purposes related to programming, coding, application development,
 *  or information technology.  Permission for such use, copying, modification,
 *  merger, publication, distribution, sublicensing, creation of derivative works,
 *  or sale is expressly withheld.
 *
 *  This project and source code may use libraries or frameworks that are
 *  released under various Open-Source licenses. Use of those libraries and
 *  frameworks are governed by their own individual licenses.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package com.raywenderlich.android.assistedgallery.bitmap

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.raywenderlich.android.assistedgallery.R
import com.raywenderlich.android.assistedgallery.bitmap.fetcher.BitmapFetcher
import com.raywenderlich.android.assistedgallery.bitmap.filter.ImageFilter
import com.raywenderlich.android.assistedgallery.bitmap.filter.NoOpImageFilter
import com.raywenderlich.android.assistedgallery.di.Schedulers
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ImageLoader @AssistedInject constructor(
  @Assisted private val bitmapFetcher: BitmapFetcher,
  @Assisted @Schedulers.IO private val bgDispatcher: CoroutineDispatcher,
  @Assisted @Schedulers.Main private val uiDispatcher: CoroutineDispatcher,
  @DrawableRes private val loadingDrawableId: Int = R.drawable.loading_animation_drawable,
  private val imageFilter: ImageFilter = NoOpImageFilter
) {

  suspend fun loadImage(imageUrl: String, into: ImageView) =
    withContext(bgDispatcher) {
      val prevScaleType: ImageView.ScaleType = into.scaleType
      withContext(uiDispatcher) {
        with(into) {
          scaleType = ImageView.ScaleType.CENTER
          setImageDrawable(ContextCompat.getDrawable(into.context, loadingDrawableId))
        }
      }
      val bitmap = bitmapFetcher.fetchImage(imageUrl)
      val transformedBitmap = imageFilter.transform(bitmap)
      withContext(uiDispatcher) {
        with(into) {
          scaleType = prevScaleType
          setImageBitmap(transformedBitmap)
        }
      }
    }
}