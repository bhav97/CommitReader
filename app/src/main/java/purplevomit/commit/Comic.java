package purplevomit.commit;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bhav on 9/23/16 for the CommitReader Project.
 */
public class Comic implements Parcelable {
    public final static String INTENT_EXTRA = Comic.class.getSimpleName() + ".intent";
    public final static String BUNDLE_EXTRA = Comic.class.getSimpleName() + ".bundle";
    public long id;
    public long height;
    public long width;
    public final String thumbnail;
    public final String logo;
    public String image;
    public final String url;
    public final String title;
    public final int page;
    public boolean exposed = false;

    public Comic(long id,
                 long height,
                 long width,
                 String thumbnail,
                 String logo,
                 String image,
                 String url,
                 String title,
                 int page) {

        this.id = id;
        this.height = height;
        this.width = width;
        this.thumbnail = thumbnail;
        this.logo = logo;
        this.image = image;
        this.url = url;
        this.title = title;
        this.page = page;
    }

    protected Comic(Parcel in) {
        id = in.readLong();
        height = in.readLong();
        width = in.readLong();
        thumbnail = in.readString();
        logo = in.readString();
        image = in.readString();
        url = in.readString();
        title = in.readString();
        page = in.readInt();
        exposed = in.readByte() != 0x00;
    }

    public static final Creator<Comic> CREATOR = new Creator<Comic>() {
        @Override
        public Comic createFromParcel(Parcel in) {
            return new Comic(in);
        }

        @Override
        public Comic[] newArray(int size) {
            return new Comic[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(height);
        parcel.writeLong(width);
        parcel.writeString(thumbnail);
        parcel.writeString(logo);
        parcel.writeString(image);
        parcel.writeString(url);
        parcel.writeString(title);
        parcel.writeInt(page);
    }

    public static class ComicRequest {

        public final String url;

        public ComicRequest(String url) {
            this.url = url;
        }
    }
}