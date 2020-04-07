package com.jkys.zyyh.mic.common.funcation;

import java.util.List;

@FunctionalInterface
public interface PageIdQueryFunction<T, R> {

    /**
     *  遍历接口
     *
     * @param id 其实主键id，或者需要遍历的字段
     * @param limit 分页条数
     * @return
     */
    List<T> queryList(R id, int limit);

}
