/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.contacts.list;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.contacts.R;
import com.android.contacts.common.compat.ProviderStatusCompat;

/**
 * Fragment shown when contacts are unavailable. It contains provider status
 * messaging as well as instructions for the user.
 */
public class ContactsUnavailableFragment extends Fragment implements OnClickListener {

    private View mView;
    private ImageView mImageView;
    private TextView mMessageView;
    private Button mAddAccountButton;
    private Button mImportContactsButton;
    private ProgressBar mProgress;
    private View mButtonsContainer;
    private int mNoContactsMsgResId = -1;

    private OnContactsUnavailableActionListener mListener;

    private Integer mProviderStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.contacts_unavailable_fragment, null);

        mImageView = (ImageView) mView.findViewById(R.id.empty_image);
        final LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) mImageView.getLayoutParams();
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;
        final int topMargin =
                screenHeight / getResources()
                        .getInteger(R.integer.contacts_no_account_empty_image_margin_divisor)
                - getResources()
                        .getDimensionPixelSize(R.dimen.contacts_no_account_empty_image_offset);
        layoutParams.setMargins(0, topMargin, 0, 0);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        mImageView.setLayoutParams(layoutParams);

        mMessageView = (TextView) mView.findViewById(R.id.message);
        mAddAccountButton = (Button) mView.findViewById(R.id.add_account_button);
        mAddAccountButton.setOnClickListener(this);
        mAddAccountButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R
                .color.primary_color), PorterDuff.Mode.SRC_ATOP);
        mImportContactsButton = (Button) mView.findViewById(R.id.import_contacts_button);
        mImportContactsButton.setOnClickListener(this);
        mImportContactsButton.getBackground().setColorFilter(ContextCompat.getColor(getContext(),
                R.color.primary_color), PorterDuff.Mode.SRC_ATOP);
        mProgress = (ProgressBar) mView.findViewById(R.id.progress);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mButtonsContainer = mView.findViewById(R.id.buttons_container);
        }

        if (mProviderStatus != null) {
            updateStatus(mProviderStatus);
        }

        return mView;
    }

    public void setOnContactsUnavailableActionListener(
            OnContactsUnavailableActionListener listener) {
        mListener = listener;
    }

    public void updateStatus(int providerStatus) {
        mProviderStatus = providerStatus;
        if (mView == null) {
            // The view hasn't been inflated yet.
            return;
        }
        if (providerStatus == ProviderStatusCompat.STATUS_EMPTY) {
            updateViewsForEmptyStatus();
        } else if (providerStatus == ProviderStatusCompat.STATUS_BUSY) {
            updateViewsForBusyStatus(R.string.upgrade_in_progress);
        } else if (providerStatus == ProviderStatusCompat.STATUS_CHANGING_LOCALE) {
            updateViewsForBusyStatus(R.string.locale_change_in_progress);
        }
    }

    /**
     * Update views in the fragment when provider status is empty.
     */
    private void updateViewsForEmptyStatus() {
        mProgress.setVisibility(View.GONE);
    }

    /**
     * Update views in the fragment when provider status is busy.
     *
     * @param resId resource ID of the string to show in mMessageView.
     */
    private void updateViewsForBusyStatus(int resId) {
        mMessageView.setText(resId);
        mMessageView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);
        updateButtonVisibilty(View.GONE);
        mProgress.setVisibility(View.VISIBLE);

        final ViewGroup.MarginLayoutParams layoutParams =
                (ViewGroup.MarginLayoutParams) mMessageView.getLayoutParams();
        final int marginTop =
                (int) getResources().getDimension(R.dimen.update_contact_list_top_margin);
        layoutParams.setMargins(0, marginTop, 0, 0);
        mMessageView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.add_account_button:
                mListener.onAddAccountAction();
                break;
            case R.id.import_contacts_button:
                mListener.onImportContactsFromFileAction();
                break;
        }
    }

    private void updateButtonVisibilty(int visibility) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mAddAccountButton.setVisibility(visibility);
            mImportContactsButton.setVisibility(visibility);
            mButtonsContainer.setVisibility(visibility);
        } else {
            mAddAccountButton.setVisibility(visibility);
            mImportContactsButton.setVisibility(visibility);
        }
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
