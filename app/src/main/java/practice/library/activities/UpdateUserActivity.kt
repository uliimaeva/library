package practice.library.activities

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import org.json.JSONObject
import practice.library.databinding.ActivityLogInBinding
import practice.library.databinding.ActivityUpdateUserBinding
import practice.library.models.User
import practice.library.models.UserPatch
import practice.library.retrofit.Common
import practice.library.retrofit.createUserEnum
import practice.library.viewModels.UsersViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateUserActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateUserBinding

    lateinit var first_text: String
    lateinit var second_text: String
    lateinit var third_text: String
    lateinit var login_text: String
    lateinit var password_text: String

    private var viewModel: UsersViewModel = UsersViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.loginTIET.setText(Common.currentUser!!.login)
        binding.passwordTIET.setText(Common.currentUser!!.password)
        binding.firstNameTIET.setText(Common.currentUser!!.first_name)
        binding.secondNameTIET.setText(Common.currentUser!!.second_name)
        binding.thirdNameTIET.setText(Common.currentUser!!.third_name)
        binding.back.setOnClickListener { startActivity(Intent (this, MainActivity::class.java)) }
        binding.btnRegister.setOnClickListener { updateData() }
    }

    private fun makeToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun updateData(){
        Common.userUpdateState.postValue(createUserEnum.NoReaction)
        for (field in arrayListOf(
            binding.loginTIL,
            binding.passwordTIL,
            binding.firstNameTIL,
            binding.secondNameTIL,
            binding.thirdNameTIL,
        )) {
            field.error = null
        }

        first_text = binding.firstNameTIET.text.toString()
        second_text = binding.secondNameTIET.text.toString()
        third_text = binding.thirdNameTIET.text.toString()
        login_text = binding.loginTIET.text.toString()
        password_text = binding.passwordTIET.text.toString()
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

        val patch_data = UserPatch(first_text, second_text, third_text, login_text, password_text)
        viewModel.patchUser(Common.currentUser!!.id, patch_data, object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    makeToast("Данные обновлены!")
                    Common.currentUser = response.body()
                    goToMain()
                } else {
                    try {
                        val error_obj = JSONObject(response.errorBody()?.string()!!);
                        val message: String = error_obj.getString("detail");
                        Log.d("USERS-PATCH", String.format("API error: %s", message))
                        when(message) {
                            "Password is too easy" -> binding.passwordTIL.error = "Пароль слишком легкий!"
                            "Login must be phone number" -> binding.loginTIL.error = "Логин должен иметь формат +7(XXX)XXX-XXXX!"
                            "Password contains forbidden symbols" -> binding.passwordTIL.error = "Некорректный пароль!"
                            "Login must be unique" -> binding.loginTIL.error = "Пользователь с таким логином уже есть!"
                            else -> {
                                Log.d("USER-PATCH", String.format("Unhandled error: %s", message));
                                makeToast("Неизвестная ошибка")
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("USERS-PATCH", String.format("Unhandled exception: %s", e.toString()))
                        makeToast("Неизвестная ошибка")
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("USERS-PATCH", String.format("Request failed %s", t.toString()))
                makeToast("Неизвестная ошибка")
            }
            }
        )
    }
}