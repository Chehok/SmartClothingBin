package com.example.clothingBin.config;


import com.example.clothingBin.config.ApiException;
import com.example.clothingBin.config.ErrorCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.ToString;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

@Log
@ToString
public class RequestParams<T> {

    private Map<String, Object> map;

    private List<Sort.Order> sortOrders = new ArrayList<>();

    public Class<T> clazz;

    public RequestParams(Class<T> clazz) {
        this.clazz = clazz;
        this.map = new HashMap<>();
    }

    public RequestParams() {
        this.map = new HashMap<>();
    }

    public void put(String key, Object value) {
        if (value!=null) {
            this.map.put(key, value);
        }
    }

    public void setParameterMap(Map<String, String[]> map) {
        for (String key : map.keySet()) {

            String[] values = map.get(key);

            String value = Arrays.stream(values).collect(Collectors.joining(","));
            put(key, value);
        }
    }

    public String getString(String key, String defaultValue) {
        if (map.containsKey(key)) {
            String value = String.valueOf(map.get(key));

            if (StringUtils.isEmpty(value)) {
                return defaultValue;
            }

            return value;
        }

        return defaultValue;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public int getInt(String key, int defaultValue) {
        String value = getString(key);

        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }

        return Integer.parseInt(value);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public long getLong(String key, long defaultValue) {
        String value = getString(key);

        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }

        return Long.parseLong(value);
    }

    public long getLong(String key) {
        return getLong(key, 0);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key);

        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }

        return Boolean.parseBoolean(value);
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public Pageable getPageable() {
        int page = getInt("pageNumber", 0);
        int size = getInt("pageSize", Integer.MAX_VALUE);

        return PageRequest.of(page, size);
    }

    public void addSort(String value, Sort.Direction direction) {
        if (!hasSortParameter()) {
            sortOrders.add(new Sort.Order(direction, value));
        }
    }

    public boolean hasSortParameter() {
        return StringUtils.isNotEmpty(getString("sort"));
    }

/*    public Sort getSort() {
        if (hasSortParameter()) {
            List<Sort.Order> orders = new ArrayList<>();

            String sortParameter = getString("sort");

            String[] sortValues = sortParameter.split(",");

            for (int i = 0; i < sortValues.length; i += 2) {
                orders.add(new Sort.Order(Sort.Direction.fromString(sortValues[i + 1]), sortValues[i]));
            }

            return new Sort(orders);
        }

        if (ArrayUtils.isNotEmpty(sortOrders)) {
            return new Sort(sortOrders);
        }

        return null;
    }*/

    public Predicate getPredicate() {
        try {
            BooleanBuilder builder = new BooleanBuilder();

            String className = clazz.getSimpleName();

            Class<EntityPathBase> queryDslClass = (Class<EntityPathBase>) ClassUtils.getClass(String.format("me.imtsoft.core.domain.%s" + ".Q%s", className.toLowerCase(), className));

            PathBuilder entityPath = new PathBuilder(queryDslClass, "entity");

            if (map.containsKey("where")) {
                String where = (String) map.get("where");

                String[] whereValues = where.split(",");

                if (whereValues.length % 3 != 0) {
                    throw new ApiException(ErrorCode.INVALID_PATTERN);
                }

                for (int i = 0; i < whereValues.length; i += 3) {
                    String filedName = whereValues[i];
                    String fieldValue = whereValues[i + 1];
                    String operator = whereValues[i + 2];
                    PathBuilder relation = entityPath.get(filedName);
                    System.out.print(relation);
                }
            } else {
                return null;
            }

            for (String key : map.keySet()) {
                String value = (String) map.get(key);
            }

            return builder;
        } catch (Exception e) {
            log.warning(e.getMessage());
        }
        return null;
    }

    public String getFilter() {
        return getString("filter", "");
    }

    public String getUseYn() {
        return getString("useYn", "");
    }

    public Object getObject(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    public Object getNotEmptyObject(String key) {
        if (!map.containsKey(key)) {
            throw new ApiException(ErrorCode.EMPTY_VALUE);
        }

        Object object = map.get(key);

        if (object == null || StringUtils.isEmpty(object.toString())) {
            throw new ApiException(ErrorCode.EMPTY_VALUE);
        }

        return object;
    }

    public Integer getNotEmptyInt(String key) {
        return Integer.parseInt(getNotEmptyString(key));
    }

    public String getNotEmptyString(String key) {
        return (String) getNotEmptyObject(key);
    }

    public Long getNotEmptyLong(String key) {
        return Long.parseLong(getNotEmptyString(key));
    }
}
