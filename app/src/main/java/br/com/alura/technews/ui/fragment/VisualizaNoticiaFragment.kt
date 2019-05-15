package br.com.alura.technews.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import br.com.alura.technews.R
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.ui.activity.NOTICIA_ID_CHAVE
import br.com.alura.technews.ui.fragment.extensions.mostraErro
import br.com.alura.technews.ui.viewmodel.VisualizaNoticiaViewModel
import kotlinx.android.synthetic.main.visualiza_noticia.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.IllegalArgumentException

private const val NOTICIA_NAO_ENCONTRADA = "Notícia não encontrada"
private const val MENSAGEM_FALHA_REMOCAO = "Não foi possível remover notícia"
private const val TITULO_APPBAR = "Notícia"

class VisualizaNoticiaFragment : Fragment() {

    private val noticiaId: Long by lazy {
        arguments?.getLong(NOTICIA_ID_CHAVE)
            ?: throw IllegalArgumentException("Id inválido")
    }
    private val viewModel: VisualizaNoticiaViewModel by viewModel { parametersOf(noticiaId) }
    var quandoSelecionaMenuEdicao: (noticia: Noticia) -> Unit = {}
    var quandoFinalizaTela: () -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        verificaIdDaNoticia()
        buscaNoticiaSelecionada()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.visualiza_noticia,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = TITULO_APPBAR
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.visualiza_noticia_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.visualiza_noticia_menu_edita -> {
                viewModel.noticiaEncontrada.value?.let(quandoSelecionaMenuEdicao)
            }
            R.id.visualiza_noticia_menu_remove -> remove()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buscaNoticiaSelecionada() {
        viewModel.noticiaEncontrada.observe(this, Observer { noticiaEncontrada ->
            noticiaEncontrada?.let {
                preencheCampos(it)
            }
        })
    }

    private fun verificaIdDaNoticia() {
        if (noticiaId == 0L) {
            mostraErro(NOTICIA_NAO_ENCONTRADA)
            quandoFinalizaTela()
        }
    }

    private fun preencheCampos(noticia: Noticia) {
        visualiza_noticia_titulo.text = noticia.titulo
        visualiza_noticia_texto.text = noticia.texto
    }

    private fun remove() {
        viewModel.remove().observe(this, Observer {
            if (it.erro == null) {
                quandoFinalizaTela()
            } else {
                mostraErro(MENSAGEM_FALHA_REMOCAO)
            }
        })
    }

}