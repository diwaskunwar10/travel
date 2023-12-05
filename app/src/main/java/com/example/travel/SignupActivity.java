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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

	private EditText editTextName;
	private EditText editTextEmail;
	private EditText editTextPassword;
	private EditText editTextConfirmPassword;
	private Button buttonSignUp;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		// Initialize your UI elements
		editTextName = findViewById(R.id.username);
		editTextEmail = findViewById(R.id.email);
		editTextPassword = findViewById(R.id.password);
		editTextConfirmPassword = findViewById(R.id.Cpassword);
		buttonSignUp = findViewById(R.id.signup);
		TextView loginTextView = findViewById(R.id.loginRedirec); // Replace with your TextView ID
		loginTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignupActivity.this, LoginActivity.class); // Replace CurrentActivity with your activity name
				startActivity(intent);
			}
		});


		buttonSignUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = editTextName.getText().toString();
				String email = editTextEmail.getText().toString();
				String password = editTextPassword.getText().toString();
				String confirmPassword = editTextConfirmPassword.getText().toString();

				if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
					Toast.makeText(SignupActivity.this, "Please fill all input fields", Toast.LENGTH_SHORT).show();
				} else if (!password.equals(confirmPassword)) {
					Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
				} else if (!isValidEmail(email)) {
					Toast.makeText(SignupActivity.this, "Incorrect email format", Toast.LENGTH_SHORT).show();
				} else {
					// Perform registration process here (send data to PHP script)
					performRegistration(name, email, password);
				}
			}
		});
	}

	private boolean isValidEmail(String email) {
		return Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	private void performRegistration(final String name, final String email, final String password) {
		String url = "http://192.168.1.67/travel/sinup.php"; // Replace with your PHP script URL

		StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						// Handle the server response here
						Toast.makeText(SignupActivity.this, response, Toast.LENGTH_SHORT).show();
						// Add any further handling of the response (e.g., navigate to another activity on success)
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// Handle error responses here
						Toast.makeText(SignupActivity.this, "Registration failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<>();
				params.put("name", name);
				params.put("email", email);
				params.put("password", password);
				return params;
			}
		};

		// Add the request to the RequestQueue (Volley handles the request asynchronously)
		Volley.newRequestQueue(this).add(stringRequest);
	}



	private static boolean emailExists(Connection connection, String email) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
			String query = "SELECT email FROM users WHERE email = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, email);
			return preparedStatement.executeQuery().next();
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
}

