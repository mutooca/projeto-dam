package ao.uan.fc.dam.mobile.request;
public class RegistarUtilizadorRequest {
        private String nome;
        private String email;
        private String palavraChave;


        public RegistarUtilizadorRequest(
                String nome,
                String email,
                String palavraChave
        ){

            this.nome = nome;
            this.email = email;
            this.palavraChave = palavraChave;

        }


        public String getNome() {
            return nome;
        }


        public String getEmail() {
            return email;
        }


        public String getPalavraChave() {
            return palavraChave;
        }
}
