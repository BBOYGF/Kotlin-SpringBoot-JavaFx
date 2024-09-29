package com.app.util

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.generator.AutoGenerator
import com.baomidou.mybatisplus.generator.InjectionConfig
import com.baomidou.mybatisplus.generator.config.*
import com.baomidou.mybatisplus.generator.config.po.TableInfo
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine
import java.util.*

/**
 * 生成代码工具类
 */
object GenerateCodeUtil {
    fun generateSQLCode() {
        val generator = AutoGenerator()
        val projectPath = System.getProperty("user.dir")
        // 设置FreemarkerTemplateEngine模板引擎
        generator.setTemplateEngine(FreemarkerTemplateEngine())
        //全局设置
        val globalConfig = GlobalConfig()
        // kotlin 策略 关键点
//    globalConfig.isKotlin=true
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
        dataSourceConfig.setUrl("jdbc:sqlite:C:\\projects\\kotlin_springboot_javaFx\\data\\database.db3")
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
                return (projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName()) + "Mapper" + StringPool.DOT_XML
            }
        })
        cfg.setFileOutConfigList(focList)
        generator.setCfg(cfg)
        val templateConfig = TemplateConfig()
        templateConfig.setXml(null)
        generator.setTemplate(templateConfig)
        //策略设置
        val strategy = StrategyConfig()
        // 不生成注释
//    strategy.setEntityTableFieldAnnotationEnable(false)
        //数据库表映射到实体的名字策略  下划线转换成驼峰
        strategy.setNaming(NamingStrategy.underline_to_camel)
        //lombok模型
        strategy.setEntityLombokModel(true)
        //生成@RestController控制器
        strategy.setRestControllerStyle(true)
        val toTypedArray = scanner("表名，多个英文逗号分隔").split(",").toTypedArray()
        strategy.setInclude(*toTypedArray)
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