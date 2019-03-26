package nam.tran.data.model

data class Song(val id : Int,val name : String,val image : String
                ,val link128 : String,val link320 : String,val lossless : String,val link_local : String
                ,val singer : Singer?){

    override fun equals(other: Any?): Boolean {
        return other is Song && other.id == id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + image.hashCode()
        result = 31 * result + link128.hashCode()
        result = 31 * result + link320.hashCode()
        result = 31 * result + lossless.hashCode()
        result = 31 * result + link_local.hashCode()
        result = 31 * result + (singer?.hashCode() ?: 0)
        return result
    }
}