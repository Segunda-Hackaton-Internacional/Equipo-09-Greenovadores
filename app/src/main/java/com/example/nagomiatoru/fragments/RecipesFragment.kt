package com.example.nagomiatoru.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nagomiatoru.R
import com.example.nagomiatoru.adapters.RecipeAdapter
import com.example.nagomiatoru.data.App
import com.example.nagomiatoru.databinding.FragmentRecipesBinding
import com.example.nagomiatoru.models.Recipe

class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvRecipes.layoutManager = LinearLayoutManager(requireContext())

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        fetchRecipesFromFirestore()
    }

    private fun fetchRecipesFromFirestore() {
        App.firestore.collection("recipes")
            .get()
            .addOnSuccessListener { result ->

                val recipes = mutableListOf<Recipe>()

                for (document in result) {
                    val name = document.getString("name") ?: "No name"
                    val description = document.getString("description") ?: ""
                    val ingredients = document.get("ingredients") as? List<String> ?: emptyList()
                    val imageUrl = document.getString("imageUrl") ?: ""

                    recipes.add(
                        Recipe(
                            name = name,
                            description = description,
                            ingredients = ingredients,
                            imageUrl = imageUrl
                        )
                    )
                }

                adapter = RecipeAdapter(recipes) { recipe ->
                    //TODO VER RECETA
                    val fragment = RecipeDetailFragment.newInstance(recipe)
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null) // Para que al back vaya al fragmento anterior
                        .commit()
                }

                binding.rvRecipes.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", exception.message.toString())
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
