package nam.tran.data.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static nam.tran.data.model.SongStatus.*;

@IntDef({NONE_STATUS, DOWNLOADING, CANCEL_DOWNLOAD, CANCELING_DOWNLOAD, ERROR, PLAY, PLAYING, PAUSE_SONG, CLOSE})
@Retention(RetentionPolicy.SOURCE)
public @interface SongStatus {
    int NONE_STATUS = 0;
    int DOWNLOADING = 111;
    int CANCEL_DOWNLOAD = 222;
    int CANCELING_DOWNLOAD = 333;
    int ERROR = 444;
    int PLAY = 555;
    int PLAYING = 666;
    int PAUSE_SONG = 777;
    int CLOSE = 888;
}
