package com.anthonyponte.seamcalc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProviders
import com.anthonyponte.seamcalc.databinding.ActivityMainBinding
import com.google.android.material.color.MaterialColors
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var model: MeasuresViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        binding.toolbarMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.info -> {
                    startActivity(Intent(this@MainActivity, InfoActivity::class.java))
                    true
                }
                R.id.compartir -> {
                    binding.contentMain.etEspesorCuerpo.text

                    val text = getString(
                        R.string.compartir_texto,
                        binding.contentMain.etEspesorCuerpo.text,
                        binding.contentMain.etEspesorTapa.text,
                        binding.contentMain.etGanchoCuerpo.text,
                        binding.contentMain.etGanchoTapa.text,
                        binding.contentMain.etAlturaCierre.text,
                        binding.contentMain.etEspesorCierre.text,
                        binding.contentMain.tvTraslape.text,
                        binding.contentMain.tvSuperposicion.text,
                        binding.contentMain.tvPenetracion.text,
                        binding.contentMain.tvEspacioLibre.text,
                        binding.contentMain.tvCompacidad.text
                    )

                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT, text
                        )
                        type = "text/plain"
                    }

                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                    true
                }
                else -> false
            }
        }

        val colorOnBackground = MaterialColors.getColor(root, R.attr.colorOnBackground)
        val colorError = MaterialColors.getColor(root, R.attr.colorError)
        val colorSuccess = MaterialColors.getColor(root, R.attr.colorSuccess)
        val colorWarning = MaterialColors.getColor(root, R.attr.colorWarning)

        binding.contentMain.etEspesorCuerpo.addTextChangedListener(watcher)
        binding.contentMain.etEspesorTapa.addTextChangedListener(watcher)
        binding.contentMain.etGanchoCuerpo.addTextChangedListener(watcher)
        binding.contentMain.etGanchoTapa.addTextChangedListener(watcher)
        binding.contentMain.etAlturaCierre.addTextChangedListener(watcher)
        binding.contentMain.etEspesorCierre.addTextChangedListener(watcher)

        model = ViewModelProviders.of(this).get(MeasuresViewModel::class.java)

        model.measures.observe(this) { item ->
            binding.contentMain.tvTraslape.text = format(item.traslape)
            binding.contentMain.tvSuperposicion.text = format(item.superposicion)
            binding.contentMain.tvPenetracion.text = format(item.penetracion)
            binding.contentMain.tvEspacioLibre.text = format(item.espacioLibre)
            binding.contentMain.tvCompacidad.text = format(item.compacidad)

            if (item.traslape >= 1) {
                binding.contentMain.tvTraslape.setTextColor(colorSuccess)
            } else if (item.traslape < 0) {
                binding.contentMain.tvTraslape.setTextColor(colorError)
            } else {
                binding.contentMain.tvTraslape.setTextColor(colorOnBackground)
            }

            if (item.superposicion >= 80) {
                binding.contentMain.tvSuperposicion.setTextColor(colorSuccess)
            } else if (item.superposicion < 80 && item.superposicion >= 45) {
                binding.contentMain.tvSuperposicion.setTextColor(colorWarning)
            } else if (item.superposicion < 45 && item.superposicion != 0.0) {
                binding.contentMain.tvSuperposicion.setTextColor(colorError)
            } else {
                binding.contentMain.tvSuperposicion.setTextColor(colorOnBackground)
            }

            if (item.penetracion >= 95) {
                binding.contentMain.tvPenetracion.setTextColor(colorSuccess)
            } else if (item.penetracion < 95 && item.penetracion > 70) {
                binding.contentMain.tvPenetracion.setTextColor(colorWarning)
            } else if (item.penetracion <= 70 && item.penetracion != 0.0) {
                binding.contentMain.tvPenetracion.setTextColor(colorError)
            } else {
                binding.contentMain.tvPenetracion.setTextColor(colorOnBackground)
            }

            if (item.espacioLibre > 0.19) {
                binding.contentMain.tvEspacioLibre.setTextColor(colorError)
            } else if (item.espacioLibre <= 0.19 && item.espacioLibre != 0.0) {
                binding.contentMain.tvEspacioLibre.setTextColor(colorSuccess)
            } else {
                binding.contentMain.tvEspacioLibre.setTextColor(colorOnBackground)
            }

            if (item.compacidad >= 85) {
                binding.contentMain.tvCompacidad.setTextColor(colorSuccess)
            } else if (item.compacidad < 85 && item.compacidad >= 75) {
                binding.contentMain.tvCompacidad.setTextColor(colorWarning)
            } else if (item.compacidad < 75 && item.compacidad >= 1) {
                binding.contentMain.tvCompacidad.setTextColor(colorError)
            } else if (item.compacidad == 0.0) {
                binding.contentMain.tvCompacidad.setTextColor(colorOnBackground)
            }
        }

        binding.contentMain.btnLimpiar.setOnClickListener {
            val measure = Measures()
            measure.traslape = 0.0
            measure.compacidad = 0.0
            measure.penetracion = 0.0
            measure.superposicion = 0.0
            measure.espacioLibre = 0.0

            model.setMeasure(measure)

            binding.contentMain.etEspesorCuerpo.text?.clear()
            binding.contentMain.etEspesorTapa.text?.clear()
            binding.contentMain.etGanchoCuerpo.text?.clear()
            binding.contentMain.etGanchoTapa.text?.clear()
            binding.contentMain.etAlturaCierre.text?.clear()
            binding.contentMain.etEspesorCierre.text?.clear()

            binding.contentMain.etEspesorCuerpo.requestFocusFromTouch()
        }
    }

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            binding.contentMain.btnLimpiar.isEnabled = isNotEmpty()

            if (isNotEmpty()) {
                val espesorCuerpo = binding.contentMain.etEspesorCuerpo.text.toString().toDouble()
                val espesorTapa = binding.contentMain.etEspesorTapa.text.toString().toDouble()
                val ganchoCuerpo = binding.contentMain.etGanchoCuerpo.text.toString().toDouble()
                val ganchoTapa = binding.contentMain.etGanchoTapa.text.toString().toDouble()
                val alturaCierre = binding.contentMain.etAlturaCierre.text.toString().toDouble()
                val espesorCierre = binding.contentMain.etEspesorCierre.text.toString().toDouble()

                val measures = Measures()

                measures.traslape = traslape(ganchoTapa, ganchoCuerpo, espesorTapa, alturaCierre)

                measures.superposicion =
                    superposicion(
                        ganchoTapa,
                        ganchoCuerpo,
                        espesorTapa,
                        alturaCierre,
                        espesorCuerpo
                    )

                measures.penetracion =
                    penetracion(ganchoCuerpo, espesorCuerpo, alturaCierre, espesorTapa)

                measures.espacioLibre = espacioLibre(espesorCierre, espesorTapa, espesorCuerpo)

                measures.compacidad = compacidad(espesorCuerpo, espesorTapa, espesorCierre)

                model.setMeasure(measures)
            } else {
                val measures = Measures()
                measures.traslape = 0.0
                measures.compacidad = 0.0
                measures.penetracion = 0.0
                measures.superposicion = 0.0
                measures.espacioLibre = 0.0

                model.setMeasure(measures)
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    fun isNotEmpty(): Boolean {
        return binding.contentMain.etEspesorCuerpo.text.toString().isNotEmpty() &&
                binding.contentMain.etEspesorTapa.text.toString().isNotEmpty() &&
                binding.contentMain.etGanchoCuerpo.text.toString().isNotEmpty() &&
                binding.contentMain.etGanchoTapa.text.toString().isNotEmpty() &&
                binding.contentMain.etAlturaCierre.text.toString().isNotEmpty() &&
                binding.contentMain.etEspesorCierre.text.toString().isNotEmpty()
    }

    private fun traslape(
        ganchoTapa: Double,
        ganchoCuerpo: Double,
        espesorTapa: Double,
        alturaCierre: Double
    ): Double {
        return (ganchoTapa + ganchoCuerpo + 1.1 * espesorTapa) - alturaCierre
    }

    private fun superposicion(
        ganchoTapa: Double,
        ganchoCuerpo: Double,
        espesorTapa: Double,
        alturaCierre: Double,
        espesorCuerpo: Double
    ): Double {
        return ((ganchoTapa + ganchoCuerpo + 1.1 * espesorTapa - alturaCierre) / (alturaCierre - 1.1 * (2 * espesorTapa + espesorCuerpo))) * 100
    }

    private fun penetracion(
        ganchoCuerpo: Double,
        espesorCuerpo: Double,
        alturaCierre: Double,
        espesorTapa: Double
    ): Double {
        return ((ganchoCuerpo - 1.1 * espesorCuerpo) * 100) / (alturaCierre - 1.1 * ((2 * espesorTapa) + espesorCuerpo))
    }

    private fun espacioLibre(
        espesorCierre: Double,
        espesorTapa: Double,
        espesorCuerpo: Double
    ): Double {
        return espesorCierre - (3 * espesorTapa + 2 * espesorCuerpo)
    }

    private fun compacidad(
        espesorCuerpo: Double,
        espesorTapa: Double,
        espesorCierre: Double
    ): Double {
        return ((2 * espesorCuerpo + 3 * espesorTapa) / espesorCierre) * 100
    }

    private fun format(num: Double): String {
        val df = DecimalFormat("#.###")
        return df.format(num)
    }
}