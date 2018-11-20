package com.github.esabook.speechtx.activities

import ai.api.AIListener
import ai.api.android.AIConfiguration
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import ai.api.model.Entity
import ai.api.model.EntityEntry
import android.Manifest
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.github.esabook.speechtx.APIService.APIFactory
import com.github.esabook.speechtx.APIService.IDictionaryAPIService
import com.github.esabook.speechtx.R
import com.github.esabook.speechtx.adapters.DictionaryCounterAdapter
import com.github.esabook.speechtx.databinding.ActivityMainBinding
import com.github.esabook.speechtx.models.DictionaryDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class MainActivity : AppCompatActivity(), AIListener {

    lateinit var mBinding: ActivityMainBinding
    lateinit var mService: AIService
    lateinit var listAdapter: DictionaryCounterAdapter

    var currentDictData: ArrayList<DictionaryDTO> = ArrayList()
    var onListening = false

    val AIToken = "d8d0612552e54ceaab5b47c96c98113d"
    val mConfig = AIConfiguration(
        AIToken,
        ai.api.AIConfiguration.SupportedLanguages.English,
        AIConfiguration.RecognitionEngine.System
    )

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listAdapter = DictionaryCounterAdapter(this, 0, currentDictData)

        mService = AIService.getService(this, mConfig)
        mService.setListener(this)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.listDictionary.adapter = listAdapter
        mBinding.btnRecord.setOnClickListener {

            mBinding.tvQuery.setText(null)

            if (onListening)
                mService.cancel()
            else
                if (isRecordPermissionGranted())
                    requestRecordPermission()
                else {

                    val entity = Entity("QI")
                    for (d in currentDictData) {
                        entity.addEntry(EntityEntry(d.word, arrayOf(d.word)))
                    }

                    Thread(
                        Runnable {
                            mService.uploadUserEntities(Collections.singletonList(entity))
                        })
                    mService.startListening()
                }
        }

        fetchDictionaryData()

    }

    private fun fetchDictionaryData() {
        var iDictionaryService = APIFactory().create(IDictionaryAPIService::class.java)
        var dict = iDictionaryService.dictionary
            .enqueue(object : Callback<IDictionaryAPIService.Dict> {
                override fun onFailure(call: Call<IDictionaryAPIService.Dict>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<IDictionaryAPIService.Dict>,
                    response: Response<IDictionaryAPIService.Dict>
                ) {
                    currentDictData.addAll(response.body()?.dictionary ?: ArrayList())
                    listAdapter.notifyDataSetChanged()
                }
            })
    }

    /**
     *
     */
    private fun isRecordPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    }

    protected fun requestRecordPermission() {
        val perm: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
        ActivityCompat.requestPermissions(this, perm, 361)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            mBinding.btnRecord.performClick()
        }
    }


    /**
     *
     */
    override fun onAudioLevel(level: Float) {
    }

    override fun onResult(result: AIResponse?) {
        val recognizedText = result?.result?.resolvedQuery?.toUpperCase()
        mBinding.tvQuery.setText(recognizedText)

        var pattern: Pattern
        var matcher: Matcher

        for (v: DictionaryDTO in currentDictData) {
            pattern = Pattern.compile(v.word?.toUpperCase())
            matcher = pattern.matcher(recognizedText)

            while (matcher.find()) {
                v.frequency++
                v.highlighted = true
                listAdapter.notifyDataSetChangedNoSort()
            }
        }

        listAdapter.notifyDataSetChanged()
        onListeningFinished()
    }

    override fun onListeningStarted() {
        onListening = true
    }

    override fun onError(error: AIError?) {
        onListeningFinished()

    }

    override fun onListeningCanceled() {
        onListeningFinished()
    }

    override fun onListeningFinished() {
        onListening = false
    }

}