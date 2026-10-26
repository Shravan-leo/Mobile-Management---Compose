package com.example.mobilemodels.domain.model

data class MobileModel(
    val id: Int,
    val name: String,
    val price: Int,
    val deviceNames: List<String> = emptyList()
)
