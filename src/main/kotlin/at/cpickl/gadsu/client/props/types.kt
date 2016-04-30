package at.cpickl.gadsu.client.props

import at.cpickl.gadsu.GadsuException


object SqlPropTypeCallback : PropTypeCallback<SqlPropType> {
    override fun onString(prop: StringProp) = SqlPropStringType(prop.value)
    override fun onMultiEnum(prop: MultiEnumProp) = SqlPropMultiEnumType(prop.entries)
}
// TODO refactor to extension methods
fun transformSqlPropToProp(key: String, sqlProp: SqlPropType) =
        onPropType(key, sqlProp, object: PropTypeCallback<Prop>{
            override fun onString(prop: StringProp) = prop
            override fun onMultiEnum(prop: MultiEnumProp) = prop
        })
fun transformPropToSqlProp(key: String, prop: Prop) = onPropType(key, prop, SqlPropTypeCallback)

interface SimplePropTypeCallback<R> {
    fun onString(): R
    fun onMultiEnum(): R
}
fun <R> onSimplePropType(key: String, callback: SimplePropTypeCallback<R>): R {
    if (allStrings.contains(key)) {
        return callback.onString()
    }
    if (allMultiEnums.containsKey(key)) {
        return callback.onMultiEnum()
    }
    throw GadsuException("Unhandled prop key '$key' type!")
}


interface PropTypeCallback<R> {
    fun onString(prop: StringProp): R
    fun onMultiEnum(prop: MultiEnumProp): R
}

fun <R> onPropType(key: String, sqlProp: SqlPropType, callback: PropTypeCallback<R>): R {
    return onSimplePropType(key, object: SimplePropTypeCallback<R> {
        override fun onString(): R {
            return callback.onString(StringProp((sqlProp as SqlPropStringType).value))
        }
        override fun onMultiEnum(): R {
            return callback.onMultiEnum(MultiEnumProp((sqlProp as SqlPropMultiEnumType).values))
        }
    })
}
fun <R> onPropType(key: String, sqlRow: PropSqlRow, callback: PropTypeCallback<R>): R {
    return onSimplePropType(key, object: SimplePropTypeCallback<R> {
        override fun onString(): R {
            return callback.onString(StringProp(sqlRow.sqlValue))
        }
        override fun onMultiEnum(): R {
            val entries = sqlRow.sqlValue.split(",").toList()
            return callback.onMultiEnum(MultiEnumProp(entries))
        }
    })
}
fun <R> onPropType(key: String, prop: Prop, callback: PropTypeCallback<R>): R {
    return onSimplePropType(key, object: SimplePropTypeCallback<R> {
        override fun onString(): R {
            val typedProp = _smartCast(key, prop, StringProp::class.java)
            return callback.onString(typedProp)
        }
        override fun onMultiEnum(): R {
            val typedProp = _smartCast(key, prop, MultiEnumProp::class.java)
            return callback.onMultiEnum(typedProp)
        }
    })
}

private fun <P : Prop> _smartCast(key: String, genericProp: Prop?, targetType: Class<P>): P {
    if (genericProp == null) {
        throw GadsuException("internal state error! genericProp == null")
    }
    if (genericProp.javaClass != targetType) {
        throw GadsuException("Data constraint exception: key '$key.' should be of type '${targetType.simpleName}', " +
                "but was: ${genericProp.javaClass.simpleName} (prop='$genericProp')")
    }
    return genericProp as P
}
