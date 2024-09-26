package com.app


import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.generator.AutoGenerator
import com.baomidou.mybatisplus.generator.InjectionConfig
import com.baomidou.mybatisplus.generator.config.*
import com.baomidou.mybatisplus.generator.config.po.TableInfo
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine
import com.app.Constants.APP_NAME
import com.app.Constants.ICON_PATH
import com.app.Constants.SPLASH_SCREEN
import com.app.service.MyService
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import java.util.*
import kotlin.collections.ArrayList


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

    override fun start(primaryStage: Stage?) {
        var value: Int = 0
        primaryStage!!
        val defaultScope = CoroutineScope(Dispatchers.Default)
        val uiScope = CoroutineScope(Dispatchers.Main)
        val iconImage = Image(javaClass.getResourceAsStream(ICON_PATH))
        val vBox = VBox().apply {
            val image = Image(javaClass.getResourceAsStream(SPLASH_SCREEN))
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
            uiScope.launch {
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
        val service: MyService = appContext.getBean(MyService::class.java)
        println("${service.greet()}")
        logger.info("初始化SpringBoot完成！")
    }


    private fun generateSQLCode() {
        val generator = AutoGenerator()
        val projectPath = System.getProperty("user.dir")
        // 设置FreemarkerTemplateEngine模板引擎
        generator.setTemplateEngine(FreemarkerTemplateEngine())
        //全局设置
        val globalConfig: GlobalConfig = GlobalConfig()
        globalConfig.setOutputDir(System.getProperty("user.dir") + "/src/main/kotlin")
        //作者
        globalConfig.setAuthor("guofan")
        //打开输入目录
        globalConfig.setOpen(false)
        //xml开启BaseResultMap
        globalConfig.setBaseResultMap(true)
        //xml开启BaseColumnList
        globalConfig.setBaseColumnList(true)
        //实体属性Swagger2注解
        globalConfig.setSwagger2(true)
        generator.setGlobalConfig(globalConfig)
        //配置数据源
        val dataSourceConfig = DataSourceConfig()
        dataSourceConfig.setUrl("jdbc:sqlite:./data/database")
        dataSourceConfig.setDriverName("org.sqlite.JDBC")
        dataSourceConfig.setUsername("")
        dataSourceConfig.setPassword("")
        //添加数据源
        generator.setDataSource(dataSourceConfig)
        //包配置
        val pc = PackageConfig()
        pc.setParent("com.app")
            .setEntity("pojo")
            .setMapper("mapper")
            .setService("service")
            .setServiceImpl("service.impl")
        //                .setController("controller");
        //设置包
        generator.setPackageInfo(pc)
        //自定义配置
        val cfg: InjectionConfig = object : InjectionConfig() {
            override fun initMap() {
            }
        }
        //如果模板引擎是freemarker
        val templatePath = "/templates/mapper.xml.ftl"
        //自定义输出配置
        val focList: MutableList<FileOutConfig> = ArrayList()
        //自定义配置会被优先输出
        focList.add(object : FileOutConfig(templatePath) {
            override fun outputFile(tableInfo: TableInfo): String {
                return (projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName()).toString() + "Mapper" + StringPool.DOT_XML
            }
        })
        cfg.setFileOutConfigList(focList)
        generator.setCfg(cfg)
        val templateConfig = TemplateConfig()
        templateConfig.setXml(null)
        generator.setTemplate(templateConfig)
        //策略设置
        val strategy = StrategyConfig()
        //数据库表映射到实体的名字策略  下划线转换成驼峰
        strategy.setNaming(NamingStrategy.underline_to_camel)
        //lombok模型
        strategy.setEntityLombokModel(true)
        //生成@RestController控制器
        strategy.setRestControllerStyle(true)
        strategy.setInclude(*scanner("表名，多个英文逗号分隔").split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray())
        strategy.setControllerMappingHyphenStyle(true)
        //表前缀
        strategy.setTablePrefix("t_")
        //添加策略
        generator.setStrategy(strategy)
        generator.execute()
    }

    private fun scanner(tip: String): String {
        val scanner: Scanner = Scanner(System.`in`)
        val help = StringBuilder()
        help.append("请输入$tip：")
        println(help.toString())
        if (scanner.hasNext()) {
            val ipt: String = scanner.next()
            if (ipt != null || ipt == "") {
                return ipt
            }
        }
        throw MybatisPlusException("请输入正确的$tip!")
    }

}

