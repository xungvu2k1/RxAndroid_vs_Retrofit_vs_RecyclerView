package com.example.rxandroid_vs_retrofit_vs_recyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofit_vs_recyclerview.adapter.CommentAdapter
import com.example.retrofit_vs_recyclerview.api.APIService
import com.example.retrofit_vs_recyclerview.models.Comment
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    val BASE_URL2: String = "https://jsonplaceholder.typicode.com/"
    val TAG: String = "retrofit_intro"

    //recylerview
    private lateinit var rcvComment: RecyclerView
    private lateinit var mCommentAdapter: CommentAdapter
    private lateinit var commentList: List<Comment>

    //rx
    private lateinit var mDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rcvComment = findViewById(R.id.rcv_comment)
        mCommentAdapter = CommentAdapter(this)

        val linearLayoutManager = LinearLayoutManager(this)
        rcvComment.layoutManager = linearLayoutManager

        // đường kẻ phân cách giữa các item
        val mDividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        rcvComment.addItemDecoration(mDividerItemDecoration)

        val observer: Observer<List<Comment>> = getCommentsObserver()

        val apiService: APIService = Retrofit.Builder()
            .baseUrl(BASE_URL2)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // call Adapter Factory RxJava3
            .build()
            .create(APIService::class.java)

        val btnCallApi = findViewById<Button>(R.id.btn_Call_api)
        btnCallApi.setOnClickListener{
            apiService.getComments().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
        }
    }

    private fun getCommentsObserver(): Observer<List<Comment>> {
        return object : Observer<List<Comment>> {
            override fun onSubscribe(d: Disposable) {
                Log.e("Tagx", "onSubscribe ")
                mDisposable = d
            }

            override fun onError(e: Throwable) {
                Log.e("Tagx", "onError")
            }

            override fun onNext(t: List<Comment>) {
                Log.e("Tagx", "onNext : $t")
                Log.e("Tagx", "onNext thread: " + Thread.currentThread().name)
                commentList = t
            }

            override fun onComplete() {
                Log.e("Tagx", "onComplete")
                mCommentAdapter.setData(commentList)
                rcvComment.adapter = mCommentAdapter

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mDisposable.isDisposed) {//ngắt kết nối giữa observer và observable
            mDisposable.dispose()
            Log.e("Tagx", "Disposed")
        }
    }
}

