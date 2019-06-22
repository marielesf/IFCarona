package br.edu.ifrs.canoas.ifcaronasolidaria.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.edu.ifrs.canoas.ifcaronasolidaria.model.InteresseCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.Rota;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.Usuario;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioMoodle;
import br.edu.ifrs.canoas.ifcaronasolidaria.model.UsuarioHelper;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIInteresseCarona;
import br.edu.ifrs.canoas.ifcaronasolidaria.persist.RestfulAPIUsuario;
import br.edu.ifrs.canoas.ifcaronasolidaria.service.InteresseCaronaService;
import ifcaronasolidaria.canoas.ifrs.edu.br.ifcarona.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuscaCaronaListaAdapter extends ArrayAdapter<Rota> {

    private ArrayList<Rota> lista = new ArrayList<Rota>();
    private Context contexto;

    public BuscaCaronaListaAdapter(Context ct){
        super(ct, 0, new ArrayList<Rota>());
        this.contexto = ct;
    }

    @Override
    public int getCount() {
        return this.lista.size();
    }


    @Override
    public long getItemId(int i) {
        return (long) this.lista.get(i).getIdRota();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Rota rotaDisponivel = this.lista.get(position);

        LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.item_busca_carona, null);

        TextView tw_descricao_rota = (TextView) layout.findViewById(R.id.descricao_rota);
       // tw_descricao_rota.setBackground();
        TextView tw_hora_saida = (TextView) layout.findViewById(R.id.hora_saida);
        final Button btnSolicitar = (Button) layout.findViewById(R.id.btnSolicitar);



        tw_descricao_rota.setText(rotaDisponivel.toString());
        tw_hora_saida.setText(rotaDisponivel.getHoraSaida());
        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Rota itemRota = lista.get(position);
                RestfulAPIInteresseCarona restfulAPIInteresseCarona = new RestfulAPIInteresseCarona();
                Call<Boolean> callCheckUserRota = restfulAPIInteresseCarona.checkUserRota(itemRota.getIdRota(), UsuarioHelper.getUsuarioLogado(getContext()).getUserid());
                callCheckUserRota.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {


                        if(response.body()){
                            Toast.makeText(getContext(), "Você já solicitou esta carona", Toast.LENGTH_SHORT).show();
                        }else {

                            final UsuarioMoodle retorno = UsuarioHelper.getUsuarioLogado(getContext());

                            AlertDialog.Builder alert = new AlertDialog.Builder(contexto);
                            alert.setIcon(R.drawable.logo);
                            alert.setTitle("Solicitar carona?");
                            //bild.setMessage("DE " + itemRota.getIdEnderecoInicio() + " ATÉ " + itemRota.getIdEnderecoFim());
                            alert.setMessage("Rota: De " + itemRota.getIdEnderecoInicio() + " até " + itemRota.getIdEnderecoFim() +
                                    "\nHorário Saída :" + itemRota.getHoraSaida());

                            alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    RestfulAPIUsuario restfulAPIUsuario = new RestfulAPIUsuario();
                                    restfulAPIUsuario.readQuery(retorno.getUserid()).enqueue(new Callback<List<Usuario>>() {
                                        @Override
                                        public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                                            Usuario usuario = response.body().get(0);
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                                            InteresseCarona interesseCarona = new InteresseCarona(itemRota, usuario, sdf.format(Calendar.getInstance().getTime()));
                                            InteresseCaronaService.getInstance().pushNew(interesseCarona).push();
                                            Toast.makeText(contexto, "Solicitação enviada!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<List<Usuario>> call, Throwable t) {
                                            if(!UsuarioHelper.verificaConexao(getContext())){
                                                Toast.makeText(getContext(), "ERRO: Sem Internet", Toast.LENGTH_SHORT).show();
                                            }else {
                                                Toast.makeText(getContext(), "ERRO: não foi possível conectar com o banco.", Toast.LENGTH_SHORT).show();
                                                Log.e("ERRO", t.getMessage());
                                            }
                                        }
                                    });

                                }
                            });
                            alert.setNegativeButton("Cancelar", null);
                            alert.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        if(!UsuarioHelper.verificaConexao(getContext())){
                            Toast.makeText(getContext(), "ERRO: Sem Internet", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), "ERRO: não foi possível conectar com o banco.", Toast.LENGTH_SHORT).show();
                            Log.e("ERRO", t.getMessage());
                        }
                    }
                });
            }
        });
        return layout;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                filterResults.values = lista;
                filterResults.count = lista.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            }
        };
        return filter;
    }
    public ArrayList<Rota> getLista() {
        return lista;
    }

    public void setLista(ArrayList<Rota> lista) {
        this.lista = lista;
    }

    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }
}
