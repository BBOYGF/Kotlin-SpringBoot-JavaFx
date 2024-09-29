import com.app.pojo.Obj
import com.app.service.LicenceService
import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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

        primaryStage?.run {
            scene = Scene(VBox().apply {
                alignment = Pos.CENTER
                val label = Label("当前显示")
                children.add(label)
                children.add(Button("获取值").apply {
                    onAction = EventHandler {
                        println("当前线程${Thread.currentThread().name}")
                        // 使用后台线程执行
//                        method1(uiScope, label)
//                        method2(defaultScope, uiScope, label)
//                        method3(defaultScope, uiScope, label)
//                        val job: Job = defaultScope.launch {
//                            println("启动前")
//                            delay(1000)
//                            println("启动后")
//                        }
//                        Thread.sleep(500)
//                        job.cancel()
//                        Thread.sleep(1000)

//                        val thread = Thread {
//                            println("启动前")
//                            Thread.sleep(1000)
//                            println("启动后")
//                        }
//                        thread.start()
//                        Thread.sleep(500)
//                        thread.stop()
//                        Thread.sleep(1000)
                        val startTime = System.currentTimeMillis()
//
//                        val deferred1: Deferred<String> = defaultScope.async {
//                            delay(500)
//                            return@async "100"
//                        }
//                        val deferred2: Deferred<String> = defaultScope.async {
//                            delay(1000)
//                            return@async "200"
//                        }
//
//                        uiScope.launch {
//
//                            try {
//                                val str1 = deferred1.await()
//                                val str2 = deferred2.await()
//                                label.text = "$str1 |:| $str2"
//                            } catch (e: Exception) {
//                                println("产生异常:$e")
//                            }
//                            println("用时：${System.currentTimeMillis() - startTime}毫秒")
//                        }
                        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
                            println("Caught $exception")
                        }
                        // 方式二
                        uiScope.launch {
                            var str1 = ""
                            var str2 = ""
//                            supervisorScope {
                                val job1 = defaultScope.launch {
                                    delay(1000)
                                    str1 = "100"
                                }
                                val job2 = defaultScope.launch(exceptionHandler) {
                                    delay(1000)
                                    1/0
                                    str2 = "200"
                                }

                                job1.join()
                                job2.join()
//                            }
                            label.text = "$str1 |:| $str2"
                            println("用时：${System.currentTimeMillis() - startTime}毫秒")
                        }


                    }
                })

            }, 400.0, 600.0)
            initLicenceService()
            show()
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

}