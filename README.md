**1. 创建binary帐号并配置**
- 登陆 [bintray](https://bintray.com/) 并创建帐号
- 创建成功之后点击Add New Repository 创建一个 Mavne 仓库
![](https://upload-images.jianshu.io/upload_images/4443425-edbd4055384b8f09.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- 选择Maven类型
![](https://upload-images.jianshu.io/upload_images/4443425-9c69d8afcde89b3d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
**2. 配置Android studio**
- 在项目的根目录的build.gradle中添加如下代码：
```
classpath 'com.github.dcendents:android-maven-gradle-plugin:1.4.1'
classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.4'
```
- 在需要上传的module工程的根目录下创建j-build.gradle文件,内容如下：
```
// 这里添加下面两行代码。
apply plugin: 'com.github.dcendents.android-maven'
apply plugin: 'com.jfrog.bintray'

// 定义两个链接，下面会用到。
def siteUrl = 'https://github.com/za3999/libs' // 项目主页。
def gitUrl = 'https://github.com/za3999/libs.git' // Git仓库的url。

// 唯一包名，比如implementation 'com.cf.lib:common:1.0.0'中的com.cf.lib就是这里配置的。
group = "com.cf.lib"

//项目引用的版本号，比如implementation 'com.cf.lib:common:1.0.0'中的1.0.0就是这里配置的。
version = "1.0.0"

install {
    repositories.mavenInstaller {
        // 生成pom.xml和参数
        pom {
            project {
                packaging 'aar'
                // 项目描述，复制我的话，这里需要修改。
                name 'common'// 可选，项目名称。
                description 'common'// 可选，项目描述。
                url siteUrl // 项目主页，这里是引用上面定义好。

                // 软件开源协议，现在一般都是Apache License2.0吧，复制我的，这里不需要修改。
                licenses {
                    license {
                        name 'The Apache Software License, Version 2.0'
                        url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                    }
                }

                //填写开发者基本信息，复制我的，这里需要修改。
                developers {
                    developer {
                        id 'cfzheng' // 开发者的id。
                        name 'cfzheng' // 开发者名字。
                        email 'zhangcaifu3@gmail.com' // 开发者邮箱。
                    }
                }

                // SCM，复制我的，这里不需要修改。
                scm {
                    connection gitUrl // Git仓库地址。
                    developerConnection gitUrl // Git仓库地址。
                    url siteUrl // 项目主页。
                }
            }
        }
    }
}

// 生成jar包的task，不需要修改。
task sourcesJar(type: Jar) {
    from android.sourceSets.main.java.srcDirs
    classifier = 'sources'
}

// 生成jarDoc的task，不需要修改。
task javadoc(type: Javadoc) {
    source = android.sourceSets.main.java.srcDirs
    classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
    // destinationDir = file("../javadoc/")
    failOnError false // 忽略注释语法错误，如果用jdk1.8你的注释写的不规范就编译不过。
}

// 生成javaDoc的jar，不需要修改。
task javadocJar(type: Jar, dependsOn: javadoc) {
    classifier = 'javadoc'
    from javadoc.destinationDir
}
artifacts {
    archives javadocJar
    archives sourcesJar
}

// 这里是读取Bintray相关的信息，我们上传项目到github上的时候会把gradle文件传上去，所以不要把帐号密码的信息直接写在这里，写在local.properties中，这里动态读取。
Properties properties = new Properties()
properties.load(project.rootProject.file('local.properties').newDataInputStream())
bintray {
    user = properties.getProperty("bintray.user") // Bintray的用户名。
    key = properties.getProperty("bintray.apikey") // Bintray刚才保存的ApiKey。

    configurations = ['archives']
    pkg {
        repo = "lib"  //Repository名字 需要自己在bintray网站上先添加
        name = "common"// 发布到Bintray上的项目名字
        userOrg = 'cfzheng'//Bintray的组织id
        websiteUrl = siteUrl
        vcsUrl = gitUrl
        licenses = ["Apache-2.0"]
        publish = true // 是否是公开项目。
    }
}
```
`注：在最后的bintray里面有从local.properties文件中获取用户名跟apikey。这是保密信息，我们不能暴露给别人，build.gradle文件我们会提交到git服务器上，但是local.properties文件不会提交。`
- 在module根目录的build.gradle文件末尾添加如下代码：
```
apply from: 'j-build.gradle'
```
- 在local.properties中添加我们的binary user和key 
```
bintray.user=xxxx
bintray.apikey=xxxxxxxx
```
`user是我们注册的名字，apikey需要去jcenter官网查看。进入修改用户界面，点击左侧apikey按钮，然后输入密码就能看到了。把这个key复制到local.properties中替换掉。`
主要截图如下:
![](https://upload-images.jianshu.io/upload_images/4443425-da8d719fc32921ba.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/4443425-379e02adf2812034.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/4443425-12aa137cf94251b1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**3. Gradle命令上传**
上传项目到jcenter需要用到gradle命令，首先把gralde加入到环境变量。
在android studio底部有个Terminal的按钮。点击它进入Terminal界面:
依次输入以下命令
```
gradle clean
gradle install
gradle bintrayUpload

或者
./gradlew clean
./gradlew install
./gradlew bintrayUpload
```
出现BUILD SUCCESSFUL就表示成功了。

**4. 在项目中使用**
- 获取你的 Maven URL 并配置在需要使用的项目根目录的build.gradle 中
```
maven { url 'https://cfzheng.bintray.com/lib'}
```
- 在 Moudle 的 build.gradle 文件中添加依赖引用了
```
implementation 'com.cf.lib:baselibrary:1.0.0'
```
地址来源如下图：
 ![](https://upload-images.jianshu.io/upload_images/4443425-56a99a056f03312d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![](https://upload-images.jianshu.io/upload_images/4443425-4113d8110b039c5c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![](https://upload-images.jianshu.io/upload_images/4443425-fb22dc9ffc9a973c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
`现在你的 lib 可以正常使用了，但是需要在各自的 Project 的 build.gradle 中添加 maven { url 'https://cfzheng.bintray.com/lib'}，否则无法使用。这里需要要将你的 lib 同步到 Jcenter 库，因为 Jcenter 是一个标准托管库，几乎所有 build.gradle 中都会包含 jcenter() 以便访问。`

**5.Add to Jcetner**
所以在刚才项目页面点击进去查看详情，点击Add to Jcetner：
![](https://upload-images.jianshu.io/upload_images/4443425-1b66db1132651ae2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
之后的页面中填写项目信息，交给Bintray管理员审核：
![](https://upload-images.jianshu.io/upload_images/4443425-7015aeb95c69ca8f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
审核通过之后，就不需要配置Maven URL了。

**6. 多module和注解参考**
 https://blog.csdn.net/lmj623565791/article/details/51148825
