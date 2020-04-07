package com.jkys.zyyh.mic.common.util;

import com.google.common.base.Preconditions;
import com.jkys.zyyh.mic.common.funcation.PageIdQueryFunction;
import com.jkys.zyyh.mic.common.funcation.PageQueryFunction;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 分页遍历迭代器
 */
public abstract class IteratorPageUtils {


    /**
     * 分页遍历所有数据
     *
     * @param pageQueryFunction 分页请求
     * @param doAction          要做的事情
     * @param <T>
     */
    public static <T> void iterateElements(PageQueryFunction<T> pageQueryFunction, Consumer<List<T>> doAction, int pageSize) {
        int pageIndex = 1;
        List<T> list;
        do {
            list = pageQueryFunction.queryList(pageIndex, pageSize);
            if (CollectionUtils.isNotEmpty(list)) {
                //do something
                doAction.accept(list);
                pageIndex++;
            }
        } while (CollectionUtils.isNotEmpty(list) && list.size() >= pageSize);
    }

    /**
     * 分页遍历list所有数据
     *
     * @param list     要分页的lsit
     * @param doAction 要做的事情
     * @param <T>
     * @throws IllegalArgumentException pageSize <= 0
     * @throws NullPointerException list,doAction null
     */
    public static <T> void iterateElements(List<T> list, Consumer<List<T>> doAction, int pageSize) {
        Preconditions.checkNotNull(doAction);
        Preconditions.checkArgument(Objects.nonNull(list));
        Preconditions.checkArgument(pageSize > 0);

        int pageIndex = 0;
        int pageStart;
        int pageEnd;
        int total = list.size();

        //肯定要遍历第一边,用do while
        do {
            pageStart = pageIndex * pageSize;
            pageEnd = pageSize * (pageIndex + 1);

            if (pageEnd > total) {
                pageEnd = total;
            }

            List<T> sublist = list.subList(pageStart, pageEnd);

            if (CollectionUtils.isNotEmpty(list)){
                doAction.accept(sublist);
            }

            pageIndex++;
        } while (total > pageEnd);

    }

    /**
     * 主键[或其他字段]遍历所有数据 根据id遍历
     *
     * @param pageIdQueryFunction 根据id查询 需要查到的内容包含id
     * @param getIdFunc           实体获取主键id
     * @param doAction            要做的事情
     * @param <T>
     */
    public static <T, R> void iterateElements(PageIdQueryFunction<T, R> pageIdQueryFunction, Function<T, R> getIdFunc, Consumer<List<T>> doAction, int limit) {
        R id = null;
        List<T> list;
        do {
            list = pageIdQueryFunction.queryList(id, limit);
            if (CollectionUtils.isNotEmpty(list)) {
                doAction.accept(list);
                id = getIdFunc.apply(list.get(list.size() - 1));
            }
        } while (CollectionUtils.isNotEmpty(list) && list.size() >= limit);
    }

}
