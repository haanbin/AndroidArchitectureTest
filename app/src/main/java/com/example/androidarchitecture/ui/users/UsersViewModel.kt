package com.example.androidarchitecture.ui.users

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.androidarchitecture.BuildConfig
import com.example.androidarchitecture.Event
import com.example.androidarchitecture.data.entities.Result
import com.example.androidarchitecture.data.entities.UserFormat
import com.example.androidarchitecture.domain.GetRandomUsersUseCase
import com.example.androidarchitecture.ui.base.BaseViewModel
import com.example.androidarchitecture.ui.random.RandomViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo

class UsersViewModel @ViewModelInject constructor(
    private val getRandomUsersUseCase: GetRandomUsersUseCase
) : BaseViewModel() {

    private val _userFormats = MutableLiveData<List<UserFormat>>()
    val userFormats: LiveData<List<UserFormat>>
        get() = _userFormats

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>>
        get() = _toastMessage

    private val queryMap = HashMap<String, String>()
    private val randomUrl = BuildConfig.randomUrl
    val loadMoreTest = {
        queryMap["page"] = ((queryMap["page"]?.toInt() ?: 0) + 1).toString()
        loadRandomUser()
    }

    init {
        with(queryMap) {
            put("page", "1")
            put("results", "20")
            put("seed", "abc")
        }
    }

    fun start() {
        loadRandomUser()
    }

    fun loadMore() {
        queryMap["page"] = (queryMap["page"]?.toInt() ?: 0 + 1).toString()
        loadRandomUser()
    }

    fun reloadList() {
        _userFormats.value = listOf()
        queryMap["page"] = "1"
        loadRandomUser()
    }

    private fun loadRandomUser() {
        getRandomUsersUseCase(queryMap, randomUrl)
            .map {
                it.results.asSequence()
                    .map { result ->
                        getUserFormat(result)
                    }.toList()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val itemList = (_userFormats.value ?: listOf()).toMutableList()
                    itemList.addAll(it)
                    _userFormats.value = itemList
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
