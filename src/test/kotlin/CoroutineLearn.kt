import com.app.pojo.Obj
import com.app.service.LicenceService
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.flowOn
import org.springframework.web.servlet.function.ServerResponse.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.io.OutputStream


/**
 * 协程学习
 */
class CoroutineLearn : Application() {
    /**
     * 许可服务
     */
    private lateinit var licenceService: LicenceService

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(CoroutineLearn::class.java, *args)
        }
    }

    override fun start(primaryStage: Stage?) {
        // 定义默认线程作用域
        val defaultScope = CoroutineScope(Dispatchers.Default)
        val uiScope = CoroutineScope(Dispatchers.Main)
        val flow = flow {
            for (i in 1..100) {
                delay(50)
                // 发射
                emit(0.5)
                println("发射 当前线程${Thread.currentThread().name}")
            }
        }.flowOn(Dispatchers.Default)
        val sharedFlow = MutableSharedFlow<Int>(replay = 1)
        val stateFlow = MutableStateFlow<Int>(0)

        var value: Int = 0
        primaryStage?.run {
            scene = Scene(VBox().apply {
                alignment = Pos.CENTER
                val label = Label("当前显示")
                val progressBar = ProgressBar(0.0)
                children.add(progressBar)
                children.add(label)
                children.add(Button("获取值").apply {
                    onAction = EventHandler {
//                        isDisable = true
//                        println("当前线程${Thread.currentThread().name}")
//                        defaultScope.launch {
//                            for (i in 1..100) {
//                                delay(100)
//                                    progressBar.progress = (i / 100.0)
//                            }
//                            uiScope.launch {
//                                isDisable = false
//                            }
//                        }
                        // flow

//                        defaultScope.launch {
//                            flow.collect {
//                                progressBar.progress = it
//                                println("接收：当前线程${Thread.currentThread().name}")
//                            }
//                        }
//                        flow.onEach {
//                            progressBar.progress = it
//                            println("接收：当前线程${Thread.currentThread().name}")
//                        }.launchIn(CoroutineScope(Dispatchers.Main))
//                        isDisable = false
                        // 热流
                        defaultScope.launch {
                            println("发射：当前线程${Thread.currentThread().name}")
                            sharedFlow.emit(value++)
                        }
//                        stateFlow.value = value++
                    }
                })
//                uiScope.launch {
//                    sharedFlow.collect {
//                        println("接收：当前线程${Thread.currentThread().name}")
//                        label.text = "当前值：$it"
//                    }
//                    stateFlow.collect {
//                        println("接收：当前线程${Thread.currentThread().name}")
//                        label.text = "当前值：$it"
//                    }
//                }
//                stateFlow.filter { it % 2 == 0 }.map { it + 1 }.onEach {
//                    println("接收：当前线程${Thread.currentThread().name}")
//                    label.text = "当前值：$it"
//                }.launchIn(CoroutineScope(Dispatchers.Main))

                children.add(Button("绑定Flow").apply {
                    onAction = EventHandler {
                        uiScope.launch {
                            sharedFlow.collect {
                                println("接收：当前线程${Thread.currentThread().name}")
                                label.text = "当前值：$it"
                            }
                        }
                    }
                })
            }, 400.0, 600.0)
            initLicenceService()
            show()
        }
    }

    private suspend fun computeOne(): Int {
        return withContext(Dispatchers.IO) {
            print("Coroutine #1: Calculating ...")
            delay(2400)
            val res = 12
            println(", got $res")
            return@withContext res
        }
    }

    /**
     * 方式1 原始方式 异步方式
     */
    private fun method1(uiScope: CoroutineScope, label: Label) {
        val cell: Call<Obj> = licenceService.requestLicenceById(1)
        cell.enqueue(object : Callback<Obj> {
            override fun onResponse(p0: Call<Obj>, p1: Response<Obj>) {
                println("请求成功： ${p1.body().toString()}")
                uiScope.launch {
                    label.text = p1.body()?.title
                }
            }

            override fun onFailure(p0: Call<Obj>, p1: Throwable) {
                println("请求失败$p1")
            }

        })
    }

    /**
     * 方式2 原始方式 同步方式
     */
    private fun method2(defaultScope: CoroutineScope, uiScope: CoroutineScope, label: Label) {
        val cell: Call<Obj> = licenceService.requestLicenceById(1)
        defaultScope.launch {
            val execute = cell.execute()
            uiScope.launch {
                label.text = execute.body()?.title
            }
        }
    }

    /**
     * 方式3 使用suspend的方式
     */
    private fun method3(defaultScope: CoroutineScope, uiScope: CoroutineScope, label: Label) {
        defaultScope.launch {
            val suspendCell = licenceService.requestLicenceByIdSuspend(1)
            uiScope.launch {
                label.text = suspendCell.title
            }
        }
    }


    /**
     * 初始化许可服务
     */
    private fun initLicenceService() {
        // 创建工厂类
        val retrofit = Retrofit.Builder().baseUrl("https://jsonplaceholder.typicode.com") // 替换为你的 API base URL
            .addConverterFactory(GsonConverterFactory.create()) // 如果需要使用 Gson 进行数据解析
            .build()
        // 创建服务类
        licenceService = retrofit.create(LicenceService::class.java)
    }

    inline fun InputStream.copyTo(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        progress: (Long) -> Unit
    ): Long {
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = read(buffer)
        while (bytes >= 0) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            bytes = read(buffer)
            progress(bytesCopied) //在最后调用内联函数
        }
        return bytesCopied
    }

}