package cc.nufe.tools.listener;

/**
 * interface接口
 */
public interface RequestCallback {

    void success(String result);
    void error(String error);
}