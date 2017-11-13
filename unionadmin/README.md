#unionadmin

2017-08-06
TODO  
1.每日零点，需要整理网站主和广告主账户中的金额  
2.每日零点，需生成日结的结算记录，需判断是否是周结日或月结日并相应生成结算记录  
3.涉及到用户的修改，都需要使用zookeeper
--------------------------------------------------------------

2017-06-01
解决了let's encrypt不被手机浏览器信任的问题  
https://www.ccer.me/2016/12/17/534
--------------------------------------------------------------

2017-05-16
1.使用了let's encrypt为域名加上https  
参考：  
1.http://www.fancyecommerce.com/2017/04/07/centos-%E4%B8%8B%E5%AE%89%E8%A3%85-lets-encrypt-%E6%B0%B8%E4%B9%85%E5%85%8D%E8%B4%B9-ssl-%E8%AF%81%E4%B9%A6/  
2.https://www.freehao123.com/lets-encrypt/
--------------------------------------------------------------

2017-05-09
1.完善了权限管理页面的功能  
2.完善了角色管理页面的功能  
参考：  
1.http://loudev.com/#home  
2.http://api.jqueryui.com/dialog/#option-modal  
3.http://icheck.fronteed.com/
--------------------------------------------------------------

2017-05-06
1.增加了data table的插件  
2.增加了modal窗体  
3.增加了form表单的控件  
参考:  
1.http://bootstrap-table.wenzhixin.net.cn/documentation/  
2.http://www.runoob.com/bootstrap/bootstrap-modal-plugin.html  
3.http://davidstutz.github.io/bootstrap-multiselect/  
4.http://malsup.com/jquery/form/#ajaxSubmit

---------------------------------------------------------------
2017-05-03
1.对login页面进行改造，去掉多余的选项，添加验证码功能，并完成输入的简单验证  
2.内页使用layout，抽出相同部分，比如菜单和登录信息  
参考：  
1.http://www.voidcn.com/blog/xyr05288/article/p-5761393.html  
2.字体图标--http://fontawesome.io/icons/

----------------------------------------------------------------
2017-04-22
1.增加了管理员的CRUD代码  
2.还是决定去掉fastJson，选用之前的json组件

-----------------------------------------------------------------
2017-04-20
1.调整了一些代码，总体上不变  
2.增加角色的CRUD代码

-----------------------------------------------------------------
2017-04-18
1.调整了SysOperateLog的字段  
2.增加了对Controller的aop处理
3.增加了SysAuthority的查询方法
4.添加了阿里巴巴fastJson组件  
参考：http://blog.csdn.net/zknxx/article/details/53240959

------------------------------------------------------------------
2017-04-17
1.WebMvcConfig已经没用了，过期处理  
2.调试了对ERROR的处理（403，500等）

------------------------------------------------------------------
2017-04-14
1.经过调试发现Config类可以简化，所以修改了WebSecurityConfig类中的configure(HttpSecurity http)方法  
2.发现UserDetailsAuthenticationProvider有点画蛇添足，所以干脆去掉，在WebSecurityConfig中直接注解原生的DaoAuthenticationProvider
3.thymeleaf页面上的标签需要严格的xml格式，就是结束标签
4.密码的加密方式改了，从Md5PasswordEncoder改成了StandardPasswordEncoder  
5.登录模块初步调试成功，能进入系统首页

-------------------------------------------------------------------
2017-04-13
添加spring boot的Config配置类：DataSourceConfig，WebSecurityConfig  
PS:由于Autowire存在互相依赖的关系，导致初始化失败，所以把spring security的自定义类部分去掉Autowire  
参考：  
1.SecurityMetaSource类型不对，http://www.68idc.cn/help/jiabenmake/qita/20150214220546.html  
2.thymeleaf纯JAVA配置，http://blog.csdn.net/z28126308/article/details/54429853

--------------------------------------------------------------------
2017-04-12  
着手添加spring security权限控制的自定义模块  
参考：  
1.http://docs.spring.io/spring-security/site/docs/4.2.2.RELEASE/reference/htmlsingle/#ns-web-advanced 
2.http://blog.csdn.net/shierqu/article/details/49538791

---------------------------------------------------------------------
2017-04-11  
1.新建Maven项目，添加spring bott starts依赖    
2.新建后台管理权限管理的核心pojo：manage，role，authority    
3.添加repository，service，controller  
