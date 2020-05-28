package com.example.contact.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Contact implements Parcelable {
    private String id;
    private String name;
    private int type;
    private boolean emoji;

    protected Contact(Parcel in) {
        id = in.readString();
        name = in.readString();
        type = in.readInt();
        emoji = in.readByte() != 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(type);
        dest.writeByte((byte) (emoji ? 1 : 0));
    }

    // contact == 0
    // title == 1

    public Contact(String id, String name, int type, boolean emoji) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.emoji = emoji;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isEmoji() {
        return emoji;
    }

    public void setEmoji(boolean emoji) {
        this.emoji = emoji;
    }

    public static Creator<Contact> getCREATOR() {
        return CREATOR;
    }
}
