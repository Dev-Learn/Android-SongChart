package nam.tran.data.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static nam.tran.data.model.SongStatus.*;

@IntDef({NONE_STATUS, DOWNLOADING, CANCEL_DOWNLOAD, ERROR, PLAY, PLAYING, PAUSE_SONG, CLOSE})
@Retention(RetentionPolicy.SOURCE)
public @interface SongStatus {
    int NONE_STATUS = 0;
    int DOWNLOADING = 111;
    int CANCEL_DOWNLOAD = 222;
    int ERROR = 333;
    int PLAY = 444;
    int PLAYING = 555;
    int PAUSE_SONG = 666;
    int CLOSE = 777;
}
