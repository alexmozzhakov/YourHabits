package com.doapps.habits.helper;

import com.bumptech.glide.load.Key;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class IntegerVersionSignature implements Key {
    private final int currentVersion;

    public IntegerVersionSignature(int currentVersion) {
        this.currentVersion = currentVersion;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntegerVersionSignature) {
            IntegerVersionSignature other = (IntegerVersionSignature) obj;
            return currentVersion == other.currentVersion;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return currentVersion;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ByteBuffer.allocate(Integer.SIZE).putInt(currentVersion).array());
    }
}