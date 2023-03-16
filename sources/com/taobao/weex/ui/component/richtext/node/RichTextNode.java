package com.taobao.weex.ui.component.richtext.node;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import androidx.collection.ArrayMap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.common.Constants;
import com.taobao.weex.dom.WXCustomStyleSpan;
import com.taobao.weex.dom.WXStyle;
import com.taobao.weex.utils.WXLogUtils;
import com.taobao.weex.utils.WXResourceUtils;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class RichTextNode {
    public static final String ATTR = "attr";
    public static final String CHILDREN = "children";
    public static final String ITEM_CLICK = "itemclick";
    private static final int MAX_LEVEL = 255;
    public static final String PSEUDO_REF = "pseudoRef";
    public static final String STYLE = "style";
    public static final String TYPE = "type";
    public static final String VALUE = "value";
    protected Map<String, Object> attr;
    protected List<RichTextNode> children;
    protected final String mComponentRef;
    protected final Context mContext;
    protected final String mInstanceId;
    protected final String mRef;
    protected Map<String, Object> style;

    private static int createPriorityFlag(int i) {
        if (i <= 255) {
            return (255 - i) << 16;
        }
        return 16711680;
    }

    /* access modifiers changed from: protected */
    public abstract boolean isInternalNode();

    public abstract String toString();

    protected RichTextNode(Context context, String str, String str2) {
        this.mContext = context;
        this.mInstanceId = str;
        this.mComponentRef = str2;
        this.mRef = null;
    }

    protected RichTextNode(Context context, String str, String str2, String str3, Map<String, Object> map, Map<String, Object> map2) {
        this.mContext = context;
        this.mInstanceId = str;
        this.mComponentRef = str2;
        this.mRef = str3;
        if (map != null) {
            this.style = map;
        } else {
            this.style = new ArrayMap(0);
        }
        if (map2 != null) {
            this.attr = map2;
        } else {
            this.attr = new ArrayMap(0);
        }
        this.children = new LinkedList();
    }

    public static Spannable parse(Context context, String str, String str2, String str3) {
        RichTextNode createRichTextNode;
        JSONArray parseArray = JSON.parseArray(str3);
        if (parseArray == null || parseArray.isEmpty()) {
            return new SpannableString("");
        }
        ArrayList arrayList = new ArrayList(parseArray.size());
        for (int i = 0; i < parseArray.size(); i++) {
            JSONObject jSONObject = parseArray.getJSONObject(i);
            if (!(jSONObject == null || (createRichTextNode = RichTextNodeManager.createRichTextNode(context, str, str2, jSONObject)) == null)) {
                arrayList.add(createRichTextNode);
            }
        }
        return parse(arrayList);
    }

    public static int createSpanFlag(int i) {
        return createPriorityFlag(i) | 17;
    }

    public String getRef() {
        return this.mRef;
    }

    /* access modifiers changed from: package-private */
    public final void parse(Context context, String str, String str2, JSONObject jSONObject) {
        JSONObject jSONObject2 = jSONObject.getJSONObject("style");
        if (jSONObject2 != null) {
            ArrayMap arrayMap = new ArrayMap();
            this.style = arrayMap;
            arrayMap.putAll(jSONObject2);
        } else {
            this.style = new ArrayMap(0);
        }
        JSONObject jSONObject3 = jSONObject.getJSONObject(ATTR);
        if (jSONObject3 != null) {
            ArrayMap arrayMap2 = new ArrayMap(jSONObject3.size());
            this.attr = arrayMap2;
            arrayMap2.putAll(jSONObject3);
        } else {
            this.attr = new ArrayMap(0);
        }
        JSONArray jSONArray = jSONObject.getJSONArray(CHILDREN);
        if (jSONArray != null) {
            this.children = new ArrayList(jSONArray.size());
            for (int i = 0; i < jSONArray.size(); i++) {
                RichTextNode createRichTextNode = RichTextNodeManager.createRichTextNode(context, str, str2, jSONArray.getJSONObject(i));
                if (createRichTextNode != null) {
                    this.children.add(createRichTextNode);
                }
            }
            return;
        }
        this.children = new ArrayList(0);
    }

    public void addChildNode(RichTextNode richTextNode) {
        if (this.children == null) {
            this.children = new LinkedList();
        }
        if (richTextNode != null && isInternalNode()) {
            this.children.add(richTextNode);
        }
    }

    public void removeChildNode(String str) {
        List<RichTextNode> list = this.children;
        if (list != null && !list.isEmpty() && !TextUtils.isEmpty(str)) {
            try {
                for (RichTextNode next : this.children) {
                    if (TextUtils.equals(next.mRef, str)) {
                        this.children.remove(next);
                    }
                }
            } catch (Exception e) {
                WXLogUtils.getStackTrace(e);
            }
        }
    }

    public void updateStyles(Map<String, Object> map) {
        if (map != null && !map.isEmpty()) {
            this.style.putAll(map);
        }
    }

    public void updateAttrs(Map<String, Object> map) {
        if (this.attr != null && !map.isEmpty()) {
            this.attr.putAll(map);
        }
    }

    /* access modifiers changed from: protected */
    public void updateSpans(SpannableStringBuilder spannableStringBuilder, int i) {
        int color;
        WXSDKInstance sDKInstance = WXSDKManager.getInstance().getSDKInstance(this.mInstanceId);
        if (this.style != null && sDKInstance != null) {
            LinkedList<Object> linkedList = new LinkedList<>();
            WXCustomStyleSpan createCustomStyleSpan = createCustomStyleSpan();
            if (createCustomStyleSpan != null) {
                linkedList.add(createCustomStyleSpan);
            }
            if (this.style.containsKey(Constants.Name.FONT_SIZE)) {
                linkedList.add(new AbsoluteSizeSpan(WXStyle.getFontSize(this.style, sDKInstance.getDefaultFontSize(), sDKInstance.getInstanceViewPortWidthWithFloat())));
            }
            if (this.style.containsKey("backgroundColor") && (color = WXResourceUtils.getColor(this.style.get("backgroundColor").toString(), 0)) != 0) {
                linkedList.add(new BackgroundColorSpan(color));
            }
            if (this.style.containsKey("color")) {
                linkedList.add(new ForegroundColorSpan(WXResourceUtils.getColor(WXStyle.getTextColor(this.style))));
            }
            int createSpanFlag = createSpanFlag(i);
            for (Object span : linkedList) {
                spannableStringBuilder.setSpan(span, 0, spannableStringBuilder.length(), createSpanFlag);
            }
        }
    }

    private static Spannable parse(List<RichTextNode> list) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        for (RichTextNode span : list) {
            spannableStringBuilder.append(span.toSpan(1));
        }
        return spannableStringBuilder;
    }

    public Spannable toSpan(int i) {
        List<RichTextNode> list;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(toString());
        if (isInternalNode() && (list = this.children) != null) {
            for (RichTextNode span : list) {
                spannableStringBuilder.append(span.toSpan(i + 1));
            }
        }
        updateSpans(spannableStringBuilder, i);
        return spannableStringBuilder;
    }

    private WXCustomStyleSpan createCustomStyleSpan() {
        int fontWeight = this.style.containsKey(Constants.Name.FONT_WEIGHT) ? WXStyle.getFontWeight(this.style) : -1;
        int fontStyle = this.style.containsKey(Constants.Name.FONT_STYLE) ? WXStyle.getFontStyle(this.style) : -1;
        String fontFamily = this.style.containsKey(Constants.Name.FONT_FAMILY) ? WXStyle.getFontFamily(this.style) : null;
        if (fontWeight == -1 && fontStyle == -1 && fontFamily == null) {
            return null;
        }
        return new WXCustomStyleSpan(fontStyle, fontWeight, fontFamily);
    }

    public RichTextNode findRichNode(String str) {
        String str2 = this.mRef;
        if (str2 != null && TextUtils.equals(str2, str)) {
            return this;
        }
        List<RichTextNode> list = this.children;
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (RichTextNode findRichNode : this.children) {
            RichTextNode findRichNode2 = findRichNode.findRichNode(str);
            if (findRichNode2 != null) {
                return findRichNode2;
            }
        }
        return null;
    }
}
