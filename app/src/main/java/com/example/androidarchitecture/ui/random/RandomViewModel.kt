package com.example.androidarchitecture.ui.random

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.androidarchitecture.BuildConfig
import com.example.androidarchitecture.Event
import com.example.androidarchitecture.data.entities.Result
import com.example.androidarchitecture.data.entities.UserFormat
import com.example.androidarchitecture.domain.GetRandomUsersUseCase
import com.example.androidarchitecture.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import javax.inject.Inject

@HiltViewModel
class RandomViewModel @Inject constructor(
    private val getRandomUsersUseCase: GetRandomUsersUseCase
) : BaseViewModel() {

    private val queryMap = HashMap<String, String>()

    private val _userFormat = MutableLiveData<UserFormat>()
    val userFormat: LiveData<UserFormat>
        get() = _userFormat

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>>
        get() = _toastMessage

    private val randomUrl = BuildConfig.randomUrl

    fun start() {
        loadRandomUser()
    }

    fun onRefresh() {
        loadRandomUser()
    }

    private fun loadRandomUser() {
        getRandomUsersUseCase(queryMap, randomUrl)
            .map {
                getUserFormat(it.results[0])
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _userFormat.value = it
                },
                {
                    _toastMessage.value = Event(it.message.toString())
                    Log.e(RandomViewModel::javaClass.javaClass.name, it.message.toString())
                }
            ).addTo(compositeDisposable)
    }

    private fun getUserFormat(result: Result): UserFormat {
        val name = "name : ${result.name.first} ${result.name.last}"
        val age = "age : ${result.dob.age}"
        val location = "location : ${result.location.street.number}, " +
            "${result.location.street.name}, ${result.location.city}, " +
            "${result.location.state}, ${result.location.country}"
        return UserFormat(
            result.login.uuid,
            name,
            result.picture.large,
            age,
            location
        )
    }
}
