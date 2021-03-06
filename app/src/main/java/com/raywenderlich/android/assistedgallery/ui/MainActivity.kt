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
package com.raywenderlich.android.assistedgallery.ui

//import com.raywenderlich.android.assistedgallery.bitmap.ImageLoaderFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.raywenderlich.android.assistedgallery.R
import com.raywenderlich.android.assistedgallery.bitmap.filter.GrayScaleImageFilter
import com.raywenderlich.android.assistedgallery.bitmap.strategies.imageurl.ImageUrlStrategy
import com.raywenderlich.android.assistedgallery.ui.viewmodels.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CoroutineScope, LifecycleObserver {

  @Inject
  lateinit var imageLoaderViewModelFactory: ImageLoaderViewModelFactory

  private val imageLoaderViewModel: ImageLoaderViewModel by viewModels {
    provideFactory(
      imageLoaderViewModelFactory,
      GrayScaleImageFilter()
    )
  }

  @Inject
  lateinit var imageUrlStrategy: ImageUrlStrategy

  lateinit var mainImage: ImageView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    mainImage = findViewById<ImageView>(R.id.main_image).apply {
      setOnLongClickListener {
        loadImage()
        true
      }
    }
    lifecycle.addObserver(this)
    imageLoaderViewModel.bitmapLiveData.observe(this) { event ->
      with(mainImage) {
        when (event) {
          is DrawableEvent -> {
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setImageDrawable(ContextCompat.getDrawable(this@MainActivity, event.drawableId))
          }
          is BitmapEvent -> {
            scaleType = ImageView.ScaleType.FIT_XY
            setImageBitmap(event.bitmap)
          }
        }
      }
    }
  }

  @OnLifecycleEvent(ON_START)
  fun loadImage() {
    imageLoaderViewModel.loadImage(imageUrlStrategy())
  }

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Main
}