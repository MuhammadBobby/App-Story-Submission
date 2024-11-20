package com.dicoding.picodiploma.loginwithanimation.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity

@Suppress("DEPRECATION", "CAST_NEVER_SUCCEEDS")
class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        observeLoginResult()
        observeLoading()
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    //action
    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            //validate & run login function
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launchWhenStarted {
                    viewModel.login(email, password)
                }
            }
        }
    }


    //observe loading
    private fun observeLoading() {
        lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    binding.progressBarLogIn.visibility = View.VISIBLE
                } else {
                    binding.progressBarLogIn.visibility = View.GONE
                }
            }
        }
    }


    //observe login result
    private fun observeLoginResult() {
        //observe
        lifecycleScope.launchWhenStarted{
            viewModel.loginResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        //cek response
                        val apiResponse = it.getOrNull()
                        apiResponse?.let { response ->
                            if (!response.error!!) {
                                //get data
                                val loginResult = response.loginResult
                                val email = binding.edLoginEmail.text.toString()

                                //user model
                                val userModel = loginResult?.token?.let { it1 ->
                                    UserModel(
                                        email = email,
                                        token = it1,
                                        isLogin = true
                                    )
                                }

                                // save session is login
                                userModel?.let { it1 -> viewModel.saveSession(it1) }

                                showSuccessDialog(email)
                            } else {
                                showErrorDialog(response.message)
                            }
                        }
                    } else {
                        showErrorDialog("Email atau Password salah")
                    }
                }
            }
        }
    }


    //alert dialog error
    private fun showErrorDialog(message: String?) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Failed!")
            .setContentText("$message")
            .setConfirmText("OK")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }


    //alert dialog success
    private fun showSuccessDialog(email: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Login Berhasil!")
            .setContentText("Selamat datang, $email.")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                // goto ke MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()
    }

}