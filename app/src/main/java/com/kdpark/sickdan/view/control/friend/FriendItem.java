package com.kdpark.sickdan.view.control.friend;

import android.os.Parcel;
import android.os.Parcelable;

import com.kdpark.sickdan.model.dto.RelationshipStatus;

import lombok.Builder;
import lombok.Data;

@Data
public class FriendItem implements Parcelable {

    private Long id;
    private String displayName;
    private String email;
    private RelationshipStatus status;

    @Builder
    public FriendItem(Long id, String displayName, String email, RelationshipStatus status) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.status = status;
    }

    protected FriendItem(Parcel in) {
        this.id = in.readLong();
        this.displayName = in.readString();
        this.email = in.readString();
        this.status = (RelationshipStatus) in.readSerializable();
    }

    public static final Creator<FriendItem> CREATOR = new Creator<FriendItem>() {
        @Override
        public FriendItem createFromParcel(Parcel in) {
            return new FriendItem(in);
        }

        @Override
        public FriendItem[] newArray(int size) {
            return new FriendItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.displayName);
        dest.writeString(this.email);
        dest.writeSerializable(status);
    }
}
