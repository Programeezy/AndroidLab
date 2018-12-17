package tim.android.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import tim.android.BuildConfig;
import tim.android.R;

public class ImageUtil {

    public static final String DEFAULT_IMAGE_PATH = Uri.parse("android.resource://"
            + BuildConfig.APPLICATION_ID +
            "/"
            + R.drawable.zmagar).toString();

    public static void loadImage(Context context, String path, ImageView view) {
        Glide.with(context)
                .load(path)
                .into(view);
    }
}
