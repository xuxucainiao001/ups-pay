package com.pgy.ups.pay.commom.utils;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.hibernate.validator.HibernateValidator;

import com.pgy.ups.common.exception.ParamValidException;

/**
   * 参数验证器
 * @author 墨凉
 *
 */

public class ValidateUtils {
	
	/**
	 * 初始化验证器
	 */
	private static Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(true)
			.buildValidatorFactory().getValidator();

	public static <T> void validate(T obj) throws ParamValidException {
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
		// 抛出检验异常
		if (constraintViolations.size() > 0) {
			throw new ParamValidException(
					String.format("参数校验失败:%s", constraintViolations.iterator().next().getMessage()));
		}
	}

}
