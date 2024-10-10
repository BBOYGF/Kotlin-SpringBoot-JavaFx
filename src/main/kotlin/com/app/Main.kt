package com.app


import com.app.Constants.APP_NAME
import com.app.Constants.ICON_PATH
import com.app.Constants.SPLASH_SCREEN
import com.app.view_model.MainViewModel.textProp
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
import kotlinx.coroutines.withContext
import org.mybatis.spring.annotation.MapperScan
import org.mybatis.spring.annotation.MapperScans
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

@MapperScan("com.app.mapper")
@SpringBootApplication
class Main : Application() {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(javaClass)
        lateinit var appContext: ConfigurableApplicationContext

        @JvmStatic
        fun main(args: Array<String>) {
            // 根据表生成Mybatis 代码 新增表时可以启动改注释
//            GenerateCodeUtil.generateSQLCode()
            launch(Main::class.java, *args)
        }
    }

    override fun start(primaryStage: Stage?) {
        var value: Int = 0
        primaryStage!!
        val defaultScope = CoroutineScope(Dispatchers.Default)
        val uiScope = CoroutineScope(Dispatchers.Main)
        val iconImage = Image(javaClass.getResourceAsStream(ICON_PATH))
        val image = Image(javaClass.getResourceAsStream(SPLASH_SCREEN))
        val vBox = VBox().apply {
            prefWidth = image.width
            prefHeight = image.height
            alignment = Pos.CENTER
            children.add(ImageView(image))
        }

        val splashStage = Stage().apply {
            scene = Scene(vBox)
            initStyle(StageStyle.UNDECORATED)
            icons.add(iconImage)
            show()
        }
        defaultScope.launch {
            logger.info("当前线程：${Thread.currentThread().name}")
            // 后台线程初始化SpringBoot
            try {
                initSpringBoot()
            } catch (e: Exception) {
                logger.error("springboot初始化异常", e)
                uiScope.launch {
                    splashStage.close()
                    primaryStage.close()
                }
                return@launch
            }

            // 显示主窗口
            withContext(Dispatchers.Main) {
                logger.info("当前线程：${Thread.currentThread().name}")
                splashStage.close()
                primaryStage.apply {
                    width = 800.0
                    height = 600.0
                    title = APP_NAME
                    icons.add(iconImage)
                    scene = Scene(VBox().apply {
                        alignment = Pos.CENTER
                        maxWidth = Double.MAX_VALUE
                        maxHeight = Double.MAX_VALUE
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
                    })
                    setOnCloseRequest {
                        appContext.close()
                        logger.info("关闭SpringBoot")
                    }
                    show() // 显示窗口
                }
            }
        }
    }

    /**
     * 初始化SpringBoot
     */
    private fun initSpringBoot() {
        appContext = runApplication<Main>()
        logger.info("初始化SpringBoot完成！")
    }
}


