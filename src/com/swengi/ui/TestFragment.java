package com.swengi.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.swengi.core.R;

public class TestFragment extends Fragment {

	public static final String ARG_TEST_NUMBER = "test_number";
	public static final String ARG_DEP_TITLE = "dep_title";

	public TestFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_test, container, false);
		int i = getArguments().getInt(ARG_TEST_NUMBER);
		String depT = getArguments().getString(ARG_DEP_TITLE);
		TextView data = (TextView) view.findViewById(R.id.fragment_test_text);
		data.setText("Fragment ID: " + i + ", Fragment Title: " + depT);
		return view;
	}
}
