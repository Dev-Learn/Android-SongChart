package nam.tran.data.model

data class Song(val id : Int,val name : String,val image : String
                ,val link128 : String,val link320 : String,val lossless : String,val link_local : String
                ,val singer : Singer?)