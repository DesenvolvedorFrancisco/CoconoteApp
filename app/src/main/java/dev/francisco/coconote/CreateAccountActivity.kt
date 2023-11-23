package dev.francisco.coconote

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity : AppCompatActivity() {
    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    var confirmPasswordEditText: EditText? = null
    var createAccountButton: Button? = null
    var progressBar: ProgressBar? = null
    var loginButtonTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text)
        createAccountButton = findViewById(R.id.create_account_btn)
        progressBar = findViewById(R.id.progress_bar)
        loginButtonTextView = findViewById(R.id.login_text_view_btn)
        createAccountButton.setOnClickListener(View.OnClickListener { v: View? -> createAccount() })
        loginButtonTextView.setOnClickListener(View.OnClickListener { v: View? -> finish() })
    }

    fun createAccount() {
        val email = emailEditText!!.text.toString()
        val password = passwordEditText!!.text.toString()
        val confirmPassword = confirmPasswordEditText!!.text.toString()
        val isValidate = validateData(email, password, confirmPassword)
        if (!isValidate) {
            return
        }
        createAccountInFirebase(email, password)
    }

    fun createAccountInFirebase(email: String?, password: String?) {
        changeInProgress(true)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener(
            this@CreateAccountActivity
        ) { task ->
            changeInProgress(false)
            if (task.isSuccessful) {
                //conta criada correctamente
                Utility.showToast(
                    this@CreateAccountActivity,
                    "Conta criada com sucesso, abra a sua caixa de email para confirmar"
                )
                firebaseAuth.currentUser!!.sendEmailVerification()
                firebaseAuth.signOut()
                finish()
            } else {
                //failure
                Utility.showToast(
                    this@CreateAccountActivity, task.exception!!
                        .localizedMessage
                )
            }
        }
    }

    fun changeInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar!!.visibility = View.VISIBLE
            createAccountButton!!.visibility = View.GONE
        } else {
            progressBar!!.visibility = View.GONE
            createAccountButton!!.visibility = View.VISIBLE
        }
    }

    fun validateData(email: String?, password: String, confirmPassword: String): Boolean {
        //Validar os dados do usuario.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText!!.error = "Email invalido"
            return false
        }
        if (password.length < 6) {
            passwordEditText!!.error = "O tamanho da senha é inválido"
            return false
        }
        if (password != confirmPassword) {
            confirmPasswordEditText!!.error = "A senha não conscide"
            return false
        }
        return true
    }
}