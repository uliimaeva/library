package practice.library.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import org.w3c.dom.Text
import practice.library.databinding.ActivitySigInBinding
import practice.library.models.User
import practice.library.retrofit.Common;
import practice.library.retrofit.loginUserEnum
import practice.library.viewModels.BookViewModel
import practice.library.viewModels.UsersViewModel


class SignInActivity : AppCompatActivity() {

    lateinit var binding: ActivitySigInBinding
    lateinit var login_text: String
    lateinit var password_text: String
    private var viewModel: UsersViewModel = UsersViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnRegister.setOnClickListener { signIn() }
        binding.footerTextRegister.setOnClickListener { startActivity(Intent (this, LogInActivity::class.java)) }
        uploadScreen()

    }

    private fun uploadScreen() {
        viewModel.getUsers()
    }

    private fun signIn() {
        binding.loginTIL.error = null
        binding.passwordTIL.error = null
        login_text = binding.loginTIET.text.toString()
        password_text = binding.passwordTIET.text.toString()
        if (login_text.isEmpty()){
            binding.loginTIL.error  = "Пустое поле!"
            return
        } else if (password_text.isEmpty()){
            binding.passwordTIL.error  = "Пустое поле!"
            return
        }

        if (!"\\+7\\(\\d{3}\\)\\d{3}-\\d{4}".toRegex().matches(login_text)) {
            binding.loginTIL.error = "Неправильный формат логина!"
            return
        }

        Common.allUsers.observe(this, object: Observer<MutableList<User>> {
            override fun onChanged(t: MutableList<User>?) {
                if (t == null) {
                    Common.userLoginState = loginUserEnum.FailedRequest;
                    return
                }
                for (user in t) {
                    Log.d("LOGIN", String.format("Comparing %s to %s", user.login, login_text))
                    if (user.login == login_text) {
                        if (user.password == password_text) {
                            Common.currentUser = user;
                            Common.userLoginState = loginUserEnum.Ok
                            return
                        } else {
                            Common.userLoginState = loginUserEnum.WrongPassword
                            return
                        }
                    }
                }
                Common.userLoginState = loginUserEnum.NotFound;
            }

        })
        when (Common.userLoginState) {
            loginUserEnum.Ok -> {
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                startActivity(Intent (this, MainActivity::class.java))
            }
            loginUserEnum.NotFound -> binding.passwordTIL.error  = "Пользователь не найден"
            loginUserEnum.FailedRequest -> binding.passwordTIL.error  = "Ошибка запроса"
            loginUserEnum.WrongPassword -> binding.passwordTIL.error  = "Пароль неверный!"
            else -> Toast.makeText(this, "АААААААААААА", Toast.LENGTH_SHORT).show()
        }
    }
}