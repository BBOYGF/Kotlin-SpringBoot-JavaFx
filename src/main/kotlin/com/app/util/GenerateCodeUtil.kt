package com.app.util

import com.baomidou.mybatisplus.generator.FastAutoGenerator
import com.baomidou.mybatisplus.generator.config.GlobalConfig
import com.baomidou.mybatisplus.generator.config.PackageConfig
import com.baomidou.mybatisplus.generator.config.StrategyConfig
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine


/**
 * 生成代码工具类
 */
object GenerateCodeUtil {
    fun generateSQLCode() {

        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create("jdbc:sqlite:C:\\projects\\kotlin_springboot_javaFx\\data\\database.db3", "", "")
            .globalConfig { builder: GlobalConfig.Builder ->
                builder.author("GuoFan") // 设置作者
                    .commentDate("yyyy-MM-dd")
                    .outputDir("src\\main\\kotlin") // 输出目录
                    .disableOpenDir() // 禁止打开输出目录
                    .enableKotlin() // 开启kotlin模式
            }
            .packageConfig { builder: PackageConfig.Builder ->
                builder.parent("com.app") // 设置父包名
                    .entity("pojo") // 设置实体类包名
                    .mapper("mapper") // 设置 Mapper 接口包名
                    .service("service") // 设置 Service 接口包名
                    .serviceImpl("service.impl") // 设置 Service 实现类包名
                    .xml("mapper.xml") // 设置 Mapper XML 文件包名
            }
            .strategyConfig { builder: StrategyConfig.Builder ->
                builder.addInclude("t_user") // 设置需要生成的表名
                    .addTablePrefix("t_", "c_") // 设置过滤表前缀
                    .entityBuilder()
                    .enableLombok() // 启用 Lombok
                    .enableTableFieldAnnotation() // 启用字段注解
                    .controllerBuilder()
                    .enableRestStyle() // 启用 REST 风格

            }
            .templateEngine(FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
            .execute() // 执行生成
    }


}