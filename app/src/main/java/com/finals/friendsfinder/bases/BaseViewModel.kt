package com.finals.friendsfinder.bases

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.ActivityUtils
import com.finals.friendsfinder.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException

open class BaseViewModel : ViewModel() {
    private var disposable: CompositeDisposable? = null

    init {
        disposable = CompositeDisposable()
    }

    fun <RP> request(
        apiService: Observable<RP>,
        onSuccess: ((response: RP) -> Unit),
        onFailed: ((error: Throwable) -> Unit)
    ) {
        disposable?.add(
            apiService
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<RP>() {
                    override fun onNext(t: RP) {
                        onSuccess(t)
                    }

                    override fun onError(e: Throwable) {
                        val resource = ActivityUtils.getTopActivity()
                        val message =
                            if (e is UnknownHostException) {
                                resource.getString(R.string.str_error_not_found_server)
                            } else {
                                resource.getString(R.string.str_error_occurs)
                            }
                        onFailed(Throwable(message))
                    }

                    override fun onComplete() {
                    }

                })
        )
    }
}