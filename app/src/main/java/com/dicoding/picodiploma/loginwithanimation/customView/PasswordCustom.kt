package com.dicoding.picodiploma.loginwithanimation.customView

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

@SuppressLint("UseCompatLoadingForDrawables")
class PasswordCustom @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnFocusChangeListener {

    private val minPasswordLength = 8

    init {
        // Add listener to track focus and text changes
        onFocusChangeListener = this

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validatePassword(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing
            }
        })
    }

    private fun validatePassword(password: String) {
        error = if (password.length < minPasswordLength) {
            "Password must be at least $minPasswordLength characters"
        } else {
            null
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            // Validate when focus is lost
            validatePassword(text?.toString().orEmpty())
        }
    }
}
