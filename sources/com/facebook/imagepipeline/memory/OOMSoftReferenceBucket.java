package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.OOMSoftReference;
import java.util.LinkedList;
import javax.annotation.Nullable;

class OOMSoftReferenceBucket<V> extends Bucket<V> {
    private LinkedList<OOMSoftReference<V>> mSpareReferences = new LinkedList<>();

    public OOMSoftReferenceBucket(int i, int i2, int i3) {
        super(i, i2, i3, false);
    }

    @Nullable
    public V pop() {
        OOMSoftReference oOMSoftReference = (OOMSoftReference) this.mFreeList.poll();
        Preconditions.checkNotNull(oOMSoftReference);
        V v = oOMSoftReference.get();
        oOMSoftReference.clear();
        this.mSpareReferences.add(oOMSoftReference);
        return v;
    }

    /* access modifiers changed from: package-private */
    public void addToFreeList(V v) {
        OOMSoftReference poll = this.mSpareReferences.poll();
        if (poll == null) {
            poll = new OOMSoftReference();
        }
        poll.set(v);
        this.mFreeList.add(poll);
    }
}
