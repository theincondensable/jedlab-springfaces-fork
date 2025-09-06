package com.jedlab.framework.web;

import java.util.List;

public abstract class PaginationHelper<E>
{

    private int pageSize;
    private int page;

    public PaginationHelper(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public abstract int getItemsCount();

    public abstract List<E> createPageDataModel();

    public int getPageFirstItem()
    {
        return page * pageSize;
    }

    public int getPageLastItem()
    {
        int i = getPageFirstItem() + pageSize - 1;
        int count = getItemsCount() - 1;
        if (i > count)
        {
            i = count;
        }
        if (i < 0)
        {
            i = 0;
        }
        return i;
    }

    public boolean isHasNextPage()
    {
        return (page + 1) * pageSize + 1 <= getItemsCount();
    }

    public void nextPage()
    {
        if (isHasNextPage())
        {
            page++;
        }
    }

    public void lastPage()
    {
        if (isHasNextPage())
        {
            if(pageSize > 0)
                page = getItemsCount() / pageSize;
            else
                page = 0;
        }
    }

    public void firstPage()
    {
        page = 0;
    }

    public boolean isHasPreviousPage()
    {
        return page > 0;
    }

    public void previousPage()
    {
        if (isHasPreviousPage())
        {
            page--;
        }
    }

    public int getPageSize()
    {
        return pageSize;
    }
}