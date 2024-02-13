package com.rideshare.app.custom;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class SSNFormattingTextWatcher implements TextWatcher {
    private EditText mEditText;
    private boolean mShouldDeleteSpace;

    public SSNFormattingTextWatcher(EditText editText) {
        mEditText = editText;
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        if (start > 0) {
//            CharSequence charDeleted = s.subSequence(start - 1, start);
//            mShouldDeleteSpace = "-".equals(charDeleted.toString());
//        }

        CharSequence charDeleted = s.subSequence(start, start + count);
        mShouldDeleteSpace = "-".equals(charDeleted.toString());
    }

    public void afterTextChanged(Editable editable) {
        mEditText.removeTextChangedListener(this);

        int cursorPosition = mEditText.getSelectionStart();
        String withSpaces = formatText(editable);
        mEditText.setText(withSpaces);

        mEditText.setSelection(cursorPosition + (withSpaces.length() - editable.length()));

        if (mShouldDeleteSpace) {
            //  userNameET.setSelection(userNameET.getSelectionStart() - 1);
            mShouldDeleteSpace = false;
        }

        mEditText.addTextChangedListener(this);
    }


    private String formatText(CharSequence text)
    {
        StringBuilder formatted = new StringBuilder();
        int count = 0;
        if(text.length()==3||text.length()==6)
        {
            if (!mShouldDeleteSpace)
                formatted.append(text+"-");
            else
                formatted.append(text);
        }
        else
            formatted.append(text);
        return formatted.toString();
    }


}