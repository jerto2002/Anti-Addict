package com.example.testmvvm.Viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testmvvm.Model.DataChart

/*
 *      MainViewModel
 *      - viewModel that updates the MainFragment (the visible UI)
 *      - gets the data from model
 */
class MainViewModel: ViewModel() {

    // Create the model which contains data for our UI
    private val model = DataChart(data = HashMap<String, Double>())

    // Create MutableLiveData which MainFragment can subscribe to
    // When this data changes, it triggers the UI to do an update
    val dataChart = MutableLiveData<HashMap<String, Double>>()

    // Get the updated text from our model and post the value to MainFragment
    fun getChart() {
        val data = model.data
        dataChart.postValue(data)
    }
}