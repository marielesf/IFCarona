package br.edu.ifrs.canoas.ifcaronasolidaria.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.Rota;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIOfertarCarona;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;

public class GerenciaListaRotaAdapter extends BaseAdapter {

    private ArrayList<Rota> lista;
    private Context contexto;

    public GerenciaListaRotaAdapter(ArrayList<Rota> lista, Context ct) {
        this.lista = lista == null ? new ArrayList<Rota>() : lista;
        this.contexto = ct;
    }

    @Override
    public int getCount() {
        return this.lista.size();
    }

    @Override
    public Object getItem(int i) {
        return this.lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (long) this.lista.get(i).getIdRota();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Rota rotaDisponivel = this.lista.get(position);

        LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.item_gerencia_rota, null);

        Switch sw_switchAtivaRota = (Switch) layout.findViewById(R.id.switchAtivaRota);
        TextView tw_descricao_rota = (TextView) layout.findViewById(R.id.descricao_rota);
        TextView tw_hora_saida = (TextView) layout.findViewById(R.id.hora_saida);

        sw_switchAtivaRota.setChecked(rotaDisponivel.isAtivo());
        sw_switchAtivaRota.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean bChecked) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            RestfulAPIOfertarCarona carona = new RestfulAPIOfertarCarona();
                            if (bChecked) {
                                carona.update(String.valueOf(rotaDisponivel.getIdRota()), true);

                            } else {
                                carona.update(String.valueOf(rotaDisponivel.getIdRota()), false);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        tw_descricao_rota.setText(rotaDisponivel.toString());
        tw_hora_saida.setText(rotaDisponivel.getHoraSaida());


        return layout;
    }


}
