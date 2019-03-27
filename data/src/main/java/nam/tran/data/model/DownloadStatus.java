package nam.tran.data.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static nam.tran.data.model.DownloadStatus.*;

@IntDef({NONE, PAUSE, RESUME})
@Retention(RetentionPolicy.SOURCE)
public @interface DownloadStatus {
    int NONE = 0;
    int PAUSE = 1111;
    int RESUME = 2222;
}
