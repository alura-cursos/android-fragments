package br.com.alura.technews.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.activity.extensions.transacaoFragment
import br.com.alura.technews.ui.fragment.ListaNoticiasFragment
import br.com.alura.technews.ui.fragment.VisualizaNoticiaFragment
import kotlinx.android.synthetic.main.activity_noticias.*

private const val TAG_FRAGMENT_VISUALIZA_NOTICIA = "visualizaNoticia"

class NoticiasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noticias)
        configuraFragmentPeloEstado(savedInstanceState)
    }

    private fun configuraFragmentPeloEstado(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            abreListaNoticias()
        } else {
            tentaReabrirFragmentVisualizaNoticia()
        }
    }

    private fun tentaReabrirFragmentVisualizaNoticia() {
        supportFragmentManager
            .findFragmentByTag(TAG_FRAGMENT_VISUALIZA_NOTICIA)?.let { fragment ->

                val argumentos = fragment.arguments
                val novoFragment = VisualizaNoticiaFragment()
                novoFragment.arguments = argumentos

                removeFragmentVisualizaNoticia(fragment)

                transacaoFragment {
                    val container = configuraContainerFragmentVisualizaNoticia()
                    replace(container, novoFragment, TAG_FRAGMENT_VISUALIZA_NOTICIA)
                }
            }
    }

    private fun FragmentTransaction.configuraContainerFragmentVisualizaNoticia(): Int {
        if (activity_noticias_container_secundario != null) {
            return R.id.activity_noticias_container_secundario
        }
        addToBackStack(null)
        return R.id.activity_noticias_container_primario
    }

    private fun abreListaNoticias() {
        transacaoFragment {
            replace(R.id.activity_noticias_container_primario, ListaNoticiasFragment())
        }
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        when (fragment) {
            is ListaNoticiasFragment -> {
                configuraListaNoticias(fragment)
            }
            is VisualizaNoticiaFragment -> {
                configuraVisualizaNoticia(fragment)
            }
        }
    }

    private fun configuraVisualizaNoticia(fragment: VisualizaNoticiaFragment) {
        fragment.quandoFinalizaTela = {
            supportFragmentManager
                .findFragmentByTag(TAG_FRAGMENT_VISUALIZA_NOTICIA)?.let { fragment ->
                    removeFragmentVisualizaNoticia(fragment)
                }
        }
        fragment.quandoSelecionaMenuEdicao = this::abreFormularioEdicao
    }

    private fun removeFragmentVisualizaNoticia(fragment: Fragment) {
        transacaoFragment {
            remove(fragment)
        }
        supportFragmentManager.popBackStack()
    }

    private fun configuraListaNoticias(fragment: ListaNoticiasFragment) {
        fragment.quandoNoticiaSeleciona = this::abreVisualizadorNoticia
        fragment.quandoFabSalvaNoticiaClicado = this::abreFormularioModoCriacao
    }

    private fun abreFormularioModoCriacao() {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        startActivity(intent)
    }

    private fun abreVisualizadorNoticia(noticia: Noticia) {
        val fragment = VisualizaNoticiaFragment()
        val dados = Bundle()
        dados.putLong(NOTICIA_ID_CHAVE, noticia.id)
        fragment.arguments = dados
        transacaoFragment {
            val container = configuraContainerFragmentVisualizaNoticia()
            replace(container, fragment, TAG_FRAGMENT_VISUALIZA_NOTICIA)
        }
    }

    private fun abreFormularioEdicao(noticia: Noticia) {
        val intent = Intent(this, FormularioNoticiaActivity::class.java)
        intent.putExtra(NOTICIA_ID_CHAVE, noticia.id)
        startActivity(intent)
    }

}
