package cz.gattserver.grass.core.model.util;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Date;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.common.util.DateUtils;

public class PredicateBuilder {

	private BooleanBuilder booleanBuilder;

	public PredicateBuilder() {
		this.booleanBuilder = new BooleanBuilder();
	}

	/**
	 * Přidání porovnání je rovno.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder eq(StringPath path, String value) {
		if (isNotBlank(value))
			booleanBuilder.and(path.eq(value));
		return this;
	}

	/**
	 * Přidání porovnání je rovno.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder eq(BooleanPath path, boolean value) {
		booleanBuilder.and(path.eq(value));
		return this;
	}

	/**
	 * Přidání porovnání je rovno.
	 * 
	 * @param <T>
	 *            {@link Enum} typ
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Enum<T>> PredicateBuilder eq(EnumPath<T> path, T value) {
		if (value != null)
			booleanBuilder.and(path.eq(value));
		return this;
	}

	/**
	 * Přidání porovnání je null, empty nebo mezera.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @return this pro řetězení
	 */
	public PredicateBuilder isBlank(StringPath path) {
		booleanBuilder.and(path.isNull().or(path.isEmpty()).or(path.eq(" ")));
		return this;
	}

	/**
	 * Přidání porovnání je null nebo 0
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @return this pro řetězení
	 */
	public PredicateBuilder eqNullOrZero(NumberExpression<Long> path) {
		booleanBuilder.and(path.isNull().or(path.eq(0L)));
		return this;
	}

	/**
	 * Přidání porovnání je rovno.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */

	/**
	 * Přidání porovnání je rovno.
	 * 
	 * @param <T>
	 *            {@link Number} a {@link Comparable} typ
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder eq(NumberExpression<T> path, T value) {
		if (value != null)
			booleanBuilder.and(path.eq(value));
		return this;
	}

	/**
	 * Přidání porovnání je není rovno.
	 * 
	 * @param <T>
	 *            {@link Number} a {@link Comparable} typ
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder ne(NumberExpression<T> path, T value) {
		if (value != null)
			booleanBuilder.and(path.ne(value));
		return this;
	}

	/**
	 * Přidání porovnání není rovno.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder ne(StringPath path, String value) {
		if (value != null)
			booleanBuilder.and(ExpressionUtils.or(path.isNull(), path.ne(value)));
		return this;
	}

	/**
	 * Přidání porovnání není rovno.
	 * 
	 * @param expression
	 *            výraz pro porovnání
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder ne(StringExpression expression, String value) {
		if (value != null)
			booleanBuilder.and(ExpressionUtils.or(expression.isNull(), expression.ne(value)));
		return this;
	}

	/**
	 * Přidání porovnání není null, není empty a není mezera.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @return this pro řetězení
	 */
	public PredicateBuilder neEmpty(StringPath path) {
		booleanBuilder.and(path.isNotNull());
		booleanBuilder.and(path.isNotEmpty());
		booleanBuilder.and(path.ne(" "));
		return this;
	}

	/**
	 * Přidání porovnání je větší než.
	 * 
	 * @param <T>
	 *            {@link Number} a {@link Comparable} typ
	 * @param expression1
	 *            výraz 1 k porovnání
	 * @param expression2
	 *            výraz 2 k porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder gtOrValueNull(NumberExpression<T> expression1,
			NumberExpression<T> expression2) {
		// je potřeba pro případy porovnání x > null, pak je výsledek totiž
		// false, s porovnáním na "nebo null" pak takové případy vychází
		if (expression2 != null)
			booleanBuilder.and(ExpressionUtils.or(expression1.gt(expression2), expression2.isNull()));
		return this;
	}

	/**
	 * Přidání porovnání je větší než.
	 * 
	 * @param <T>
	 *            {@link Number} a {@link Comparable} typ
	 * @param expression
	 *            výraz k porovnání
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder gt(NumberExpression<T> expression, T value) {
		if (value != null)
			booleanBuilder.and(expression.gt(value));
		return this;
	}

	/**
	 * Přidání porovnání je větší než nebo rovno.
	 * 
	 * @param <T>
	 *            {@link Number} a {@link Comparable} typ
	 * @param expression
	 *            výraz k porovnání
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder ge(NumberExpression<T> expression, T value) {
		if (value != null)
			booleanBuilder.and(expression.goe(value));
		return this;
	}

	/**
	 * Přidání porovnání je menší než.
	 * 
	 * @param <T>
	 *            {@link Number} a {@link Comparable} typ
	 * @param expression
	 *            výraz k porovnání
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder lt(NumberExpression<T> expression, T value) {
		if (value != null)
			booleanBuilder.and(expression.lt(value));
		return this;
	}

	/**
	 * Přidání porovnání je menší než nebo rovno.
	 * 
	 * @param <T>
	 *            {@link Number} a {@link Comparable} typ
	 * @param expression
	 *            výraz k porovnání
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder le(NumberExpression<T> expression, T value) {
		if (value != null)
			booleanBuilder.and(expression.loe(value));
		return this;
	}

	/**
	 * Přidání porovnání NOT EXISTS.
	 * 
	 * @param query
	 *            sub query
	 * @return this pro řetězení
	 */
	public PredicateBuilder notExists(JPAQuery<?> query) {
		if (query != null)
			booleanBuilder.and(query.notExists());
		return this;
	}

	/**
	 * Přidání porvnání EXISTS.
	 * 
	 * @param query
	 *            sub query
	 * @return this pro řetězení
	 */
	public PredicateBuilder exists(JPAQuery<?> query) {
		if (query != null)
			booleanBuilder.and(query.exists());
		return this;
	}

	/**
	 * Přidání porvnání NOT IN.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param values
	 *            hodnoty pro porovnání
	 * 
	 * @return this pro řetězení
	 */
	public PredicateBuilder notIn(StringPath path, String[] values) {
		if (values != null)
			booleanBuilder.and(path.notIn(values));
		return this;
	}

	/**
	 * Přidání porvnání IN.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param values
	 *            hodnoty pro porovnání
	 * 
	 * @return this pro řetězení
	 */
	public PredicateBuilder in(StringPath path, String[] values) {
		if (values != null)
			booleanBuilder.and(path.in(values));
		return this;
	}

	/**
	 * Přidání porvnání IS NULL.
	 * 
	 * @param <T>
	 *            {@link Number} a {@link Comparable} typ
	 * @param expression
	 *            výraz k porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder isNull(NumberExpression<T> expression) {
		if (expression != null)
			booleanBuilder.and(expression.isNull());
		return this;
	}

	/**
	 * Přidání porvnání IS NULL.
	 * 
	 * @param expression
	 *            výraz k porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder isNull(BeanPath<?> expression) {
		if (expression != null)
			booleanBuilder.and(expression.isNull());
		return this;
	}

	/**
	 * Přidání porvnání IS NULL nebo je ' '.
	 * 
	 * @param expression
	 *            výraz k porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder isEmpty(StringExpression expression) {
		if (expression != null)
			booleanBuilder.and(
					ExpressionUtils.or(expression.eq(""), ExpressionUtils.or(expression.isNull(), expression.eq(" "))));
		return this;
	}

	/**
	 * Přidání porvnání IS NOT NULL a zároveň není ' '.
	 * 
	 * @param expression
	 *            výraz k porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder isNotEmpty(StringExpression expression) {
		if (expression != null) {
			booleanBuilder.and(expression.isNotNull());
			booleanBuilder.and(expression.ne(" "));
			booleanBuilder.and(expression.ne(""));
		}
		return this;
	}

	/**
	 * Přidání porvnání IS NOT NULL.
	 * 
	 * @param expression
	 *            výraz k porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder isNotNull(NumberExpression<?> expression) {
		if (expression != null)
			booleanBuilder.and(expression.isNotNull());
		return this;
	}

	/**
	 * Přidání porvnání IS NOT NULL.
	 * 
	 * @param expression
	 *            výraz k porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder isNotNull(BeanPath<?> expression) {
		if (expression != null)
			booleanBuilder.and(expression.isNotNull());
		return this;
	}

	public static String prepareForLike(String value) {
		// nahradí znaky * znakem % pro SQL a JPQL vyhledávání v LIKE a navíc
		// přidá ještě jednou % aby se smazal rozdíl mezi údaji v DB, které mají
		// za sebou mezery a údaji v aplikaci, které se zadávají bez mezer
		return value.replace('*', '%') + '%';
	}

	/**
	 * Přidá porovnání SQL IGNORE CASE LIKE.
	 * 
	 * @param expression
	 *            výraz k porovnání
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder iLike(StringExpression expression, String value) {
		if (isNotBlank(value) && !"*".equals(value))
			booleanBuilder.and(expression.likeIgnoreCase(prepareForLike(value)));
		return this;
	}

	/**
	 * Přidá porovnání SQL IGNORE CASE LIKE s tím, že se text může nacházet
	 * uprostřed
	 * 
	 * @param expression
	 *            výraz k porovnání
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder anyILike(StringExpression expression, String value) {
		if (isNotBlank(value) && !"*".equals(value))
			booleanBuilder.and(expression.likeIgnoreCase(prepareForLike("*" + value)));
		return this;
	}

	/**
	 * Přidá porovnání SQL NOT IGNORE CASE LIKE.
	 * 
	 * @param expression
	 *            výraz k porovnání
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder notILike(StringExpression expression, String value) {
		if (isNotBlank(value))
			booleanBuilder.andNot(expression.likeIgnoreCase(prepareForLike(value)));
		return this;
	}

	/**
	 * Přidá porovnání SQL LIKE.
	 * 
	 * @param expression
	 *            výraz k porovnání
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder like(NumberExpression<?> expression, String value) {
		if (isNotBlank(value))
			booleanBuilder.and(expression.like(prepareForLike(value)));
		return this;
	}

	/**
	 * Přidá porovnání SQL LIKE.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param value
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder like(NumberPath<Long> path, Long value) {
		if (value != null)
			like(path, value.toString());
		return this;
	}

	/**
	 * Přidá porovnání SQL between.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param from
	 *            hodnota pro porovnání
	 * @param to
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder between(StringPath path, String from, String to) {
		if (isNotBlank(from) && isNotBlank(to)) {
			booleanBuilder.and(path.between(from, to));
		} else if (isNotBlank(from)) {
			iLike(path, prepareForLike(from));
		} else if (isNotBlank(to)) {
			iLike(path, prepareForLike(to));
		}
		return this;
	}

	/**
	 * Přidá porovnání SQL between.
	 * 
	 * @param <T>
	 *            {@link Number} a {@link Comparable} typ
	 * @param expression
	 *            výraz k porovnání
	 * @param from
	 *            hodnota pro porovnání
	 * @param to
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public <T extends Number & Comparable<?>> PredicateBuilder between(NumberExpression<T> expression, T from, T to) {
		if (from != null && to != null) {
			booleanBuilder.and(expression.between(from, to));
		} else if (from != null) {
			booleanBuilder.and(expression.eq(from));
		} else if (to != null) {
			booleanBuilder.and(expression.eq(to));
		}
		return this;
	}

	/**
	 * Přidá porovnání SQL between.
	 * 
	 * @param path
	 *            cesta k atributu entity
	 * @param from
	 *            hodnota pro porovnání
	 * @param to
	 *            hodnota pro porovnání
	 * @return this pro řetězení
	 */
	public PredicateBuilder between(DateTimePath<Date> path, Date from, Date to) {
		if ((from != null) && to != null) {
			booleanBuilder.and(path.between(DateUtils.resetTime(from), DateUtils.resetTimeToMidnight(to)));
		} else if (from != null) {
			booleanBuilder.and(path.gt(DateUtils.resetTime(from)));
		} else if (to != null) {
			booleanBuilder.and(path.lt(DateUtils.resetTime(to)));
		}
		return this;
	}

	/**
	 * Vrací celkový objekt predicate pro použítí v dotazu.
	 * 
	 * @return builder
	 */
	public BooleanBuilder getBuilder() {
		return booleanBuilder;
	}

}
