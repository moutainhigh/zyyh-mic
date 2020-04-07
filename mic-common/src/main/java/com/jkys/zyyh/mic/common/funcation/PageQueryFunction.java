package com.jkys.zyyh.mic.common.funcation;

import java.util.List;

@FunctionalInterface
public interface PageQueryFunction<T> {
    List<T> queryList(int pageIndex, int pageSize);
}
