package com.stasbar.concurrency.data

import java.math.BigDecimal


/**
 * Created by stasbar on 23.10.2017
 */
data class Transaction(val from: String, val to: String, val amount: BigDecimal)