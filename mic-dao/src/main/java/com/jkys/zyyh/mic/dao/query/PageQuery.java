package com.jkys.zyyh.mic.dao.query;

import java.io.Serializable;

/**
 * <Description> <br>
 *
 * @author Rocky<br>
 * @version 1.0<br>
 * @createDate 2019/11/01 10:48 上午 <br>
 */
public class PageQuery implements Serializable {

    /**
     * 当前页数索引
     */
    private int pageIndex;

    /**
     * 每页记录数
     */
    private int pageSize;

    /**
     * 行的偏移量
     */
    private int offset;

    /**
     * 最大数目
     */
    private int rows;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageParam(int pageIndex, int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        } else {
            this.pageSize = 10;
        }
        if (pageIndex > 0) {
            this.pageIndex = pageIndex - 1;
        }
        this.offset = this.pageIndex * this.pageSize;
        this.rows = this.pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}