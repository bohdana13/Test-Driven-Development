package com.redko.lab2.response;

/*
@author   User
@project   lab2
@class  PaginationMetaData
@version  1.0.0
@since 26.04.2026 - 21.48
*/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PaginationMetaData extends BaseMetaData {
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;

}