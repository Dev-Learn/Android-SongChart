package nam.tran.data.model.core

interface IHeader<T>{
    val isHeader: Boolean
    val headerValue : T?
}