package com.example.nagomiatoru.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nagomiatoru.R
import com.example.nagomiatoru.models.Recipe

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onViewMoreClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvRecipeTitle)
        val description: TextView = itemView.findViewById(R.id.tvRecipeDescription)
        val image: ImageView = itemView.findViewById(R.id.ivRecipeImage)
        val btnViewMore: Button = itemView.findViewById(R.id.btnViewMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.title.text = recipe.name
        holder.description.text = recipe.description
        Glide.with(holder.itemView.context)
            .load(recipe.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.image)

        holder.btnViewMore.setOnClickListener {
            onViewMoreClick(recipe)
        }
    }

    override fun getItemCount(): Int = recipes.size
}
