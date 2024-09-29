package com.app.pojo

/**
 * 响应 封装类
 */
class RespBean(i: Int, message: String?, nothing: Any?) {
    private val code: Long = 0
    private val message: String? = null
    private val obj: Any? = null

    //成功返回
    fun success(message: String?): RespBean {
        return RespBean(200, message, null)
    }

    //成功返回返回
    fun success(message: String?, obj: Any?): RespBean {
        return RespBean(200, message, obj)
    }

    //失败返回
    fun error(message: String?): RespBean {
        return RespBean(500, message, null)
    }

    //失败返回对象
    fun error(message: String?, obj: Any?): RespBean {
        return RespBean(500, message, obj)
    }

}