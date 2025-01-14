package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentSingInBinding
import ru.netology.nmedia.viewmodel.SignInViewModel

class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSingInBinding.inflate(
            inflater,
            container,
            false
        )

        val viewModel: SignInViewModel by viewModels()

        lateinit var appAuth: AppAuth

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigateUp()
        }

        with(binding) {
            login.requestFocus()
            signIn.setOnClickListener {
                viewModel.loginAttempt(login.text.toString(), password.text.toString())

                // Ошибка ввода логина/пароля
                viewModel.dataState.observe(viewLifecycleOwner) { state ->
                    if (state.loginError) {
                        binding.login.error = getString(R.string.wrong_login)
                    }
                    if (state.passwordError) {
                        binding.password.error = getString(R.string.wrong_password)
                    }
                    if(!state.loginError && !state.passwordError){
                        findNavController().navigateUp()
                    }
                }
            }
        }

        return binding.root
    }

}