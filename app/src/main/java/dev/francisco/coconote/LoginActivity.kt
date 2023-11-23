package dev.francisco.coconote

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import dev.francisco.coconote.Utility.showToast

class LoginActivity : AppCompatActivity() {
    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    var loginButton: Button? = null
    var progressBar: ProgressBar? = null
    var createAccountBtnTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        emailEditText = findViewById(R.id.email_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_btn)
        progressBar = findViewById(R.id.progress_bar)
        createAccountBtnTextView = findViewById(R.id.create_account_text_view_btn)
        loginButton.setOnClickListener(View.OnClickListener { v: View? -> loginUser() })
        createAccountButtonTextView.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(this@LoginActivity, CreateAccountActivity::class.java)
            )
        })
    }

    fun loginUser() {
        val email = emailEditText!!.text.toString()
        val password = passwordEditText!!.text.toString()
        val isValidate = validateData(email, password)
        if (!isValidate) {
            return
        }
        loginAccountInFirebase(email, password)
    }

    fun loginAccountInFirebase(email: String?, password: String?) {
        val firebaseAuth = FirebaseAuth.getInstance()
        changeInProgress(true)
        firebaseAuth.signInWithEmailAndPassword(email!!, password!!).addOnCompleteListener { task ->
            changeInProgress(false)
            if (task.isSuccessful) {
                //logado com sucesso
                if (firebaseAuth.currentUser!!.isEmailVerified) {
                    // ir para o mainactivity
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    showToast(
                        this@LoginActivity,
                        " Verifique a sua caixa de email, para confirmar a sua Conta"
                    )
                }
            } else {
                //falha ao logar
                showToast(this@LoginActivity, task.exception!!.localizedMessage)
            }
        }
    }

    fun changeInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar!!.visibility = View.VISIBLE
            loginButton!!.visibility = View.GONE
        } else {
            progressBar!!.visibility = View.GONE
            loginButton!!.visibility = View.VISIBLE
        }
    }

    fun validateData(email: String?, password: String): Boolean {
        //Validar os dados do usuario.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText!!.error = "Email invalido"
            return false
        }
        if (password.length < 6) {
            passwordEditText!!.error = "O tamanho da senha é inválido"
            return false
        }
        return true
    }
}