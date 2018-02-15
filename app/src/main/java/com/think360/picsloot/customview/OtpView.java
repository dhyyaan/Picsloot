package com.think360.picsloot.customview;

/**
 * Created by think360 on 04/04/17.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.think360.picsloot.R;

public class OtpView extends LinearLayout {
    private AppCompatEditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField, mOtpFiveField,
            mCurrentlyFocusedEditText;
    //mOtpSixField,
    public OtpView(Context context) {
        super(context);
        init(null);
    }

    public OtpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public OtpView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray styles = getContext().obtainStyledAttributes(attrs, R.styleable.OtpView);
        LayoutInflater mInflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.otpview_layout, this);
        mOtpOneField = findViewById(R.id.otp_one_edit_text);
        mOtpTwoField =  findViewById(R.id.otp_two_edit_text);
        mOtpThreeField =  findViewById(R.id.otp_three_edit_text);
        mOtpFourField = findViewById(R.id.otp_four_edit_text);
        mOtpFiveField =  findViewById(R.id.otp_five_edit_text);
   //     mOtpSixField = findViewById(R.id.otp_six_edit_text);
       if(mOtpOneField.getText().toString().equals("")){
            mOtpTwoField.setEnabled(false);
            mOtpThreeField.setEnabled(false);
            mOtpFourField.setEnabled(false);
            mOtpFiveField.setEnabled(false);
         //   mOtpSixField.setEnabled(false);

       }
        styleEditTexts(styles);
        styles.recycle();
    }

    /**
     * Get an instance of the present otp
     */
    private String makeOTP(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(mOtpOneField.getText().toString());
        stringBuilder.append(mOtpTwoField.getText().toString());
        stringBuilder.append(mOtpThreeField.getText().toString());
        stringBuilder.append(mOtpFourField.getText().toString());
        stringBuilder.append(mOtpFiveField.getText().toString());
     //   stringBuilder.append(mOtpSixField.getText().toString());
        return stringBuilder.toString();
    }

    /**
     * Checks if all four fields have been filled
     * @return length of OTP
     */
    public boolean hasValidOTP(){
        return makeOTP().length()==5;
    }

    /**
     * Returns the present otp entered by the user
     * @return OTP
     */
    public String getOTP(){
        return makeOTP();
    }

    /**
     * Used to set the OTP. More of cosmetic value than functional value
     * @param otp Send the four digit otp
     */
    public void setOTP(String otp){
        if(otp.length()!=5){
            Log.e("OTPView","Invalid otp param");
            return;
        }
        if(mOtpOneField.getInputType()== InputType.TYPE_CLASS_NUMBER
                && !otp.matches("[0-9]+")){
            Log.e("OTPView","OTP doesn't match INPUT TYPE");
            return;
        }
        mOtpOneField.setText(otp.charAt(0));
        mOtpTwoField.setText(otp.charAt(1));
        mOtpThreeField.setText(otp.charAt(2));
        mOtpFourField.setText(otp.charAt(3));
        mOtpFiveField.setText(otp.charAt(4));
     //   mOtpSixField.setText(otp.charAt(5));
    }

    private void styleEditTexts(TypedArray styles) {
        int textColor = styles.getColor(R.styleable.OtpView_android_textColor, Color.BLACK);

        mOtpOneField.setTextColor(textColor);
        mOtpTwoField.setTextColor(textColor);
        mOtpThreeField.setTextColor(textColor);
        mOtpFourField.setTextColor(textColor);
        mOtpFiveField.setTextColor(textColor);
     //   mOtpSixField.setTextColor(textColor);
        setEditTextInputStyle(styles);
    }

    private void setEditTextInputStyle(TypedArray styles) {
        int inputType =
                styles.getInt(R.styleable.OtpView_android_inputType, EditorInfo.TYPE_TEXT_VARIATION_NORMAL);
        mOtpOneField.setInputType(inputType);
        mOtpTwoField.setInputType(inputType);
        mOtpThreeField.setInputType(inputType);
        mOtpFourField.setInputType(inputType);
        mOtpFiveField.setInputType(inputType);
    //    mOtpSixField.setInputType(inputType);
        String text = styles.getString(R.styleable.OtpView_otp);
        if (!TextUtils.isEmpty(text) && text.length() == 6) {
            mOtpOneField.setText(String.valueOf(text.charAt(0)));
            mOtpTwoField.setText(String.valueOf(text.charAt(1)));
            mOtpThreeField.setText(String.valueOf(text.charAt(2)));
            mOtpFourField.setText(String.valueOf(text.charAt(3)));
            mOtpFiveField.setText(String.valueOf(text.charAt(4)));
         //   mOtpSixField.setText(String.valueOf(text.charAt(5)));
        }
        setFocusListener();
        setOnTextChangeListener();
    }

    private void setFocusListener() {
        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mCurrentlyFocusedEditText = (AppCompatEditText) v;
                mCurrentlyFocusedEditText.setSelection(mCurrentlyFocusedEditText.getText().length());

            }

        };

        mOtpOneField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpTwoField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpThreeField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpFourField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpFiveField.setOnFocusChangeListener(onFocusChangeListener);
       // mOtpSixField.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void setOnTextChangeListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(mOtpOneField.getText().toString().equals("")){

                    mOtpTwoField.setEnabled(false);



                }else{

                    mOtpTwoField.setEnabled(true);
                    if(mOtpTwoField.getText().toString().equals("")){

                        mOtpThreeField.setEnabled(false);


                    }else{
                        mOtpThreeField.setEnabled(true);
                        if(mOtpThreeField.getText().toString().equals("")){
                            mOtpFourField.setEnabled(false);

                        }else{

                            mOtpFourField.setEnabled(true);
                            if(mOtpFourField.getText().toString().equals("")){
                                mOtpFiveField.setEnabled(false);
                            }else{
                                mOtpFiveField.setEnabled(true);
                               /* if(mOtpFiveField.getText().toString().equals("")){
                                    mOtpSixField.setEnabled(false);
                                }else{
                                    mOtpSixField.setEnabled(true);
                                }*/
                            }

                        }

                    }

                }



              if (mCurrentlyFocusedEditText!=null && mCurrentlyFocusedEditText.getText().length() >= 1
                        && mCurrentlyFocusedEditText != mOtpFiveField) {
                    mCurrentlyFocusedEditText.focusSearch(View.FOCUS_RIGHT).requestFocus();

                } else if (mCurrentlyFocusedEditText!=null && mCurrentlyFocusedEditText.getText().length() >= 1
                        && mCurrentlyFocusedEditText == mOtpFiveField) {
                  InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                  imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


                } else if (mCurrentlyFocusedEditText!=null){
                    String currentValue = mCurrentlyFocusedEditText.getText().toString();
                    if (currentValue.length() <= 0 && mCurrentlyFocusedEditText.getSelectionStart() <= 0) {
                        mCurrentlyFocusedEditText.focusSearch(View.FOCUS_LEFT).requestFocus();
                    }
                }

            }
        };
        mOtpOneField.addTextChangedListener(textWatcher);
        mOtpTwoField.addTextChangedListener(textWatcher);
        mOtpThreeField.addTextChangedListener(textWatcher);
        mOtpFourField.addTextChangedListener(textWatcher);
        mOtpFiveField.addTextChangedListener(textWatcher);
     //   mOtpSixField.addTextChangedListener(textWatcher);
    }

    public void simulateDeletePress() {
        mCurrentlyFocusedEditText.setText("");
    }
}