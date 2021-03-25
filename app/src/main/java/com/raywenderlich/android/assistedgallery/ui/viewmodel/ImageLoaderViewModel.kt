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

package com.raywenderlich.android.assistedgallery.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.raywenderlich.android.assistedgallery.bitmap.fetcher.BitmapFetcher
import com.raywenderlich.android.assistedgallery.bitmap.filter.ImageFilter
import com.raywenderlich.android.assistedgallery.di.Schedulers
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class ImageLoaderViewModel @AssistedInject constructor(
  private val bitmapFetcher: BitmapFetcher,
  @Schedulers.IO private val bgDispatcher: CoroutineDispatcher,
  @Assisted private val imageFilter: ImageFilter
) : ViewModel() {

  private val _bitmapLiveData = MutableLiveData<Bitmap>()
  val bitmapLiveData: LiveData<Bitmap>
    get() = _bitmapLiveData

  fun loadImage(imageUrl: String) {
    viewModelScope.launch(bgDispatcher) {
      val bitmap = bitmapFetcher.fetchImage(imageUrl)
      val filteredBitmap = imageFilter.transform(bitmap)
      _bitmapLiveData.postValue(filteredBitmap)
    }
  }

  companion object {
    fun provideFactory(
      assistedFactory: ImageLoaderViewModelFactory,
      imageFilter: ImageFilter
    ): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
          return assistedFactory.create(imageFilter) as T
        }
      }
  }
}