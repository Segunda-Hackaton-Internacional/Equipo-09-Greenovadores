package com.example.nagomiatoru.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.nagomiatoru.R
import com.example.nagomiatoru.databinding.FragmentRecipeDetailBinding
import com.example.nagomiatoru.models.Recipe

class RecipeDetailFragment : Fragment() {

    private var recipe: Recipe? = null
    private var _binding: FragmentRecipeDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipe = it.getParcelable("recipe")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        recipe?.let {
            binding.tvRecipeTitle.text = it.name
            binding.tvRecipeDescription.text = it.description
            binding.tvIngredients.text = it.ingredients.joinToString(separator = "\n")
            Glide.with(this)
                .load(it.imageUrl)
                .into(binding.ivRecipeImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(recipe: Recipe) = RecipeDetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable("recipe", recipe)
            }
        }
    }
}