package practice.library.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.widget.Toast
import androidx.lifecycle.Observer
import android.content.Intent
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import practice.library.databinding.ActivityLogInBinding
import practice.library.models.User
import practice.library.models.UserCreate
import practice.library.retrofit.Common
import practice.library.retrofit.createUserEnum
import practice.library.viewModels.UsersViewModel

class LogInActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogInBinding

    lateinit var first_text: String
    lateinit var second_text: String
    lateinit var third_text: String
    lateinit var login_text: String
    lateinit var password_text: String
    lateinit var confirm_password_text: String

    private var viewModel: UsersViewModel = UsersViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.footerTextRegister.setOnClickListener { startActivity(Intent (this, SignInActivity::class.java)) }
        binding.btnRegister.setOnClickListener { signIn() }
        binding.loginTIET.addTextChangedListener(PhoneNumberFormattingTextWatcher());
    }

    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun goToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
    }

    private fun signIn() {
        Common.userCreateState.postValue(createUserEnum.NoReaction)
        for (field in arrayListOf(
            binding.loginTIL,
            binding.passwordTIL,
            binding.firstNameTIL,
            binding.secondNameTIL,
            binding.thirdNameTIL,
            binding.confirmPasswordTIL
        )) {
            field.error = null
        }

        first_text = binding.firstNameTIET.text.toString()
        second_text = binding.secondNameTIET.text.toString()
        third_text = binding.thirdNameTIET.text.toString()
        login_text = binding.loginTIET.text.toString()
        password_text = binding.passwordTIET.text.toString()
        confirm_password_text = binding.confirmPasswordTIET.text.toString()
        if (first_text.isEmpty()) {
            binding.firstNameTIL.error = "Пустое поле!"
            return
        }
        if (second_text.isEmpty()) {
            binding.secondNameTIL.error = "Пустое поле!"
            return
        }
        if (third_text.isEmpty()) {
            binding.thirdNameTIL.error = "Пустое поле!"
            return
        }
        if (login_text.isEmpty()) {
            binding.loginTIL.error = "Пустое поле!"
            return
        }
        if (password_text.isEmpty()) {
            binding.passwordTIL.error = "Пустое поле!"
            return
        }
        if (confirm_password_text.isEmpty()) {
            binding.confirmPasswordTIL.error = "Пустое поле!"
            return
        }
        if (password_text != confirm_password_text){
            binding.confirmPasswordTIL.error = "Пароль не совпадает!"
            return
        }

        val create_data = UserCreate(first_text, second_text, third_text, login_text, password_text);
        viewModel.createUser(create_data)
        Common.userCreateState.observe(this, object: Observer<createUserEnum>{
            override fun onChanged(t: createUserEnum?) {
                if (t == null) {
                    makeToast("Неизвестная ошибка")
                    return
                }
                when (t) {
                    createUserEnum.Ok -> {
                        makeToast("Пользователь создан!")
                        goToSignIn()
                    }
                    createUserEnum.PasswordTooEasy -> {
                        binding.passwordTIL.error = "Пароль слишком легкий!"
                    }
                    createUserEnum.LoginPatternMismatch -> {
                        binding.loginTIL.error = "Логин должен иметь формат +7(XXX)XXX-XXXX!"
                    }
                    createUserEnum.InvalidPassword -> {
                        binding.passwordTIL.error = "Некорректный пароль!"
                    }
                    createUserEnum.LoginRepeat -> {
                        binding.loginTIL.error = "Пользователь с таким логином уже есть!"
                    }
                    createUserEnum.NoReaction -> {}
                    else -> {
                        makeToast("Технические шоколадки...")
                    }
                }
            }
        })
    }
}