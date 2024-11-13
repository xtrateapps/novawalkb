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
import com.novaservices.netwalk.domain.NovaWalkUser
import com.novaservices.nova.utils.RetrofitInstance
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.register.setOnClickListener {
//            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
//            startActivity(intent)
//        }

        binding.loginLink.setOnClickListener {
//            var intent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(intent)
//

            GlobalScope.launch(Dispatchers.IO) {
                val LoginUserResponse = try {
                    val newUser = NovaWalkUser(
                        "",
                        "",
                        binding.username.text.toString(),
                        binding.password.text.toString(),
                        "",
                        null,
                        "",
                        "",
                        "",
                        null
                    )
                    Log.i("network responses", newUser.toString())
                    RetrofitInstance.api.loginNovawalk(newUser)
                } catch (error: IOException) {
//                    binding.loading.visibility = View.GONE
//                    binding.notLoading.visibility = View.VISIBLE
                    Log.i("Network responses", error.toString())
                    this@LoginActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@LoginActivity,
                            error.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
//                           Toast.makeText(this@LoginActivity, "weird serror", Toast.LENGTH_LONG).show()
//                 binding.loginWrapper.visibility = View.VISIBLE
                    return@launch
                } catch (e: HttpException) {
//                    binding.loading.visibility = View.GONE
//                    binding.notLoading.visibility = View.VISIBLE
                    Log.i("Network responses", e.toString())
//                   binding.loginWrapper.visibility = View.VISIBLE
                    this@LoginActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            this@LoginActivity,
                            e.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                    return@launch
                }
                if (LoginUserResponse.isSuccessful && LoginUserResponse.body() != null) {
                    withContext(Dispatchers.Main) {
                        Log.i("network responses login", LoginUserResponse.body().toString())
                        when(LoginUserResponse.body()?.code) {
                            0 -> {
                                this@LoginActivity.runOnUiThread(Runnable {
                                    Toast.makeText(this@LoginActivity, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show()
                                })
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                                intent.putExtra("email", LoginUserResponse.body()?.result?.get(0)!!.email.toString())
                                intent.putExtra("username", binding.username.text.toString())
                                intent.putExtra("id", LoginUserResponse.body()?.id)
                                intent.putExtra("region_id", LoginUserResponse.body()?.region_id)
                                startActivity(intent)
                            }
                            1 -> {
//                                binding.loading.visibility = View.GONE
//                                binding.notLoading.visibility = View.VISIBLE
                                this@LoginActivity.runOnUiThread(Runnable {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Credenciales Erroneas",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            }
                            else -> {
                                val intent = Intent(this@LoginActivity, LoginActivity::class.java)
                                startActivity(intent)
                                this@LoginActivity.runOnUiThread(Runnable {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Ocurrio un error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            }
                        }
                    }
                } else {
                    Log.i("network responses login", LoginUserResponse.body().toString())
//                       binding.loading.visibility = View.GONE
//                        binding.notLoading.visibility = View.VISIBLE
//                    val intent = Intent(this@LoginActivity, LoginActivity::class.java)
//                    startActivity(intent)
//                    Toast.makeText(this@LoginActivity, "Ocurrio un error", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }
}