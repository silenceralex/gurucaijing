package com.caijing.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.beans.DirectFieldAccessor;

/**
 * 
 * @author Norther
 * 
 */
public abstract class AopUtils extends org.springframework.aop.support.AopUtils {

	private static final String ADVISED_FIELD_NAME = "advised";

	private static final String CLASS_JDK_DYNAMIC_AOP_PROXY = "org.springframework.aop.framework.JdkDynamicAopProxy";

	private static final Log log = LogFactory.getLog(AopUtils.class);

	@SuppressWarnings("unchecked")
	public static Class<?> getTargetClass(Object candidate) {
		if (!isJdkDynamicProxy(candidate)) {
			return org.springframework.aop.support.AopUtils.getTargetClass(candidate);
		}

		return getTargetClassFromJdkDynamicAopProxy(candidate);
	}

	@SuppressWarnings("unchecked")
	private static Class<?> getTargetClassFromJdkDynamicAopProxy(Object candidate) {
		
		try {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(candidate);
			if (!invocationHandler.getClass().getName().equals(CLASS_JDK_DYNAMIC_AOP_PROXY)) {
				log.warn("get target class error, the invocationHandler of JdkDynamicProxy isn`t the instance of " + CLASS_JDK_DYNAMIC_AOP_PROXY);
				return candidate.getClass();
			}
			AdvisedSupport advised = (AdvisedSupport) new DirectFieldAccessor(invocationHandler).getPropertyValue(ADVISED_FIELD_NAME);
			Class targetClass = advised.getTargetClass();
			if (Proxy.isProxyClass(targetClass)) {
				// 目标类还是代理，递归
				Object target = advised.getTargetSource().getTarget();
				return getTargetClassFromJdkDynamicAopProxy(target);
			}
			return targetClass;
		} catch (Exception e) {
			log.error("get target class from " + CLASS_JDK_DYNAMIC_AOP_PROXY + " error", e);
			return candidate.getClass();
		}
	}

}
