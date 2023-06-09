package io.dcloud.nineoldandroids.animation;

import android.view.animation.Interpolator;
import io.dcloud.nineoldandroids.animation.Animator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class AnimatorSet extends Animator {
    private ValueAnimator mDelayAnim = null;
    private long mDuration = -1;
    private boolean mNeedsSort = true;
    /* access modifiers changed from: private */
    public HashMap<Animator, Node> mNodeMap = new HashMap<>();
    /* access modifiers changed from: private */
    public ArrayList<Node> mNodes = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Animator> mPlayingSet = new ArrayList<>();
    private AnimatorSetListener mSetListener = null;
    /* access modifiers changed from: private */
    public ArrayList<Node> mSortedNodes = new ArrayList<>();
    private long mStartDelay = 0;
    /* access modifiers changed from: private */
    public boolean mStarted = false;
    boolean mTerminated = false;

    private class AnimatorSetListener implements Animator.AnimatorListener {
        private AnimatorSet mAnimatorSet;

        AnimatorSetListener(AnimatorSet animatorSet) {
            this.mAnimatorSet = animatorSet;
        }

        public void onAnimationCancel(Animator animator) {
            ArrayList<Animator.AnimatorListener> arrayList;
            AnimatorSet animatorSet = AnimatorSet.this;
            if (!animatorSet.mTerminated && animatorSet.mPlayingSet.size() == 0 && (arrayList = AnimatorSet.this.mListeners) != null) {
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    AnimatorSet.this.mListeners.get(i).onAnimationCancel(this.mAnimatorSet);
                }
            }
        }

        public void onAnimationEnd(Animator animator) {
            animator.removeListener(this);
            AnimatorSet.this.mPlayingSet.remove(animator);
            boolean z = true;
            ((Node) this.mAnimatorSet.mNodeMap.get(animator)).done = true;
            if (!AnimatorSet.this.mTerminated) {
                ArrayList access$4 = this.mAnimatorSet.mSortedNodes;
                int size = access$4.size();
                int i = 0;
                while (true) {
                    if (i >= size) {
                        break;
                    } else if (!((Node) access$4.get(i)).done) {
                        z = false;
                        break;
                    } else {
                        i++;
                    }
                }
                if (z) {
                    ArrayList<Animator.AnimatorListener> arrayList = AnimatorSet.this.mListeners;
                    if (arrayList != null) {
                        ArrayList arrayList2 = (ArrayList) arrayList.clone();
                        int size2 = arrayList2.size();
                        for (int i2 = 0; i2 < size2; i2++) {
                            ((Animator.AnimatorListener) arrayList2.get(i2)).onAnimationEnd(this.mAnimatorSet);
                        }
                    }
                    this.mAnimatorSet.mStarted = false;
                }
            }
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }
    }

    private static class Dependency {
        static final int AFTER = 1;
        static final int WITH = 0;
        public Node node;
        public int rule;

        public Dependency(Node node2, int i) {
            this.node = node2;
            this.rule = i;
        }
    }

    private static class DependencyListener implements Animator.AnimatorListener {
        private AnimatorSet mAnimatorSet;
        private Node mNode;
        private int mRule;

        public DependencyListener(AnimatorSet animatorSet, Node node, int i) {
            this.mAnimatorSet = animatorSet;
            this.mNode = node;
            this.mRule = i;
        }

        private void startIfReady(Animator animator) {
            if (!this.mAnimatorSet.mTerminated) {
                Dependency dependency = null;
                int size = this.mNode.tmpDependencies.size();
                int i = 0;
                while (true) {
                    if (i < size) {
                        Dependency dependency2 = this.mNode.tmpDependencies.get(i);
                        if (dependency2.rule == this.mRule && dependency2.node.animation == animator) {
                            animator.removeListener(this);
                            dependency = dependency2;
                            break;
                        }
                        i++;
                    } else {
                        break;
                    }
                }
                this.mNode.tmpDependencies.remove(dependency);
                if (this.mNode.tmpDependencies.size() == 0) {
                    this.mNode.animation.start();
                    this.mAnimatorSet.mPlayingSet.add(this.mNode.animation);
                }
            }
        }

        public void onAnimationCancel(Animator animator) {
        }

        public void onAnimationEnd(Animator animator) {
            if (this.mRule == 1) {
                startIfReady(animator);
            }
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
            if (this.mRule == 0) {
                startIfReady(animator);
            }
        }
    }

    private static class Node implements Cloneable {
        public Animator animation;
        public ArrayList<Dependency> dependencies = null;
        public boolean done = false;
        public ArrayList<Node> nodeDependencies = null;
        public ArrayList<Node> nodeDependents = null;
        public ArrayList<Dependency> tmpDependencies = null;

        public Node(Animator animator) {
            this.animation = animator;
        }

        public void addDependency(Dependency dependency) {
            if (this.dependencies == null) {
                this.dependencies = new ArrayList<>();
                this.nodeDependencies = new ArrayList<>();
            }
            this.dependencies.add(dependency);
            if (!this.nodeDependencies.contains(dependency.node)) {
                this.nodeDependencies.add(dependency.node);
            }
            Node node = dependency.node;
            if (node.nodeDependents == null) {
                node.nodeDependents = new ArrayList<>();
            }
            node.nodeDependents.add(this);
        }

        public Node clone() {
            try {
                Node node = (Node) super.clone();
                node.animation = this.animation.clone();
                return node;
            } catch (CloneNotSupportedException unused) {
                throw new AssertionError();
            }
        }
    }

    private void sortNodes() {
        if (this.mNeedsSort) {
            this.mSortedNodes.clear();
            ArrayList arrayList = new ArrayList();
            int size = this.mNodes.size();
            for (int i = 0; i < size; i++) {
                Node node = this.mNodes.get(i);
                ArrayList<Dependency> arrayList2 = node.dependencies;
                if (arrayList2 == null || arrayList2.size() == 0) {
                    arrayList.add(node);
                }
            }
            ArrayList arrayList3 = new ArrayList();
            while (arrayList.size() > 0) {
                int size2 = arrayList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    Node node2 = (Node) arrayList.get(i2);
                    this.mSortedNodes.add(node2);
                    ArrayList<Node> arrayList4 = node2.nodeDependents;
                    if (arrayList4 != null) {
                        int size3 = arrayList4.size();
                        for (int i3 = 0; i3 < size3; i3++) {
                            Node node3 = node2.nodeDependents.get(i3);
                            node3.nodeDependencies.remove(node2);
                            if (node3.nodeDependencies.size() == 0) {
                                arrayList3.add(node3);
                            }
                        }
                    }
                }
                arrayList.clear();
                arrayList.addAll(arrayList3);
                arrayList3.clear();
            }
            this.mNeedsSort = false;
            if (this.mSortedNodes.size() != this.mNodes.size()) {
                throw new IllegalStateException("Circular dependencies cannot exist in AnimatorSet");
            }
            return;
        }
        int size4 = this.mNodes.size();
        for (int i4 = 0; i4 < size4; i4++) {
            Node node4 = this.mNodes.get(i4);
            ArrayList<Dependency> arrayList5 = node4.dependencies;
            if (arrayList5 != null && arrayList5.size() > 0) {
                int size5 = node4.dependencies.size();
                for (int i5 = 0; i5 < size5; i5++) {
                    Dependency dependency = node4.dependencies.get(i5);
                    if (node4.nodeDependencies == null) {
                        node4.nodeDependencies = new ArrayList<>();
                    }
                    if (!node4.nodeDependencies.contains(dependency.node)) {
                        node4.nodeDependencies.add(dependency.node);
                    }
                }
            }
            node4.done = false;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: java.util.ArrayList} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void cancel() {
        /*
            r3 = this;
            r0 = 1
            r3.mTerminated = r0
            boolean r0 = r3.isStarted()
            if (r0 == 0) goto L_0x0076
            r0 = 0
            java.util.ArrayList<io.dcloud.nineoldandroids.animation.Animator$AnimatorListener> r1 = r3.mListeners
            if (r1 == 0) goto L_0x002b
            java.lang.Object r0 = r1.clone()
            r1 = r0
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            java.util.Iterator r2 = r1.iterator()
        L_0x0019:
            boolean r0 = r2.hasNext()
            if (r0 != 0) goto L_0x0021
            r0 = r1
            goto L_0x002b
        L_0x0021:
            java.lang.Object r0 = r2.next()
            io.dcloud.nineoldandroids.animation.Animator$AnimatorListener r0 = (io.dcloud.nineoldandroids.animation.Animator.AnimatorListener) r0
            r0.onAnimationCancel(r3)
            goto L_0x0019
        L_0x002b:
            io.dcloud.nineoldandroids.animation.ValueAnimator r1 = r3.mDelayAnim
            if (r1 == 0) goto L_0x003b
            boolean r1 = r1.isRunning()
            if (r1 == 0) goto L_0x003b
            io.dcloud.nineoldandroids.animation.ValueAnimator r1 = r3.mDelayAnim
            r1.cancel()
            goto L_0x005c
        L_0x003b:
            java.util.ArrayList<io.dcloud.nineoldandroids.animation.AnimatorSet$Node> r1 = r3.mSortedNodes
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x005c
            java.util.ArrayList<io.dcloud.nineoldandroids.animation.AnimatorSet$Node> r1 = r3.mSortedNodes
            java.util.Iterator r1 = r1.iterator()
        L_0x0049:
            boolean r2 = r1.hasNext()
            if (r2 != 0) goto L_0x0050
            goto L_0x005c
        L_0x0050:
            java.lang.Object r2 = r1.next()
            io.dcloud.nineoldandroids.animation.AnimatorSet$Node r2 = (io.dcloud.nineoldandroids.animation.AnimatorSet.Node) r2
            io.dcloud.nineoldandroids.animation.Animator r2 = r2.animation
            r2.cancel()
            goto L_0x0049
        L_0x005c:
            if (r0 == 0) goto L_0x0073
            java.util.Iterator r0 = r0.iterator()
        L_0x0062:
            boolean r1 = r0.hasNext()
            if (r1 != 0) goto L_0x0069
            goto L_0x0073
        L_0x0069:
            java.lang.Object r1 = r0.next()
            io.dcloud.nineoldandroids.animation.Animator$AnimatorListener r1 = (io.dcloud.nineoldandroids.animation.Animator.AnimatorListener) r1
            r1.onAnimationEnd(r3)
            goto L_0x0062
        L_0x0073:
            r0 = 0
            r3.mStarted = r0
        L_0x0076:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: io.dcloud.nineoldandroids.animation.AnimatorSet.cancel():void");
    }

    public void end() {
        this.mTerminated = true;
        if (isStarted()) {
            if (this.mSortedNodes.size() != this.mNodes.size()) {
                sortNodes();
                Iterator<Node> it = this.mSortedNodes.iterator();
                while (it.hasNext()) {
                    Node next = it.next();
                    if (this.mSetListener == null) {
                        this.mSetListener = new AnimatorSetListener(this);
                    }
                    next.animation.addListener(this.mSetListener);
                }
            }
            ValueAnimator valueAnimator = this.mDelayAnim;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (this.mSortedNodes.size() > 0) {
                Iterator<Node> it2 = this.mSortedNodes.iterator();
                while (it2.hasNext()) {
                    it2.next().animation.end();
                }
            }
            ArrayList<Animator.AnimatorListener> arrayList = this.mListeners;
            if (arrayList != null) {
                Iterator it3 = ((ArrayList) arrayList.clone()).iterator();
                while (it3.hasNext()) {
                    ((Animator.AnimatorListener) it3.next()).onAnimationEnd(this);
                }
            }
            this.mStarted = false;
        }
    }

    public ArrayList<Animator> getChildAnimations() {
        ArrayList<Animator> arrayList = new ArrayList<>();
        Iterator<Node> it = this.mNodes.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().animation);
        }
        return arrayList;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public long getStartDelay() {
        return this.mStartDelay;
    }

    public boolean isRunning() {
        Iterator<Node> it = this.mNodes.iterator();
        while (it.hasNext()) {
            if (it.next().animation.isRunning()) {
                return true;
            }
        }
        return false;
    }

    public boolean isStarted() {
        return this.mStarted;
    }

    public Builder play(Animator animator) {
        if (animator == null) {
            return null;
        }
        this.mNeedsSort = true;
        return new Builder(animator);
    }

    public void playSequentially(Animator... animatorArr) {
        if (animatorArr != null) {
            this.mNeedsSort = true;
            int i = 0;
            if (animatorArr.length == 1) {
                play(animatorArr[0]);
                return;
            }
            while (i < animatorArr.length - 1) {
                i++;
                play(animatorArr[i]).before(animatorArr[i]);
            }
        }
    }

    public void playTogether(Animator... animatorArr) {
        if (animatorArr != null) {
            this.mNeedsSort = true;
            Builder play = play(animatorArr[0]);
            for (int i = 1; i < animatorArr.length; i++) {
                play.with(animatorArr[i]);
            }
        }
    }

    public void setInterpolator(Interpolator interpolator) {
        Iterator<Node> it = this.mNodes.iterator();
        while (it.hasNext()) {
            it.next().animation.setInterpolator(interpolator);
        }
    }

    public void setStartDelay(long j) {
        this.mStartDelay = j;
    }

    public void setTarget(Object obj) {
        Iterator<Node> it = this.mNodes.iterator();
        while (it.hasNext()) {
            Animator animator = it.next().animation;
            if (animator instanceof AnimatorSet) {
                ((AnimatorSet) animator).setTarget(obj);
            } else if (animator instanceof ObjectAnimator) {
                ((ObjectAnimator) animator).setTarget(obj);
            }
        }
    }

    public void setupEndValues() {
        Iterator<Node> it = this.mNodes.iterator();
        while (it.hasNext()) {
            it.next().animation.setupEndValues();
        }
    }

    public void setupStartValues() {
        Iterator<Node> it = this.mNodes.iterator();
        while (it.hasNext()) {
            it.next().animation.setupStartValues();
        }
    }

    public void start() {
        this.mTerminated = false;
        this.mStarted = true;
        sortNodes();
        int size = this.mSortedNodes.size();
        for (int i = 0; i < size; i++) {
            Node node = this.mSortedNodes.get(i);
            ArrayList<Animator.AnimatorListener> listeners = node.animation.getListeners();
            if (listeners != null && listeners.size() > 0) {
                Iterator it = new ArrayList(listeners).iterator();
                while (it.hasNext()) {
                    Animator.AnimatorListener animatorListener = (Animator.AnimatorListener) it.next();
                    if ((animatorListener instanceof DependencyListener) || (animatorListener instanceof AnimatorSetListener)) {
                        node.animation.removeListener(animatorListener);
                    }
                }
            }
        }
        final ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < size; i2++) {
            Node node2 = this.mSortedNodes.get(i2);
            if (this.mSetListener == null) {
                this.mSetListener = new AnimatorSetListener(this);
            }
            ArrayList<Dependency> arrayList2 = node2.dependencies;
            if (arrayList2 == null || arrayList2.size() == 0) {
                arrayList.add(node2);
            } else {
                int size2 = node2.dependencies.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    Dependency dependency = node2.dependencies.get(i3);
                    dependency.node.animation.addListener(new DependencyListener(this, node2, dependency.rule));
                }
                node2.tmpDependencies = (ArrayList) node2.dependencies.clone();
            }
            node2.animation.addListener(this.mSetListener);
        }
        if (this.mStartDelay <= 0) {
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                Node node3 = (Node) it2.next();
                node3.animation.start();
                this.mPlayingSet.add(node3.animation);
            }
        } else {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mDelayAnim = ofFloat;
            ofFloat.setDuration(this.mStartDelay);
            this.mDelayAnim.addListener(new AnimatorListenerAdapter() {
                boolean canceled = false;

                public void onAnimationCancel(Animator animator) {
                    this.canceled = true;
                }

                public void onAnimationEnd(Animator animator) {
                    if (!this.canceled) {
                        int size = arrayList.size();
                        for (int i = 0; i < size; i++) {
                            Node node = (Node) arrayList.get(i);
                            node.animation.start();
                            AnimatorSet.this.mPlayingSet.add(node.animation);
                        }
                    }
                }
            });
            this.mDelayAnim.start();
        }
        ArrayList<Animator.AnimatorListener> arrayList3 = this.mListeners;
        if (arrayList3 != null) {
            ArrayList arrayList4 = (ArrayList) arrayList3.clone();
            int size3 = arrayList4.size();
            for (int i4 = 0; i4 < size3; i4++) {
                ((Animator.AnimatorListener) arrayList4.get(i4)).onAnimationStart(this);
            }
        }
        if (this.mNodes.size() == 0 && this.mStartDelay == 0) {
            this.mStarted = false;
            ArrayList<Animator.AnimatorListener> arrayList5 = this.mListeners;
            if (arrayList5 != null) {
                ArrayList arrayList6 = (ArrayList) arrayList5.clone();
                int size4 = arrayList6.size();
                for (int i5 = 0; i5 < size4; i5++) {
                    ((Animator.AnimatorListener) arrayList6.get(i5)).onAnimationEnd(this);
                }
            }
        }
    }

    public AnimatorSet clone() {
        AnimatorSet animatorSet = (AnimatorSet) super.clone();
        animatorSet.mNeedsSort = true;
        animatorSet.mTerminated = false;
        animatorSet.mStarted = false;
        animatorSet.mPlayingSet = new ArrayList<>();
        animatorSet.mNodeMap = new HashMap<>();
        animatorSet.mNodes = new ArrayList<>();
        animatorSet.mSortedNodes = new ArrayList<>();
        HashMap hashMap = new HashMap();
        Iterator<Node> it = this.mNodes.iterator();
        while (it.hasNext()) {
            Node next = it.next();
            Node clone = next.clone();
            hashMap.put(next, clone);
            animatorSet.mNodes.add(clone);
            animatorSet.mNodeMap.put(clone.animation, clone);
            ArrayList arrayList = null;
            clone.dependencies = null;
            clone.tmpDependencies = null;
            clone.nodeDependents = null;
            clone.nodeDependencies = null;
            ArrayList<Animator.AnimatorListener> listeners = clone.animation.getListeners();
            if (listeners != null) {
                Iterator<Animator.AnimatorListener> it2 = listeners.iterator();
                while (it2.hasNext()) {
                    Animator.AnimatorListener next2 = it2.next();
                    if (next2 instanceof AnimatorSetListener) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(next2);
                    }
                }
                if (arrayList != null) {
                    Iterator it3 = arrayList.iterator();
                    while (it3.hasNext()) {
                        listeners.remove((Animator.AnimatorListener) it3.next());
                    }
                }
            }
        }
        Iterator<Node> it4 = this.mNodes.iterator();
        while (it4.hasNext()) {
            Node next3 = it4.next();
            Node node = (Node) hashMap.get(next3);
            ArrayList<Dependency> arrayList2 = next3.dependencies;
            if (arrayList2 != null) {
                Iterator<Dependency> it5 = arrayList2.iterator();
                while (it5.hasNext()) {
                    Dependency next4 = it5.next();
                    node.addDependency(new Dependency((Node) hashMap.get(next4.node), next4.rule));
                }
            }
        }
        return animatorSet;
    }

    public AnimatorSet setDuration(long j) {
        if (j >= 0) {
            Iterator<Node> it = this.mNodes.iterator();
            while (it.hasNext()) {
                it.next().animation.setDuration(j);
            }
            this.mDuration = j;
            return this;
        }
        throw new IllegalArgumentException("duration must be a value of zero or greater");
    }

    public void playTogether(Collection<Animator> collection) {
        if (collection != null && collection.size() > 0) {
            this.mNeedsSort = true;
            Builder builder = null;
            for (Animator next : collection) {
                if (builder == null) {
                    builder = play(next);
                } else {
                    builder.with(next);
                }
            }
        }
    }

    public void playSequentially(List<Animator> list) {
        if (list != null && list.size() > 0) {
            this.mNeedsSort = true;
            int i = 0;
            if (list.size() == 1) {
                play(list.get(0));
                return;
            }
            while (i < list.size() - 1) {
                i++;
                play(list.get(i)).before(list.get(i));
            }
        }
    }

    public class Builder {
        private Node mCurrentNode;

        Builder(Animator animator) {
            Node node = (Node) AnimatorSet.this.mNodeMap.get(animator);
            this.mCurrentNode = node;
            if (node == null) {
                this.mCurrentNode = new Node(animator);
                AnimatorSet.this.mNodeMap.put(animator, this.mCurrentNode);
                AnimatorSet.this.mNodes.add(this.mCurrentNode);
            }
        }

        public Builder after(Animator animator) {
            Node node = (Node) AnimatorSet.this.mNodeMap.get(animator);
            if (node == null) {
                node = new Node(animator);
                AnimatorSet.this.mNodeMap.put(animator, node);
                AnimatorSet.this.mNodes.add(node);
            }
            this.mCurrentNode.addDependency(new Dependency(node, 1));
            return this;
        }

        public Builder before(Animator animator) {
            Node node = (Node) AnimatorSet.this.mNodeMap.get(animator);
            if (node == null) {
                node = new Node(animator);
                AnimatorSet.this.mNodeMap.put(animator, node);
                AnimatorSet.this.mNodes.add(node);
            }
            node.addDependency(new Dependency(this.mCurrentNode, 1));
            return this;
        }

        public Builder with(Animator animator) {
            Node node = (Node) AnimatorSet.this.mNodeMap.get(animator);
            if (node == null) {
                node = new Node(animator);
                AnimatorSet.this.mNodeMap.put(animator, node);
                AnimatorSet.this.mNodes.add(node);
            }
            node.addDependency(new Dependency(this.mCurrentNode, 0));
            return this;
        }

        public Builder after(long j) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            ofFloat.setDuration(j);
            after((Animator) ofFloat);
            return this;
        }
    }
}
