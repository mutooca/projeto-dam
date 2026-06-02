package ao.uan.fc.dam.mobile.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ao.uan.fc.dam.mobile.R;
import com.google.android.material.button.MaterialButton;

public class AdicionarLocal extends DialogFragment {
    private OnLocalNameSubmitted listener;

    public interface OnLocalNameSubmitted {
        void onLocalNameSubmitted(String nome);
    }

    public void setOnLocalNameSubmitted(OnLocalNameSubmitted listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_adicionar_local, container, false);
        final EditText inputNome = view.findViewById(R.id.local);
        MaterialButton btnAdicionar = view.findViewById(R.id.btnRec);
        
        btnAdicionar.setOnClickListener(v -> {
            String nome = inputNome.getText().toString().trim();
            if (nome.isEmpty()) {
                inputNome.setError("Nome obrigatorio");
                return;
            }
            if (this.listener != null) {
                this.listener.onLocalNameSubmitted(nome);
            }
            dismiss();
        });
        
        return view;
    }
}
