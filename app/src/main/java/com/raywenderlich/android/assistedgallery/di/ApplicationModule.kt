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

package com.raywenderlich.android.assistedgallery.di

import com.raywenderlich.android.assistedgallery.bitmap.fetcher.BitmapFetcher
import com.raywenderlich.android.assistedgallery.bitmap.fetcher.BitmapFetcherImpl
import com.raywenderlich.android.assistedgallery.bitmap.strategies.imageurl.ImageUrlStrategy
import com.raywenderlich.android.assistedgallery.bitmap.strategies.imageurl.PlaceImgUrlStrategy
import com.raywenderlich.android.assistedgallery.bitmap.strategies.size.ScreenSizeStrategy
import com.raywenderlich.android.assistedgallery.bitmap.strategies.size.SizeStrategy
import com.raywenderlich.android.assistedgallery.di.ApplicationModule.Bindings
import com.raywenderlich.android.assistedgallery.di.Schedulers.IO
import com.raywenderlich.android.assistedgallery.di.Schedulers.Main
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module(includes = arrayOf(Bindings::class))
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

  @Provides
  @IO
  fun ioScheduler(): CoroutineDispatcher = Dispatchers.IO

  @Provides
  @Main
  fun uiScheduler(): CoroutineDispatcher = Dispatchers.Main


  @Module
  @InstallIn(ApplicationComponent::class)
  interface Bindings {

    @Binds
    fun bindBitmapFetcher(
      impl: BitmapFetcherImpl
    ): BitmapFetcher

    @Binds
    fun bindSizeStrategy(
      impl: ScreenSizeStrategy
    ): SizeStrategy

    @Binds
    fun bindImageUrlStrategy(
      impl: PlaceImgUrlStrategy
    ): ImageUrlStrategy
  }
}