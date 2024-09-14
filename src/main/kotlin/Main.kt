package com.telecwin.javafx


import com.telecwin.javafx.service.MyService
import com.telecwin.javafx.view_model.MainViewModel.textProp
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage
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
            appContext = runApplication<Main>(*args)
            logger.info("初始化SpringBoot完成！")
            launch(Main::class.java, *args)
        }
    }

    override fun start(stage: Stage?) {
        var value: Int = 0
        stage!!
        stage.title = "Hello JavaFX"
        val vBox = VBox().apply {
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
        val scene = Scene(vBox, 400.0, 300.0) // 创建场景

        val service: MyService = appContext.getBean(MyService::class.java)
        println("${service.greet()}")

        stage.scene = scene // 设置场景
        stage.show() // 显示窗口
        stage.setOnCloseRequest {
            appContext.close()
            logger.info("关闭SpringBoot")
        }
    }

}

