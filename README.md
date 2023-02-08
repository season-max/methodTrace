# 耗时监测插件

- 基于 **Gradle Plugin + Transform + AMS** 方法耗时监测插件

## 使用

1.在 project 的 build.gradle 中引入依赖

```groovy
buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }

    dependencies {
        classpath 'com.zhangyue.plugins:method-trace:${plugin_version}'
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

2.在 module 的 build.gradle 中引入插件和依赖

```groovy
apply plugin: 'method-trace-plugin'
trace_config {
    printlnLog = true
    pkgList = ['com.zhangyue.ireader.methodtrace']
    checkOnlyMainThread = false
    errorThreshold = 50
    warnThreshold = 30
    infoThreshold = 20
    printCallStack = true
    customHandle = "com.zhangyue.ireader.methodtrace.MyMethodTraceHandle"
}

dependencies {
    implementation 'com.github.season-max:methodTrace:0.14'
}
```

3.项目运行之后，在 logcat 过滤 methodTrace 获取日志信息

## 插件配置项说明

### printlnLog

> 是否打印插件内部日志信息

### pkgList

> 想要执行耗时监测的包名，会对包名下的所有 class 进行插桩，直接匹配到某个 class 全限定名称也可以

### checkOnlyMainThread

> 是否只对主线程方法进行耗时监测，如果是，将只打印主线程日志信息

### infoThreshold，warnThreshold，errorThreshold

> 设置了三级 log 的阈值，要保证 0 < info < warn < error,至少需要设置一个级别的阈值

### printCallStack

> 是否打印方法的堆栈信息，默认不打印。打印堆栈使 log 信息过长，不易查看。用户可以根据需要配置

### customHandle

> 自定义监测处理接口的全限定名称，需要实现[IMethodTraceHandle]接口，将方法入口、出口逻辑托管给用户

```kotlin
/**
 * @author season
 * 对方法入口、出口的逻辑自定义处理
 *
 * 需要添加 [IgnoreMethodTrace] 注解，防止被插桩
 */
@Keep
@IgnoreMethodTrace
class MyMethodTraceHandle : IMethodTraceHandle {
    override fun onMethodEnter(
        any: Any,
        className: String,
        methodName: String,
        args: String,
        returnType: String
    ) {
        Log.e("method_trace_handle", "-------> onMethodEnter")
    }

    override fun onMethodExit(
        any: Any,
        className: String,
        methodName: String,
        args: String,
        returnType: String
    ) {
        Log.e("method_trace_handle", "-------> onMethodExit")
    }
}
```

### 注解

- HookMethodTrace：执行插桩的注解，作用于方法或者类型上。即使某个方法所在的类或者某个类和插件设置的 **pkgList** 不匹配，如果有此注解，也会执行插桩逻辑
- IgnoreMethodTrace：忽略插桩的注解，作用于类型上，优先级大于 HookMethodTrace

## 效果

### 编译阶段

编译完成之后，在 module/build 文件夹下生成 **methodTrace.json** 文件，记录插桩信息，方便溯源
![method trace list](/png/trace_method_list.png)

### 运行阶段

app 运行之后，会在 logcat 中打印出大于等于设置 log 级别阈值的信息

- info --> log.i
  ![info_log](/png/log/info_log.png)
- warn --> log.w
  ![warn_log](/png/log/warn_log.png)
- error --> log.e
  ![error_log](/png/log/error_log.png)

#### 输入日志信息

- this : 当前实例。如果是 static 方法，为 null
- pkgName : 包名
- className : 类全限定名称
- methodName : 方法名称
- costTime : 耗时，单位 ms
- pid : 进程 pid
- threadName : 线程名称
- callStack : 调用堆栈

## 基本原理

> 通过插桩的方式，在每个匹配的方法入口和出口处添加钩子，计算耗时信息
