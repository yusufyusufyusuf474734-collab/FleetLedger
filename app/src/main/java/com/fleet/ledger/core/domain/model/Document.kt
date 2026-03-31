package com.fleet.ledger.core.domain.model

enum class DocumentType(val label: String) {
    KASKO("Kasko"),
    TRAFIK_SIGORTASI("Trafik Sigortası"),
    KOLTUK_SIGORTASI("Koltuk Sigortası"),
    RUHSAT("Araç Ruhsatı"),
    MUAYENE("Araç Muayenesi"),
    DIGER("Diğer")
}

data class Document(
    val id: Long = 0,
    val vehicleId: Long,
    val type: DocumentType,
    val title: String,
    val company: String = "",
    val policyNo: String = "",
    val startDate: Long? = null,
    val expiryDate: Long? = null,
    val amount: Double = 0.0,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
