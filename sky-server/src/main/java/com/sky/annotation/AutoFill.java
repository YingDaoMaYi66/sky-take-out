package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//自定义注解，这个注解是为方法添加的
@Target(ElementType.METHOD)
//保留策略
//1:SOURCE：注解只在源代码中存在，编译后被丢弃
//2:CLASS：注解在源代码中存在，编译后被丢弃，运行时无法获取
//3:RUNTIME：注解在源代码中存在，编译后也存在，运行时可以获取
//4:可以通过反射机制获取注解信息
//5:常用于需要在运行时动态处理的场景，例如AOP，依赖注入，自定义注解处理等
@Retention(RetentionPolicy.RUNTIME)
//这是一个自定义注解的定义，@AutoFill 注解用于标记方法，并通过其属性 value() 指定数据库操作类型（如 UPDATE 或 INSERT）。以下是其作用的简要说明
//        属性： OperationType value() 是注解的一个属性，要求使用该注解时必须指定操作类型。
public @interface AutoFill {
    //数据库操作类型，UPDATE INSERT 因为update 只需要更新两个参数，而insert 需要更新四个参数
    OperationType value();
}
