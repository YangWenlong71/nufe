package cc.nufe.tools.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import cc.nufe.tools.R;
import cc.nufe.tools.View.SystemUtil;
import cc.nufe.tools.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //顶部框颜色
        SystemUtil.setStatusBarColor(this, Color.parseColor("#2C3E50"));
        //绑定工作
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        LoginViewModel loginViewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(LoginViewModel.class);
        //绑定工作
        binding.setLoginvm(loginViewModel);
        //建立感应
        binding.setLifecycleOwner(this);
        //把他当作初始化函数使用更方便,比如要用个列表
        loginViewModel.setBinding(binding,this);
    }




}