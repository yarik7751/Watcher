package com.yarik.watcher.core.database.entity

/** Статус заявки. Порядок объявления = порядок вкладок на экране заявок. */
enum class JobStatus {
    NEW,
    IN_PROGRESS,
    AWAIT_PAYMENT,
    DONE,
}

enum class PaymentMethod {
    CASH,
    CARD,
    TRANSFER,
}

enum class PhotoKind {
    BEFORE,
    AFTER,
}
