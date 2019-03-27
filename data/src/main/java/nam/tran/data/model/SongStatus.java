package nam.tran.data.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static nam.tran.data.model.SongStatus.*;

@IntDef({DOWNLOAD, CANCEL_DOWNLOAD, PLAY, STOP})
@Retention(RetentionPolicy.SOURCE)
public @interface SongStatus {
    int DOWNLOAD = 0;
    int CANCEL_DOWNLOAD = 111;
    int PLAY = 222;
    int STOP = 333;
}
