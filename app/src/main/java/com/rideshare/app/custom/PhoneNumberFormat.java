package com.rideshare.app.custom;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class PhoneNumberFormat implements TextWatcher {
    private EditText mEditText;
    private boolean mShouldDeleteSpace;

    public PhoneNumberFormat(EditText editText) {
        mEditText = editText;
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        CharSequence charDeleted = s.subSequence(start, start + count);
        if ("-".equals(charDeleted.toString())) {
            mShouldDeleteSpace = true;
        } else if ("(".equals(charDeleted.toString())) {
            mShouldDeleteSpace = true;
        } else if (")".equals(charDeleted.toString())) {
            mShouldDeleteSpace = true;
        } else if (" ".equals(charDeleted.toString())) {
            mShouldDeleteSpace = true;
        }
    }

    public void afterTextChanged(Editable editable) {
        mEditText.removeTextChangedListener(this);

        int cursorPosition = mEditText.getSelectionStart();
        String withSpaces = formatText(editable);
        mEditText.setText(withSpaces);

        mEditText.setSelection(cursorPosition + (withSpaces.length() - editable.length()));

        if (mShouldDeleteSpace) {
//            mEditText.setSelection(mEditText.getSelectionStart() - 1);
            mShouldDeleteSpace = false;
        }

        mEditText.addTextChangedListener(this);
    }


    private String formatText(CharSequence text) {
        // (555) 555-1234
        StringBuilder formatted = new StringBuilder();
        int count = 0;
        if (text.length() == 1) {
            if (!mShouldDeleteSpace && !text.toString().equalsIgnoreCase("(")) {
                formatted.append("(" + text);
            } else {
                formatted.append(text);
            }
        } else if (text.length() == 4) {
            if (!mShouldDeleteSpace) {
                formatted.append(text + ") ");
            } else {
                formatted.append(text);
            }
        }
//        else if (text.length() == 5) {
//            if (!mShouldDeleteSpace) {
//                formatted.append(text + " ");
//            } else {
//                formatted.append(text);
//            }
//        }
        else if (text.length() == 9) {
            if (!mShouldDeleteSpace)
                formatted.append(text + "-");
            else
                formatted.append(text);
        } else {
            formatted.append(text);
        }

        return formatted.toString();
    }


}
