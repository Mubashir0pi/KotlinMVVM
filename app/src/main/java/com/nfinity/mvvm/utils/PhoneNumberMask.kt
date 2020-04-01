package com.plutuscommerce.gbk.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout


object PhoneNumberMask {
    private val mask8 = "####-####"
    private val mask9 = "#####-####"
    private val mask10 = "(##) ####-####"
    private val mask11 = "#### ######"

    fun unmask(s: String): String {
        return s.replace("[^0-9]*".toRegex(), "")
    }

    fun insert(editText: EditText,relativeLayout: RelativeLayout): TextWatcher {
        return object : TextWatcher {
            internal var isUpdating: Boolean = false
            internal var old = ""

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val str = PhoneNumberMask.unmask(s.toString())
                val mask: String
                val defaultMask = getDefaultMask(str)
                if(str.length==11){
                    mask = mask11
                    relativeLayout.visibility=View.VISIBLE
                } else if(str.length==10){
                    mask = mask10
                    relativeLayout.visibility=View.VISIBLE
                } else if(str.length==9){
                    mask= mask9
                    relativeLayout.visibility=View.VISIBLE

                }else{
                    mask = defaultMask
                }



                var mascara = ""
                if (isUpdating) {
                    old = str
                    isUpdating = false
                    return
                }
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#' && str.length > old.length || m != '#' && str.length < old.length && str.length != i) {
                        mascara += m
                        continue
                    }

                    try {
                        mascara += str[i]
                    } catch (e: Exception) {
                        break
                    }

                    i++
                }
                isUpdating = true
                editText.setText(mascara)
                editText.setSelection(mascara.length)

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }
            override fun afterTextChanged(s: Editable) {

            }
        }
    }

    private fun getDefaultMask(str: String): String {
        var defaultMask = mask8
        if (str.length > 11) {
            defaultMask = mask11
        }
        return defaultMask
    }

}