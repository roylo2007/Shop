package com.roy.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.row_movie.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.URL

class MovieActivity : AppCompatActivity(), AnkoLogger{

    lateinit var movies: List<Movie>
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.myjson.com/bins/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        doAsync {
           /* val json = URL("https://api.myjson.com/bins/1gsak2").readText()
            movies = Gson().fromJson<List<Movie>>(json,
                object : TypeToken<List<Movie>>(){}.type)*/
            val movieService = retrofit.create(MovieService::class.java)
            movies = movieService.listMovies().execute().body()!!.toList()
            movies.forEach {
                info("${it.Title} ${it.imdbRating}")
            }
            uiThread {
                recycler.layoutManager = LinearLayoutManager(it)
                recycler.setHasFixedSize(true)
                recycler.adapter = MovieAdapter()
            }
        }
    }

    inner class MovieAdapter(): RecyclerView.Adapter<MovieHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_movie, parent, false)
            return MovieHolder(view)
        }

        override fun getItemCount(): Int {
            val size = movies.size?:0
            return size
        }

        override fun onBindViewHolder(holder: MovieHolder, position: Int) {
            val movie = movies[position]
            holder.bindMovie(movie)
        }

    }

    inner class MovieHolder(view: View):RecyclerView.ViewHolder(view){
        val Title = view.movie_title
        val Imdb = view.movie_imdb
        val Director = view.movie_director
        val Poster = view.movie_poster
        fun bindMovie(movie: Movie) {
            Title.text = movie.Title
            Imdb.text = movie.imdbRating
            Director.text = movie.Director
            Glide.with(this@MovieActivity)
                .load(movie.Images[0])
                .override(300)
                .into(Poster)
        }
    }
}

data class Movie(
    val Actors: String,
    val Awards: String,
    val ComingSoon: Boolean,
    val Country: String,
    val Director: String,
    val Genre: String,
    val Images: List<String>,
    val Language: String,
    val Metascore: String,
    val Plot: String,
    val Poster: String,
    val Rated: String,
    val Released: String,
    val Response: String,
    val Runtime: String,
    val Title: String,
    val Type: String,
    val Writer: String,
    val Year: String,
    val imdbID: String,
    val imdbRating: String,
    val imdbVotes: String,
    val totalSeasons: String
)

interface MovieService{
    @GET("1gsak2")
    fun listMovies(): Call<List<Movie>>
}