package com.example.authandmessaging;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
    GoogleApiClient.OnConnectionFailedListener{
        @BindView(R.id.img_profile)
        ImageView imgProfile;
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.txt_email)
        TextView txtEmail;
        @BindView(R.id.btn_sign_out)
        Button btnSignOut;
        @BindView(R.id.prof_section)
        LinearLayout profSection;
        @BindView(R.id.btn_login)
        SignInButton btnLogin;
        private static final int REQ_CODE = 3;

        private GoogleApiClient googleApiClient;
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        profSection.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                }
            };
        GoogleSignInOptions signInOptions = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();
            }
    @OnClick(R.id.btn_sign_out)
    public void onBtnSignOutClicked() {
        signOut();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new
        ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }
    private void signIn() {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    private void handleResult(GoogleSignInResult result) {
    if (result.isSuccess()) {
        GoogleSignInAccount account = result.getSignInAccount();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            txtName.setText(firebaseUser.getDisplayName());
                            txtEmail.setText(firebaseUser.getEmail());
                            Glide.with(MainActivity.this).load(firebaseUser.getPhotoUrl().toString()).into(imgProfile);
                            updateUI(true);
                            } else {
                            updateUI(false);
                            }
                    }
                });
    } else {
        Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
    }
}
    private void updateUI(boolean isLogin) {
    if (isLogin) {
        profSection.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
    } else {
        profSection.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
    }
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQ_CODE) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleResult(result);
    }
}
    @OnClick(R.id.btn_login)
    public void onViewClicked() {
    signIn();
    }
}


