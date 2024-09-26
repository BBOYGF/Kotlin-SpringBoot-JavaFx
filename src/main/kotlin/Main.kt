package com.telecwin.javafx


import com.telecwin.javafx.service.MyService
import com.telecwin.javafx.view_model.MainViewModel.textProp
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext


@SpringBootApplication
class Main : Application() {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(javaClass)
        lateinit var appContext: ConfigurableApplicationContext

        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java, *args)
        }
    }

    override fun start(stage: Stage?) {
        var value: Int = 0
        stage!!
        stage.title = "Hello JavaFX"
        val image = Image(javaClass.getResourceAsStream("/images/splash.png"))
        val splashView = ImageView(image)
        val vBox = VBox()
        vBox.prefWidth = image.width
        vBox.prefHeight = image.height
        vBox.apply {
            alignment = Pos.CENTER
            children.add(splashView)
        }
        val splashScene = Scene(vBox)
        val splashStage = Stage()
        splashStage.scene = splashScene
        splashStage.initStyle(StageStyle.UNDECORATED)
        splashStage.show()
        val scope = CoroutineScope(Dispatchers.Default)
        val uiScope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            println("当前线程：${Thread.currentThread().name}")
            initSpringBoot()
            uiScope.launch {
                println("当前线程：${Thread.currentThread().name}")
                vBox.apply {
                    children.clear()
                    alignment = Pos.CENTER
                    children.add(Label("hello word!").apply {
                        textProperty().bindBidirectional(textProp)
                    })
                    children.add(Button().apply {
                        text = "点击"
                        setOnAction {
                            logger.info("按钮被单击！")
                            value++
                            textProp.set("当前值$value")
                        }
                    })
                }
                splashStage.close()
                stage.apply {
                    show() // 显示窗口
                    setOnCloseRequest {
                        appContext.close()
                        logger.info("关闭SpringBoot")
                    }
                }
            }
        }
    }

    /**
     * 初始化SpringBoot
     */
    private fun initSpringBoot() {
        appContext = runApplication<Main>()
        val service: MyService = appContext.getBean(MyService::class.java)
        println("${service.greet()}")
        logger.info("初始化SpringBoot完成！")
    }

}

