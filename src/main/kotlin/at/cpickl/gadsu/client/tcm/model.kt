package at.cpickl.gadsu.client.tcm



data class TcmData(val clientId: String, val entries: TcmDataEntries)

data class TcmDataEntries(val entries: List<TcmDataEntry<Any>>)


abstract class TcmDataEntry<T>(val id: String?, val key: TcmDataKey, val genericValue: T) {
    abstract fun toSqlValue(): String
}


class TcmDataStringEntry(_id: String?, _key: TcmDataKey, val realValue: String): TcmDataEntry<String>(_id, _key, realValue) {
    override fun toSqlValue() = realValue
}


//interface TcmDataEnum {}
//class TcmDataEnumEntry(_key: TcmDataKey, val realValue: TcmDataEnum): TcmDataEntry<TcmDataEnum>(_key, realValue) {
//    override fun toSqlValue() = "enumValue"
//}

enum class TcmDataKey(val sqlValue: String, val type: TcmDataType) {
    Zufriedenheit_Leben("zufriedenheit_leben", TcmDataType.STRING),
    Zufriedenheit_Sex("zufriedenheit_sex", TcmDataType.STRING);

    companion object {
        fun bySqlValue(search: String) = values().firstOrNull { it.sqlValue.equals(search) }
    }
}

enum class TcmDataType() {
//    ENUM,//("ENUM"),
    STRING {
    override fun <T> onCallback(callback: Callback<T>): T {
        return callback.onString(this)
    }
};//("STRING"),
//    BOOLEAN,//("BOOLEAN"),
//    TRILEAN;//("TRILEAN"); // yes-no-maybe

    abstract fun <T> onCallback(callback: Callback<T>): T

    interface Callback<T> {
        fun onString(type: TcmDataType): T
    }

    //    companion object {
    //        fun bySqlValue(search: String) = values().firstOrNull { it.sqlValue.equals(search) }
    //    }
}
