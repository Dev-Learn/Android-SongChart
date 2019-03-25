/*
 * Copyright 2017 Vandolf Estrellado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tran.nam.core.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    /**
     * @return layout resource id
     */
    @LayoutRes
    protected abstract fun layoutId(): Int

    open fun initLayout(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return initLayout(inflater, container)
    }

    protected fun showLoadingDialog() {
        requireActivity().run {
            if (this is BaseActivity && !isFinishing)
                showLoadingDialog()
        }
    }

    protected fun hideLoadingDialog() {
        requireActivity().run {
            if (this is BaseActivity && !isFinishing)
                hideLoadingDialog()
        }
    }

    protected fun showKeyboard() {
        requireActivity().run {
            if (this is BaseActivity && !isFinishing)
                showKeyboard()
        }
    }

    protected fun hideKeyboard() {
        requireActivity().run {
            if (this is BaseActivity && !isFinishing)
                hideKeyboard()
        }
    }
}