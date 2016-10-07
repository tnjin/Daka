package tq.afor.tjz.daka;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Tjz on 2016/10/4.
 */

public class DakaService extends AccessibilityService {
    public static final String MM_MAIN_UI="com.tencent.mm.ui.LauncherUI";
    public static final String  MM_MM_BTN = "com.tencent.mm:id/blu";
    public static final String  MM_HEADER_BAR ="com.tencent.mm:id/ev";
    public static final String MM_FTS_UI="com.tencent.mm.plugin.search.ui.FTSMainUI";
    public static final String MM_CHATTING_UI="com.tencent.mm.ui.chatting.ChattingUI";
    private String msg ="测试消息，不用管";
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int type = event.getEventType();
        switch (type) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Logs.i("SSSS","win state changed");
                sleep();
                String uiName = event.getClassName().toString();
                if(MM_MAIN_UI.equals(uiName)){
                Logs.i("SSSS","equals");
                    determineChatingUI();

                    // findViewById();
            }else if(MM_FTS_UI.equals(uiName)){
                    fillSearchBox("天晴");
                    sleep();
                    openFirstContact();
                }else if(MM_CHATTING_UI.equals(uiName)){
                    inputAndSendMsg(getRootInActiveWindow(),msg);
                    pressBackBtn();
                }else {
                    Logs.i("SSSS",event.getClassName().toString());
                }
                break;
            default:
                Logs.i("SSSS","nnnnnnnnnn");
                break;

        }

    }

    @Override
    public void onInterrupt() {

    }

    private void sleep(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 点击主页面的搜索按钮，跳转到搜索页
     */
    public boolean clickSerchBtn(){
    AccessibilityNodeInfo  rootInfo = getRootInActiveWindow();
        AccessibilityNodeInfo searchInfo = getSpecifyNode(rootInfo,"android.widget.TextView","搜索");

        if(searchInfo != null){
            searchInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Logs.i("SSSS","主界面搜索按钮点击成功");
            return true;
        }
            Logs.i("SSSS","主界面搜索按钮没有找到");
            return false;
}

    /**
     * 向搜索页的搜索框输入搜索文字
     * @param txt
     * @return
     */
    private boolean fillSearchBox(String txt){
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
      AccessibilityNodeInfo node =  getSpecifyNode(rootNode,"android.widget.EditText",null);
        if(node != null){
            setText(node,txt);
            Logs.i("SSSS","搜索框找到并填充");
            return true;
        }
        Logs.i("SSSS","搜索框没有找到");
        return false;
    }


    /**
     * 设置文本
     */
    private void setText(AccessibilityNodeInfo node, String reply) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    reply);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
        } else {

        ClipData data = ClipData.newPlainText("reply", reply);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(data);
        node.performAction(AccessibilityNodeInfo.ACTION_FOCUS); // 获取焦点
        node.performAction(AccessibilityNodeInfo.ACTION_PASTE); // 执行粘贴
        }
    }

    /**
     * 打开搜索结果的第一个联系人的聊天窗口
     * @return
     */
    private boolean openFirstContact(){
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        AccessibilityNodeInfo info = getSpecifyNode(rootNode,"android.widget.ListView",null);
        if(info == null ){
            return false;
        }
        if(rootNode.findAccessibilityNodeInfosByText("联系人") == null){
            return false;
        }

        if(info.getChildCount()<2){
            return false;
        }
        info = info.getChild(1);

        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        return true;
    }

    /**
     * 判断是否是主界面或者是聊天窗口，因为这俩界面可能一致；
     * 判断依据是是否有返回按钮
     */
    private void determineChatingUI(){
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        AccessibilityNodeInfo info =findBackBtn(rootNode);
        if(info == null){//没有返回按钮，当前页面为主界面
            clickSerchBtn();
        }else{//有返回按钮，为聊天界面
         inputAndSendMsg(rootNode,msg);
            pressBackBtn();
        }
    }


    private boolean pressBackBtn(){
        AccessibilityNodeInfo info = findBackBtn(getRootInActiveWindow());
        if(info == null){
            Logs.i("SSSS","返回按钮没有找到");
            return false;
        }else {
            info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Logs.i("SSSS","返回到搜索界面");
            return true;
        }
    }

    /**
     * 查找页面上的返回按钮，规则：第一个ImageView
     * @param rootNode
     * @return
     */
    private AccessibilityNodeInfo findBackBtn(AccessibilityNodeInfo rootNode){
        return getSpecifyNode(rootNode,"android.widget.ImageView","返回");
    }

    /**
     * 通过遍历的方式来查找指定的控件，返回第一个匹配者
     * @param rootNode
     * @param spe 指定类名,不为null
     * @param dec 指定描述类型，可以为null
     * @return
     */
    private AccessibilityNodeInfo getSpecifyNode(AccessibilityNodeInfo rootNode,String spe,String dec){
        if(rootNode == null){
            return null;
        }
        if(TextUtils.isEmpty(spe) && TextUtils.isEmpty(dec)){
            return null;
        }
        int count = rootNode.getChildCount();
        for(int i = 0 ;i<count ;i++){
            AccessibilityNodeInfo node = rootNode.getChild(i);
            String nodeClass = node.getClassName().toString();
            if(spe.equals(nodeClass)){
                if(!TextUtils.isEmpty(dec)){
                    if(dec .equals(node.getContentDescription())){
                        return node;
                    }else{
                        continue;
                    }
                }
                return node;
            }
           node =  getSpecifyNode(node,spe,dec);
            if(node == null){
                continue;
            }else{
                return node;
            }
        }
        return  null;
    }

    /**
     * 输入并且发送消息
     * @param rootNode
     * @param reply
     * @return
     */
    public boolean inputAndSendMsg(AccessibilityNodeInfo rootNode, String reply){
        if(inputMsg(rootNode,reply)){
            return sendMsg(rootNode);
        }
        return false;

    }

    /**
     * 查找EditText控件
     * @param rootNode 根结点
     * @param reply 回复内容
     * @return 找到返回true, 否则返回false
     */
    private boolean inputMsg(AccessibilityNodeInfo rootNode, String reply) {
        int count = rootNode.getChildCount();
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if ("android.widget.EditText".equals(node.getClassName())) {   // 找到输入框并输入文本
                setText(node, reply);
                Logs.i("SSSS","input....");
                return true;
            }
            if (inputMsg(node, reply)) {    // 递归查找
                return true;
            }
        }
        return false;
    }


    private boolean sendMsg(AccessibilityNodeInfo rootNode){
        // 通过文本找到当前的节点
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText("发送");
        if(nodes != null) {
            for (AccessibilityNodeInfo node : nodes) {
                if ("android.widget.Button".equals(node.getClassName()) && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK); // 执行点击
                    return true;
                }
            }
        }
        return false;
    }
}

