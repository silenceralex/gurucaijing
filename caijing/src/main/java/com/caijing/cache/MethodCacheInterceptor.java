package com.caijing.cache;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.caijing.util.AopUtils;

/**
 * @author ahuaxuan(aaron zhang)
 * @since 2008-5-13
 * @version $Id: MethodCacheInterceptor.java 1879 2008-11-07 02:58:36Z aaron $
 */
@Component("methodCacheInterceptor")
public class MethodCacheInterceptor implements MethodInterceptor {

	private static transient Log logger = LogFactory
			.getLog(MethodCacheInterceptor.class);

	private static final String CACHENAME_SPLIT_CHAR = "#";

	@Autowired
	@Qualifier("ehCache")
	private Ehcache methodCache;

	private static Map basicTypeMap = new HashMap();

	static {
		basicTypeMap.put("int", "java.lang.Integer");
		basicTypeMap.put("float", "java.lang.Float");
		basicTypeMap.put("long", "java.lang.Long");
		basicTypeMap.put("short", "java.lang.Short");
		basicTypeMap.put("double", "java.lang.Double");
		basicTypeMap.put("char", "java.lang.Character");
		basicTypeMap.put("byte", "java.lang.Byte");
		basicTypeMap.put("boolean", "java.lang.Boolean");
	}

	/**
	 * Make sure that every bean which has been intercepted must has a
	 * interface, pls do not use CGLIB to PROXY class.
	 * 
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept.MethodInvocation)
	 */

	public Object invoke(MethodInvocation invocation) throws Throwable {
		logger.debug("AOP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// String targetName = AopUtils.getTargetClass(invocation.getThis())
		// .getInterfaces()[0].getName();
		// logger.debug("AOP!!!! targetName:" + targetName);
		String methodName = invocation.getMethod().getName();
		// logger.debug("AOP!!!! methodName:" + methodName);
		Object[] arguments = invocation.getArguments();
		Class[] cs = new Class[arguments.length];
		for (int k = 0; k < arguments.length; k++) {
			cs[k] = arguments[k].getClass();
		}

		if (AopUtils.getTargetClass(invocation.getThis()).isAnnotationPresent(
				ObjectCache.class)) {
			ObjectCache oc = AopUtils.getTargetClass(invocation.getThis())
					.getAnnotation(ObjectCache.class);
			return getResult(methodName, arguments, invocation, oc.expire());
		} else {

			Method[] mss = AopUtils.getTargetClass(invocation.getThis())
					.getMethods();
			Method ms = null;
			for (Method m : mss) {
				if (m.getName().equals(methodName)) {
					// logger.debug("AOP!!!! NameMatch:" + m.getName());
					boolean argMatch = true;
					Class[] tmpCs = m.getParameterTypes();
					if (tmpCs.length != cs.length) {
						// logger.debug("AOP!!!! ArgLengthMatch FAIL:"
						// +tmpCs.length + "/" +
						// cs.length);
						argMatch = false;
						continue;
					}
					for (int k = 0; k < cs.length; k++) {
						if (!cs[k].equals(tmpCs[k])) {
							// logger.debug("AOP!!!! ArgTypeMatch FAIL:"
							// +cs[k].getSimpleName() + "/" +
							// tmpCs[k].getSimpleName());
							if (!cs[k].getName().equals(
									basicTypeMap.get(tmpCs[k].getName()))) {
								argMatch = false;
								break;
							}
						}
					}

					if (argMatch) {
						ms = m;
						break;
					}
				}
			}

			if (ms != null && ms.isAnnotationPresent(MethodCache.class)) {
				logger.debug("AOP!!!! MethodMatch!!!:" + ms.getName());
				MethodCache mc = ms.getAnnotation(MethodCache.class);
				return getResult(methodName, arguments, invocation, mc.expire());
			} else {
				return invocation.proceed();
			}
		}
	}

	private Object getResult(String methodName, Object[] arguments,
			MethodInvocation invocation, int expire) throws Throwable {
		Object result;
		String cacheKey = getCacheKey(methodName, arguments);
		logger.debug("!!!CacheKey: " + cacheKey);

		Element element = methodCache.get(cacheKey);
		if (element == null) {
			synchronized (this) {
				element = methodCache.get(cacheKey);
				if (element == null) {
//					result = berkeleyDB.get(cacheKey);
//					if (result == null) {
						logger.debug("!!!NO Cached!!!");
						result = invocation.proceed();
//					}
					if (result != null) {
//						berkeleyDB.put(cacheKey, (Result) result);
						element = new Element(cacheKey, result);
						// logger.debug("!!!result： " + result);
						// annotation没有设expire值则使用ehcache.xml中自定义值
						if (expire > 0) {
							element.setTimeToIdle(expire);
							element.setTimeToLive(expire);
						}
						methodCache.put(element);
						// methodCache.flush();
					}
				}
			}
		}
		// logger.debug("!!!element: " + element.getValue());
		// logger.debug("!!!Cache value: " +
		// methodCache.get(cacheKey).getValue());
		return methodCache.get(cacheKey).getValue();
	}

	private String getCacheKey(String methodName, Object[] arguments) {
		StringBuffer sb = new StringBuffer();
		sb.append(methodName);
		if ((arguments != null) && (arguments.length != 0)) {
			for (int i = 0; i < arguments.length; i++) {
				sb.append(CACHENAME_SPLIT_CHAR).append(arguments[i]);
			}
		}

		return sb.toString();
	}

}
