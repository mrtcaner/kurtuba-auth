package com.kurtuba.auth.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Aspect
@Component
@Profile("test")
public class LoggingAspect
{
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

	private static final Set<String> SENSITIVE_PARAM_KEYWORDS = new HashSet<>(Arrays.asList(
		"password",
		"pass",
		"pwd",
		"token",
		"access_token",
		"refresh_token",
		"id_token",
		"secret",
		"verification",
		"verificationcode",
		"verification_code",
		"activationcode",
		"resetcode",
		"one_time_code",
		"totp",
		"otp",
		"pin"
	));

	private static final Set<Class<?>> SIMPLE_TYPES = new HashSet<>(Arrays.asList(
		String.class,
		Boolean.class,
		Byte.class,
		Short.class,
		Integer.class,
		Long.class,
		Float.class,
		Double.class,
		Character.class
	));

	private static final String MASKED = "***";

	private final Environment environment;
	private final ObjectMapper objectMapper;

	public LoggingAspect(Environment environment, ObjectMapper objectMapper)
	{
		this.environment = environment;
		this.objectMapper = objectMapper;
	}

	@Around("execution(* com.kurtuba..service..*(..)) || execution(* com.kurtuba..controller..*(..))")
	public Object logMethodCall(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
	{
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		String className = methodSignature.getDeclaringType().getSimpleName();
		String methodName = methodSignature.getName();
		boolean devProfile = environment.acceptsProfiles(Profiles.of("dev"));

		long startNanos = System.nanoTime();
		try
		{
			return proceedingJoinPoint.proceed();
		}
		finally
		{
			long durationMs = (System.nanoTime() - startNanos) / 1_000_000;
			String params = buildParamsLog(methodSignature, proceedingJoinPoint.getArgs(), devProfile);
			LOGGER.info("Method {}.{} executed in {} ms | params: {}", className, methodName, durationMs, params);
		}
	}

	private String buildParamsLog(MethodSignature methodSignature, Object[] args, boolean devProfile)
	{
		String[] paramNames = methodSignature.getParameterNames();
		if (args == null || args.length == 0)
		{
			return "{}";
		}

		ObjectNode paramsNode = objectMapper.createObjectNode();
		for (int i = 0; i < args.length; i++)
		{
			String paramName = resolveParamName(paramNames, i);
			Object arg = args[i];
			paramsNode.set(paramName, toLogNode(paramName, arg, devProfile));
		}
		return paramsNode.toString();
	}

	private String resolveParamName(String[] paramNames, int index)
	{
		if (paramNames == null || index >= paramNames.length || paramNames[index] == null || paramNames[index].isBlank())
		{
			return "arg" + index;
		}
		return paramNames[index];
	}

	private JsonNode toLogNode(String key, Object value, boolean devProfile)
	{
		if (value == null)
		{
			return objectMapper.nullNode();
		}

		if (!devProfile && isSensitiveKey(key))
		{
			return objectMapper.getNodeFactory().textNode(MASKED);
		}

		if (isSimpleValue(value))
		{
			return objectMapper.valueToTree(value);
		}

		if (value.getClass().isArray())
		{
			return arrayToNode(key, value, devProfile);
		}

		if (value instanceof Iterable<?> iterable)
		{
			ArrayNode arrayNode = objectMapper.createArrayNode();
			for (Object item : iterable)
			{
				arrayNode.add(toLogNode(key, item, devProfile));
			}
			return arrayNode;
		}

		if (value instanceof Map<?, ?> map)
		{
			ObjectNode mapNode = objectMapper.createObjectNode();
			for (Map.Entry<?, ?> entry : map.entrySet())
			{
				String entryKey = String.valueOf(entry.getKey());
				mapNode.set(entryKey, toLogNode(entryKey, entry.getValue(), devProfile));
			}
			return mapNode;
		}

		try
		{
			JsonNode objectNode = objectMapper.valueToTree(value);
			if (!devProfile)
			{
				maskSensitiveFields(objectNode);
			}
			return objectNode;
		}
		catch (IllegalArgumentException ex)
		{
			return objectMapper.getNodeFactory().textNode(safeToString(value, devProfile));
		}
	}

	private ArrayNode arrayToNode(String key, Object value, boolean devProfile)
	{
		int length = Array.getLength(value);
		ArrayNode arrayNode = objectMapper.createArrayNode();
		for (int i = 0; i < length; i++)
		{
			arrayNode.add(toLogNode(key, Array.get(value, i), devProfile));
		}
		return arrayNode;
	}

	private void maskSensitiveFields(JsonNode node)
	{
		if (node == null || node.isNull())
		{
			return;
		}

		if (node.isObject())
		{
			ObjectNode objectNode = (ObjectNode) node;
			Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
			while (fields.hasNext())
			{
				Map.Entry<String, JsonNode> field = fields.next();
				if (isSensitiveKey(field.getKey()))
				{
					objectNode.put(field.getKey(), MASKED);
				}
				else
				{
					maskSensitiveFields(field.getValue());
				}
			}
			return;
		}

		if (node.isArray())
		{
			for (JsonNode child : node)
			{
				maskSensitiveFields(child);
			}
		}
	}

	private boolean isSensitiveKey(String key)
	{
		if (key == null)
		{
			return false;
		}
		String normalized = key.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
		for (String sensitiveKeyword : SENSITIVE_PARAM_KEYWORDS)
		{
			String normalizedKeyword = sensitiveKeyword.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
			if (normalized.contains(normalizedKeyword))
			{
				return true;
			}
		}
		return false;
	}

	private boolean isSimpleValue(Object value)
	{
		Class<?> type = value.getClass();
		return type.isPrimitive()
			|| SIMPLE_TYPES.contains(type)
			|| Number.class.isAssignableFrom(type)
			|| Enum.class.isAssignableFrom(type)
			|| Temporal.class.isAssignableFrom(type)
			|| type.getPackageName().startsWith("java.time")
			|| UUID.class.isAssignableFrom(type);
	}

	private String safeToString(Object value, boolean devProfile)
	{
		String className = value.getClass().getSimpleName();
		if (!devProfile && isSensitiveKey(className))
		{
			return MASKED;
		}

		try
		{
			return value.toString();
		}
		catch (Exception ex)
		{
			return className;
		}
	}
}
