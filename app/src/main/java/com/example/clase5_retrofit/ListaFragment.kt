package com.example.clase5_retrofit

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListaFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DodsAdapter
    private lateinit var searchView: SearchView
    private var listaImagenes = mutableListOf<String>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myView = inflater.inflate(R.layout.fragment_lista, container, false)
        recyclerView = myView.findViewById(R.id.recyclerviewlista)
        searchView = myView.findViewById(R.id.searchviewrecycler)
        return myView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView.setOnQueryTextListener(this)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = DodsAdapter(listaImagenes)
        recyclerView.adapter = adapter
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(URL_DOGS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchPorRaza(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getListaImagenes("breed/$query/images")
            val respuesta: RazasResponse? = call.body()

            activity?.runOnUiThread {
                if (call.isSuccessful) {
                    val imagenes = (respuesta?.imagenes ?: emptyList())
                    listaImagenes.clear()
                    listaImagenes.addAll(imagenes)
                    adapter.notifyDataSetChanged()
                } else {
                    onError()
                }
                hideKeyBoard()
            }
        }
    }

    private fun hideKeyBoard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun onError() {
        Toast.makeText(activity, "Ocurrio un error", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()) {
            searchPorRaza(query.lowercase())
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    companion object {
        const val URL_DOGS = "https://dog.ceo/api/"
    }
}
