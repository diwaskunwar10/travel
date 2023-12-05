package com.example.travel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
				String confirmPassword = editTextPassword.getText().toString();

				if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ) {
					Toast.makeText(LoginActivity.this, "Please fill all input fields", Toast.LENGTH_SHORT).show();
				}
				 else if (!isValidEmail(email)) {
					Toast.makeText(LoginActivity.this, "Incorrect email format", Toast.LENGTH_SHORT).show();
				} else {
					// Perform registration process here (send data to PHP script)
					performLogin( email, password);
				}
			}
		});


		// Your other code in onCreate...

	}
	private void handleLoginResponse(String response) {
		try {
			JSONObject jsonResponse = new JSONObject(response);
			boolean error = jsonResponse.getBoolean("error");
			String message = jsonResponse.getString("message");

			if (!error) {
				// Successful login logic here
				Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
				// Redirect to MainActivity or perform necessary actions upon successful login
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				finish(); // Finish the LoginActivity to prevent going back on back press
			} else {
				// Handle different error cases based on the message received
				if (message.equals("noemail")) {
					Toast.makeText(LoginActivity.this, "Email not found", Toast.LENGTH_SHORT).show();
				} else if (message.equals("nomatch")) {
					Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
		}
	}

	private void performLogin(final String email, final String password) {
		String url = "http://192.168.1.67/travel/login.php"; // Replace with your PHP script URL

		StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// Handle the server response here
						handleLoginResponse(response);
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// Handle error responses here
						Toast.makeText(LoginActivity.this, "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

		// Add the request to the RequestQueue (Volley handles the request asynchronously)
		Volley.newRequestQueue(this).add(stringRequest);
	}

	private boolean isValidEmail(String email) {
		return Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}



	private void loginUser(String email, String password) {
		String url = "YOUR_PHP_SCRIPT_URL"; // Replace with the URL of your PHP script

		JSONObject jsonBody = new JSONObject();
		try {
			jsonBody.put("email", email);
			jsonBody.put("password", password);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							boolean error = response.getBoolean("error");
							String message = response.getString("message");

							if (!error) {
								// Successful login logic here
								// Navigate to the next activity or perform necessary actions
							} else {
								// Handle error cases based on the message received
								if (message.equals("noemail")) {
									// Handle no email error
								} else if (message.equals("nomatch")) {
									// Handle password does not match error
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// Handle Volley error
			}
		});

		Volley.newRequestQueue(this).add(jsonObjectRequest);
	}

	// Method to redirect to SignUpActivity
	public void redirectToSignUp(View view) {
		Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
		startActivity(intent);
	}
}
