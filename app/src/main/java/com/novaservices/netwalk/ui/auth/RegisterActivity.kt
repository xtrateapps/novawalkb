package com.novaservices.netwalk.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.novaservices.lotonovabanklot.domain.Manager
import com.novaservices.netwalk.MainActivity
import com.novaservices.netwalk.R
import com.novaservices.netwalk.databinding.ActivityLoginBinding
import com.novaservices.netwalk.databinding.ActivityRegisterBinding
import com.novaservices.netwalk.domain.NovaWalkUser
import com.novaservices.nova.utils.RetrofitInstance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerLink.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val RegisterUserResponse = try {
                    val newUser = NovaWalkUser(
                        "${binding.name.text.toString()}",
                        "${binding.lastname.text.toString()}",
                        "${binding.username.text.toString()}",
                        "${binding.password.text.toString()}",
                        "${binding.dni.text.toString()}",
                        null,
                        "${binding.email.text.toString()}",
                        "${binding.phone.text.toString()}",
                        "${binding.name.text.toString()}",
                        null
                    )
                    Log.i("network responses", newUser.toString())
                    RetrofitInstance.api.registerNovawalk(newUser)
                } catch (error: IOException) {
                    binding.notLoading.visibility = View.VISIBLE
                    Log.i("Network responses", error.toString())
                    this@RegisterActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@RegisterActivity,
                            error.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
//                           Toast.makeText(this@LoginActivity, "weird serror", Toast.LENGTH_LONG).show()
//                 binding.loginWrapper.visibility = View.VISIBLE
                    return@launch
                } catch (e: HttpException) {
                    Log.i("Network responses", e.toString())
//                   binding.loginWrapper.visibility = View.VISIBLE
                    this@RegisterActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@RegisterActivity,
                            e.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                    return@launch
                }
                if (RegisterUserResponse.isSuccessful && RegisterUserResponse.body() != null) {
                    withContext(Dispatchers.Main) {
                        Log.i("network responses login", RegisterUserResponse.body().toString())
                        when(RegisterUserResponse.body()?.code) {
                            0 -> {
                                this@RegisterActivity.runOnUiThread(Runnable {
                                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                })
                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//                            intent.putExtra("email", LoginUserResponse.body()?.result?.get(0)!!.email.toString())
                            intent.putExtra("username2", binding.username.text.toString())
//                            intent.putExtra("roleId", LoginUserResponse.body()?.result?.get(0)!!.role_id.toString())
                            startActivity(intent)
                            }
                            1 -> {
                                this@RegisterActivity.runOnUiThread(Runnable {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Credenciales Erroneas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            }
                            else -> {
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                startActivity(intent)
                                this@RegisterActivity.runOnUiThread(Runnable {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Ocurrio un error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            }
                        }
                    }
                } else {
//                       binding.loading.visibility = View.GONE
//                        binding.notLoading.visibility = View.VISIBLE
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this@RegisterActivity, "Ocurrio un error", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}