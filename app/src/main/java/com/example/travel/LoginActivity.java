package com.example.travel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
	private EditText editTextEmail;
	private EditText editTextPassword;
	private Button buttonLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		editTextEmail = findViewById(R.id.email);
		editTextPassword = findViewById(R.id.password);
		buttonLogin = findViewById(R.id.login);


		buttonLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = editTextEmail.getText().toString();
				String password = editTextPassword.getText().toString();

				if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
					showToast("Please fill all input fields");
				} else if (!isValidEmail(email)) {
					showToast("Incorrect email format");
				} else {
					performLogin(email, password);
				}
			}
		});
	}

	private void showToast(String message) {
		Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
	}

	private void handleLoginResponse(String response) {
		try {
			JSONObject jsonResponse = new JSONObject(response);
			boolean error = jsonResponse.getBoolean("error");
			String message = jsonResponse.getString("message");

			if (!error) {
				if (jsonResponse.has("email")) {
					String userEmail = jsonResponse.getString("email");

					// Store the user's email in SharedPreferences
					String password = editTextPassword.getText().toString();
					 // Your hashing function

// Storing hashed password in SharedPreferences
					SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("currentUserEmail", userEmail);
					editor.putString("currentUserPasswordHash", password);
					editor.apply();

					Toast.makeText(LoginActivity.this, "Email"+userEmail, Toast.LENGTH_SHORT).show();
					startActivity(new Intent(LoginActivity.this, MainActivity.class));
					finish();
				} else {
					showToast("Email not found in response");
				}
			}  else {
				if (message.equals("noemail")) {
					showToast("Email not found");
				} else if (message.equals("nomatch")) {
					showToast("Incorrect password");
				}
			}
		} catch (JSONException e) {
			showToast("Error parsing response");
			e.printStackTrace();
		}
	}

	private void performLogin(final String email, final String password) {
		String url = urls.BASE_PHP+urls.GET_LOGIN;

		StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						handleLoginResponse(response);
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						showToast("Login failed: " + error.getMessage());
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<>();
				params.put("email", email);
				params.put("password", password);
				return params;
			}
		};

		Volley.newRequestQueue(this).add(stringRequest);
	}

	private boolean isValidEmail(String email) {
		return Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	public void redirectToSignUp(View view) {
		startActivity(new Intent(LoginActivity.this, SignupActivity.class));
	}
	public void loginasAdmin(View view) {
		startActivity(new Intent(LoginActivity.this,Admin.class));
	}
}
