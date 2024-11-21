package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.services.retrofit.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory

@Suppress("DEPRECATION")
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //init binding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //run function
        setupView()
        setupAction()
        observeRegisterResult()
        observeLoading()
    }


    //setup view function
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


    //setup action function
    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val email = binding.edRegisterEmail.text.toString().trim()
            val name = binding.edRegisterName.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            //validate input
            if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password.length < 8) {
                binding.edRegisterPassword.error = "Password must be at least 8 characters long"
                Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
            } else {
                //register with viewmodel (coroutine)
                lifecycleScope.launchWhenStarted {
                    signupViewModel.register(name, email, password)
                }
            }
        }
    }


    //observe register result
    private fun observeRegisterResult() {
        //observe
        lifecycleScope.launchWhenStarted{
            signupViewModel.registerResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        val email = binding.edRegisterEmail.text.toString()
                        showSuccessDialog(email)
                    } else {
                        showErrorDialog(it.exceptionOrNull()?.message)
                    }
                }
            }
        }
    }


    //observe loading
    private fun observeLoading() {
        lifecycleScope.launchWhenStarted {
            signupViewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    binding.progressBarSignUp.visibility = View.VISIBLE
                } else {
                    binding.progressBarSignUp.visibility = View.GONE
                }
            }
        }
    }


    //alert dialog error
    private fun showErrorDialog(message: String?) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Failed!")
            .setContentText("Gagal Registrasi : $message")
            .setConfirmText("OK")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }


    //alert dialog success
    private fun showSuccessDialog(email: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Success!")
            .setContentText("Akun dengan $email Berhasil Dibuat.")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                finish() // Kembali ke layar sebelumnya
            }
            .show()
    }
}