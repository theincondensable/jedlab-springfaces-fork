package com.jedlab.framework.spring.service;

import org.hibernate.Criteria;

public interface Restriction {
    void applyFilter(Criteria var1);
}