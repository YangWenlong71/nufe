package cc.nufe.tools.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import cc.nufe.tools.databinding.ActivityLoginBinding;
import cc.nufe.tools.listener.RequestCallback;
import cc.nufe.tools.listener.RequestListenerUser;

public class LoginViewModel extends AndroidViewModel {


    private static ActivityLoginBinding binding;
    @SuppressLint("StaticFieldLeak")
    private static LoginActivity loginActivity;


    public static MutableLiveData<String> _btn=new MutableLiveData<>();
    public static MutableLiveData<String> _score=new MutableLiveData<>();
    public static MutableLiveData<String> _exam=new MutableLiveData<>();
    public static MutableLiveData<String> _post=new MutableLiveData<>();
    public static MutableLiveData<String> _qq=new MutableLiveData<>();
    public static MutableLiveData<String> _web=new MutableLiveData<>();
    //liveData
    private MutableLiveData<String> _str;
    public LoginViewModel(@NonNull Application application) {
        super(application);

        _btn.setValue("备考刷题");
        _score.setValue("成绩查询");
        _exam.setValue("准考证");
        _post.setValue("邮寄订单");
        _qq.setValue("官方qq群");
        _web.setValue("官方网站");
    }

    public void setBinding(ActivityLoginBinding binding, LoginActivity loginActivity) {
        //把binding和mainActivity都赋值给MainVM作为静态变量备用，因为很多绑定的控件都只能用静态方法
        LoginViewModel.binding =binding;
        LoginViewModel.loginActivity =loginActivity;
    }
    //值得学习的一篇文章
    //https://blog.csdn.net/qq_25890433/article/details/116310790?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-1.no_search_link&spm=1001.2101.3001.4242.2
//这个是给activity双向通信用的
    public MutableLiveData<String> get_str(){
        if (_str ==null){
            _str = new MutableLiveData<>();
            _str.setValue("");
        }
        return _str;
    }

    private RequestListenerUser requestListenerUser;
    public void certification(){
        String phone = _str.getValue();
        Log.e("phone",phone);
        String h = "http://www.nufe.cc:8000/api/all_lessons_title";
        requestListenerUser = new RequestListenerUser();
        //requestListenerUser.useRequestListener("http://www.nufe.cc:8000/api/all_lessons_title");
        requestListenerUser.setRequestListener(h,new RequestCallback() {
            @Override
            public void success(String result) {
                Log.e("phone_success",result);
                Intent intent = new Intent();
                intent.setClass(loginActivity, ListActivity.class);//从哪里跳到哪里
                loginActivity.startActivity(intent);

            }
            @Override
            public void error(String error) {
                Log.e("phone_error",error);
            }
        });
    }

    public void searchScore(){
        Uri uri = Uri.parse("https://sdata.jseea.cn/tpl_front/score/practiceScoreList.html");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        loginActivity.startActivity(intent);
    }
    public void searchExam(){
        Uri uri = Uri.parse("https://sdata.jseea.cn/tpl_front/shzk/llkcbk/queryPrintTZSNew.html");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        loginActivity.startActivity(intent);
    }
    public void searchPost(){
        Uri uri = Uri.parse("http://www.nufe.cc/post/index.php");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        loginActivity.startActivity(intent);
    }
    public void addqq(){
        joinQQGroup("n7YgzJ4U9rc-CCYWZ722oRIXD4r4BYuy");
    }
    public void goweb(){
        Uri uri = Uri.parse("http://www.nufe.cc/");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        loginActivity.startActivity(intent);
    }

    private boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            loginActivity.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(loginActivity, "未安装手Q或安装的版本不支持", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}
